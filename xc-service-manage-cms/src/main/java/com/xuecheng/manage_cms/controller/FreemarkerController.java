package com.xuecheng.manage_cms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * 〈一句话功能简述〉<br>
 * 〈页面静态化〉
 *
 * @author hyz
 * @create 2018/11/16 0016
 * @since 1.0.0
 */
@Controller
@RequestMapping("/freemarker")
public class FreemarkerController {
    @Autowired
    private RestTemplate restTemplate;
	@RequestMapping("/banner")
	public String index_banner(Map<String, Object> map) {
        ResponseEntity<Map> forEntity = restTemplate.getForEntity("http://localhost:31001/cms/config/getmodel/5a791725dd573c3574ee333f", Map.class);
        Map body = forEntity.getBody();
        map.putAll(body);
        return "index_banner";
	}
}
