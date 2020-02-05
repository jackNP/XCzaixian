package com.xuecheng.manage_cms.config;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Administrator
 * @version 1.0
 * @create 2018-09-15 15:15
 **/
//mongo数据库的配置类
@Configuration
public class MongoConfig {
    //取配置文件中的mongodb数据库路径和相应参数
    @Value("${spring.data.mongodb.database}")
    String db;

    //再相应的数据库中创建一个GridFS下载器
    @Bean
    public GridFSBucket getGridFSBucket(MongoClient mongoClient){
        //通过请求路径指向一个数据库
        MongoDatabase database = mongoClient.getDatabase(db);
        //再指向的数据库中，创建可操作的grid文件管理器的数据库对象
        GridFSBucket bucket = GridFSBuckets.create(database);
        return bucket;
    }
}
