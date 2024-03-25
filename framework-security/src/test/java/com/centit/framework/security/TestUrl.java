package com.centit.framework.security;

public class TestUrl {
    public static void main(String[] args) {
        System.out.println(CentitSecurityMetadata.parseUrlToApi("/dde/run/D12341234?w34"));
        System.out.println(CentitSecurityMetadata.parseUrlToApi("/dde/run/draft/D12341234?w34"));
        System.out.println(CentitSecurityMetadata.parseUrlToApi("/dde/run/D12341234"));
        System.out.println(CentitSecurityMetadata.parseUrlToApi("/dde/run/draft/D1234123f"));
    }
}
