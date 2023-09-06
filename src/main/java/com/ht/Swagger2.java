package com.ht;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author yuezhang
 * @version 1.0.0
 * @date 3/5/22 下午12:02.
 */
@Configuration
@EnableSwagger2 // swagger2
public class Swagger2 {

	/****
     * 前端接口配置
     * @return
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                //分组名称
                .groupName("ClientApi")
                .select()
                //扫描的包名称
                .apis(RequestHandlerSelectors.basePackage("com.ht.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    
    /***
     * 构建 api文档的详细信息函数
     * @return
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Swagger2 API接口文档")
                .description("我的swagger文档1.0")
                .termsOfServiceUrl("http://blog.didispace.com/")
                .contact("yeu")
                .version("1.0")
                .build();
    }

}