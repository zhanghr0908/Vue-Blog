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

## 项目部分说明

**登录逻辑**，如下图

![登录逻辑](Vue-Blog/img/img/登录逻辑.jpg)

- **Token和cookie+session的区别**

  - **session的状态保持与弊端**

    - 当用户第一次通过浏览器使用用户名和密码访问服务器时，服务器会验证用户数据，验证成功后再服务器端写入session数据，向客户端浏览器返回sessionid，浏览器将sessionid保存在cookie中，当用户再次访问服务器时会携带sessionid，服务器会拿着sessionid从数据库获取session数据，然后进行用户信息查询，查询到用户信息就会将其返回，从而实现状态保持。

    - 这样做的弊端在于：

      1-服务器压力增大，通常session是存储在内存中的，每个用户通过认证之后都会将session数据保存在服务器的内存中，而当用户量增大时，服务器的压力增大；

      2-CSRF跨站伪造请求攻击，session是基于cookie进行用户识别，cookie如果被截获，用户就会很容易受到跨站请求伪造的攻击；

      3-扩展性不强，如果将来搭建了多个服务器，虽然每个服务器都执行的是同样的业务逻辑，但是session数据是保存在内存中的，在多个服务器间是不共享的，用户访问的是服务器1，当用户再次请求时可能访问的是另一台服务器2，服务器2获取不到session信息，就判定用户没有登录过。

  - token与session的不同

    - 1-认证成功后，会对当前用户数据进行加密，生成一个加密字符串token并返还给客户端，服务端不进行保存；
    - 2-浏览器会将接收到的token值存储在Local Storage中；（这里好像也可以放在cookie中，让跨域更方便）
    - 3-再次访问时服务器端对token值的处理：服务器对浏览器传来的token值进行解密，解密完成后进行用户数据的查询，如果查询成功则通过认证实现状态保持。所以即使有了多台服务器，服务器也只是做了tokon的解密和用户数据的查询，它不需要在服务端去保留用户的认证信息或者会话信息，这就意味着基于token认证机制的应用不需要去考虑用户在哪一台服务器登录了，这就为应用的扩展提供了遍历，解决了session扩展性的弊端。

- **JWT：JSON Web Token**

  - **jwt是什么**

    jwt是指JSON Web Token，是一种特殊的Token。JWT Token由头部、荷载部和签名部三部分组成，签名部分是由加密算法生成，无法反向解密，而头部和荷载部是由Base64编码算法生成，是可以反向编码回原样的，所以不要在JWT Token中存放敏感数据。当用户登录验证通过后，会生成用户令牌（Token）返回给客户端。JWT最适合的场景是：不需要服务端保存用户状态的场景。其相对于一般token的优点在于无状态和编码数据

  - **使用 jwt token认证的优势**

    - 1-无状态：token自身包含了身份验证所需要的所有信息，使得我们的服务器不需要存储Session信息，增加了系统的可用性和伸缩性，大大减轻了服务端的压力。
    - 2-有效避免了CSRF攻击：一般我们使用JWT的话，在我们登录成功获得token之后，一般会选择存放在local storage中，然后我们在前端通过某些方式会给每个发到后端的请求加上这个token，这样就不会出现CSRF漏洞的问题。
    - 3-适合移动端应用：token只要可以被客户端存储就能够使用，也可以跨语言使用
    - 4-编码数据：jwt能在荷载部编码部分信息，把常用数据如用户部分信息编码进去，能够大大减少数据库的查询次数，但是不要放敏感数据
    - 5-单点登录友好

- **配置ShiroConfig主要做几件事**

  - 1-引入RedisSessionDAO和RedisCacheManager，为了解决shiro的权限数据和会话信息能保存到redis中，实现会话共享。
  - 2-重写SessionManager和DefaultWebSecurityManager，同时在DefaultWebSecurityManager中为了关闭shiro自带的session方式，我们需要设置为false，这样用户就不再能通过session方式登录shiro。后面将采用jwt凭证登录。
  - 3-在ShiroFilterChainDefinition中，不再通过编码形式拦截Controller访问路径，而是所有的路由都需要经过JwtFilter这个过滤器，然后判断请求头中是否包含jwt的信息，有就登录，没有就跳过。跳过之后，由Controller中的shiro注解进行再次拦截，比如@RequiresAuthentication，这样来控制权限访问。

-  **AccountRealm**

  是shiro进行登录或者权限校验的逻辑和核心，需要重写三个方法

  - 1-supports->为了让realm支持jwt凭证校验（shiro默认supports的是UsernamePasswordToken，而我现在采用了jwt的方式，所以这里我自定义一个JwtToken，来完成shiro的supports方法。）
  - 2-doGetAuthenticationInfo->登录认证校验
  - 3-doGetAuthorizationInfo->权限校验

- **jwt的过滤器JwtFilter**

  这里继承的是shiro内置的AuthenticatingFilter，需要重写几个方法

  - 1-createToken：实现登录，需要生成自定义支持的JwtToken
  - 2-onAccessDenied：拦截校验，当头部没有Authorization时，直接通过，不需要自动登录；当带有的时候，首先校验jwt的有效性，没有问题就直接执行executeLogin方法实现自动登录
  - 3-onLoginFailure：登录异常时进入的方法，这里直接把异常信息封装后抛出
  - 4-preHandle：拦截器的前置拦截，前后端分离项目，项目中除了需要跨域全局配置之外，在拦截器中也需要提供跨域支持，这样，拦截器才不会在进入Controller之前就被限制

- 



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

