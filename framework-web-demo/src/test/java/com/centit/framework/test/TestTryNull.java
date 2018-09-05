package com.centit.framework.test;

import com.centit.framework.common.SysParametersUtils;
import com.centit.support.file.FileIOOpt;

import java.io.IOException;
import java.io.InputStream;

public class TestTryNull {
    public static void main(String[] args) {
        try(InputStream resource = SysParametersUtils
            .class.getResourceAsStream("/system23.properties")){
            //new ClassPathResource("system.properties").getInputStream();
            if(resource!=null) {
                String s = FileIOOpt.readStringFromInputStream(resource);
                System.out.println(s);
            }
        } catch (IOException e) {
            System.out.println("获取系统参数出错！");
            //e.printStackTrace();
        }
    }
}
