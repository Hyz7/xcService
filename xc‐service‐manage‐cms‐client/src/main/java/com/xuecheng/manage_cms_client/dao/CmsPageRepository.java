package com.xuecheng.manage_cms_client.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * 〈一句话功能简述〉<br>
 * 〈页面持久层〉
 *
 * @author hyz
 * @create 2018/11/21 0021
 * @since 1.0.0
 */
public interface CmsPageRepository extends MongoRepository<CmsPage,String> {
}
