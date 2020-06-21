## 概述

如果想要直接体验，直接 `clone` 项目，由于是`springboot`项目而且并没有和`DB`做交互，所以可以直接启动，借助postman进行体验`shiro`对权限的控制。

## 特性

* 完全使用了 `Shiro` 的注解配置，保持高度的灵活性。
* 放弃 `Cookie` ，`Session` ，使用JWT进行鉴权，完全实现无状态鉴权。
* `JWT` 密钥支持过期时间。
* 对跨域提供支持，单点登录支持。

## 准备工作

   请先确认熟悉以下几点。
- JDK 1.8环境
- Maven 3.6.3
- Spring Boot 基本语法，至少要懂得 `Controller` 、 `RestController` 、 `Autowired` 等这些基本注释。其实看看官方的 `Getting-Start` 教程就差不多了。
- [JWT](https://jwt.io/) （Json Web Token）的基本概念，并且会简单操作JWT的 [JAVA SDK](https://github.com/auth0/java-jwt)。
- `Shiro` 的基本操作，看下官方的 [10 Minute Tutorial](http://shiro.apache.org/10-minute-tutorial.html) 即可。

为什么要用 JWT ，如果要实现前后端分离，所以不可能使用 `session`， `cookie` 的方式进行鉴权，JWT 就被派上了用场，可以通过一个加密密钥来进行前后端的鉴权。

## 程序逻辑

1. 我们 POST 用户名与密码到 `/login` 进行登入，如果成功返回一个加密 token，失败的话直接返回 401 错误。
2. 之后用户访问每一个需要权限的网址请求必须在 `header` 中添加 `Authorization` 字段，例如 `Authorization: token` ，`token` 为密钥。
3. 后台会进行 `token` 的校验，如果有误会直接返回 401。

## Token加密说明

- 携带了 `username` 信息在 token 中。
- 设定了过期时间。
- 使用用户登入密码对 `token` 进行加密。

## Token校验流程

1. 获得 `token` 中携带的 `username` 信息。
2. 进入数据库搜索这个用户，得到他的密码。
3. 使用用户的密码来检验 `token` 是否正确。

## 准备Maven文件

新建一个 Maven 工程，添加相关的 dependencies。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.6.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>

    <groupId>com.darryl</groupId>
    <artifactId>jwt</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <description>this project for learning JWT(java web token)</description>


    <dependencies>
        <!--shiro-->
        <dependency>
            <groupId>org.apache.shiro</groupId>
            <artifactId>shiro-spring</artifactId>
            <version>1.3.2</version>
        </dependency>
        <!--java web token-->
        <dependency>
            <groupId>com.auth0</groupId>
            <artifactId>java-jwt</artifactId>
            <version>3.2.0</version>
        </dependency>
        <!--lombok-->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <!--springboot web-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <version>2.1.6.RELEASE</version>
        </dependency>

    </dependencies>

</project>
```

## 构建简易的数据源

为了缩减教程的代码，我使用 `HashMap` 本地模拟了一个数据库，结构如下：

| username | password | role  | permission |
| -------- | -------- | ----- | ---------- |
| smith    | smith123 | user  | view       |
| danny    | danny123 | admin | view,edit  |

这是一个最简单的用户权限表，如果想更加进一步了解，自行百度 RBAC。

之后再构建一个 `UserService` 来模拟数据库查询，并且把结果放到 `UserBean` 之中。

**UserService.java**

```java
/**
 * @Auther: Darryl
 * @Description: user service
 * @Date: 2020/06/21
 */
@Component
public class UserService {
	public UserBean getUser(String username) {
		// 没有此用户直接返回null
		if (! DataSource.getData().containsKey(username))
			return null;

		UserBean user = new UserBean();
		Map<String, String> detail = DataSource.getData().get(username);

		user.setUsername(username);
		user.setPassword(detail.get("password"));
		user.setRole(detail.get("role"));
		user.setPermission(detail.get("permission"));
		return user;
	}
}
```

**UserBean.java**

```java
/**
 * @Auther: Darryl
 * @Description: user model bean
 * @Date: 2020/06/21
 */
@Data
public class UserBean {
	private String username;

	private String password;

	private String role;

	private String permission;
}
```

## 配置 JWT

一个简单的 JWT 加密，校验工具，并且使用用户自己的密码充当加密密钥，这样保证了 token 即使被他人截获也无法破解。并且我们在 `token` 中附带了 `username` 信息，并且设置密钥5分钟就会过期。

```java
/**
 * @Auther: Darryl
 * @Description: jwt util
 * @Date: 2020/06/21
 */
public class JwtUtil {

	// 过期时间5分钟
	private static final long EXPIRE_TIME = 5*60*1000;


	/**
	 * 根据用户信息，如用户名和密码生成token值，同时带有过期时间5min
	 * @param username 用户名
	 * @param password 密码
	 * @return token值
	 */
	public static String generateToken(String username, String password) {
		try {
			Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
			Algorithm algorithm = Algorithm.HMAC256(password);
			return JWT.create().withClaim("username", username).withExpiresAt(date).sign(algorithm);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	/**
	 * 从token中获取username
	 * @param token token值
	 * @return token中的用户信息
	 */
	public static String getUserName(String token) {
		try {
			DecodedJWT decodedToken = JWT.decode(token);
			return decodedToken.getClaim("username").asString();
		} catch (JWTDecodeException e) {
			return null;
		}
	}

	/**
	 * 校验token是否正确
	 * @param token token值
	 * @param username 用户名
	 * @param password 用户密码
	 * @return 是否校验通过
	 */
	public static boolean verify(String token, String username, String password) {
		try {
			// 对密码进行加密
			Algorithm algorithm = Algorithm.HMAC256(password);
			JWTVerifier verifier = JWT.require(algorithm).withClaim("username", username).build();
			// 校验
			verifier.verify(token);
			return true;
		} catch (UnsupportedEncodingException e) {
			return false;
		}
	}

}
```

## 构建URL

**ResponseResult.java**

实现 restful，那我们要保证每次返回的格式都是相同的，因此我建立了一个 `ResponseResult` 来统一返回一个响应bean，使用spring框架中的ResponseEntity进行封装返回。

```java
/**
 * @Auther: Darryl
 * @Description: 响应结果体
 * @Date: 2020/06/21
 */
@Data
public class ResponseResult {
	// 响应消息简述
	private String msg;
	// 响应数据结构体
	private Object data;
}
```

**URL结构**

| URL                 | 作用                      |
| ------------------- | ----------------------- |
| /login              | 登入                      |
| /article            | 所有人都可以访问，但是用户与游客看到的内容不同 |
| /requireAuth       | 登入的用户才可以进行访问            |
| /requireRole       | admin的角色用户才可以登入         |
| /requirePermission | 拥有view和edit权限的用户才可以访问   |

**DemoController**

```java
/**
 * @Auther: Darryl
 * @Description: demo controller for test shiro
 * @Date: 2020/06/21
 */
@RestController
public class DemoController {

	@Autowired
	private UserService userService;

	/**
	 * 登录入口
	 * @param username 用户名
	 * @param password 密码
	 * @return token值给前端
	 */
	@PostMapping(value = "/login")
	public ResponseEntity<ResponseResult> login (String username, String password) {
		UserBean user = userService.getUser(username);
		ResponseResult responseResult = new ResponseResult();

		if (user != null && password.equals(user.getPassword())) {
			responseResult.setMsg("登录成功");
			responseResult.setData(JwtUtil.generateToken(username, password));
			return new ResponseEntity<>(responseResult, HttpStatus.OK);
		} else {
			responseResult.setMsg("用户名和密码不对，请重试～");
			return new ResponseEntity<>(responseResult, HttpStatus.FORBIDDEN);
		}
	}

	/**
	 * 登录是否会返回不同的结果给用户
	 * @param request 请求
	 * @return 响应体
	 */
	@GetMapping(value = "/article")
	public ResponseEntity<ResponseResult> article(HttpServletRequest request) {
		Subject subject = SecurityUtils.getSubject();
		ResponseResult responseResult = new ResponseResult();

		if (subject.isAuthenticated()) {
			responseResult.setMsg("已经登录");
			responseResult.setData("你好 " + JwtUtil.getUserName(request.getHeader("Authorization")));
			return new ResponseEntity<>(responseResult, HttpStatus.OK);
		} else {
			responseResult.setMsg("未登录");
			responseResult.setData("你好，游客");
			return new ResponseEntity<>(responseResult, HttpStatus.OK);
		}
	}

	/**
	 * 必须要登录鉴权才能访问的接口
	 * @return 响应体
	 */
	@GetMapping(value = "/requireAuth")
	@RequiresAuthentication
	public ResponseEntity<ResponseResult> requireAuth() {
		ResponseResult responseResult = new ResponseResult();
		responseResult.setMsg("登录成功");
		responseResult.setData("Hello world～～～");
		return new ResponseEntity<>(responseResult, HttpStatus.OK);
	}

	@RequestMapping(path = "/401")
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ResponseEntity<ResponseResult> unauthorized() {
		ResponseResult responseResult = new ResponseResult();
		responseResult.setMsg("无权限，请先登录");
		return new ResponseEntity<>(responseResult, HttpStatus.UNAUTHORIZED);
	}

	/**
	 * 需要角色鉴权后才能访问
	 * @return
	 */
	@GetMapping("/requireRole")
	@RequiresRoles("admin")
	public ResponseEntity<ResponseResult>  requireRole() {
		ResponseResult responseResult = new ResponseResult();
		responseResult.setMsg("登录成功并且角色鉴权成功");
		responseResult.setData("该用户该角色可以访问");
		return new ResponseEntity<>(responseResult, HttpStatus.OK);
	}

	@GetMapping("/requirePermission")
	@RequiresPermissions(logical = Logical.AND, value = {"view", "edit"})
	public ResponseEntity<ResponseResult> requirePermission() {
		ResponseResult responseResult = new ResponseResult();
		responseResult.setMsg("权限设置");
		responseResult.setData("该用户该角色可以访问");
		return new ResponseEntity<>(responseResult, HttpStatus.OK);
	}

}
```

**处理框架异常**

之前说过 restful 要统一返回的格式，所以我们也要全局处理 `Spring Boot` 的抛出异常。利用 `@RestControllerAdvice` 能很好的实现。

```java
/**
 * @Auther: Darryl
 * @Description: 做一个controller的AOP，用于捕获shiro的异常
 * @Date: 2020/06/21
 */
@RestControllerAdvice
public class ExceptionController {

	// 捕捉shiro的异常
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ExceptionHandler(ShiroException.class)
	public ResponseEntity<ResponseResult> handle401(ShiroException e) {
		ResponseResult responseResult = new ResponseResult();
		responseResult.setMsg("Unauthorized");
		responseResult.setData(e.getMessage());
		return new ResponseEntity(responseResult, HttpStatus.UNAUTHORIZED);
	}

	// 捕捉其他所有异常
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<String> globalException(HttpServletRequest request, Throwable ex) {
		ResponseResult responseResult = new ResponseResult();
		responseResult.setMsg("Unauthorized");
		responseResult.setData(ex.getMessage());
		return new ResponseEntity(responseResult, getStatus(request));
	}

	private HttpStatus getStatus(HttpServletRequest request) {
		Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
		if (statusCode == null) {
			return HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return HttpStatus.valueOf(statusCode);
	}
}
```

## 配置 Shiro

大家可以先看下官方的 [Spring-Shiro](http://shiro.apache.org/spring.html) 整合教程，有个初步的了解。不过既然我们用了 `Spring-Boot`，那我们肯定要争取零配置文件。

**实现JWTToken**

`JwtToken` 差不多就是 `Shiro` 用户名密码的载体。因为我们是前后端分离，服务器无需保存用户状态，所以不需要 `RememberMe` 这类功能，我们简单的实现下 `AuthenticationToken
` 接口即可。因为 `token` 自己已经包含了用户名等信息，所以这里我就弄了一个字段。如果你喜欢钻研，可以看看官方的 `UsernamePasswordToken` 是如何实现的。

```java
/**
 * @Auther: Darryl
 * @Description: 因为我们的token中包含了用户信息，该类基本可以认为是用户名和密码的载体，简单只需要一个token成员变量即可
 *               用于realm登录使用
 * @Date: 2020/06/21
 */
public class JwtToken implements AuthenticationToken {

	private String token;

	public JwtToken(String token) {
		this.token = token;
	}

	@Override
	public Object getPrincipal() {
		return token;
	}

	@Override
	public Object getCredentials() {
		return token;
	}
}
```

**实现Realm**

`realm` 的用于处理用户是否合法的这一块，需要我们自己实现。

```java
/**
 * @Auther: Darryl
 * @Description: realm 的用于处理用户是否合法的这一块，需要我们自己实现。
 * @Date: 2020/06/21
 */
@Service
public class DarrylRealm extends AuthorizingRealm {

	private UserService userService;

	// set 注入方法
	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	// 必须重写此方法，要将我们自定义的token满足支持
	@Override
	public boolean supports(AuthenticationToken token) {
		return token instanceof JwtToken;
	}

	// 只有当需要检测用户权限的时候才会调用此方法，例如校验角色，校验权限等
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
		String username = JwtUtil.getUserName(principalCollection.toString());
		UserBean user = userService.getUser(username);
		SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
		simpleAuthorizationInfo.addRole(user.getRole());
		Set<String> permission = new HashSet<>(Arrays.asList(user.getPermission().split(",")));
		simpleAuthorizationInfo.addStringPermissions(permission);
		return simpleAuthorizationInfo;
	}

	// 使用此方法进行用户名正确与否验证，错误抛出异常即可。
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
		String token = (String) authenticationToken.getCredentials();
		String userName = JwtUtil.getUserName(token);
		// token非法
		if (StringUtils.isEmpty(userName)) {
			throw new AuthenticationException("token invalid");
		}
		UserBean user = userService.getUser(userName);
		// 没有该用户
		if (user == null) {
			throw new AuthenticationException("User didn't existed!");
		}
		// 用户名和密码校验不通过
		if (!JwtUtil.verify(token, userName, user.getPassword())) {
			throw new AuthenticationException("Username or password error");
		}
		return new SimpleAuthenticationInfo(token, token, "darryl_realm");
	}
}
```

在 `doGetAuthenticationInfo()` 中用户可以自定义抛出很多异常，详情见文档。

***重写 Filter***

所有的请求都会先经过 `Filter`，所以我们继承官方的 `BasicHttpAuthenticationFilter` ，并且重写鉴权的方法。

```java
/**
 * @Auther: Darryl
 * @Description: jwt filter
 * @Date: 2020/06/21
 */
public class JwtFilter extends BasicHttpAuthenticationFilter {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	// 检测header里面是否包含Authorization字段，即请求中是否带有token
	@Override
	protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		String authorization = httpServletRequest.getHeader("Authorization");
		return authorization != null;
	}

	// 执行登入
	@Override
	protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		String authorization = httpServletRequest.getHeader("Authorization");
		JwtToken token = new JwtToken(authorization);
		// 交给realm进行登入
		getSubject(request, response).login(token);
		// 如果没有出错，就直接返回true
		return true;
	}

	/**
	 * 这里始终返回true，说明都是允许访问
	 * 例如我们提供一个地址 GET /article，登入用户和游客看到的内容是不同的，如果在这里返回了false，请求会被直接拦截，用户看不到任何东西
	 * 所以我们在这里返回true，Controller中可以通过 subject.isAuthenticated() 来判断用户是否登入，
	 * 如果有些资源只有登入用户才能访问，我们只需要在方法上面加上 @RequiresAuthentication 注解即可
	 * @param request 请求
	 * @param response 响应
	 * @param mappedValue
	 * @return
	 */
	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
		if (isLoginAttempt(request, response)) {
			try {
				executeLogin(request, response);
			} catch (Exception e) {
				response401(request, response);
			}
		}
		return true;
	}

	/**
	 * 对跨域提供支持
	 */
	@Override
	protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
		httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
		httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
		// 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
		if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
			httpServletResponse.setStatus(HttpStatus.OK.value());
			return false;
		}
		return super.preHandle(request, response);
	}

	/**
	 * 将非法请求跳转到 /401
	 */
	private void response401(ServletRequest req, ServletResponse resp) {
		try {
			HttpServletResponse httpServletResponse = (HttpServletResponse) resp;
			httpServletResponse.sendRedirect("/401");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
}
```

`getSubject(request, response).login(token);` 这一步就是提交给了 `realm` 进行处理。

**配置Shiro**

```java
/**
 * @Auther: Darryl
 * @Description: shiro config class
 * @Date: 2020/06/21
 */
@Configuration
public class ShiroConfig {

	// 构建security manager bean
	@Bean("securityManager")
	public DefaultWebSecurityManager getManager(DarrylRealm darrylRealm) {
		DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
		// 注入我们自定义的realm
		manager.setRealm(darrylRealm);
		// 关闭shiro自带的session
		DefaultSubjectDAO dao = new DefaultSubjectDAO();
		DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
		defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
		dao.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
		manager.setSubjectDAO(dao);
		return manager;
	}

	@Bean("shiroFilter")
	public ShiroFilterFactoryBean factory(DefaultWebSecurityManager manager) {
		ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
		// 注入我们自定一的filter
		Map<String, Filter> filterMap = new HashMap<>();
		filterMap.put("jwt", new JwtFilter());
		factoryBean.setFilters(filterMap);
		// 注入security manager
		factoryBean.setSecurityManager(manager);
		// 注入未授权URI
		factoryBean.setUnauthorizedUrl("/401");
		// 自定义路由规则，所有的请求都过我们自定义的filter；/401的请求不过我们自定义的filter
		Map<String, String> filterRuleMap = new HashMap<>();
		filterRuleMap.put("/**", "jwt");
		filterRuleMap.put("/401", "anon");
		factoryBean.setFilterChainDefinitionMap(filterRuleMap);
		return factoryBean;
	}

	/**
	 * 下面的代码是添加注解支持
	 */
	@Bean
	@DependsOn("lifecycleBeanPostProcessor")
	public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
		DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
		// 强制使用cglib，防止重复代理和可能引起代理出错的问题
		// https://zhuanlan.zhihu.com/p/29161098
		defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
		return defaultAdvisorAutoProxyCreator;
	}

	@Bean
	public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}

	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
		AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
		advisor.setSecurityManager(securityManager);
		return advisor;
	}
}
```

里面 URL 规则自己参考文档即可 http://shiro.apache.org/web.html 。

## 总结

学习 springboot 集成 shiro 进行权限控制，中间使用到了JWT的思想，能更好的做权限的控制，前后端分离，单点登录，解决跨域问题等。
