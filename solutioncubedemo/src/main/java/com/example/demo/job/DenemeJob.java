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
	
	/*
	 * todo 40 tane repository tanımı olacak
	 * 40 tane dto<list> tanımı olacak
	 * com.eample.demo.dao içerisnde 40 tane dto tanımı 40 tane repository tanımı yapılacak eklenecek
	 */

	@Autowired
	DenemeSC denemeSC;

	@Override
	protected void executeInternal (JobExecutionContext jobExecutionContext) throws JobExecutionException {
		
		//todo 40 tane rest çağrımı yapılacak (40 method çağrılacak)
		denemeSC.execute();
		
	}

}