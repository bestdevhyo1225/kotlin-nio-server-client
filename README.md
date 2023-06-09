# Client - Server

- Java NIO
- Kotlin

## Install Kotlin

```shell
brew install kotlin
```

## Build

> NioServer.kt

```shell
kotlinc src/main/kotlin/server/NioServer.kt -include-runtime -d NioServer.jar
```

> NioClient.kt

```shell
kotlinc src/main/kotlin/client/NioClient.kt -include-runtime -d NioClient.jar
```

## Execute

> NioServer.kt

```shell
kotlin NioServer.jar
```

or

```shell
java -jar NioServer.jar
```

> NioClient.kt

```shell
kotlin NioClient.jar
```

or

```shell
java -jar NioClient.jar
```
