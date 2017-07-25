package com.centit.framework.appclient;

import java.io.IOException;

import org.apache.http.impl.client.CloseableHttpClient;

import com.centit.framework.core.common.ResponseJSON;
import com.centit.support.network.HttpExecutor;

public class RestfulHttpRequest {

	public static ResponseJSON getResponseData(String httpGetUrl) {        
		CloseableHttpClient httpclient = HttpExecutor.createHttpClient();
		ResponseJSON resJson = getResponseData(httpclient,httpGetUrl);
		try {
			httpclient.close();
		} catch (IOException e) {
		}
		return resJson;
    }
	
	public static ResponseJSON getResponseData(CloseableHttpClient httpclient,String httpGetUrl) {        
		ResponseJSON resJson=null;
		try {
			resJson = ResponseJSON.valueOfJson(
					HttpExecutor.simpleGet(httpclient, httpGetUrl, "") );
		} catch (IOException e) {
		}
        return resJson;
    }
}
