package com.example.demo.payload;

import java.time.LocalDateTime;
import java.time.ZoneId;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sun.istack.NotNull;

	
public class ScheculedDenemeRequest {
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS][.SS][.S]")
	@NotNull
	private LocalDateTime dateTime;
	
	@NotNull
	private int scheculeInterval;


	public LocalDateTime getDateTime() {
		return dateTime;
	}
	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}
	public int getScheculeInterval() {
		return scheculeInterval;
	}
	public void setScheculeInterval(int scheculeInterval) {
		this.scheculeInterval = scheculeInterval;
	}

}