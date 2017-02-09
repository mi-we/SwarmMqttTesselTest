package com.example.measurer;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
class MeasurementsRepository {
	
	final Set<Measurement> measurements = new HashSet<>(); 
	
	@PostConstruct
	private void initialize() {
		measurements.add(new Measurement(LocalDateTime.now(), "23.4"));
	}
	
	public void push(Measurement measurement) {
		measurements.add(measurement);
	}
	
	public Set<Measurement> getAll() {
		return Collections.unmodifiableSet(measurements);
	}
}
