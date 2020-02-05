package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.data.mongodb.repository.MongoRepository;

//dao层接口继承mongoDB数据库类。
public interface CmsPageRepository extends MongoRepository<CmsPage,String> {
    //用于校验是否已存在相同页面的方法。通过页面名称，路径，站点id,校验
    CmsPage findByPageNameAndPageWebPathAndSiteId(String pageName,String pageWebPath,String siteId);
}
