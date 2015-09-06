# guice-properties-el [![Build Status](https://travis-ci.org/lukaszbudnik/guice-properties-el.svg)](https://travis-ci.org/lukaszbudnik/guice-properties-el)
Expression Language in Guice Named properties

# Usage

guice-properties-el supports Expression Language 2.1 syntax (variables, method calls, conditions, etc.).
By default guice-properties-el provides `env` and `properties` variables.

## Properties file

Here's a simple properties `test.properties` file:


```
# env == System.getenv()
test.env.home=${env['HOME']}
# properties == System.getProperties()
test.properties.javaVersion=${properties['java.version']}
# EL 2.1 features like method calls and condition:
test.eval=${env.containsKey('TRAVIS') ? 'on travis' : 'locally'}
```

## Java model

The above properties are mapped using `@Named` and `@Inject` annotations:

```
public class TestModel {

    @Inject
    @Named("test.env.home")
    private String testEnvHome;

    @Inject
    @Named("test.properties.javaVersion")
    private String testPropertiesJavaVersion;

    @Inject
    @Named("test.eval")
    private String testEval;

}
```

## Injector

Finally, create the injector and get the instance:

```
Injector injector = Guice.createInjector(new PropertiesElResolverModule("/test.properties"));
TestModel testModel = injector.getInstance(TestModel.class);
```

# Examples

See `src/test/java` for lots of unit tests and examples.

# Download

Use the following Maven dependency:

```xml
<dependency>
  <groupId>com.github.lukaszbudnik.guice-properties-el</groupId>
  <artifactId>guice-properties-el</artifactId>
  <version>{version}</version>
</dependency>
```

# License

Copyright 2015 Łukasz Budnik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   <http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.