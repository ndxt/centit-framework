package com.centit.framework.config;

import org.springframework.context.annotation.ComponentScan;

/**
 * Created by zou_wy on 2017/3/29.
 */
@ComponentScan(basePackages = {"com.centit.framework.*.controller"},
               includeFilters = {@ComponentScan.Filter(value= org.springframework.stereotype.Controller.class)},
               useDefaultFilters = false)
public class SystemSpringMvcConfig extends BaseSpringMvcConfig {

}
