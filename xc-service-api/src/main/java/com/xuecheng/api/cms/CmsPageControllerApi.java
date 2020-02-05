package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.PageRequest;

//@Api：修饰整个类，描述Controller的作用
//value 描述这个接口，是cms页面管理接口
//description 这个接口所负责的具体工作
@Api(value="cms页面管理接口",description = "cms页面管理接口，提供页面的增、删、改、查")
public interface CmsPageControllerApi {
    //@ApiOperation：描述一个类的一个方法或接口,描述这个方法的具体工作内容
    @ApiOperation("分页查询页面列表")
    //@ApiImplicitParams：该方法有多个请求参数
    @ApiImplicitParams({
            //page这个参数是指页码，必填，网址路径上获取，数据类型为int
            @ApiImplicitParam(name="page",value = "页 码",required=true,paramType="path",dataType="int"),
            //size这个参数是指每页显示条数，必填，网址路径上获取，数据类型为int
            @ApiImplicitParam(name="size",value = "每页记录数",required=true,paramType="path",dataType="int")
    })
    //*******具体的方法接口*************
    public QueryResponseResult findList(int page, int size,
                                        QueryPageRequest queryPageRequest);

    //*******新增cms页面接口方法*************
    @ApiOperation("新增cms页面")
    @ApiImplicitParam(name="cmsPage",value = "新增对象",required=true,dataType="CmsPage")
    public CmsPageResult add(CmsPage cmsPage);


    //根据页面id查询页面信息
    @ApiOperation("根据页面id查询页面信息")
    public CmsPage findById(String id);
    //修改页面
    @ApiOperation("修改页面")
    public CmsPageResult edit(String id,CmsPage cmsPage);

    //删除页面
    @ApiOperation("删除页面")
    public ResponseResult delete(String id);
}
