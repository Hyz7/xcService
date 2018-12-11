package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.dao.CourseBaseRepository;
import com.xuecheng.manage_course.dao.CourseMapper;
import com.xuecheng.manage_course.dao.TeachplanMapper;
import com.xuecheng.manage_course.dao.TeachplanRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 〈一句话功能简述〉<br>
 * 〈课程管理页面业务层〉
 *
 * @author hyz
 * @create 2018/12/4 0004
 * @since 1.0.0
 */
@Service
public class CourseService {
	@Autowired
	private TeachplanMapper teachplanMapper;
	@Autowired
	private TeachplanRepository teachplanRepository;
	@Autowired
	private CourseBaseRepository courseBaseRepository;
	@Autowired
	private CourseMapper courseMapper;

	public TeachplanNode findTeachplanList(String courseId) {
		return teachplanMapper.selectList(courseId);
	}

	@Transactional()
	public ResponseResult addTeachplan(Teachplan teachplan) {
		// 判断参数是否合法
		if (teachplan == null || StringUtils.isEmpty(teachplan.getCourseid())
				|| StringUtils.isEmpty(teachplan.getPname())){
			ExceptionCast.cast(CommonCode.INVALID_PARAM);
		}
		// 获得课程id
		String courseid = teachplan.getCourseid();
		// 获得父节点
		String parentid = teachplan.getParentid();
		// 如果父节点为空
		if (StringUtils.isEmpty(parentid)){
			// 根据课程id获取根节点
			parentid = getTeachplanRoot(courseid);
		}
		// 获得父节点信息
		Optional<Teachplan> optional = teachplanRepository.findById(parentid);
		if (!optional.isPresent()){
			ExceptionCast.cast(CommonCode.INVALID_PARAM);
		}
		Teachplan teachplanNode = optional.get();
		// 获得父节点级别
		String grade = teachplanNode.getGrade();
		// 创建一个新节点
		Teachplan teachplanNew = new Teachplan();
		teachplanNew.setCourseid(courseid);
		teachplanNew.setParentid(parentid);
		BeanUtils.copyProperties(teachplan,teachplanNew);
		if ("1".equals(grade)){
			teachplanNew.setGrade("2");
		}else {
			teachplanNew.setGrade("3");
		}
		teachplanRepository.save(teachplanNew);
		return new ResponseResult(CommonCode.SUCCESS);
	}

	/**
	 * 根据课程id获取根节点
	 * @param courseid 课程id
	 * @return 根节点id
	 */
	private String getTeachplanRoot(String courseid) {
		// 得到课程基本信息
		Optional<CourseBase> optional = courseBaseRepository.findById(courseid);
		if (!optional.isPresent()){
			ExceptionCast.cast(CommonCode.INVALID_PARAM);
			return null;
		}
		// 课程基本信息
		CourseBase courseBase = optional.get();
		List<Teachplan> teachplanList = teachplanRepository.findByCourseidAndParentid(courseid, "0");
		// 判断是不是新课程
		if (teachplanList == null || teachplanList.size() <= 0){
			// 创建一个新的课程计划
			Teachplan teachplan = new Teachplan();
			teachplan.setCourseid(courseid);
			teachplan.setParentid("0");
			teachplan.setGrade("1");
			teachplan.setPname(courseBase.getName());
			teachplan.setStatus("0");
			teachplanRepository.save(teachplan);
			return teachplan.getId();
		}
		return teachplanList.get(0).getId();
	}
}
