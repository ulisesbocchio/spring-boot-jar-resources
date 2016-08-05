[![Build Status](https://travis-ci.org/ulisesbocchio/spring-boot-jar-resources.svg?branch=master)](https://travis-ci.org/ulisesbocchio/spring-boot-jar-resources)
[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/ulisesbocchio/spring-boot-jar-resources?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.ulisesbocchio/spring-boot-jar-resources/badge.svg?style=plastic)](https://maven-badges.herokuapp.com/maven-central/com.github.ulisesbocchio/spring-boot-jar-resources)

# spring-boot-jar-resources

When using Spring Boot out of the box, resources from classpath are jarred, and while they can be accessed through input streams, they cannot be accessed as Files. Some libraries require Files as input instead of input streams or Spring Resources. This library deals with that limitation by allowing you to do `resource.getFile()` on any jarred resource. It does so by extracting the files from the jar to a temporary location transparently to you.

## How to use this library?

Simply add the following dependency to your project:

```xml
<dependency>
	<groupId>com.github.ulisesbocchio</groupId>
	<artifactId>spring-boot-jar-resources</artifactId>
	<version>1.1</version>
</dependency>
```

And the following configuration to your Spring Boot app:

```java
new SpringApplicationBuilder()
            .sources(Application.class)
            .resourceLoader(new JarResourceLoader())
            .run(args);
```

Alternatively, provide a path to the JarResourceLoader where jarred resources will be extracted when accessed through a File handle.

```java
new SpringApplicationBuilder()
            .sources(Application.class)
            .resourceLoader(new JarResourceLoader("/path/to/extract"))
            .run(args);
```

If you want to expose the path to be configurable, since version `1.2` you can do this:

```java
public static void main(String[] args) {
        StandardEnvironment environment = new StandardEnvironment();
        new SpringApplicationBuilder()
            .sources(SpringBootJarResourcesDemoApplication.class)
            .environment(environment)
            .resourceLoader(new JarResourceLoader(environment, "resources.extract.dir"))
            .build()
            .run(args);
    }
```

With this you can run you `app.jar` this ways:

* `java -Dresources.extract.dir=/some/path -jar app.jar`
* `java -jar app.jar --resources.extract.dir=/some/path`
* `export RESOURCES_EXTRACT_DIR=/some/path && java -jar app.jar`

Or put `resources.extract.dir` in `application.properties`

Basically this new constructor takes the `environment` from which the property (with the name provided, i.e. `resources.extract.dir`) will be retrieved to get the extract directory.
Notice that we initialize a `StandardEnvironment` on the first line of the main method, that we also provide to the `SpringApplicationBuilder.environment(ConfigurableEnvironment)` method so that Spring can populate this object. That same environment is also passed as first argument to the `JarResourceLoader` constructor. This is required so that both Spring and the `JarResourceLoader` can share the same properties.

## Demo App

For more information and sample implementation check out the [Demmo App](https://github.com/ulisesbocchio/spring-boot-jar-resources-samples/tree/master/spring-boot-jar-resources-demo)

## How this library works?

Internally, this library simply wraps existing resources loaded by DefaultResourceLoader with a custom JarResource implementation that deals with the details of extracting the resource from the Jar. The implementation only extracts resources from jars if they need to be extracted, i.e. if actually being inside a jar. If for some reason, such as when running within an IDE or using an absolute path to load resources, the resources are not inside a jar, then the actual file is used instead.
 
