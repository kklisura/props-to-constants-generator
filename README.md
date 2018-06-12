# props-to-constants-generator

# Introduction

`props-to-constants-generator` generates a constants class of keys in your properties file. This enables you to have compile time dependency of keys on a properties file.

Tehnically, `props-to-constants-generator` is an Java annotation processor that processes `PropertySourceConstants` annotations which contain information from which properties file you wish to generate constants and output constants class name. For every `PropertySourceConstants` annotation, it reads a specified properties file and outputs the class containing constants of property keys.

# How to use

Add the following depencency to your `pom.xml`:
```xml
<dependency>
  <groupId>com.github.kklisura.java.processing</groupId>
  <artifactId>props-to-constants-generator</artifactId>
  <version>1.0.0</version>
  <scope>provided</scope>
</dependency>
```

Add `PropertySourceConstants` annotation:

```java
@PropertySourceConstants(resourceName = "messages.properties", className = "Messages_")
public class Application {
  public static void main(String[] args) {
    // ...
  }
}
```
When you compile the application like you normaly do, `Messages_` class should be generated in `target/generated-sources/annotations/...` directory.

# Example

Given the following example properties file:

```properties
hello=123
hello.world=321
```

...and the above configuration, the following constants class would be created:

```java
public final class Messages_ {
  public static final String HELLO = "hello";
  public static final String HELLO_WORLD = "hello.world";

  private Messages_() {}
}
```

# License

MIT
