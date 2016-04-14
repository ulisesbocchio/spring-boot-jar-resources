[![Build Status](https://travis-ci.org/ulisesbocchio/spring-boot-jar-resources.svg?branch=master)](https://travis-ci.org/ulisesbocchio/spring-boot-jar-resources)
[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/ulisesbocchio/spring-boot-jar-resources?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.ulisesbocchio/spring-boot-jar-resources/badge.svg?style=plastic)](https://maven-badges.herokuapp.com/maven-central/com.github.ulisesbocchio/spring-boot-jar-resources)

# spring-boot-jar-resources
When using Spring Boot out of the box, resources from classpath are jarred, and while they can be accessed through input streams, they cannot be accessed as Files. Some libraries require Files as input instead of input streams or Spring Resources. This library deals whith that limitation by allowing you to do resource.getFile() on any jarred resource. It does so by extracting the file from the jar to a temporary location.

## How to use this library?

Simply add the following dependency to your project:

```xml
<dependency>
	<groupId>com.github.ulisesbocchio</groupId>
	<artifactId>spring-boot-jar-resources</artifactId>
	<version>1.0</version>
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
            .resourceLoader(new JarResourceLoader("/path/to/extract"))
            .run(args);
```

## How this library works?

Internally, this library simply wraps existing resources loaded by DefaultResourceLoader with a custom JarResource implementation that deals with the details of extracting the resource from the Jar. The implementation only extracts resources from jars if they need to be extracted, i.e. if actually being inside a jar. If for some reason, such as when running within an IDE or using an absolute path to load resources, the resources are not inside a jar, then the actual file is used instead.

## 