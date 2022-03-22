package com.centit.framework.security.model;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CentitSecurityConfig implements ConfigAttribute, Serializable {
    private final String attrib;

    // ~ Constructors
    // ===================================================================================================
    public CentitSecurityConfig(){
        attrib="";
    }
    public CentitSecurityConfig(String config) {
        this.attrib = config;
    }

    // ~ Methods
    // ========================================================================================================

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ConfigAttribute) {
            ConfigAttribute attr = (ConfigAttribute) obj;

            return this.attrib.equals(attr.getAttribute());
        }

        return false;
    }

    @Override
    public String getAttribute() {
        return this.attrib;
    }

    @Override
    public int hashCode() {
        return this.attrib.hashCode();
    }

    @Override
    public String toString() {
        return this.attrib;
    }

    public static List<ConfigAttribute> createListFromCommaDelimitedString(String access) {
        return createList(StringUtils.commaDelimitedListToStringArray(access));
    }

    public static List<ConfigAttribute> createList(String... attributeNames) {
        Assert.notNull(attributeNames, "You must supply an array of attribute names");
        List<ConfigAttribute> attributes = new ArrayList<>(
            attributeNames.length);

        for (String attribute : attributeNames) {
            attributes.add(new SecurityConfig(attribute.trim()));
        }

        return attributes;
    }
}
