## Traffic Simulation ##

A traffic simulation engine, developed using the MASON framework.

A map of the city can be passed as an xml file. Cars are generated using a sinusoidal function that maxes out at half the max number of cars.

See issues to see what's missing/planned for future releases.

To sample v1.0, download the SampleSim.xml and trafficSimUI.jar and run:
```
java -jar trafficSimUI.jar -city SampleSim.xml
```

You can create your own map following the schema trafficSimulation.xsd