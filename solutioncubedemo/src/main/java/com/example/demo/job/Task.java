package com.example.demo.job;

import java.io.IOException;

import org.bson.BsonArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.example.demo.job.task.DenemeTask;
import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

@Component
public class Task {

    @Autowired
    private Environment env;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	private static final Logger logger = LoggerFactory.getLogger(DenemeTask.class);
	
	public void execute(String uri, String collectionName){

		String token = ProxyToken.generateToken(env.getProperty("spring.security.user.name"), env.getProperty("spring.security.user.password"));
		
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder()
		  .url(uri)
		  .get()
		  .addHeader("authorization", token)
		  .build();
		
		try {			
			Response response = client.newCall(request).execute();
			String jsonData = response.body().string();
			BsonArray parse = BsonArray.parse(jsonData);
			BasicDBList dbList = new BasicDBList();
			dbList.addAll(parse);
			DBObject dbObject = dbList;
			mongoTemplate.insert(dbObject,collectionName);					
		} catch (IOException e) {
			logger.error("Error execute job " + collectionName, e);
			e.printStackTrace();
		}	
	}
}
