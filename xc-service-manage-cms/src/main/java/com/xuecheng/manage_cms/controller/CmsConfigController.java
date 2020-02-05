package com.xuecheng.manage_cms.controller;

import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.manage_cms.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//取cms模型数据的接口实现，接口在api的config中
@RestController
//这个远程调用的数据模型的方法，是配置好的nginx.exe调用
@RequestMapping("/cms/config")
public class CmsConfigController {

    @Autowired
    private PageService pageService;

    //取得cms配置页面信息
    @GetMapping("/getmodel/{id}")
    public CmsConfig getmodel(@PathVariable String id){
        return pageService.getConfigById(id);
    }
}
