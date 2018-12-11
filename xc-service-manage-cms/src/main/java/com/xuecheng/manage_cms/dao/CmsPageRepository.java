/**
 * 〈一句话功能简述〉<br>
 * 〈CmsPage持久层〉
 *
 * @author hyz
 * @create 2018/11/14 0014
 * @since 1.0.0
 */
package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CmsPageRepository extends MongoRepository<CmsPage,String> {
    /**
     * 根据条件确认页面唯一
     * @param pageName 页面名称
     * @param siteId 站点id
     * @param pageWebPath 页面地址
     * @return 页面实体
     */
    CmsPage findByPageNameAndSiteIdAndPageWebPath(String pageName,String siteId,String pageWebPath);
}
