Proto to RSocket Reactive Interface
===================================

Generate Reactive service interfaces from services in the proto files.

For example, proto file as following:

```proto
syntax = "proto3";

option java_multiple_files = true;
option java_outer_classname = "AccountProto";

package org.mvnsearch.account;

import "google/protobuf/any.proto";
import "google/protobuf/wrappers.proto";

message Account {
    int32 id = 1;
    string email = 2;
    string phone = 3;
    int32 status = 4;
    string nick = 5;
}

service AccountService {

    rpc findById (google.protobuf.Int32Value) returns (Account);

    rpc findByStatus (google.protobuf.Int32Value) returns (stream Account);

    rpc findByIdStream (stream google.protobuf.Int32Value) returns (stream Account);
}
```

*Attention:* rpc declaration should be in one line and end with ';'.

generated Java Reactive interface:

```java
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import google.protobuf.Int32Value;

public interface AccountService {
  Mono<Account> findById (Int32Value int32Value);
  Flux<Account> findByStatus (Int32Value int32Value);
  Flux<Account> findByIdStream (Flux<Int32Value> int32ValueFlux);
}
```

### How to use it

* Use command line:

```
$ mvn org.mvnsearch:proto-rsocket-plugin:1.0.0-SNAPSHOT:proto2rsocket
```

* or add the plugin in your pom.xml

```xml
<plugin>
  <groupId>org.mvnsearch</groupId>
  <artifactId>proto-rsocket-plugin</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  <executions>
      <execution>
          <phase>generate-sources</phase>
          <goals>
              <goal>proto2rsocket</goal>
          </goals>
      </execution>
  </executions>
</plugin>
```

# References

* Language Guide (proto3): https://developers.google.cn/protocol-buffers/docs/proto3