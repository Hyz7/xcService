/**
 * 〈一句话功能简述〉<br>
 * 〈cms配置管理接口〉
 *
 * @author hyz
 * @create 2018/11/16 0016
 * @since 1.0.0
 */
package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value="cms配置管理接口",description = "cms配置管理接口，提供数据模型的管理、查询接口")
public interface CmsConfigControllerApi {
    @ApiOperation("根据id查询CMS配置信息")
    CmsConfig getModel(String id);
}
