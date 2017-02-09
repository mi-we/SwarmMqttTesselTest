package com.example.measurer;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.stream.JsonParsingException;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.jboss.logging.Logger;

import com.sun.mail.iap.ByteArray;

@ApplicationScoped
public class MqttProvider {

	private final class MqttCallbackHandler implements MqttCallback {

		private MeasurementsRepository repository;

		public MqttCallbackHandler(MeasurementsRepository repository) {
			this.repository = repository;
		}

		@Override
		public void messageArrived(String topic, MqttMessage message) throws Exception {
			byte[] payload = message.getPayload();
			String decodedPayload = new String(payload, Charset.forName("UTF-8"));
			logger.info("Received message: " + decodedPayload);

			try {
				JsonObject jsonPayload = Json.createReader(new StringReader(decodedPayload)).readObject();
				String temperature = jsonPayload.getJsonString("temperature").getString();
				if(temperature == null) {
					logger.warn("JSON does not contain property for temperature, no message persisted");
				} else {
					repository.push(new Measurement(LocalDateTime.now(), temperature));
				}
			} catch (JsonParsingException e) {
				logger.warn("JSON could not be parsed from message", e);
			}
		}

		@Override
		public void deliveryComplete(IMqttDeliveryToken token) {
			// Not implemented
		}

		@Override
		public void connectionLost(Throwable cause) {
			logger.error("Connection to MQTT broker lost!", cause);
		}
	}

	@Inject
	private MeasurementsRepository repository;

	private Logger logger = Logger.getLogger(MqttProvider.class);

	public void startUp(@Observes @Initialized(ApplicationScoped.class) Object init) throws MqttException {
		String broker = "tcp://192.168.1.51:1883";
		String clientId = "measurer";
		try {
			MqttClient client = new MqttClient(broker, clientId);
			MqttConnectOptions options = new MqttConnectOptions();
			options.setCleanSession(true);
			client.setCallback(new MqttCallbackHandler(repository));
			logger.info("Connecting with MQTT broker at " + broker);
			client.connect(options);
			client.subscribe("tessie/measurements/temperature");
		} catch (MqttException e) {
			logger.fatal("Unable to connect to MQTT broker!", e);
		}

	}
}
