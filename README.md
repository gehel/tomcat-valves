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
This valve will print the size of each session. It uses a bundled version of
[memory-measurer][memory-measurer]

To use it, add the following line in your server.xml, as part of an Engine,
Host or Context :

    <valve classname="ch.ledcom.tomcat.valves.SessionSizeValve">

You will also need to configure tomcat-valves as a java agent, by adding the
following to your startup script:

    -javaagent:path/to/tomcat-valves-<version>.jar

Notes
-----
Code in the `ch.ledcom.tomcat.valves.objectexplorer`package is taken from
[memory-measurer][memory-measurer].

[memory-measurer]: https://code.google.com/p/memory-measurer/
