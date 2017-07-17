package com.centit.framework.security;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;

import com.centit.framework.common.SysParametersUtils;
import com.centit.support.file.PropertiesReader;

//@Component("hostIpSecurityMetadataSource")
public class HostIpSecurityMetadataSource
            implements FilterInvocationSecurityMetadataSource
         {
	public static final String LOCAL_HOST_IP="127.0.0.1";
    //private static final Logger logger = LoggerFactory.getLogger(DaoInvocationSecurityMetadataSource.class);
    //private static boolean logDebug = logger.isDebugEnabled();
    private Map<String,Collection<ConfigAttribute>> hostIpAttributes=null;
    @Override
    public boolean supports(Class<?> clazz) {
        if (FilterInvocation.class.isAssignableFrom(clazz)) {
            return true;
        }
        return false;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    public Collection<ConfigAttribute> getHostConfigAttributes(String urlIp) {
        if(hostIpAttributes==null){
            //放到数据字典的httpserver的字典中
            loadConfigAttributes();
        }
        return hostIpAttributes.get(urlIp);
    }
    
    private void loadConfigAttributes(){
        if(hostIpAttributes!=null)
            hostIpAttributes.clear();
        else
            hostIpAttributes = new HashMap<String,Collection<ConfigAttribute>>();
        
        Properties hosts = PropertiesReader.getFilePathProperties(SysParametersUtils.getConfigHome()+"/host_white_list.properties");
        if(hosts!=null){
            Set<Map.Entry<Object,Object>> hostset = hosts.entrySet();
            if(hostset!=null){                
                for(Map.Entry<Object,Object> dd : hostset){
                    Set<ConfigAttribute> httpServerRole = new HashSet<ConfigAttribute>();
                    httpServerRole.add(new SecurityConfig(dd.getValue().toString()));
                    hostIpAttributes.put(dd.getKey().toString(), httpServerRole);
                }
            }
        }
        /*
          List<DataDictionary> httpServers = CodeRepositoryUtil.getDictionary("httpServer");
          if(httpServers!=null && httpServers.size()>0){
            for(DataDictionary dd : httpServers){
                Set<ConfigAttribute> httpServerRole = new HashSet<ConfigAttribute>();
                httpServerRole.add(new SecurityConfig(dd.getDataValue()));
                hostIpAttributes = new HashMap<String,Collection<ConfigAttribute>>();
                hostIpAttributes.put(dd.getDataCode(), httpServerRole);
            }
        }else*/
        if(hostIpAttributes.size()<1){
            Set<ConfigAttribute> httpServerRole = new HashSet<ConfigAttribute>();
            httpServerRole.add(new SecurityConfig("localHost"));
            hostIpAttributes.put(LOCAL_HOST_IP, httpServerRole);
        }
    }
    @Override
    // According to a URL, Find out permission configuration of this URL.
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        // guess object is a URL.
        if ((object == null) || !this.supports(object.getClass())) {
            throw new IllegalArgumentException("对不起,目标对象不是类型");
        }
        
        if(hostIpAttributes==null){
            //放到数据字典的httpserver的字典中
            loadConfigAttributes();
        }
        
        FilterInvocation fi = (FilterInvocation) object;
        HttpServletRequest request = fi.getHttpRequest();
        String urlIp = request.getRemoteHost();
        return hostIpAttributes.get(urlIp);
    }
}