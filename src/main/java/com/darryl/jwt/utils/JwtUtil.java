package com.darryl.jwt.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.io.UnsupportedEncodingException;
import java.util.Date;

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
