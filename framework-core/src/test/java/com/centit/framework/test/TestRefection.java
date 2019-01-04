package com.centit.framework.test;

import com.centit.framework.components.impl.NotificationCenterImpl;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Created by codefan on 17-6-29.
 */
public class TestRefection {
    public static void main(String[] args) {
        ParameterNameDiscoverer pd = new StandardReflectionParameterNameDiscoverer();
        Method[] mths = NotificationCenterImpl.class.getMethods();
        for (Method mth : mths) {
            System.out.println(mth.getName());
            String[] names = pd.getParameterNames(mth);
            if(names!=null) {
                for (String name : names) {
                    System.out.print("\t" + name);
                }
                System.out.println();
            }

            Parameter[] params = mth.getParameters();
            for(Parameter param : params){
                System.out.print("\t" + param.getName());
            }
            System.out.println();
        }
    }
}
