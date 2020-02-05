package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsConfig;
import org.springframework.data.mongodb.repository.MongoRepository;
//Cms页面配置信息的dao
public interface CmsConfigRepository extends MongoRepository<CmsConfig,String> {
}
