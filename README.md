tomcat-valves
=============

Custom Tomcat valves to help debug a few special cases in Tomcat applications.

SessionSerializableCheckerValve
-------------------------------
This valve will check all session attributes on each request and log a warning
if one of the attribute is not serializable. We check that the object's class
implements `java.io.Serializable` and that it can be written to an
`ObjectOutputStream`.

To use it, add the following line in your server.xml, as part of an Engine,
Host or Context:

    <valve classname="ch.ledcom.tomcat.valves.SessionSerializableCheckerValve">

SessionSizeValve
----------------
This valve will print the size of each session.

To use it, add the following line in your server.xml, as part of an Engine,
Host or Context :

    <valve classname="ch.ledcom.tomcat.valves.SessionSizeValve">

RequestAllocationRecorderValve
------------------------------
This valve will print the amount of memory allocated by each HTTP request.

The following system properties can be used to further configure it :

<dl>
  <dt>`ch.ledcom.tomcat.valves.allocation.RequestAllocationRecorderValve.disabled`</dt>
  <dd>Disable the valve</dd>
  <dt>`ch.ledcom.tomcat.valves.allocation.RequestAllocationRecorderValve.printSummary`</dt>
  <dd>Will also print a summary of the allocation for all requests</dd>
  <dt>`ch.ledcom.tomcat.valves.allocation.RequestAllocationRecorderValve.printSummary.period`</dt>
  <dd>Print the summary for one in `period` requests</dd>
</dl>

AllocationAdvice
----------------
Can be used with Spring AOP to record allocation in part of your code. See code for details.

[![Build Status](https://travis-ci.org/gehel/tomcat-valves.svg)](https://travis-ci.org/gehel/tomcat-valves)
[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/gehel/tomcat-valves/trend.png)](https://bitdeli.com/free "Bitdeli Badge")
