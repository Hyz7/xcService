/**
 * 〈一句话功能简述〉<br>
 * 〈cms模版〉
 *
 * @author hyz
 * @create 2018/11/16 0016
 * @since 1.0.0
 */
package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CmsTemplateRepository extends MongoRepository<CmsTemplate,String> {
}
