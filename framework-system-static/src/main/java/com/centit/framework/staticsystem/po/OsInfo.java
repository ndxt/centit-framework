package com.centit.framework.staticsystem.po;

import com.centit.framework.model.basedata.IOsInfo;
import lombok.Data;

import java.util.Date;

@Data
public class OsInfo implements IOsInfo, java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private String osId;
    private String osName;
    private String osUrl;
    private String osType;
    private String topUnit;
    private String osHomePage;
    private String oauthUser;
    private String oauthPassword;
    private String relOptId;
    private String  pageFlow;
    private Boolean isDelete;
    private String  picId;
    private Date createTime;
    private Date lastModifyDate;
    private String created;
    private String logoFileId;
    private String osDesc;

    public OsInfo(String osId, String osName) {
        this.osId = osId;
        this.osName = osName;
    }

}
