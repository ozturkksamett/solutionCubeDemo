package com.example.demo.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.example.demo.job.task.SensorsTask;

@Component
public class DailyJob extends QuartzJobBean  {
	
	@Autowired
	JobParameter jobParameter;

	@Autowired
	SensorsTask sensorsTask;

	@Override
	protected void executeInternal (JobExecutionContext jobExecutionContext) throws JobExecutionException {

		jobParameter.generateJobParameter();
		
		sensorsTask.executeDaily();
	}
}