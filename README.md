# A simple exercise in Google Protocol Buffers.

Here, we want to define serialization for "packets" exchanged between a client and a server, whereby the client
wants to log in to the server. I tried to write this by hand, which is feasible but annoying and probably will be
beset by "that one case one didn't think about", so why not use [Google protocol buffers](https://protobuf.dev/) ("protobufs") to
perform all of the serialization/deserialization (to a compact binary representation, which is nice).

Actually "going out to the network" is not performed in this project. This is only about testing serialization
and deserialization of data packets. And getting everything to work in Intellij IDEA.

- This is a standard Maven project, take a look at the [POM](pom.xml)
- There is a single `.proto` definition file (for now) from which protobuf source is generated: [packets.proto](src/main/protobuf/packets.proto)

Relevant links to documentation:

- [Google protobufs on GitHub](https://github.com/protocolbuffers/protobuf)
- [Google protobufs documentation](https://protobuf.dev/)
   - [Getting started](https://protobuf.dev/getting-started/)
- [Setting the project up in Intellij IDEA: "How to Work With Protobuf-Maven Projects in IntelliJ IDEA"](https://blog.jetbrains.com/idea/2023/05/how-to-work-with-protobuf-maven-projects-in-intellij-idea/)
- [The protobuf Maven artifact at Maven Repository](https://mvnrepository.com/artifact/com.google.protobuf/protobuf-java)

An interesting problem is that jsut "compiling" the project in IntellijIDEA via the corresponding menu entry does
not generate compilable Google protobuf source code. One has to execute "Run Maven > compile" through the dropdown menu for example.

The file tree of the project looks as follows:

![File tree of project](docs/protobuf_trial.png)

Finding a proper "POM" is somewhat black magic, but in the end it worked out. Pointed questions to AI 🦜 helped a lot!

The depencies pulled in via Maven are as follows. This tree can be obtained in IntelliJ IDEA under `View > Tool Windows > Maven`,
(which does not always work) or by running `mv dependency:tree` in the project directory:

```
[INFO] --- dependency:3.7.0:tree (default-cli) @ protobuf_trial ---
[INFO] name.pomelo:protobuf_trial:jar:1.1-SNAPSHOT
[INFO] +- org.jetbrains:annotations:jar:24.1.0:compile
[INFO] +- org.projectlombok:lombok:jar:1.18.42:provided
[INFO] +- com.google.protobuf:protobuf-java:jar:4.33.5:compile
[INFO] +- com.google.protobuf:protobuf-java-util:jar:4.33.5:compile
[INFO] |  +- com.google.code.findbugs:jsr305:jar:3.0.2:runtime
[INFO] |  +- com.google.code.gson:gson:jar:2.8.9:runtime
[INFO] |  \- com.google.errorprone:error_prone_annotations:jar:2.18.0:runtime
[INFO] +- org.slf4j:slf4j-api:jar:2.0.17:compile
[INFO] +- ch.qos.logback:logback-classic:jar:1.5.32:runtime
[INFO] |  \- ch.qos.logback:logback-core:jar:1.5.32:runtime
[INFO] +- org.junit.jupiter:junit-jupiter-api:jar:6.0.3:test
[INFO] |  +- org.opentest4j:opentest4j:jar:1.3.0:test
[INFO] |  +- org.junit.platform:junit-platform-commons:jar:6.0.3:test
[INFO] |  +- org.apiguardian:apiguardian-api:jar:1.1.2:test
[INFO] |  \- org.jspecify:jspecify:jar:1.0.0:test
[INFO] \- com.google.truth:truth:jar:1.4.5:test
[INFO]    +- com.google.guava:guava:jar:33.4.3-android:test
[INFO]    |  +- com.google.guava:failureaccess:jar:1.0.2:test
[INFO]    |  +- com.google.guava:listenablefuture:jar:9999.0-empty-to-avoid-conflict-with-guava:test
[INFO]    |  +- org.checkerframework:checker-qual:jar:3.43.0:test
[INFO]    |  \- com.google.j2objc:j2objc-annotations:jar:3.0.0:test
[INFO]    +- junit:junit:jar:4.13.2:test
[INFO]    |  \- org.hamcrest:hamcrest-core:jar:1.3:test
[INFO]    +- com.google.auto.value:auto-value-annotations:jar:1.11.0:test
[INFO]    \- org.ow2.asm:asm:jar:9.8:test
```

![Dependency tree](docs/maven_dependency_tree.png)

