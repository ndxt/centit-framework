package com.centit.framework.staticsystem.security;

import com.centit.framework.security.model.CentitUserDetails;
import com.centit.support.algorithm.DatetimeOpt;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserAccessTokenMetadata{

    public static final int tokenLifetime =  120;//minute

    public static class TokenObject{
        private Date createTime;
        private CentitUserDetails tokenData;

        public TokenObject(){

        }

        public TokenObject(CentitUserDetails tokenData){
            this.createTime = DatetimeOpt.currentUtilDate();
            this.tokenData = tokenData;
        }

        public TokenObject(Date createTime,CentitUserDetails tokenData){
            this.createTime = createTime;
            this.tokenData = tokenData;
        }

        public boolean checkAlive(){
            boolean alive= DatetimeOpt.currentUtilDate().before(
                    DatetimeOpt.addMinutes(this.createTime, tokenLifetime));
            if(alive)
                this.createTime = DatetimeOpt.currentUtilDate();
            return alive;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }

        public CentitUserDetails getUserDetails() {
            return tokenData;
        }

        public void setUserDetails(CentitUserDetails tokenData) {
            this.tokenData = tokenData;
        }
    }

    protected static final Map<String,TokenObject> accessTokens = new ConcurrentHashMap<String,TokenObject>();
    protected static final Map<String,String> userTokens = new ConcurrentHashMap<String,String>();

    public static void addToken(String token,CentitUserDetails data){
        String userChannel =  data.getUserCode();

        String oldToken = userTokens.get(userChannel);
        if(StringUtils.isNotBlank(oldToken))
            accessTokens.remove(oldToken);

        userTokens.put(userChannel, token);
        accessTokens.put(token, new TokenObject(data));
    }


    public static CentitUserDetails getTokenUserDetails(String token){
        TokenObject tokenData = accessTokens.get(token);

        if(tokenData == null)
            return null;
        if(! tokenData.checkAlive()){
            accessTokens.remove(token);
            return null;
        }
        return tokenData.getUserDetails();
    }

    public static void clearExpiredTokenData(String token){
        List<String> expiredToken = new ArrayList<String>();
        for(Map.Entry<String,TokenObject> ent : accessTokens.entrySet()){
            if(!ent.getValue().checkAlive())
                expiredToken.add(ent.getKey());
        }

        for(String key:expiredToken)
            accessTokens.remove(key);
    }

}
