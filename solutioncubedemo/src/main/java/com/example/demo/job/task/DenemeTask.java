package com.example.demo.job.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.job.Task;

@Component
public class DenemeTask {

    @Autowired
	private Task task;
    
    private final String COLLECTION_NAME = this.getClass().getName().substring(0, this.getClass().getName().length() - 3); 
    
	public void execute() {
		
		String uri = "https://api.triomobil.com/facility/v1/sensors?_sortBy=label&_sortOrder=ASC&_page=1&_perPage=20";
		
		task.execute(uri, COLLECTION_NAME);
	}
}
