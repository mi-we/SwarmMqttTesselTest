var Tessel = require('tessel-export');
var climatelib = require("climate-si7020");
var mqtt = require('mqtt');

var tessel = new Tessel({
  ports: {
    A: true,
    B: false
  }
});
var climate = climatelib.use(tessel.port['A']);

climate.on('ready', function () {
  climate.setHeater(false, function(err) {
    if (err) console.log("Unable to turn of heater elemnt on climate module!", err);
  });

  setInterval(function () {
    climate.readHumidity(function (err, humid) {
      climate.readTemperature('c', function (err, temp) {
        console.log('Degrees:', temp.toFixed(4) + 'F', 'Humidity:', humid.toFixed(4) + '%RH');

        var data = {
          temperature: temp.toFixed(2)
        };

        console.log("Connecting to broker...");
        var client = mqtt.connect("mqtt://192.168.1.51", {
          connectTimeout: 5000
        });

        client.on('connect', function () {
          console.log("successfully connected to broker!");

          client.publish("tessie/measurements/temperature", JSON.stringify(data), {}, function (err) {
            if (err) {
              console.log("Error while pushing message to broker: ", err);
            } else {
              console.log("Successfully pushed message!");
              client.end();
            }
          });
        });

        client.on('error', function (err) {
          console.log("Something bad happened: ", err);
        });

        client.on('close', function() {
          console.log("Closed connection to broker.");
        });

        client.on('offline', function () {
          console.log("Client went offline!");
        });

      });
    });
  }, 60000);
});