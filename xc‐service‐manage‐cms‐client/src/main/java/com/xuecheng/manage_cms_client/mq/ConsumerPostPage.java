package com.xuecheng.manage_cms_client.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_cms_client.service.CmsPageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 〈一句话功能简述〉<br>
 * 〈监听mq,接受页面发布消息〉
 *
 * @author hyz
 * @create 2018/11/21 0021
 * @since 1.0.0
 */
@Component
public class ConsumerPostPage {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerPostPage.class);
    @Autowired
    private CmsPageService cmsPageService;

    @RabbitListener(queues = {"${xuecheng.mq.queue}"})
    private void postPage(String msg){
        // 获取json串中的pageId
        Map map = JSON.parseObject(msg, Map.class);
        String pageId = (String) map.get("pageId");
        // 校验页面是否合法
        CmsPage cmsPage = cmsPageService.findCmsPageById(pageId);
        if (cmsPage == null){
            LOGGER.error("receive postPage msg cmsPage is null , pageId:{}"+pageId);
            return;
        }
        // 调用service
        cmsPageService.savePageToServerPath(pageId);

    }
}
