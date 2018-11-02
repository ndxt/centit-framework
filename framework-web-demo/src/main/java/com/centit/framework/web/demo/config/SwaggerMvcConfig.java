package com.centit.framework.web.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
//@Import(Swagger2DocumentationConfiguration.class)
public class SwaggerMvcConfig {

    @Bean
    public Docket buildDocket(){
        return new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(buildApiInf())
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.otherpackage.controller"))//controller路径
            .paths(PathSelectors.any())
            .build();
    }

    private ApiInfo buildApiInf(){
        return new ApiInfoBuilder()
            .title("框架接口")
            .termsOfServiceUrl("https://ndxt.github.io")
            .description("南大先腾框架接口")
            .contact(new Contact("codefan", "https://ndxt.github.io", "codefan@centit.com"))
            .build();
    }

}
