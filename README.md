# props-to-constants-generator

# Introduction

`props-to-constants-generator` generates a constants class or enum of the keys in your properties file. This enables you to have compile time dependency of keys on a properties file.

Tehnically, `props-to-constants-generator` is an Java annotation processor that processes `PropertySourceConstants` annotations specifying which properties file you wish to generate constants from, the output class name and the style of output. For every `PropertySourceConstants` annotation, it reads a specified properties file and outputs a constants class or enum containing the property keys.

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

Configure the compiler plugin in your `pom.xml`:

```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-compiler-plugin</artifactId>
      <configuration>
        <annotationProcessorPaths>
          <path>
            <groupId>com.github.kklisura.java.processing</groupId>
            <artifactId>props-to-constants-generator</artifactId>
            <version>1.0.0</version>
          </path>
        </annotationProcessorPaths>
      </configuration>
    </plugin>
  </plugins>
</build>
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
When you compile the application like you normally do, the `Messages_` class will be generated in the `target/generated-sources/annotations/...` directory.
The generated class is created in the annotated class's package, unless you fully qualify the `className` attribute.

## Style

By default the output style is a class containing constants, however you can specify `enum` as the output style to generate an enum so you can
create type-safe methods relating to your property keys.

```java
@PropertySourceConstants(resourceName = "messages.properties", className = "Messages_", style = Style.ENUM)
public class Application {
  public static void main(String[] args) {
    // ...
  }
}
```

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

With `Style.ENUM` the following enum class is created:

```java
public enum Messages_ {
  HELLO("hello"),
  HELLO_WORLD("hello.world"),
  ;

  private String key;

  private Messages_(String key) { this.key = key; }

  public String getKey() { return this.key; }

  public String fromBundle(java.util.ResourceBundle resourceBundle) { return resourceBundle.getString(this.key); }
}
```

# License

MIT
