package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
//模板信息的dao
public interface CmsTemplateRepository extends MongoRepository<CmsTemplate,String> {
}
