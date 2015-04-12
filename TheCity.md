# Introduction #

The city is the medium agents use to interact with each other. An explanation of that medium
follows below.

# The City #

The city is made up of two elements: Streets and crossings. Streets connect with each other in crossings. In this simulation engine, a street is analogous to an edge, that is it only a segment that connects two crossings--vertices.

These elements have the following properties:
  * **Streets** have a max speed, length and orientation (North-South or East-West). They are aware of how many _cars_ are on them, and can be filled up if their length is covered by cars. When a street is filled, no cars can go in, unless some go out. Currently, only one lane per street is supported

  * **Crossings** can have _traffic lights_. Each crossing may have one _traffic lights_.

# The Agents #

There are two types of agents at play in a simulation: _cars_ and _traffic lights_:

  * **Cars** have a max speed. They are created at _crossings_ and travel through the length of _streets_ at constant time. Each car has a inmutable path calculated using Dijsktra's shortest path algorithm, at creation time, based on a random pick of start and end nodes. A car may not start and end at the same node. Finally, cars can move if no other cars block their path, or, if when at the end of a street, the light is not red.

  * **Traffic Lights** have 2 lights: North-South and East-West. Each light directs it's respective street orientation.