# spring-boot-jar-resources

[![Join the chat at https://gitter.im/ulisesbocchio/spring-boot-jar-resources](https://badges.gitter.im/ulisesbocchio/spring-boot-jar-resources.svg)](https://gitter.im/ulisesbocchio/spring-boot-jar-resources?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
When using Spring Boot out of the box, resources from classpath are jarred, and while they can be accessed through input streams, they cannot be accessed as Files. Some libraries require Files as input instead of input streams or Spring Resources. This library deals whith that limitation by allowing you to do resource.getFile() on any jarred resource. It does so by extracting the file from the jar to a temporary location.

## How to use this library?

Simply add the following configuration to your Spring Boot app:

```java
new SpringApplicationBuilder()
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

Internally, this library simply wraps existing resources loaded by DefaultResourceLoader with a custom JarResource implementation that deals with the details of extracting the resource from the Jar.