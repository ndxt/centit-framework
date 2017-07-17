package com.centit.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


@SuppressWarnings("deprecation")
public class TestHttpClient {

	// 创建CookieStore实例
	static CookieStore cookieStore = null;
	static HttpClientContext context = null;
	String loginUrl = "http://codefanbook:8180/framework-sys-module/j_spring_security_check?ajax=true";
	String testUrl = "http://codefanbook:8180/framework-sys-module/system/currentuser";
	String loginErrorUrl = "http://codefanbook:8180/framework-sys-module/system/currentuser";


	public void nottestLogin() throws Exception {
		System.out.println("----testLogin");

		// // 创建HttpClientBuilder
		// HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		// // HttpClient
		// CloseableHttpClient client = httpClientBuilder.build();
		// 直接创建client
		CloseableHttpClient client = HttpClients.createDefault();

		HttpPost httpPost = new HttpPost(loginUrl);
		Map<String,String> parameterMap = new HashMap<String,String>();
		parameterMap.put("username", "admin");
		parameterMap.put("password", "000000");
		UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(
				getParam(parameterMap), "UTF-8");
		httpPost.setEntity(postEntity);
		System.out.println("request line:" + httpPost.getRequestLine());
		try {
			// 执行post请求
			HttpResponse httpResponse = client.execute(httpPost);
			String location = httpResponse.getFirstHeader("Location")
					.getValue();
			if (location != null && location.startsWith(loginErrorUrl)) {
				System.out.println("----loginError");
			}
			printResponse(httpResponse);

			// 执行get请求
			System.out.println("----the same client");
			HttpGet httpGet = new HttpGet(testUrl);
			System.out.println("request line:" + httpGet.getRequestLine());
			HttpResponse httpResponse1 = client.execute(httpGet);
			printResponse(httpResponse1);

			// cookie store
			setCookieStore(httpResponse);
			// context
			setContext();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				// 关闭流并释放资源
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	public void notTestContext() throws Exception {
		System.out.println("----testContext");
		// 使用context方式
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(testUrl);
		System.out.println("request line:" + httpGet.getRequestLine());
		try {
			// 执行get请求
			HttpResponse httpResponse = client.execute(httpGet, context);
			System.out.println("context cookies:"
					+ context.getCookieStore().getCookies());
			printResponse(httpResponse);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				// 关闭流并释放资源
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	public void notTestCookieStore() throws Exception {
		System.out.println("----testCookieStore");
		// 使用cookieStore方式
		CloseableHttpClient client = HttpClients.custom()
				.setDefaultCookieStore(cookieStore).build();
		HttpGet httpGet = new HttpGet(testUrl);
		System.out.println("request line:" + httpGet.getRequestLine());
		try {
			// 执行get请求
			HttpResponse httpResponse = client.execute(httpGet);
			System.out.println("cookie store:" + cookieStore.getCookies());
			printResponse(httpResponse);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				// 关闭流并释放资源
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void printResponse(HttpResponse httpResponse)
			throws ParseException, IOException {
		// 获取响应消息实体
		HttpEntity entity = httpResponse.getEntity();
		// 响应状态
		System.out.println("status:" + httpResponse.getStatusLine());
		System.out.println("headers:");
		HeaderIterator iterator = httpResponse.headerIterator();
		while (iterator.hasNext()) {
			System.out.println("\t" + iterator.next());
		}
		// 判断响应实体是否为空
		if (entity != null) {
			String responseString = EntityUtils.toString(entity);
			System.out.println("response length:" + responseString.length());
			System.out.println("response content:"
					+ responseString.replace("\r\n", ""));
		}
	}

	public static void setContext() {
		System.out.println("----setContext");
		context = HttpClientContext.create();
		Registry<CookieSpecProvider> registry = RegistryBuilder
				.<CookieSpecProvider> create()
				.register(CookieSpecs.BEST_MATCH, new BestMatchSpecFactory())
				.register(CookieSpecs.BROWSER_COMPATIBILITY,
						new BrowserCompatSpecFactory()).build();
		context.setCookieSpecRegistry(registry);
		context.setCookieStore(cookieStore);
	}

	public static void setCookieStore(HttpResponse httpResponse) {
		System.out.println("----setCookieStore");
		cookieStore = new BasicCookieStore();
		// JSESSIONID
		String setCookie = httpResponse.getFirstHeader("Set-Cookie").getValue();
		String JSESSIONID = setCookie.substring("JSESSIONID=".length(),
				setCookie.indexOf(";"));
		System.out.println("JSESSIONID:" + JSESSIONID);
		// 新建一个Cookie
		BasicClientCookie cookie = new BasicClientCookie("JSESSIONID",
				JSESSIONID);
		cookie.setVersion(0);
		cookie.setDomain("127.0.0.1");
		cookie.setPath("/CwlProClient");
		// cookie.setAttribute(ClientCookie.VERSION_ATTR, "0");
		// cookie.setAttribute(ClientCookie.DOMAIN_ATTR, "127.0.0.1");
		// cookie.setAttribute(ClientCookie.PORT_ATTR, "8080");
		// cookie.setAttribute(ClientCookie.PATH_ATTR, "/CwlProWeb");
		cookieStore.addCookie(cookie);
	}

	public static List<NameValuePair> getParam(Map<String,String> parameterMap) {
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		Iterator<Entry<String,String> > it = parameterMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String,String> parmEntry = it.next();
			param.add(new BasicNameValuePair(  parmEntry.getKey(),
					  parmEntry.getValue()));
		}
		return param;
	}
}
