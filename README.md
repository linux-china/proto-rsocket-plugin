Proto to RSocket Interface
==========================

Convert service in proto file to Reactive service interface.

For example, proto file as following:

```
syntax = "proto3";

... 

service AccountService {

    rpc findById (google.protobuf.Int32Value) returns (Account);

    rpc findByStatus (google.protobuf.Int32Value) returns (stream Account);

    rpc findByIdStream (stream google.protobuf.Int32Value) returns (stream Account);
}
```

generated Java Reactive interface:

```java
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import google.protobuf.Int32Value;

public interface AccountService {
  Mono<Account> findById (Mono<Int32Value> obj);
  Flux<Account> findByStatus (Mono<Int32Value> obj);
  Flux<Account> findByIdStream (Flux<Int32Value> obj);
}
```

### How to use it

* from command line:

```
$ mvn org.mvnsearch:proto-rsocket-plugin:1.0.0-SNAPSHOT:proto2rsocket
```

* add plugin in your pom.xml

```xml
<plugin>
  <groupId>org.mvnsearch</groupId>
  <artifactId>proto-rsocket-plugin</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  <executions>
      <execution>
          <phase>compile</phase>
          <goals>
              <goal>proto2rsocket</goal>
          </goals>
      </execution>
  </executions>
</plugin>
```


# References

* Language Guide (proto3): https://developers.google.cn/protocol-buffers/docs/proto3