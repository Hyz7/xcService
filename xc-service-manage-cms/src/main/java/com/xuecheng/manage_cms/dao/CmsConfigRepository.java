/**
 * 〈一句话功能简述〉<br>
 * 〈cms配置管理接口〉
 *
 * @author hyz
 * @create 2018/11/16 0016
 * @since 1.0.0
 */
package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CmsConfigRepository extends MongoRepository<CmsConfig,String> {
}
