package com.example.measurer;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/measurements")
public class MeasurementsResource {

	@Inject
	private MeasurementsRepository repository;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response doGet() {
		JsonArrayBuilder measurements = Json.createArrayBuilder();
		repository.getAll().stream().map(Measurement::toJson).forEach(measurements::add);
		return Response.ok(measurements.build()).build();
	}
}