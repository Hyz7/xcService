/**
 * 〈一句话功能简述〉<br>
 * 〈cms服务接口〉
 *
 * @author hyz
 * @create 2018/11/13 0013
 * @since 1.0.0
 */
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

@Api(value="cms页面管理接口",description = "cms页面管理接口，提供页面的增、删、改、查")
public interface CmsPageControllerApi {

    /**
     *
     * @param page 当前页数
	 * @param size 每页显示记录数
	 * @param queryPageRequest 查询条件
     * @author hyz
     * @Description: 页面查询
     * @return com.xuecheng.framework.model.response.QueryResponseResult
     * @date 2018/11/13 0013 15:12
     */
    @ApiOperation("分页查询页面列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name="page",value = "页码",required=true,paramType="path",dataType="int"),
            @ApiImplicitParam(name="size",value = "每页记录数",required=true,paramType="path",dataType="int")
    })
    QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest);

    /**
     * 新增页面
     * @param cmsPage 页面属性
     * @return 返回结果集
     */
    @ApiOperation("新增页面")
    CmsPageResult add(CmsPage cmsPage);

    /**
     * 修改页面信息
     * @param id 主键
     * @param cmsPage 页面信息
     * @return 结果集
     */
    @ApiOperation("修改页面")
    CmsPageResult update(String id,CmsPage cmsPage);

    /**
     * 通过ID查询页面
     * @param id 主键
     * @return 页面信息
     */
    @ApiOperation("通过ID查询页面")
    CmsPage findById(String id);

    /**
     * 根据id删除页面
     * @param id 主键
     * @return 结果集
     */
    @ApiOperation("根据id删除页面")
    ResponseResult del(String id);

    /**
     * 发布页面
     * @param pageId 页面id
     * @return 结果集
     */
    @ApiOperation("发布页面")
    ResponseResult post(String pageId);
}
