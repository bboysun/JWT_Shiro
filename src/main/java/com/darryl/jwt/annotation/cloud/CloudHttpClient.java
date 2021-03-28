package com.darryl.jwt.annotation.cloud;

import com.darryl.jwt.annotation.cloud.enums.Protocol;
import com.darryl.jwt.annotation.cloud.enums.SerializeMethod;
import com.darryl.jwt.annotation.cloud.utils.CloudHttpClientUtils;
import com.darryl.jwt.annotation.cloud.serializers.CloudSerializer;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.*;

/**
 * @Auther: Darryl
 * @Description: http client wrapper
 * @Date: 2020/09/05
 */
public class CloudHttpClient {

	private static Logger log = LoggerFactory.getLogger(CloudHttpClient.class);

	private static Map<SerializeMethod, CloudSerializer> serializers;

	private static final Map<RequestMethod, HttpCloud> httpCloudMap = Maps.newHashMap();

	static {
		httpCloudMap.put(RequestMethod.GET, new HttpCloud() {
			@Override
			public Object doExecute(Protocol protocol, CloudMethod method, String url, Object... args) {
				// args数组不要直接引用角标，使用list操作
				List<Object> argsList = Lists.newArrayList();
				argsList.addAll(Arrays.asList(args));
				// 构建请求参数
				url = buildPathParam(url, method.getPathParameters(), argsList);
				Map<String, Object> keyValueParams = buildParam(method.getParameters(), argsList);
				String response = CloudHttpClientUtils.get(protocol, url, keyValueParams);
				if (String.class.equals(method.getReturnType())) {
					return response;
				}
				CloudSerializer cloudResponseSerializer = serializers.get(method.getResponseSerializeMethod());
				return cloudResponseSerializer.desrialize(method.getReturnType(), response);
			}
		});
	}

	static String buildPostParam(CloudSerializer requestSerializer, Object... args) {
		// POST 参数只能放在最后一个
		if (requestSerializer == null) {
			return null;
		}
		return requestSerializer.serialize(args[args.length - 1]);
	}

	static Map<String, Object> buildParam(List<String> paramNames, List<Object> argsList) {
		if (CollectionUtils.isEmpty(argsList)) {
			return new HashMap<>();
		}
		Map<String, Object> resultParams = Maps.newHashMap();
		for (int i=0; i!=paramNames.size(); ++i) {
			resultParams.put(paramNames.get(i), argsList.get(i));
		}
		return resultParams;
	}

	static String buildPathParam(String url, Map<String, Integer> pathParamNames, List<Object> argsList) {
		if (url.contains(CloudProxy.PLACEHOLDER_PREFIX) && pathParamNames != null) {
			List<Integer> removeIndexList = Lists.newArrayList();
			for (Map.Entry<String, Integer> entry : pathParamNames.entrySet()) {
				String pathParamName = entry.getKey();
				Integer argIndex = entry.getValue();
				if (argsList.size() > argIndex) {
					String pathParamValue = String.valueOf(argsList.get(argIndex));
					url = url.replace(CloudProxy.PLACEHOLDER_PREFIX + pathParamName + CloudProxy.PLACEHOLDER_SUFFIX,
							pathParamValue);
					removeIndexList.add(argIndex);
				}
			}
			// 删除args中cloudPathParam的取值，避免影响CloudParam参数的赋值
			if (removeIndexList.size() > 0) {
				Collections.sort(removeIndexList);
				// 降序
				Collections.reverse(removeIndexList);
				for (int removeIndex : removeIndexList) {
					argsList.remove(removeIndex);
				}
			}
		}
		return url;
	}

	interface HttpCloud {
		Object doExecute(Protocol protocol, CloudMethod method, String url, Object... args);
	}
}
