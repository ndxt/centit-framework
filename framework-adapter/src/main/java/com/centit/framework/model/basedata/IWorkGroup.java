package com.centit.framework.model.basedata;

import java.math.BigDecimal;
import java.util.Date;

public interface IWorkGroup {

    String getRoleCode();

    String getGroupId();

    String getUserCode();

    String getIsValid();

    Date getAuthTime();

    String getCreator();

    String getUpdator();

    Date getUpdateDate();

    BigDecimal getUserOrder();

    String getRunToken();

    String getAuthDesc();


}
