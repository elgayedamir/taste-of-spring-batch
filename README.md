# Political Speeches Statistics

## Objective 
Get familiar with Spring Batch core concepts, patterns and architecture.

## Overview
This application exposes a rest end-point that accepts one or more CSV file URLs containing data about political speeches (All files have the same data format).
After loading the files, statistics about the political speeches are extracted and returned as response.
For more details, check [Requirements.md](./Requirements.md)


## Build

Running the following maven command builds the spring boot application and generates an uber JAR

```
mvn clean package
```

## Run
* Using Maven
  
```
spring-boot:run
```

* Running JAR after running build command (form <b>Build</b> section)

```
java -jar <PATH_TO>/political-speeches-statistics.jar
```