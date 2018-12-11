/**
 * 〈一句话功能简述〉<br>
 * 〈课程信息持久层〉
 *
 * @author hyz
 * @create 2018/12/4 0004
 * @since 1.0.0
 */
package com.xuecheng.manage_course.dao;

import com.github.pagehelper.Page;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TeachplanMapper {
    /**
     * 查询课程计划列表
     * @param courseId 课程ID
     * @return 结果
     */
    TeachplanNode selectList(String courseId);

    @Select("select * from teachplan")
    Page<Teachplan> query();
}
