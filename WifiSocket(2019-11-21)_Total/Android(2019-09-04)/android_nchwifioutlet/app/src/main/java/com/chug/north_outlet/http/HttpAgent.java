package com.chug.north_outlet.http;

import android.app.Activity;
import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.chug.north_outlet.App;
import com.chug.north_outlet.utils.XlinkUtils;


import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;


/**
 * 登录注册接口调用
 * 
 * @author Liuxy
 * @2015年6月24日下午5:03:29 </br>
 * @explain
 */
@SuppressWarnings("deprecation")
public class HttpAgent {
	private final String url = "https://app.xlink.cn";
	private static HttpAgent instance;
	// url
	public final String registerUrl = url + "/v1/user/register";
	public final String loginUrl = url + "/v1/user/login";
	public final String resetUrl = url + "/v1/user/reset";
	// 3个签名头部
	private final static String AccessID = "X-AccessId";
	private final static String X_ContentMD5 = "X-ContentMD5";
	private final static String X_Sign = "X-Sign";
	// /**
	// * xlink 企业后台管理 注册的 资源密钥
	// */
	// public static String SECRET_KEY = "d77e9c6aae024cfcb585fd834f3addf3";
	// /**
	// * 资源id
	// */
	// public static String ACCESS_ID = "a45c1f1a861348738b0538d989657772";
	/**
	 * xlink 企业后台管理 注册的 资源密钥
	 */
//	public static String SECRET_KEY = "b0b458e1650b442cb2690a8e2df6d836";
	public static String SECRET_KEY = "4b5d7675883440fa9c1fdb8264659ad2";
	/**
	 * 资源id
	 */
//	public static String ACCESS_ID = "0b02e52a735e42d5b8011ab92911d90c";
	public static String ACCESS_ID = "46a174b0fbe240e9b86d197c9a48fd34";

	/**
	 * md5算法
	 * 
	 * @param s
	 * @return
	 */
	public final static String MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = s.getBytes();
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static HttpAgent getInstance() {
		if (instance == null) {
			instance = new HttpAgent();
		}
		return instance;
	}

	/**
	 * 全局的http代理
	 */
	private static AsyncHttpClient client;

	private HttpAgent() {
		client = new AsyncHttpClient();
		client.setTimeout(5000);
	}

	/**
	 * 通过SECRET_KEY 和内容md5 进行加密签名
	 * 
	 * @param contentmd5
	 * @return
	 */
	private XHeader getSign(String contentmd5) {
		String singnMd5 = MD5(SECRET_KEY + contentmd5);
		XHeader header = new XHeader(X_Sign, singnMd5, null);
		return header;
	}


	/**
	 * 获取appid接口（登录接口）
	 * 
	 * @param user
	 * @param pwd
	 * @param handler
	 */
	public void getAppId(String user, String pwd, TextHttpResponseHandler handler) {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", user);
		map.put("pwd", pwd);
		JSONObject data = XlinkUtils.getJsonObject(map);
		// 请求entity
		StringEntity entity = null;
		entity = new StringEntity(data.toString(), "UTF-8");
		// 3个http 请求头部
		Header[] headers = new Header[3];
		// AccessID
		headers[0] = new XHeader(AccessID, ACCESS_ID, null);
		// entity md5签名后
		String contentMD5 = MD5(data.toString());
		// 内容md5验证
		headers[1] = new XHeader(X_ContentMD5, contentMD5, null);
		// 内容加SECRET_KEY 签名认证
		headers[2] = getSign(contentMD5);
		post(loginUrl, headers, entity, handler);
	}

	/**
	 * 
	 * @param url
	 *            url地址
	 * @param headers
	 *            http请求头部
	 * @param entity
	 *            http 实体
	 * @param handler
	 *            回调
	 */
	public void post( String url, Header[] headers, HttpEntity entity, AsyncHttpResponseHandler handler) {

		client.post(App.getAppContext(), url, headers, entity, "text/html", handler);
	}

}
