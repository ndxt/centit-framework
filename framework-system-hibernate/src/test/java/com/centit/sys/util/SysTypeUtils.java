package com.centit.sys.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.centit.framework.system.po.UserInfo;

public class SysTypeUtils {
	
    public static final int sysType  = 1;
    
    public static void main(String[] args) {
		//JSONObject jsonObject = new JSONObject();
		
		
		List<UserInfo> userInfos = new ArrayList<UserInfo>();
		
		for (int i = 0; i < 10; i++) {
			UserInfo u = new UserInfo();
			
			u.setUserCode("usercode_" + i);
			u.setUserName("username_" + i);
			
			userInfos.add(u);
		}
		
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("errorcode", 1);
		params.put("userinfos", userInfos);
		
		SimplePropertyPreFilter p = new SimplePropertyPreFilter(UserInfo.class);
		p.getExcludes().add("userName");
		
		
		String text = JSONObject.toJSONString(params, p);
		
		System.out.println(text);
		
	}
}

