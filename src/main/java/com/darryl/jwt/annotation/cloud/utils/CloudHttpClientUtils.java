package com.darryl.jwt.annotation.cloud.utils;

import com.alibaba.fastjson.JSON;
import com.darryl.jwt.annotation.cloud.enums.Protocol;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.info.InfoProperties;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.swing.*;
import java.net.URI;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Auther: Darryl
 * @Description: 封装http请求
 * @Date: 2020/09/06
 */
public class CloudHttpClientUtils {

	private static Logger log = LoggerFactory.getLogger(CloudHttpClientUtils.class);
	private static final Integer CONNECT_TIME_OUT = 20 * 1000;
	private static final Integer READ_TIME_OUT = 4000 * 1000;
	private static final Map<Protocol, RestTemplate> httpClients = Maps.newHashMap();
	private static final Joiner.MapJoiner KV_JOINER = Joiner.on("&").withKeyValueSeparator("=");

	static {
		httpClients.put(Protocol.HTTP, buildHttpRestTemplate());
		httpClients.put(Protocol.HTTPS, buildHttpsRestTemplate());
	}

	private static RestTemplate buildHttpsRestTemplate() {
		TrustStrategy trustStrategy = (X509Certificate[] chain, String authType) -> true;
		SSLContext sslContext = null;
		try {
			sslContext = SSLContexts.custom().loadTrustMaterial(null, trustStrategy)
					.build();
		} catch (Exception e) {
			log.error("httpClient init fail, exception: ", e);
		}
		SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(sslContext);
		CloseableHttpClient httpClient = HttpClients.custom()
				.setSSLSocketFactory(factory)
				.build();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(httpClient);
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		return restTemplate;
	}

	public static RestTemplate buildHttpRestTemplate() {
		PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
		manager.setMaxTotal(300);
		manager.setDefaultMaxPerRoute(100);
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		httpClientBuilder.setConnectionManager(manager);
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setHttpClient(httpClientBuilder.build());
		factory.setConnectTimeout(CONNECT_TIME_OUT);
		factory.setReadTimeout(READ_TIME_OUT);
		RestTemplate restTemplate = new RestTemplate(factory);
		return restTemplate;
	}

	public static String get(Protocol protocol, String url, Map<String, Object> params) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Accept", MediaType.APPLICATION_JSON.toString());
		HttpEntity<String> formEntity = new HttpEntity<>(null, headers);
		try {
			URL newUrl = new URL(url);
			URI uri = new URI(newUrl.getProtocol(), newUrl.getAuthority(), newUrl.getPath(), getUrlParam(params), null);
			log.info("request info : {}, {}, {}", url, params, JSON.toJSONString(uri));
			ResponseEntity<String> responseEntity = httpClients.get(protocol).exchange(uri, HttpMethod.GET,
					formEntity, String.class);
			return responseEntity.getBody();
		} catch (Exception e) {
			log.error("http get error, url:{}, params:{}", url, params, e);
			throw new RuntimeException(e);
		}
	}

	public static String post(Protocol protocol, String url, Map<String, Object> params,
	                          String content, String contenType) {
		HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = MediaType.parseMediaType(contenType);
		headers.setContentType(mediaType);
		headers.add("Accept", MediaType.APPLICATION_JSON.toString());
		HttpEntity<String> formEntity = new HttpEntity<>(content, headers);
		try {
			URL newUrl = new URL(addUrlParam(url, params));
			URI uri = new URI(newUrl.getProtocol(), newUrl.getHost() + ":" + newUrl.getPort(),
					newUrl.getPath(), newUrl.getQuery(), null);
			log.info("params is {}", JSON.toJSONString(formEntity));
			String res = httpClients.get(protocol).postForObject(uri, formEntity,
					String.class);
			return res;
		} catch (Exception e) {
			log.error("http get error, url:{}, params:{}", url, params, e);
			throw new RuntimeException(e);
		}
	}

	private static String getUrlParam(Map<String, Object> params) {
		if (CollectionUtils.isEmpty(params)) {
			return "";
		}
		params =
				params.entrySet().stream().filter(entry -> !Objects.isNull(entry.getValue()))
						.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		return KV_JOINER.join(params);
	}

	private static String addUrlParam(String url, Map<String, Object> params) {
		if (CollectionUtils.isEmpty(params)) {
			return url;
		}
		if (!url.contains("?")) {
			url = url + "?";
		}
		if (!url.endsWith("?")) {
			url = url + "&";
		}
		params = params.entrySet().stream().filter(entry -> !Objects.isNull(entry.getValue()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		log.warn("request url is {}", url + KV_JOINER.join(params));
		return url + KV_JOINER.join(params);
	}
}
