/**
 * 〈一句话功能简述〉<br>
 * 〈课程计划接口〉
 *
 * @author hyz
 * @create 2018/12/4 0004
 * @since 1.0.0
 */
package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value="课程计划页面管理接口",description = "课程计划管理接口，提供课程计划的增、删、改、查")
public interface CourseControllerApi {
    /**
     * 查询课程计划页面列表
     * @param courseId 课程id
     * @return 结果
     */
    @ApiOperation("课程计划页面列表")
    TeachplanNode findTeachplanList(String courseId);

    /**
     * 添加课程计划
     * @param teachplan 课程计划信息
     * @return 结果
     */
    @ApiOperation("添加课程计划")
    ResponseResult addTeachplan(Teachplan teachplan);
}
