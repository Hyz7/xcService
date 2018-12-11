package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.manage_cms.dao.CmsConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 〈一句话功能简述〉<br>
 * 〈cms配置管理业务层〉
 *
 * @author hyz
 * @create 2018/11/16 0016
 * @since 1.0.0
 */
@Service
public class CmsConfigService {
    @Autowired
    private CmsConfigRepository cmsConfigRepository;

    public CmsConfig getModel(String id){
        Optional<CmsConfig> optional = cmsConfigRepository.findById(id);
        if (optional.isPresent()){
            CmsConfig cmsConfig = optional.get();
            return cmsConfig;
        }
        return null;
    }
}
