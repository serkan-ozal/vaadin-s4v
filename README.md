What is vaadin-s4v?
==============

**vaadin-s4v** is a **Java Instrumentation API** based integration framework to connect Vaddin UI components with beans of Spring context.

Usage
=======

To enable Vaadin and Spring integration automatically, there are three ways:

1. Import `context.xml` of **vaadin-s4v** in your `context.xml`.

~~~~~ xml
...

<import resource="classpath*:s4v-context.xml"/>

...
~~~~~

or

2. Automatically scan Spring beans of **vaadin-s4v** in your `context.xml`.

~~~~~ xml
...

<context:component-scan base-package="com.vaadin.s4v"/>

...
~~~~~

or

3. Call explicitly integrating method at startup anywhere of your application.

~~~~~ java
...

com.vaadin.s4v.SpringIntegrator.integrateVaadinWithSpring();

...
~~~~~

And in any `com.vaadin.ui.Component` typed class, you can easily inject Spring beans in traditional way by using `@Autowired` annotation.

For example:

~~~~~ java
...

public class MyView extends VerticalLayout {

	@Autowired
	private SpringBean springBean;
	
	...
	
}	

...
~~~~~

Installation
=======

In your `pom.xml`, you must add repository and dependency for **vaadin-s4v**. 
You can change `vaadin.s4v.version` to any existing **vaadin-s4v** library version.
Latest version is `1.0.0-SNAPSHOT`.

~~~~~ xml
...
<properties>
    ...
    <vaadin.s4v.version>1.0.0-SNAPSHOT</vaadin.s4v.version>
    ...
</properties>
...
<dependencies>
    ...
	<dependency>
		<groupId>com.vaadin</groupId>
		<artifactId>vaadin-s4v</artifactId>
		<version>${vaadin.s4v.version}</version>
	</dependency>
	...
</dependencies>
...
<repositories>
	...
	<repository>
		<id>serkanozal-maven-repository</id>
		<url>https://github.com/serkan-ozal/maven-repository/raw/master/</url>
	</repository>
	...
</repositories>
...
~~~~~
