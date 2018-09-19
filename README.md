Rythm Template Engine
======================

A "Razor" like, rich featured, high performance and easy to use Java template engine


## Rythm Engine Project ##
[![Join the chat at https://gitter.im/greenlaw110/Rythm](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/greenlaw110/Rythm?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

**We are looking for people to join us on this project. Please contact Green Luo via greenlaw110@gmail.com**

## Links ##

* The [documentation](http://rythmengine.org/doc/index)
* The [fiddle](http://fiddle.rythmengine.org/) site
* The [project info](https://rythmengine.github.io/rythmengine/project-info.html)
* The [JavaDocs](https://rythmengine.github.io/rythmengine/apidocs/index.html)

## Integrations ##

* [play-rythm](https://github.com/greenlaw110/play-rythm) for Play!Framework 1.x. And the [document](http://www.playframework.com/modules/rythm-1.0.0-20121210/home) for play-rythm
* [spring-rythm](https://github.com/greenlaw110/spring-rythm) for Spring 3.x, and the [samples](https://github.com/greenlaw110/spring-rythm-samples) for learning how to use spring-rythm
* [jfinal-rythm](https://github.com/greenlaw110/jfinal-rythm) for [JFinal](http://www.jfinal.com/) and the [sample](https://github.com/greenlaw110/jfinal-bbs) to learn how to use jfinal-rythm
* [ninja-rythm](https://github.com/ninjaframework/ninja-rythm) for [NinjaFramework](http://www.ninjaframework.org/)
* [Profiwiki](http://www.profiwiki.de) - allows Rythm templates to be integrated in MediaWiki pages 

## Prerequisites ##
Java JDK >= Version 7

## Distribution ##
Available at Maven Central see 

http://mvnrepository.com/artifact/org.rythmengine/rythm-engine

Maven dependency:

```xml
<!-- http://mvnrepository.com/artifact/org.rythmengine/rythm-engine -->
<dependency>
    <groupId>org.rythmengine</groupId>
    <artifactId>rythm-engine</artifactId>
    <version>1.3.0</version>
</dependency>
```

### How to build
```
git clone https://github.com/rythmengine/rythmengine
cd rythmengine
mvn install
```

if some of the tests should fail you might want to file an issue and try
```
mvn install -DskipTests=true
```

### Testing
```
mvn test
...
Tests run: 254, Failures: 0, Errors: 0, Skipped: 3
```

## Development ##
There are several options for development environments you can use to contribute to the development of
the Rythm Engine Project:
* Pure Maven command line https://maven.apache.org/
* Eclipse https://eclipse.org/
* IntelliJ IDEA https://www.jetbrains.com/idea/

## Version history

See the [change log](CHANGELOG.md)
