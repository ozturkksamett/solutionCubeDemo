package com.example.demo.controller;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import javax.validation.Valid;

import org.json.JSONArray;
import org.json.JSONObject;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dao.Deneme;
import com.example.demo.dao.DenemeRepository;
import com.example.demo.exception.DenemeNotFoundException;
import com.example.demo.job.DenemeJob;
import com.example.demo.payload.ScheculedDenemeRequest;
import com.example.demo.payload.ScheculedDenemeResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class DenemeController {

	@Autowired
	private final DenemeRepository repository;
	@Autowired
	private Scheduler scheduler;
	private static final Logger logger = LoggerFactory.getLogger(DenemeController.class);

	DenemeController(DenemeRepository repository) {
		this.repository = repository;
	}

	@RequestMapping("/")
	public String home() {
		return "Hello World!";
	}

	@RequestMapping("/ekle")
	public void addNewValue() throws UnirestException, IOException {
		List<Deneme> denemeler = new ArrayList<Deneme>();

		OkHttpClient client = new OkHttpClient();

		Request request = new Request.Builder()
				.url("https://api.triomobil.com/facility/v1/sensors?_sortBy=label&_sortOrder=ASC&_page=1&_perPage=20")
				.get().addHeader("authorization", "971b8df0-444d-4115-b46d-7ee840ac97b6").build();
		Response response = client.newCall(request).execute();

		String jsonData = response.body().string();
		System.out.println(jsonData);

		JSONArray jsonArray = new JSONArray(jsonData);

		for (int i = 0; i < jsonArray.length(); i++) {
			Deneme deneme = new Deneme();
			String post_id = jsonArray.getJSONObject(i).getString("_id");
			deneme.setKolon1(post_id);
			denemeler.add(deneme);
			System.out.println(post_id);
		}

		repository.saveAll(denemeler);

	}

	@GetMapping("/denemeler")
	List<Deneme> all() {
		return repository.findAll();
	}

	@PostMapping("/denemeler")
	Deneme newDeneme(@RequestBody Deneme newDeneme) {
		return repository.save(newDeneme);
	}

	@GetMapping("/denemeler/{id}")
	Deneme one(@PathVariable Long id) {

		return repository.findById(id).orElseThrow(() -> new DenemeNotFoundException(id));
	}

	@PutMapping("/denemeler/{id}")
	Deneme replaceDeneme(@RequestBody Deneme newDeneme, @PathVariable Long id) {

		return repository.findById(id).map(deneme -> {
			deneme.setIddeneme(newDeneme.getIddeneme());
			deneme.setKolon1(newDeneme.getKolon1());
			return repository.save(deneme);
		}).orElseGet(() -> {
			newDeneme.setIddeneme(id);
			return repository.save(newDeneme);
		});
	}

	@DeleteMapping("/denemeler/{id}")
	void deleteDeneme(@PathVariable Long id) {
		repository.deleteById(id);
	}

	@PostMapping("/scheduleDeneme")
	public ResponseEntity<ScheculedDenemeResponse> scheduleDeneme(
			@Valid @RequestBody ScheculedDenemeRequest scheculedDenemeRequest) {

		Calendar c = Calendar.getInstance();

		System.out.println("datetime alındı" + scheculedDenemeRequest.getDateTime());

		TimeZone tz = c.getTimeZone();

		try {
			System.out.println("ZoneId alındı" + tz.toZoneId());

			ZonedDateTime dateTime = ZonedDateTime.of(scheculedDenemeRequest.getDateTime(), tz.toZoneId());
			System.out.println("dateTime " + dateTime);

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
					jobDetail.getKey().getName(), jobDetail.getKey().getGroup(), "SolutionCube job started Successfully!");
			return ResponseEntity.ok(scheculedDenemeResponse);
		} catch (SchedulerException ex) {
			logger.error("Error scheduling email", ex);
			ScheculedDenemeResponse scheculedDenemeResponse = new ScheculedDenemeResponse(false,
					"Error scheduling email. Please try later!");
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
		return JobBuilder.newJob(DenemeJob.class).withIdentity(UUID.randomUUID().toString(), "email-jobs")
				.withDescription("Send Email Job").usingJobData(jobDataMap).storeDurably().build();
	}

	private Trigger buildJobTrigger(JobDetail jobDetail, ZonedDateTime startAt, int interval) {
		return TriggerBuilder.newTrigger().forJob(jobDetail)
				.withIdentity(jobDetail.getKey().getName(), "SolutionCube insert Data").withDescription("SolutionCube tablolarını besler")
				.startAt(Date.from(startAt.toInstant()))
				.withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatHourlyForever(24)).build();
	}

}
