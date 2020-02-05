package com.xuecheng.manage_cms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EntityScan("com.xuecheng.framework.domain.cms")//扫描指定包的实体类
@ComponentScan(basePackages={"com.xuecheng.api"})//扫描指定包的接口
@ComponentScan(basePackages={"com.xuecheng.framework"})//扫描指定异常包的微服务
@ComponentScan(basePackages={"com.xuecheng.manage_cms"})//扫描本项目下的所有类
public class ManageCmsApplication {
    //往IOC容易中注入，用OkHttp3Client请求插件，用于请求页面模型数据
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate(new OkHttp3ClientHttpRequestFactory()) ;
    }

    public static void main(String[] args) {
        SpringApplication.run(ManageCmsApplication.class,args);
    }
}
