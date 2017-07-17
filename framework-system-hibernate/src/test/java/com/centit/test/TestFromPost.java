package com.centit.test;

import com.alibaba.fastjson.JSON;
import com.centit.framework.common.ValidatorUtils;
import com.centit.framework.system.po.DataCatalog;
import com.centit.framework.system.po.RoleInfo;
import com.centit.framework.system.po.UserInfo;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.network.HttpExecutor;
import org.apache.http.NameValuePair;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Lookup;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestFromPost {
	protected static Logger logger = LoggerFactory.getLogger(TestFromPost.class);
    public static void main(String[] args) {
    	testValidator();
    }

    public static void testValidator(){
    	DataCatalog catalog = new DataCatalog();
    	catalog.setCatalogName("hello");
    	 Map<String,String> errMsg =
    			ValidatorUtils.validatorEntityPo(catalog);
    	System.out.println(JSON.toJSONString(errMsg));
    }
    public static void testJson(){

		try {
			UserInfo ui= new UserInfo();
			ui.setUserCode("test");
			ui.setUserName("hello");
			ui.setLoginName("guest");
			CloseableHttpClient httpClient = HttpClients.createDefault();
	    	 String s = HttpExecutor.jsonPost(httpClient,
			         "http://codefanbook:8180/framework-sys-module/service/testJson",JSON.parseObject(
			        		 JSON.toJSONString(ui)) );
			 System.out.println(s);
		}  catch (IOException e) {
			e.printStackTrace();
		}

    }

	public static void testSession(){
        try {

        	Lookup<CookieSpecProvider> cookieSpecRegistry  = RegistryBuilder.<CookieSpecProvider>create()
                    //.register(CookieSpecs.BEST_MATCH, new BestMatchSpecFactory())
                   // .register(CookieSpecs.STANDARD, new RFC2965SpecFactory())
                    //.register(CookieSpecs.BROWSER_COMPATIBILITY, new BrowserCompatSpecFactory())
                    //.register(CookieSpecs.NETSCAPE, new NetscapeDraftSpecFactory())
                    //.register(CookieSpecs.IGNORE_COOKIES, new IgnoreSpecFactory())
                    //.register("rfc2109", new RFC2109SpecFactory())
                    //.register("rfc2965", new RFC2965SpecFactory())
                    .build();


        	CloseableHttpClient httpClient = HttpClients.custom().
        			setDefaultCookieSpecRegistry(cookieSpecRegistry).build(); //createDefault();


           String s = HttpExecutor.simpleGet(httpClient, null,
                    "http://codefanbook:8180/TestSession/TestSession",(String)null);

            System.out.println(s);

            s = HttpExecutor.simpleGet(httpClient, null,
                    "http://codefanbook:8180/TestSession/TestSession",(String)null);

            System.out.println(s);

            s = HttpExecutor.simpleGet(httpClient, null,
                    "http://codefanbook:8180/TestSession/TestSession",(String)null);

            System.out.println(s);

        } catch (Exception e) {
        	logger.error(e.getMessage(),e);//e.printStackTrace();
        }

	}

    public static void testLogin(){


        //CloseableHttpClient client2 = HttpClients.createDefault();
    	HttpClientContext context = HttpClientContext.create();
        BasicCookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();

        try {

            Map<String,String> params = new HashMap<String,String>();
            params.put("username", "admin");
            params.put("password", "000000");
            params.put("remember", "true");

            String s = HttpExecutor.formPost(httpClient,context,
                    "http://codefanbook:8180/framework-sys-module/j_spring_security_check?ajax=true",
                    params,true);
            System.out.println(s);

            context.setCookieStore(cookieStore);
            List<Cookie> list = cookieStore.getCookies();
            if(list.isEmpty())
            {
                System.out.println("不存在");
            }
            else
            {
                for (int i = 0; i < list.size(); i++) {
                    System.out.println("-"+list.get(i).toString());
                }
            }

            //ResponseJSON rj = ResponseJSON.valueOfJson(s);
            //String jsessionid = rj.getDataAsScalarObject(String.class);
            //context.setAttribute("JSESSIONID", jsessionid);

            s = HttpExecutor.simpleGet(httpClient,context,
                    "http://codefanbook:8180/framework-sys-module/system/currentuser",(String)null);

            System.out.println(s);

            s = HttpExecutor.simpleGet(httpClient,context,
                    "http://codefanbook:8180/framework-sys-module/system/currentuser",(String)null);

            System.out.println(s);

           /* params.put("j_password", "111111");
            s = HttpExecutor.formPost(client2, null,
                    "http://codefanbook:8180/framework-sys-module/j_spring_security_check?ajax=true",
                    params,true);
            System.out.println(s);

            s = HttpExecutor.simpleGet(client2, null,
                    "http://codefanbook:8180/framework-sys-module/system/currentuser",null);
            System.out.println(s);*/
        } catch (Exception e) {
        	logger.error(e.getMessage(),e);//e.printStackTrace();
        }

    }

    public static void testFormParams2(){
        CloseableHttpClient client = HttpClients.createDefault();

        try {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add( new BasicNameValuePair("optDates",
                    DatetimeOpt.convertDatetimeToString(DatetimeOpt.currentUtilDate())));
            params.add( new BasicNameValuePair("optDates",
                    DatetimeOpt.convertDatetimeToString(DatetimeOpt.addDays( DatetimeOpt.currentUtilDate(),5))));
            params.add( new BasicNameValuePair("optDates",
                    DatetimeOpt.convertDatetimeToString(DatetimeOpt.addDays( DatetimeOpt.currentUtilDate(),10))));

            String s = HttpExecutor.formPost(client, null,
                    "https://codefanbook:8543/centit/system/roleinfo/testRole", params,true);

            System.out.println(s);
        } catch (Exception e) {
        	logger.error(e.getMessage(),e);//e.printStackTrace();
        }

    }

    public static void testFormParams(){
        RoleInfo roleinfo = new RoleInfo();
        
        roleinfo.setIsValid("T");
        roleinfo.setRoleCode("testRole");
        roleinfo.setCreateDate(DatetimeOpt.currentUtilDate());
        roleinfo.setRoleDesc("测试表单提交");
        roleinfo.setRoleName("RoleName");
        Map<String,Object> extDatas = new HashMap<String,Object>();
        extDatas.put("roleInfo", roleinfo);
        extDatas.put("optCodes", new String[]{"hello","world!"});
        extDatas.put("optDates", new String[]{
                DatetimeOpt.convertDatetimeToString(DatetimeOpt.currentUtilDate()),
                DatetimeOpt.convertDatetimeToString(DatetimeOpt.addDays( DatetimeOpt.currentUtilDate(),5)),
                DatetimeOpt.convertDatetimeToString(DatetimeOpt.addDays( DatetimeOpt.currentUtilDate(),15))});
        extDatas.put("optLongs", new Long[]{1l,2l,3l,4l});
        try {
            HttpExecutor.multiFormPost(HttpClients.createDefault(), null, 
                    "http://codefanbook:8180/centit/system/roleinfo/testRole", roleinfo,extDatas,true);
        } catch (Exception e) {
        	logger.error(e.getMessage(),e);// e.printStackTrace();
        }

    }

}
