# util4j
[![Version](https://img.shields.io/badge/version-4.1.1-brightgreen.svg)](http://search.maven.org/#search|gav|1|g:%22net.jueb%22%20AND%20a:%22util4j%22)
[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![JDK 1.8](https://img.shields.io/badge/JDK-1.8-green.svg "JDK 1.8")]()

## 简介
用于服务端开发的常用工具包,来源于平时游戏服务端开发所用到的公共组件,经过稳定的上线测试。

## 提供组件
* netty网络客户端服务端
* 缓存
* CSP模型队列执行器(采用jctools实现高吞吐的任务处理)
* 热更新框架
* 字节操作工具
* 加密解密

### Maven

```xml
<dependency>
    <groupId>net.jueb</groupId>
    <artifactId>util4j</artifactId>
    <version>VERSION</version>
</dependency>
```

### Gradle
```
compile 'net.jueb:util4j-all:VERSION'
```
