package com.example.demo.controller;

import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import javax.validation.Valid;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.job.DenemeJob;
import com.example.demo.payload.ScheculedDenemeRequest;
import com.example.demo.payload.ScheculedDenemeResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class DenemeController {
	
	@Autowired
	private Scheduler scheduler;

	private static final Logger logger = LoggerFactory.getLogger(DenemeController.class);

	@RequestMapping("/")
	public String home() {
		return "Hello World!";
	}
	
	@PostMapping("/scheduleDeneme")
	public ResponseEntity<ScheculedDenemeResponse> scheduleDeneme(@Valid @RequestBody ScheculedDenemeRequest scheculedDenemeRequest) {

		try {

			Calendar c = Calendar.getInstance();
			TimeZone tz = c.getTimeZone();
			ZonedDateTime dateTime = ZonedDateTime.of(scheculedDenemeRequest.getDateTime(), tz.toZoneId());			

			if (dateTime.isBefore(ZonedDateTime.now())) {
				ScheculedDenemeResponse sheculedDenemeResponse = new ScheculedDenemeResponse(false,
						"dateTime must be after current time");
				return ResponseEntity.badRequest().body(sheculedDenemeResponse);
			}
			
			int interval = scheculedDenemeRequest.getScheculeInterval();
			
			JobDetail jobDetail = buildJobDetail(scheculedDenemeRequest);
			
			Trigger trigger = buildJobTrigger(jobDetail, dateTime, interval);
			
			scheduler.scheduleJob(jobDetail, trigger);
			
			ScheculedDenemeResponse scheculedDenemeResponse = new ScheculedDenemeResponse(true,
					jobDetail.getKey().getName(), jobDetail.getKey().getGroup(),
					"SolutionCube job started Successfully!");
			return ResponseEntity.ok(scheculedDenemeResponse);
		} catch (SchedulerException ex) {
			logger.error("Error scheduling SolutionCube job", ex);
			ScheculedDenemeResponse scheculedDenemeResponse = new ScheculedDenemeResponse(false, "Error scheduling email. Please try later!");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(scheculedDenemeResponse);
		}
	}

	private JobDetail buildJobDetail(ScheculedDenemeRequest scheculedDenemeRequest) {
		JobDataMap jobDataMap = new JobDataMap();
		/*
		 * jobDataMap.put("email", scheduleEmailRequest.getEmail());
		 * jobDataMap.put("subject", scheduleEmailRequest.getSubject());
		 * jobDataMap.put("body", scheduleEmailRequest.getBody());
		 */
		return JobBuilder.newJob(DenemeJob.class).withIdentity(UUID.randomUUID().toString(), "Solutioncube")
				.withDescription("Insert data into solutioncube mongodb").usingJobData(jobDataMap).storeDurably().build();
	}

	private Trigger buildJobTrigger(JobDetail jobDetail, ZonedDateTime startAt, int interval) {
		return TriggerBuilder.newTrigger().forJob(jobDetail)
				.withIdentity(jobDetail.getKey().getName(), "SolutionCube insert Data")
				.withDescription("SolutionCube tablolarını besler").startAt(Date.from(startAt.toInstant()))
				.withSchedule(SimpleScheduleBuilder.repeatHourlyForever(24)).build();
	}

}
