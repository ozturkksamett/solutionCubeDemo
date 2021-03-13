package com.example.demo.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.example.demo.job.task.DenemeTask;

@Component
public class DenemeJob extends QuartzJobBean  {
	
	@Autowired
	DenemeTask denemeTask;

	@Override
	protected void executeInternal (JobExecutionContext jobExecutionContext) throws JobExecutionException {
		
		denemeTask.execute();		
	}

}