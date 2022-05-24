# myproject - rpc -framework

## 项目介绍

RPC 的全称是 Remote Procedure Call，即远程过程调用。是帮助我们屏蔽网络编程细节，实现调用远程方法就跟调用本地（同一个项目中的方法）一样的体验，我们不需要因为这个方法是远程调用就需要编写很多与业务无关的代码。

本项目是在参考、学习有关rpc框架的资料后，写出来的一个简易的rpc框架。该框架的主要功能就是实现方法的远程调用。





## 框架介绍

![](D:\面试阶段\面试准备文档\myproject-capture\框架结构图.png)



### 整体的流程

​      先由服务器向注册中心发起服务注册，然后由客户端向注册中心发起服务发现，根据发现的地址去调用对应地址的远程服务器上的方法，并返回结果。



### 实现一个RPC框架的基本组件

1. 注册中心(Registry) --> 用来负责服务地址的注册和查找，由Zookeeper来实现

2. 网络传输(network transmission) --> 发送对应的信息（目标类、方法信息、方法参数等）到服务提供端, 采用Netty框架来实现

3. 序列和反序列化(Serialization/deserialization) --> 把需要传输的数据转成可传输的二进制，采用Kyro来实现

4. 动态代理 --> 隐藏方法调用的具体实现

5. 负载均衡(Load Balance) --> 避免单个服务器响应同一请求

6. 传输协议 --> 通过自定义协议，我们就可以规定传输哪些类型的数据

   ...



## 项目模块介绍

![](D:\面试阶段\面试准备文档\myproject-capture\模块介绍.png)



## 优化点

目前项目仍然还有许多不足点可以改善，比如：

- 添加服务监控中心（比如dubbo）
- 增加可配置化，避免硬编码 (比如：序列化方式、注册中心的序列化方式等)
- ...