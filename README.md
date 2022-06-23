<p align="center">
	<img src="https://img.shields.io/badge/JDK-1.8+-orange">
	<img src="https://img.shields.io/badge/SpringBoot-2.4.4.RELEASE-brightgreen">
	<img src="https://img.shields.io/badge/MyBatisPlus-3.2.0-red">
	<img src="https://img.shields.io/badge/Vue-2.6.11-brightgreen">
    <img src="https://img.shields.io/badge/ElasticSearch-7.9.3-red">
    <img src="https://img.shields.io/badge/RabbitMQ-3.7.15-blue">
</p>


## 项目预览
预览网址：http://120.48.88.228

后台管理页面访问：http://120.48.88.228/login

​		账号：visitor

​		密码：visitor




## 简介

项目是基于 Spring Boot + Vue 的前后端分离博客系统。

本项目长期维护，欢迎fork代码和star！
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

## 项目bug修复

- 邮件发送功能：当管理员回复了评论，会向评论者当时填写的邮箱发送邮件提醒

- 前端显示：将图片显示修改了一下。

  - 1、使用 cdn.jsdelivr.net 未受污染的子域：

    ​		fastly.jsdelivr.net，由 Fastly 提供

    ​		gcore.jsdelivr.net，由 G-Core 提供

    ​		testingcf.jsdelivr.net，由 CloudFlare 提供

  - 2、使用国内的静态库：

    ​		cdn.staticfile.org，七牛云和掘金的静态资源库

    ​		cdn.bytedance.com，字节跳动静态资源公共库

    ​		cdn.baomitu.com，360 前端静态资源库

  - 3、将需要的静态资源下载到本地

    ​		第一种只需将博客主题的 HTML 文件中 jsDelivr 链接里的 cdn 替换为子域名即可

    ​		第二种需要在这些国内网站上搜索 JS 库的名字，然后复制搜索结果给出的链接，再替换掉对应的 jsDelivr 链接

    ​		第三种是替换为本地路径

- 本周热议模块：当文章被删除、可见性变更、更新时，在模块中删除，同时保证缓存与数据库数据一致
- 所有数据时间的问题：解决所有LocalDateTime.now()返回时间比系统时间早8个小时的问题，设置serverTimezone=GMT%2B8，实体类中@JsonFormat(timezone = "Asia/Shanghai", pattern = "yyyy-MM-dd HH:mm:ss")；springboot启动时间不对的问题：修改容器的时区为东八区
- Mysql：Forcing close of thread xxx user: ‘root’（具体见网站文章百度云服务器部署项目时遇到的mysql的问题）
  - 在宿主机（服务器）上创建文件夹挂载到docker中的mysql容器中

## 项目后期计划

- 增加文件和图片上传功能
- 网站显示优化和性能优化

## 项目快速开始

1. 执行vueblog.sql创建数据库 
2. 开启redis、rabbitmq、elasticsearch
3. idea中修改yml配置，然后运行后端项目
4. 安装npm 在idea中启动前端页面/或者在vscode中启动前端页面

## 项目线上部署

1. 使用docker-compose来编排 
2. 需要mysql、nginx、redis、rabbitmq、elasticsearch、后端项目、

## 致谢
项目开发过程主要基于[SkyBlog](https://github.com/yubifeng/SkyBlog)，参考了很多大佬的博客[MyBlog](https://github.com/zhyocean/MyBlog)、[My-Blog](https://github.com/ZHENFENG13/My-Blog)，感谢MarkerHub的一些博客视频和文档教导，以及服务器部署的视频。

