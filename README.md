# 仿牛客

## 1、论坛首页

> 流程

+ 一次请求的执行过程

> 分步实现

- 开发社区首页，显示10个帖子
- 开发分页组件，分页显示所有帖子



## 2、发送邮件

> 邮箱设置

+ 启用客户端SMTP服务（这里选择QQ邮箱）

> Spring Email 配置

+ 导入依赖包
+ 邮箱参数配置
+ 使用JavaMailSender 发送邮件

> 模板引擎

+ 使用Thymeleaf发送HTML邮件



## 3、注册功能

> 访问注册页面

+ 点击顶部导航栏注册按钮，打开注册页面

> 提交注册数据

+ 通过表单提交注册数据
+ 服务端验证账号是否已存在、邮箱是否已注册
+ 服务端发送激活邮件

> 激活注册账号

+ 点击邮件中的链接，访问服务端的激活服务



## 4、会话管理

> http的基本性质

+ 简单的
+ 可扩展
+ 无状态，有会话

> Cookie

+ 是服务器发送到浏览器，并保存在浏览器端的小块数据。
+ 浏览器下次访问该服务器时，会自动携带该块数据，将其发送给服务器。

> session

+ 是JAVAEE的标准，用于在服务端记录客户端信息。
+ 数据存放在服务端更加安全，但是也会增加服务端的内存压力。



众所周知，session是存在于服务端的。这里就有几个问题。

如果多个用户访问同一台服务器，那这台服务器可能会无法响应大访问量而宕机，因此可以选择用nginx做一个负载均衡。

> 当使用负载均衡后，session的共享就出现了问题。

用户一访问服务器，nginx找到当前较为空闲的服务器一去处理请求，服务器一返回一个cookie(sessionId) 来唯一标识客户端主机。此时该用户携带cookie再次访问服务器，nginx给他分配了另一台服务器二去处理请求，但是服务器二内没有相应的sessionId，因此客户端与服务器端无法建立会话。

> 解决方案

1、粘性session: 哪台服务器有sessionId，就给这个用户分配到这台服务器上。这是最简单粗暴的办法，但是显然是不合理的。如果这样做了，那么负载均衡就没有意义了，因为并不均衡。这就是粘性session。

2、同步session：做一个服务器间同步session。一台服务器处理完请求后，就同步其它服务器。虽然能解决session共享问题，但是增大了服务器间的耦合度，会对服务器性能造成影响。

3、共享session：创建一台专门存储session的服务器，别的服务器向这台服务器申请session。 但是如果这台服务器宕机，那么其他服务器全部无法工作了。

**4、目前主流办法：**能存cookie就存cookie。敏感数据就存在数据库中。可以对数据库做一个集群、主从复制，这样既能保证性能，也能保证会话安全性。但是，传统的关系型数据库，数据是存储在磁盘中的，每次从数据库获取数据都要进行磁盘IO，性能是较低的。

所以现在大家都选择用NoSql数据库，如Redis，来存储数据。Redis数据存储在内存中，读取数据更快。

![image-20200430194249301](E:\myyyyyyyyyyyyyynote\仿牛客\session存储.png)



## 5、生成验证码

> Kaptcha

+ 导入依赖包
+ 编写Kaptcha配置类
+ 生成随机字符、生成图片



## 6、登录、退出功能

> 访问登录页面

+ 点击顶部导航栏的登录按钮，打开登录页面

> 登录

+ 验证账号、密码、验证码
+ 成功时，生成登录凭证，发给客户端
+ 失败时，跳转回登录页，并返回错误信息

> 退出

+ 将登录凭证修改为失效状态
+ 跳转至网站首页



![登录流程](E:\myyyyyyyyyyyyyynote\仿牛客\登录流程.PNG)

## 7、显示登录信息

> 拦截器

+ 定义拦截器，实现HandlerInterceptor
+ 配置拦截器，为它指定拦截、排除的路径

> 拦截应用

+ 在请求开始时查询登录用户
+ 在本次请求中持有用户数据
+ 在模板视图上显示用户数据
+ 在请求结束时清理用户数据



## 8、账号设置

> 上传文件（头像）

+ 请求：必须是POST
+ 表单：enctype=“multipart/form-data”
+ Spring MVC : 通过MuitipartFile处理上传文件

> 修改密码

> 开发步骤

+ 访问账号设置页面
+ 上传头像
+ 获取头像



## 9、检查登录状态

> 使用拦截器

+ 在方法前标注自定义注解
+ 拦截所有请求，只处理带有该注解的方法

> 使用自定义注解

+ 常用的元注解：

  @Target : 声明自定义注解的作用位置，作用类型

  @Retention：注解有效时期，运行时有效还是编译器有效

  @Document：自定义注解生成文档时是否带上

  @Inherited：子类继承父类，父类有自定义注解，子类是否要带上父类的注解

+ 如何读取注解：反射

  Method.getDeclaredAnnotations()

  Method.getAnnotation(Class<T> annotationClass)



使用拦截器拦截注解。



## 10、过滤敏感词

> 前缀树

+ 名称：Trie、字典树、查找树
+ 特点：查找效率高，消耗内存大
+ 应用：字符串检索、词频统计、字符串排序

> 敏感词过滤器

