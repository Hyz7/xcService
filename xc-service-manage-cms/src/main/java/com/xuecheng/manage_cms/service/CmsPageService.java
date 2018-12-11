package com.xuecheng.manage_cms.service;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.*;
import com.xuecheng.manage_cms.config.RabbitmqConfig;
import com.xuecheng.manage_cms.dao.CmsConfigRepository;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 〈一句话功能简述〉<br>
 * 〈分页查询业务层〉
 *
 * @author hyz
 * @create 2018/11/14 0014
 * @since 1.0.0
 */
@Service
public class CmsPageService {
	@Autowired
	private CmsPageRepository cmsPageRepository;

	@Autowired
	private CmsConfigRepository cmsConfigRepository;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private CmsTemplateRepository cmsTemplateRepository;

	@Autowired
	private GridFsTemplate gridFsTemplate;

	@Autowired
	private GridFSBucket gridFSBucket;

	@Autowired
	private RabbitTemplate rabbitTemplate;
	/**
	 * 分页查询
	 *
	 * @param page
	 *            当前页码数
	 * @param size
	 *            每页记录数
	 * @param queryPageRequest
	 *            查询条件
	 * @return com.xuecheng.framework.model.response.QueryResponseResult
	 */
	public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest) {
		if (queryPageRequest == null) {
			queryPageRequest = new QueryPageRequest();
		}
		// 方便页码传递
		if (page <= 0) {
			page = 1;
		}
		page = page - 1;
		if (size <= 0) {
			size = 20;
		}
		// 自定义条件查询
		// 定义条件匹配器
		ExampleMatcher exampleMatcher = ExampleMatcher.matching().withMatcher("pageAliase",
				ExampleMatcher.GenericPropertyMatchers.contains());
		// 条件值对象
		CmsPage cmsPage = new CmsPage();
		// 设置条件值（站点id）
		if (StringUtils.isNotEmpty(queryPageRequest.getSiteId())) {
			cmsPage.setSiteId(queryPageRequest.getSiteId());
		}
		// 设置模板id作为查询条件
		if (StringUtils.isNotEmpty(queryPageRequest.getTemplateId())) {
			cmsPage.setTemplateId(queryPageRequest.getTemplateId());
		}
		// 设置页面别名作为查询条件
		if (StringUtils.isNotEmpty(queryPageRequest.getPageAliase())) {
			cmsPage.setPageAliase(queryPageRequest.getPageAliase());
		}
		// 定义条件对象Example
		Example<CmsPage> example = Example.of(cmsPage, exampleMatcher);
		Pageable pageable = PageRequest.of(page, size);
		QueryResult<CmsPage> queryResult = new QueryResult<>();
		Page<CmsPage> all = cmsPageRepository.findAll(example, pageable);
		// 显示记录
		queryResult.setList(all.getContent());
		// 总记录数
		queryResult.setTotal(all.getTotalElements());
		QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS, queryResult);
		return queryResponseResult;
	}

	/**
	 * 添加页面
	 * @param cmsPage 页面信息
	 * @return com.xuecheng.framework.domain.cms.response.CmsPageResult
	 */
	public CmsPageResult add(CmsPage cmsPage) {
		if (cmsPage == null) {
			ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
		}
		CmsPage existCmsPage = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(),
				cmsPage.getSiteId(), cmsPage.getPageWebPath());
		if (existCmsPage != null) {
			ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
		}
		// 页面不存在
		// 主键由MongoDB自动生成
		cmsPage.setPageId(null);
		cmsPageRepository.save(cmsPage);
		return new CmsPageResult(CommonCode.SUCCESS, cmsPage);
	}

	/**
	 * 修改页面
	 * @param id 页面id
	 * @param cmsPage 页面内容
	 * @return com.xuecheng.framework.domain.cms.response.CmsPageResult
	 */
	public CmsPageResult update(String id, CmsPage cmsPage) {
		// 判断要修改的内容是否存在
		CmsPage one = this.findById(id);
		if (one != null) {
			// 更新模板id
			one.setTemplateId(cmsPage.getTemplateId());
			// 更新所属站点
			one.setSiteId(cmsPage.getSiteId());
			// 更新页面别名
			one.setPageAliase(cmsPage.getPageAliase());
			// 更新页面名称
			one.setPageName(cmsPage.getPageName());
			// 更新访问路径
			one.setPageWebPath(cmsPage.getPageWebPath());
			// 更新物理路径
			one.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
			// 更新dataUrl
			one.setDataUrl(cmsPage.getDataUrl());
			// 执行更新
			cmsPageRepository.save(one);
			return new CmsPageResult(CommonCode.SUCCESS, one);
		}
		return new CmsPageResult(CommonCode.FAIL, null);
	}

	/**
	 * 根据Id查询页面
	 * @param id 页面id
	 * @return com.xuecheng.framework.domain.cms.CmsPage
	 */
	public CmsPage findById(String id) {
		Optional<CmsPage> optional = cmsPageRepository.findById(id);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	/**
	 * 根据Id删除页面
	 * @param id 页面id
	 * @return com.xuecheng.framework.model.response.ResponseResult
	 */
	public ResponseResult del(String id) {
		Optional<CmsPage> optional = cmsPageRepository.findById(id);
		if (optional.isPresent()) {
			cmsPageRepository.deleteById(id);
			return new ResponseResult(CommonCode.SUCCESS);
		}
		return new ResponseResult(CommonCode.FAIL);
	}


	/**
	 * 页面静态化方法
	 *
	 * 静态化程序获取页面的DataUrl
	 *
	 * 静态化程序远程请求DataUrl获取数据模型。
	 *
	 * 静态化程序获取页面的模板信息
	 *
	 * 执行页面静态化
	 */
	public String getPageHtml(String pageId){

		//获取数据模型
		Map model = getModelByPageId(pageId);
		if(model == null){
			//数据模型获取不到
			ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
		}

		//获取页面的模板信息
		String template = getTemplateByPageId(pageId);
		if(StringUtils.isEmpty(template)){
			ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
		}

		//执行静态化
		String html = generateHtml(template, model);
		return html;

	}

	/**
	 * 执行静态化
	 * @param templateContent 模版内容
	 * @param model 数据模型
	 * @return 静态化页面
	 */
	private String generateHtml(String templateContent,Map model ){
		//创建配置对象
		Configuration configuration = new Configuration(Configuration.getVersion());
		//创建模板加载器
		StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
		stringTemplateLoader.putTemplate("template",templateContent);
		//向configuration配置模板加载器
		configuration.setTemplateLoader(stringTemplateLoader);
		//获取模板
		try {
			Template template = configuration.getTemplate("template");
			//调用api进行静态化
			String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
			return content;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 获取页面的模板信息
	 * @param pageId 页面Id
	 * @return 模版信息
	 */
	private String getTemplateByPageId(String pageId){
		//取出页面的信息

		CmsPage cmsPage = this.findById(pageId);
		if(cmsPage == null){
			//页面不存在
			ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
		}
		//获取页面的模板id
		String templateId = cmsPage.getTemplateId();
		if(StringUtils.isEmpty(templateId)){
			ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
		}
		//查询模板信息
		Optional<CmsTemplate> optional = cmsTemplateRepository.findById(templateId);
		if(optional.isPresent()){
			CmsTemplate cmsTemplate = optional.get();
			//获取模板文件id
			String templateFileId = cmsTemplate.getTemplateFileId();
			//从GridFS中取模板文件内容
			//根据文件id查询文件
			GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));

			//打开一个下载流对象
			GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
			//创建GridFsResource对象，获取流
			GridFsResource gridFsResource = new GridFsResource(gridFSFile,gridFSDownloadStream);
			//从流中取数据
			try {
				String content = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
				return content;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;

	}

	/**
	 * 获取数据模型
	 * @param pageId 页面Id
	 * @return 数据模型
	 */
	private Map getModelByPageId(String pageId){
		//取出页面的信息
		CmsPage cmsPage = this.findById(pageId);
		if(cmsPage == null){
			//页面不存在
			ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
		}
		//取出页面的dataUrl
		String dataUrl = cmsPage.getDataUrl();
		if(StringUtils.isEmpty(dataUrl)){
			//页面dataUrl为空
			ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
		}
		//通过restTemplate请求dataUrl获取数据
		ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
		Map body = forEntity.getBody();
		return body;

	}

	/**
	 * 发布页面
	 * @param pageId 页面id
	 * @return com.xuecheng.framework.model.response.ResponseResult
	 */
	public ResponseResult postPage(String pageId){
		// 执行页面静态化
		String pageHtml = this.getPageHtml(pageId);
		if (StringUtils.isEmpty(pageHtml)){
			ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
		}
		// 保存静态化文件到gridFs
		CmsPage cmsPage = this.saveHtml(pageId, pageHtml);
		// 发送消息到mq
		this.sendPostPage(pageId);
		return new ResponseResult(CommonCode.SUCCESS);
	}

	/**
	 * 保存静态化文件到gridFs
	 * @param pageId 页面id
	 * @param htmlContent 静态化页面内容
	 * @return 页面实体
	 */
	private CmsPage saveHtml(String pageId,String htmlContent){
		// 获得页面信息
		Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
		if (!optional.isPresent()){
			ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
		}
		// 储存之前先删除原有文件
		CmsPage cmsPage = optional.get();
		String htmlFileId = cmsPage.getHtmlFileId();
		if (StringUtils.isNotEmpty(htmlFileId)){
			gridFsTemplate.delete(Query.query(Criteria.where("_id").is(htmlFileId)));
		}

		InputStream inputStream = null;
		ObjectId objectId = null;
		try {
			// 将页面内容转换成流
			inputStream = IOUtils.toInputStream(htmlContent, "utf-8");
			// 将html文件保存到gridFs服务器
			objectId = gridFsTemplate.store(inputStream, cmsPage.getPageName());
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 将html页面id存到cmsPage
		cmsPage.setHtmlFileId(objectId.toString());
		CmsPage page = cmsPageRepository.save(cmsPage);
		return page;
	}

	/**
	 * 发送消息到mq
	 * @param pageId 页面id
	 */
	private void sendPostPage(String pageId){
		// 获得cmsPage
		CmsPage cmsPage = this.findById(pageId);
		if (cmsPage == null){
			ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
		}
		// 保存消息到map
		Map<String,Object> msgMap = new HashMap<>(10);
		msgMap.put("pageId",pageId);
		// 将map转为json
		String msg = JSON.toJSONString(msgMap);
		// 获得站点id
		String siteId = cmsPage.getSiteId();
		// 发送消息
		rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE,siteId,msg);
	}
}
