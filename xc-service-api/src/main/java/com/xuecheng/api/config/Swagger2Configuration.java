package com.xuecheng.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

//接口配置类
@Configuration
//开启swagger2，Api测试插件
@EnableSwagger2
public class Swagger2Configuration {
    //扫描com.xuecheng包下面的所有controller，和中的方法、
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.xuecheng"))
                .paths(PathSelectors.any())
                .build();
    }

    //Api文档的命令信息
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("学成网api文档")
                .description("学成网api文档")
//                .termsOfServiceUrl("/")
                .version("1.0")
                .build();
    }

}
