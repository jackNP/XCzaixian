package com.xuecheng.manage_cms.dao;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.data.domain.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsPageQueryTest {
    @Autowired
    CmsPageRepository cmsPageRepository;


    @Autowired
    GridFSBucket gridFSBucket;

    @Test
    public void findAll(){
        List<CmsPage> all = cmsPageRepository.findAll();
        System.err.println(all.size());
    }


    @Autowired
    GridFsTemplate gridFsTemplate;

    @Test
    public void cun(){
        //要存文件的绝对路径，
        File file = new File("D:\\Code\\xcEdu\\xcEduService01\\xc-service-manage-cms\\src\\test\\java\\com\\xuecheng\\manage_cms\\dao\\index_banner.ftl");
        try {
            //把绝对路径，塞进输入流
            FileInputStream inputStream = new FileInputStream(file);
            //通过store方法，把绝对路径中的，具体文件，存进GridFS库中，并且返回一个存入文件的id
            ObjectId objectId = gridFsTemplate.store(inputStream, "index_banner.ftl");
            System.err.println(objectId);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is("5e37d10f4e2c801a84a5def3")));
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
            System.err.println(content);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

}
