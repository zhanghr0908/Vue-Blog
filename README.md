<p align="center">
	<img src="https://img.shields.io/badge/JDK-1.8+-orange">
	<img src="https://img.shields.io/badge/SpringBoot-2.4.4.RELEASE-brightgreen">
	<img src="https://img.shields.io/badge/MyBatisPlus-3.2.0-red">
	<img src="https://img.shields.io/badge/Vue-2.6.11-brightgreen">
    <img src="https://img.shields.io/badge/ElasticSearch-7.9.3-red">
    <img src="https://img.shields.io/badge/RabbitMQ-3.7.15-blue">
</p>


## 博客预览
未购买服务器上线，目前只在内网使用。




## 简介

项目是基于 Spring Boot + Vue 的前后端分离博客系统。

本项目长期维护，欢迎fork代码和star！。
## 前端

核心框架：
 - Vue框架：Vue2.6.11、Vue Router、Vuex、vue/cli4.5.12
 - UI框架：Element UI框架
 - 异步请求：axios

markdown：
 - 编辑器：mavon-editor
 - 解析渲染：markdown-it
 - 样式：github-markdown-c


## 后端

- 核心框架：Spring Boot
- 安全框架：shiro
- Token 认证：jwt
- 持久层框架：MyBatisPlus
- java版本：JDK8
- 缓存：redis
- 搜索引擎：ElasticSearch
- 消息队列中间件：RabbitMQ


## 功能



### 首页
- 导航栏：首页、分类、归档、友链、关于我、搜索框
- 最热标签、最热博客、最新博客、本周热议


### 后台管理页面
- 登录功能：包括登录认证、访问拦截、身份授权、登出
- 文章管理：
    - 文章发布
    - 文章编辑
    - 文章删除
- 标签管理
- 友链管理
- 用户管理
- 访问日志
- 访问统计
- 初始数据

##项目快速开始
1. 执行console.sql创建数据库 
2. 开启redis、rabbitmq、elasticsearch
3. idea中修改yml配置，然后运行后端项目
4. 安装npm 在idea中启动前端页面


##项目线上部署
1. 使用docker-compose来编排 
2. 需要nginx、redis、rabbitmq、elasticsearch、后端项目、mysql
3. nginx反向代理，将https请求通过http转给后端容器，避免接口暴露和http的不安全性

## 致谢
项目开发过程主要基于[SkyBlog](https://github.com/yubifeng/SkyBlog)，参考了很多大佬的博客[MyBlog](https://github.com/zhyocean/MyBlog)、[My-Blog](https://github.com/ZHENFENG13/My-Blog)，感谢MarkerHub的一些博客视频和文档教导，以及服务器部署的视频。

