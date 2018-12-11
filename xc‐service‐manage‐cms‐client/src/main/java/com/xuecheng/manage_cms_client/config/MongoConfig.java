package com.xuecheng.manage_cms_client.config;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 〈一句话功能简述〉<br>
 * 〈MongoDB配置类〉
 *
 * @author hyz
 * @create 2018/11/16 0016
 * @since 1.0.0
 */
@Configuration
public class MongoConfig {

	@Value("${spring.data.mongodb.database}")
	private String db;
	@Bean
	public GridFSBucket getGridFSBucket(MongoClient mongoClient) {
		MongoDatabase database = mongoClient.getDatabase(db);
		GridFSBucket bucket = GridFSBuckets.create(database);
		return bucket;
	}
}
