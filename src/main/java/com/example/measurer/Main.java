package com.example.measurer;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.jaxrs.JAXRSArchive;

public class Main {

	public static void main(String[] args) throws Exception {
		Swarm swarm = new Swarm();

		JAXRSArchive deployment = ShrinkWrap.create(JAXRSArchive.class);
		deployment
		.addPackage(MeasurementsResource.class.getPackage())
		.addDependency("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.0.2");
		
		swarm.start();
		swarm.deploy(deployment);
	}

}
