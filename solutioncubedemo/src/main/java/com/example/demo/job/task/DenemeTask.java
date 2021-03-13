package com.example.demo.job.task;

import java.io.IOException;

import org.bson.BsonArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

@Component
public class DenemeTask {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	private static final Logger logger = LoggerFactory.getLogger(DenemeTask.class);
	
	public void execute(){

		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder()
		  .url("https://api.triomobil.com/facility/v1/sensors?_sortBy=label&_sortOrder=ASC&_page=1&_perPage=20")
		  .get()
		  .addHeader("authorization", "971b8df0-444d-4115-b46d-7ee840ac97b6")
		  .build();
		
		try {			
			Response response = client.newCall(request).execute();
			String jsonData = response.body().string();
			BsonArray parse = BsonArray.parse(jsonData);
			BasicDBList dbList = new BasicDBList();
			dbList.addAll(parse);
			DBObject dbObject = dbList;
			mongoTemplate.insert(dbObject,"Deneme");					
		} catch (IOException e) {
			logger.error("Error scheduling SolutionCube job", e);
			e.printStackTrace();
		}	
	}
}
