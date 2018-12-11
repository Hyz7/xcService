package com.xuecheng.framework.domain.cms.request;

import com.xuecheng.framework.model.request.RequestData;
import lombok.Data;

/**
 * 〈一句话功能简述〉<br>
 * 〈查询条件类〉
 *
 * @author hyz
 * @create 2018/11/13 0013
 * @since 1.0.0
 */
@Data
public class QueryPageRequest extends RequestData {
    //站点id
    private String siteId;
    //页面ID
    private String pageId;
    //页面名称
    private String pageName;
    //别名
    private String pageAliase;
    //模版id
    private String templateId;
}
