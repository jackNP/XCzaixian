package com.xuecheng.manage_cms.controller;

import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_cms.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletOutputStream;
import java.io.IOException;

/**
 * @author Administrator
 * @version 1.0
 * @create 2018-09-15 16:20
 **/
//由于要向浏览器输出，html静态页面，是字符串，而不是json数据格式，所以用@controller
@Controller
public class CmsPagePreviewController extends BaseController {

    @Autowired
    PageService pageService;

    //页面预览
    @RequestMapping(value="/cms/preview/{pageId}",method = RequestMethod.GET)
    public void preview(@PathVariable("pageId") String pageId) throws IOException {
        //执行静态化
        String pageHtml = pageService.getPageHtml(pageId);
        //通过response对象,得到输出流
        ServletOutputStream outputStream = response.getOutputStream();
        //输出流，把页面用utf-8字符，输出到浏览器
        outputStream.write(pageHtml.getBytes("utf-8"));
    }
}
