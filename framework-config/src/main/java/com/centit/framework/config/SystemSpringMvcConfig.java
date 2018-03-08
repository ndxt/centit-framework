package com.centit.framework.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Created by zou_wy on 2017/3/29.
 */
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(basePackages = {"com.centit.framework.**.controller"},
               includeFilters = {@ComponentScan.Filter(value= org.springframework.stereotype.Controller.class)},
               useDefaultFilters = false)
public class SystemSpringMvcConfig extends BaseSpringMvcConfig {

}
