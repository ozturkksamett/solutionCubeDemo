package com.example.demo.job;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.example.demo.dao.Deneme;
import com.example.demo.dao.DenemeRepository;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;


@Component
public class DenemeJob extends QuartzJobBean  {
	
	
	private static final Logger logger = LoggerFactory.getLogger(DenemeJob.class);

	@Autowired
	private DenemeRepository repository;



	@Override
	protected void executeInternal (JobExecutionContext jobExecutionContext) throws JobExecutionException {
		
		List<Deneme> denemeler = new ArrayList<Deneme>();
		
		OkHttpClient client = new OkHttpClient();

		Request request = new Request.Builder()
		  .url("https://api.triomobil.com/facility/v1/sensors?_sortBy=label&_sortOrder=ASC&_page=1&_perPage=20")
		  .get()
		  .addHeader("authorization", "971b8df0-444d-4115-b46d-7ee840ac97b6")
		  .build();
		Response response;
		String jsonData;
		
		try {
			response = client.newCall(request).execute();
			jsonData = response.body().string();
			JSONArray jsonArray = new JSONArray(jsonData);
			
			for (int i = 0; i < jsonArray.length(); i++)
			{
				Deneme deneme = new Deneme();
			    String post_id = jsonArray.getJSONObject(i).getString("_id");
			    deneme.setKolon1(post_id);
			    denemeler.add(deneme);
			    System.out.println(post_id);
			}
			System.out.println(jsonData);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		repository.saveAll(denemeler);
		
		logger.info("Executing Job with key {}", jobExecutionContext.getJobDetail().getKey());
		/*JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
		String subject = jobDataMap.getString("subject");*/

	}

}