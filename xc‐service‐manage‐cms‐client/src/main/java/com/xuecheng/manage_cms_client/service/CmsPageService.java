package com.xuecheng.manage_cms_client.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.manage_cms_client.dao.CmsPageRepository;
import com.xuecheng.manage_cms_client.dao.CmsSiteRepository;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Optional;

/**
 * 〈一句话功能简述〉<br>
 * 〈页面发布业务层〉
 *
 * @author hyz
 * @create 2018/11/21 0021
 * @since 1.0.0
 */
@Service
public class CmsPageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CmsPageService.class);
	@Autowired
	private CmsPageRepository cmsPageRepository;

	@Autowired
	private CmsSiteRepository cmsSiteRepository;

	@Autowired
	private GridFsTemplate gridFsTemplate;

	@Autowired
	private GridFSBucket gridFSBucket;

    /**
     * 将html页面保存到服务器物理路径
     * @param pageId 页面id
     */
	public void savePageToServerPath(String pageId){
	    // 根据页面id查询cmsPage
        CmsPage cmsPage = this.findCmsPageById(pageId);
        // 得到html文件id
        String htmlFileId = cmsPage.getHtmlFileId();
        // 从gridFs中查询html文件
        InputStream inputStream = this.getFileById(htmlFileId);
        if (inputStream == null){
            LOGGER.error("getFileById inputStream is null,htmlFileId:{}"+htmlFileId);
            return;
        }
        // 得到站点id
        String siteId = cmsPage.getSiteId();
        // 得到站点信息
        CmsSite cmsSite = this.findCmsSiteById(siteId);
        // 得到站点物理路径
        String sitePhysicalPath = cmsSite.getSitePhysicalPath();
        // 得到页面物理路径
        String pagePath = sitePhysicalPath + cmsPage.getPagePhysicalPath() + cmsPage.getPageName();
        // 将文件保存到服务器物理路径
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(new File(pagePath));
            IOUtils.copy(inputStream,fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据id查询cmsPage
     * @param pageId 页面id
     * @return com.xuecheng.framework.domain.cms.CmsPage
     */
    public CmsPage findCmsPageById(String pageId){
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        if (optional.isPresent()){
            return optional.get();
        }
        return null;
    }

    /**
     * 根据id查询cmsPage
     * @param siteId 页面id
     * @return com.xuecheng.framework.domain.cms.CmsSite
     */
    private CmsSite findCmsSiteById(String siteId){
        Optional<CmsSite> optional = cmsSiteRepository.findById(siteId);
        if (optional.isPresent()){
            return optional.get();
        }
        return null;
    }

    /**
     * 根据html文件id查询文件内容
     * @param fileId
     * @return
     */
    private InputStream getFileById(String fileId){
        // 文件对象
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
        // 打开下载流
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        // 获得流对象
        GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
        try {
            return gridFsResource.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