+ 定义前缀树
+ 根据敏感词，初始化前缀树
+ 编写过滤敏感词的方法



## 11、发布帖子

> AJAX

+ Asynchronous JavaScript and XML
+ 异步的JavaScript与XML，不是一门新技术，只是一个新的术语
+ 使用AJAX， 网页能够将增量更新呈现在页面上，而不需要刷新整个页面
+ 虽然X代码XML，但目前JSON的使用比XML更加普遍

> 发布帖子

+ 采用AJAX请求，实现发布帖子。



## 12、帖子详情

> 步骤

+ 数据访问层 DiscussPostMapper
+ 业务层 DiscussPostService
+ 控制层 DiscussPostController

+ index.html增加详情页链接
+ discuss-detail.html编辑



## 13、事务管理

> 声明式事务

+ 通过XML配置，声明某方法的事务特征
+ 通过注解，声明某方法的事务特征

> 编程式事务

+ 通过 TransactionTemplate管理事务，并通过它执行数据库操作。



## 14、显示评论

> 数据层

+ 根据实体插叙一页评论数据
+ 根据实体查询评论的数量

> 业务层

+ 处理查询评论的业务
+ 处理查询评论数量的业务

> 表现层

+ 显示帖子详情数据时，同时显示该帖子所有评论数据。



## 15、私信功能

> 私信列表

+ 查询当前用户的会话列表，每个会话只显示一条最新的私信
+ 支持分页显示

> 私信详情

+ 查询某个会话所包含的私信
+ 支持分页显示

> 发送私信

+ 采用异步的方式发送私信
+ 发送成功后刷新私信列表

> 设置已读

+ 访问私信详情时，将显示的私信设置为已读状态



## 16、统一异常处理

MVC三层架构。

前端发送数据，表现层接收数据，业务层执行业务逻辑，调用数据传输层方法。如果数据传输层出现异常，就会抛给业务层，业务层再抛给控表现层。

> @ControllerAdvice

+ 用于修饰类，表示该类是Controller的全局配置类

+ 在此类中，可以对Controller进行如下三种全局配置：

  + 异常处理方案、绑定数据方案、绑定参数方案



> @ExceptionHandler

+ 用于修饰方法，该方法会在Controller出现异常后被调用，用于处理捕获到的异常



> @ModelAttribute

+ 用于修饰方法，该方法会在Controller方法执行前被调用，用于为Model对象绑定参数



> @DataBinder

+ 用于修饰方法，该方法会在Controller方法执行前被调用，用于绑定参数的转换器。



## 17、统一记录日志

> 需求

+ 帖子模块
+ 评论模块
+ 消息模块

> AOP 面向切面编程

![AOP](E:\myyyyyyyyyyyyyynote\仿牛客\AOP.PNG)



> AOP实现

+ AspectJ
  + 是语言级的实现，扩展了Java语法，定义了AOP语法
  + 在编译器织入代码，它有一个专门的编译器，用来生成遵守Java字节码规范的class文件
+ Spring AOP
  - 纯Java实现，它不需要专门的编译过程，也不需要特殊的类加载器
  - 在运行时通过代理的方式织入代码，只支持方法类型的连接点
  - Spring支持对AspectJ的集成



> Spring AOP实现

+ JDK动态代理
  + java提供的动态代理技术，可以在运行时创建接口的代理实例
  + Spring AOP默认采用此种方式，在**接口**的代理实例中织入代码
+ CGLib动态代理
  + 采用底层的字节码技术，在运行时创建子类代理实例。
  + 当目标对象不存在接口时，Spring AOP会采用此种方式，在子类实例中织入代码



## 18、点赞功能

> 点赞

+ 支持对帖子、评论点赞
+ 支持第1次点赞，第二次取消点赞

> 首页点赞数量

+ 统计帖子的点赞数量

> 详情页点赞数量

+ 统计点赞数量
+ 显示点赞状态

> redis

避免多个用户同时点赞使得数据不一致，使用redis实现



## 19、收到的赞

> 重构点赞功能

+ 以用户为key，记录点赞数量
+ increment(key), decrement(key)

> 开发个人主页

+ 以用户为key， 查询点赞数量



## 20、关注和取消关注

> 需求

+ 开发关注、取消关注功能
+ 统计用户的关注数、粉丝数

> 关键

+ 若A关注了B， 则A是B的 follower（粉丝）， B是A的 followee（目标）
+ 关注的目标可以是用户、帖子、题目等，在实现时将这些目标抽象为实体





## 21、关注列表、粉丝列表

> 业务层

+ 查询某个用户关注的人，支持分页
+ 查询某个用户的粉丝，支持分页

> 表现层

+ 处理“查询关注的人”、“查询粉丝”请求
+ 编写“查询关注的人”、“查询粉丝”模板



## 22、优化登录模块

> 使用Redis存储验证码

+ 验证码需要频繁的访问与刷新，对性能要求较高
+ 验证码不需永久保存，通常在很短的时间就会失效
+ 分布式部署时，存在Session共享的问题

> 使用Redis存储登录凭证

+ 处理每次请求时，都要查询用户的登录凭证

> 使用Redis缓存用户信息

+ 处理每次请求时，都要根据凭证查询用户信息，访问的频率非常高