package com.example.measurer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import javax.json.Json;
import javax.json.JsonObject;

public class Measurement {

	final LocalDateTime timestamp;
	final String temperature;
	
	public Measurement(LocalDateTime timestamp, String temperature) {
		this.timestamp = timestamp;
		this.temperature = temperature;
	}
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	public String getTemperature() {
		return temperature;
	}
	
	public JsonObject toJson() {
		return Json.createObjectBuilder()
		.add("timestamp", timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))
		.add("temperature", temperature)
		.build();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		
		if(obj instanceof Measurement) {
			Measurement other = (Measurement) obj;
			return Objects.equals(timestamp, other.timestamp) && Objects.equals(temperature, other.temperature);
		}
		
		return false;
	}
}
