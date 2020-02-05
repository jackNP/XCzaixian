package com.xuecheng.manage_cms.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.dao.CmsConfigRepository;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;


@Service
public class PageService {
    @Autowired
    private CmsPageRepository cmsPageRepository;
    //获取模型数据的，对象。
    @Autowired
    private RestTemplate restTemplate;

    //cms_Template数据库的dao
    @Autowired
    private CmsTemplateRepository cmsTemplateRepository;

    //cms配置页面的模型数据查询dao
    @Autowired
    private CmsConfigRepository cmsConfigRepository;

    //再jar包中有，存ftl文件的上传器
    @Autowired
    GridFsTemplate gridFsTemplate;
    //再mongoConfig，类中配置的
    @Autowired
    private GridFSBucket gridFSBucket;

    /**
      * 页面列表分页查询
      * @param page 当前页码
      * @param size 页面显示个数
      * @param queryPageRequest 查询条件
      * @return 页面列表
      */
    public QueryResponseResult findList(int page, int size,  QueryPageRequest queryPageRequest) {

        //判断传进来的页面，小于第一页，就默认查询第一页
        if (page <= 0){
            page = 1;
        }
        //如果传进来的不是小于0，证明页面正确，就-1，因为数据库是从0开始，
        page = page - 1;

        //判断传进来的显示数量，小于10，就默认查询10条
        if (size <= 0){
            size = 10;
        }
        //组装查询所需要的页码和数量，的pageable对象
        Pageable pageable = PageRequest.of(page,size);
        //__________分页信息组装好后，开始组装条件查询_____________

        //**开始**提前准备一个空的条件查询对象
        Example<CmsPage> example = null;

        //****先判断，条件查询的对象是否为空,不为空，才开始组装查询条件****
        if (queryPageRequest!=null){
            //先准备一个cms分页对象。
            CmsPage cmsPage = new CmsPage();

            //如果站点id，不为空，就组装cms对象
            String siteId = queryPageRequest.getSiteId();
            if (siteId!=null && !siteId.equals("")){
                cmsPage.setSiteId(siteId);
            }

            //如果站点模板id，不为空，就组装cms对象
            String TemplateId = queryPageRequest.getTemplateId();
            if (TemplateId!=null && !TemplateId.equals("")){
                cmsPage.setTemplateId(TemplateId);
            }

            //如果别名，不为空，就组装cms对象
            String PageAliase = queryPageRequest.getPageAliase();
            if (PageAliase!=null && !PageAliase.equals("")){
                cmsPage.setPageAliase(PageAliase);
            }

            //别名属于模糊查询，所以需要，设置模糊条件查询匹配器
                //ExampleMatcher.GenericPropertyMatchers.contains() 包含关键字
                //ExampleMatcher.GenericPropertyMatchers.startsWith() 前缀匹配
            ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                    //链式点出，通过关键字匹配，数据库中的PageAliase列
                    .withMatcher("pageAliase",ExampleMatcher.GenericPropertyMatchers.contains());
            //把组装好的cms条件查询对象和模糊条件匹配器，
                // 添加进条件查询的静态方法中。得到一个条件查询对象
            example = Example.of(cmsPage,exampleMatcher);
        }

//********分页信息组装好后，开始组装条件查询结束********



        //执行查询,参数1,条件查询的对象，参数2，分页参数值对象
        Page<CmsPage> cmsPagePageList = cmsPageRepository.findAll(example,pageable);
        //封装分页数据集合和总记录数的对象
        QueryResult queryResult = new QueryResult();
        //得到分页数据集合
        queryResult.setList(cmsPagePageList.getContent());
        // 得到总记录数
        queryResult.setTotal(cmsPagePageList.getTotalElements());
        //封装返回到页面的固定格式对象
        QueryResponseResult queryResponseResult =
                new QueryResponseResult(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;
    }

    //_________新增页面方法，
    public CmsPageResult add(CmsPage cmsPage){

        if (cmsPage == null) {
            //证明进来的参数没有，也就是参数异常
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }


            //进来的对象，不为空，就执行校验
            CmsPage page = cmsPageRepository.findByPageNameAndPageWebPathAndSiteId
                    (cmsPage.getPageName(), cmsPage.getPageWebPath(), cmsPage.getSiteId());
            if (page == null) {
                //如果校验没有这个页面，就执行添加页面的操作
                //添加页面之前，先把页面id,清除，让MongoDB数据库自己生成主键
                cmsPage.setPageId(null);
                //执行添加页面的操作
                CmsPage cmsPage1 = cmsPageRepository.save(cmsPage);
                //返回resultful规范数据
                return new CmsPageResult(CommonCode.SUCCESS,cmsPage1);
            }
            //如果校验到了，就直接不让新增
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
            //返回一个null,防止语法报错，前面的异常会拦截，下面是不可达代码
            return null;
    }

    //修改操作，包括，数据单查回显，数据修改2个方法

    //单查回显
    public CmsPage getById(String id){
        //单查取得optional对象
        Optional<CmsPage> byId = cmsPageRepository.findById(id);
        //判断optional对象不为空就取cms页面,进行返回
        if (byId.isPresent()) {
            CmsPage cmsPage = byId.get();
            return cmsPage;
        }
        return null;
    }


    //数据修改
    public CmsPageResult edit(String id,CmsPage cmsPage){
        //先查询通过id要修改的对象
        CmsPage one = getById(id);
        //手动设置其中的属性，原因在于，有些列不允许修改
        if(one!=null){
            //准备更新数据
            //设置要修改的数据
            //更新模板id
            one.setTemplateId(cmsPage.getTemplateId());
            //更新所属站点
            one.setSiteId(cmsPage.getSiteId());
            //更新页面别名
            one.setPageAliase(cmsPage.getPageAliase());
            //更新页面名称
            one.setPageName(cmsPage.getPageName());
            //更新访问路径
            one.setPageWebPath(cmsPage.getPageWebPath());
            //更新物理路径
            one.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            //更新静态模板数据的url
            one.setDataUrl(cmsPage.getDataUrl());

            //提交修改
            cmsPageRepository.save(one);
            return new CmsPageResult(CommonCode.SUCCESS,one);
        }
        //如果没有找到cms对象，就返回失败
        return new CmsPageResult(CommonCode.FAIL,null);
    }

    //删除cms页面
    public ResponseResult delete(String id){
        //先查询有没有这个对象
        Optional<CmsPage> optional = cmsPageRepository.findById(id);
        if (optional.isPresent()){//不是空的就返回true,执行删除页面
            cmsPageRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        //返回删除失败
        return new ResponseResult(CommonCode.FAIL);
    }

    //###单查Cms_config数据库的数据模型的方法
    public CmsConfig getConfigById(String id){
        Optional<CmsConfig> optional = cmsConfigRepository.findById(id);
        if (optional.isPresent()){
            return optional.get();
        }
        return null;
    }

    //页面静态化方法
    /**
     * 静态化程序获取页面的DataUrl
     *
     * 静态化程序远程请求DataUrl获取数据模型。
     *
     * 静态化程序获取页面的模板信息
     *
     * 执行页面静态化
     */
    public String getPageHtml(String pageId){
        //取数据模型对象
        Map model= this.getModelByPageId(pageId);
        if (model==null){
            //数据模型获取不到
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }

        //获取页面的模板信息
        String template = getTemplateByPageId(pageId);
        //有这个模板，就执行静态化模板，生成新的Html，
            // 参数1，数据模型对象，参数2，静态模板
        String html = this.generateHtml(template, model);
        //返回组装好的静态页面
        return html;


    }

    //执行静态化,通过页面模板和数据模型，生成
    private String generateHtml(String templateContent,Map model){
        //创建ramework配置对象
        Configuration configuration = new Configuration(Configuration.getVersion());
        //创建ramework模板加载器
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        //往模板加载器中，加入名为：template ，的html模板字符串
        stringTemplateLoader.putTemplate("template",templateContent);
        //向configuration中，配置模板加载器
        configuration.setTemplateLoader(stringTemplateLoader);
        //获取字符串静态模板，调用api,和数据模型进行组装得到静态化页面
        try {
            Template template = configuration.getTemplate("template");
            //组装
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            return content;
        } catch (Exception e) {//异常改为Exception
            e.printStackTrace();
        }
        //如果组装失败，返回null
        return null;

    }



//    获取页面的模板信息的方法
    private String getTemplateByPageId(String pageId) {
        //根据页面id,取页面对象
        CmsPage cmsPage = this.getById(pageId);
        if (cmsPage == null) {
            //为空，抛异常，页面不存在
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        //从页面信息中拿模板的id
        String templateId = cmsPage.getTemplateId();
        if (StringUtils.isEmpty(templateId)){
            //如果模板id属性为空，就抛异常,页面模板为空
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //通过模板信息管理类，查询模板信息
        Optional<CmsTemplate> optional = cmsTemplateRepository.findById(templateId);
        if (optional.isPresent()){
            //如果有数据，就把模板数据取出来
            CmsTemplate cmsTemplate = optional.get();
            //再取模板文件的id
            String templateFileId = cmsTemplate.getTemplateFileId();
            //根据模板文件的id,查询gridfs文件管理器里面的GridFSFile文件对象
            //通过，模板id,去fs.files中的”——id“的列中查找，ftl文件名对应fs.chunks集合表中的流数据
            GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
            //通过配置类中，配置好的下载器。去下载这个GridFSFile文件对象
            // 解释：开启一个下载流，通过grid文件id,去下载
            GridFSDownloadStream gridFSDownloadStream =
                    gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
            //创建GridFsResource对象，获取流,
                // 参数1：文件管理器的路径，参数2：配置好的下载流
            GridFsResource gridFsResource =
                    new GridFsResource(gridFSFile,gridFSDownloadStream);
            ////从流中取数据,把流文件用utf-8 的字节解析为字符串
            try {
                String content = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
                //返回字符串页面
                return content;
            } catch (IOException e) {
                e.printStackTrace();
            }

        } //取模型页面完成
        //如果查询到的模板信息为空，就不去取模板
        return null;
    }


    //通过cms页面id，得到页面数据模型，封装为map
    private Map getModelByPageId(String pageId){
//        得到页面，判断是否有，没有就抛异常
        CmsPage cmsPage = this.getById(pageId);
        if (cmsPage == null) {
            //抛出没有这个页面数据的异常
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        //有这个页面，就取Dataurl
        String dataUrl = cmsPage.getDataUrl();
        //判断地址为空,抛异常，获取不到url的异常
        if (StringUtils.isEmpty(dataUrl)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }
        //通过restTemplate请求dataUrl获取数据
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
//        得到模型数据后，转换为json格式封装到另一个map中。
        Map body = forEntity.getBody();
        //返回json格式的模板信息Map
        return body;
    }
}
