package com.centit.test;

import java.util.Set;

import com.centit.framework.components.SysUnitFilterEngine;

public class TestUnitFilter {
    public static void main(String arg[]){
        
        Set<String> ss = SysUnitFilterEngine.calcSystemUnitsByExp("u0001||u0002||u0003",null,null);
        
        for(String s:ss)
            System.out.println(s);
    }
}
