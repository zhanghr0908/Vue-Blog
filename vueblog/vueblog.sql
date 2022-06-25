
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `blog`
-- ----------------------------
DROP TABLE IF EXISTS `blog`;
CREATE TABLE `blog` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL COMMENT '文章标题',
  `first_picture` varchar(255) DEFAULT NULL COMMENT '文章首图，用于随机文章展示',
  `description` longtext NOT NULL COMMENT '描述',
  `content` longtext NOT NULL COMMENT '文章正文',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `views` int(11) NOT NULL COMMENT '浏览次数',
  `words` int(11) NOT NULL COMMENT '文章字数',
  `type_id` bigint(20) NOT NULL COMMENT '文章分类id',
  `user_id` bigint(20) DEFAULT NULL COMMENT '文章作者id',
  `status` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;
-- ----------------------------
-- Table structure for `blog_tag`
-- ----------------------------
DROP TABLE IF EXISTS `blog_tag`;
CREATE TABLE `blog_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `blog_id` bigint(20) NOT NULL,
  `tag_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for `comment`
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nickname` varchar(255) NOT NULL COMMENT '昵称',
  `email` varchar(255) NOT NULL COMMENT '邮箱',
  `content` varchar(255) NOT NULL COMMENT '评论内容',
  `avatar` varchar(255) NOT NULL COMMENT '头像(图片路径)',
  `create_time` datetime DEFAULT NULL COMMENT '评论时间',
  `ip` varchar(255) DEFAULT NULL COMMENT '评论者ip地址',
  `is_admin_comment` int(1) NOT NULL COMMENT '博主回复',
  `is_published` bit(1) NOT NULL DEFAULT b'1' COMMENT '公开或非公开',
  `blog_id` bigint(255) DEFAULT NULL COMMENT '所属的文章',
  `parent_comment_id` bigint(20) DEFAULT NULL COMMENT '父评论id，-1为根评论',
  `website` varchar(255) DEFAULT NULL COMMENT '个人网站',
  `parent_comment_nickname` varchar(255) DEFAULT NULL COMMENT '被回复昵称',
  `qq` varchar(255) DEFAULT NULL COMMENT '如果评论昵称为QQ号，则将昵称和头像置为QQ昵称和QQ头像，并将此字段置为QQ号备份',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for `friend`
-- ----------------------------
DROP TABLE IF EXISTS `friend`;
CREATE TABLE `friend` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nickname` varchar(255) NOT NULL COMMENT '昵称',
  `description` varchar(255) NOT NULL COMMENT '描述',
  `website` varchar(255) NOT NULL COMMENT '站点',
  `avatar` varchar(255) NOT NULL COMMENT '头像',
  `is_published` bit(1) NOT NULL DEFAULT b'1' COMMENT '公开或隐藏',
  `views` int(11) NOT NULL DEFAULT '0' COMMENT '点击次数',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for `site_setting`
-- ----------------------------
DROP TABLE IF EXISTS `site_setting`;
CREATE TABLE `site_setting` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name_en` varchar(255) DEFAULT NULL,
  `name_zh` varchar(255) DEFAULT NULL,
  `value` longtext,
  `type` int(11) DEFAULT NULL COMMENT '1基础设置，2页脚徽标，3资料卡，4友链信息',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for `tag`
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tag_name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for `type`
-- ----------------------------
DROP TABLE IF EXISTS `type`;
CREATE TABLE `type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type_name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `nickname` varchar(255) NOT NULL COMMENT '昵称',
  `avatar` varchar(255) NOT NULL COMMENT '头像地址',
  `email` varchar(255) NOT NULL COMMENT '邮箱',
  `status` int(5) NOT NULL,
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `role` varchar(255) NOT NULL COMMENT '角色访问权限',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for `visitor`
-- ----------------------------
DROP TABLE IF EXISTS `visitor`;
CREATE TABLE `visitor` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `uuid` varchar(32) NOT NULL COMMENT '访客标识码',
  `ip` varchar(255) DEFAULT NULL COMMENT 'ip',
  `ip_source` varchar(255) DEFAULT NULL COMMENT 'ip来源',
  `os` varchar(255) DEFAULT NULL COMMENT '操作系统',
  `browser` varchar(255) DEFAULT NULL COMMENT '浏览器',
  `create_time` datetime NOT NULL COMMENT '首次访问时间',
  `last_time` datetime NOT NULL COMMENT '最后访问时间',
  `pv` int(11) DEFAULT NULL COMMENT '访问页数统计',
  `user_agent` varchar(2000) DEFAULT NULL COMMENT 'user-agent用户代理',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for `visit_log`
-- ----------------------------
DROP TABLE IF EXISTS `visit_log`;
CREATE TABLE `visit_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `uuid` varchar(36) DEFAULT NULL COMMENT '访客标识码',
  `uri` varchar(255) NOT NULL COMMENT '请求接口',
  `method` varchar(255) NOT NULL COMMENT '请求方式',
  `param` varchar(2000) NOT NULL COMMENT '请求参数',
  `behavior` varchar(255) DEFAULT NULL COMMENT '访问行为',
  `content` varchar(255) DEFAULT NULL COMMENT '访问内容',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `ip` varchar(255) DEFAULT NULL COMMENT 'ip',
  `ip_source` varchar(255) DEFAULT NULL COMMENT 'ip来源',
  `os` varchar(255) DEFAULT NULL COMMENT '操作系统',
  `browser` varchar(255) DEFAULT NULL COMMENT '浏览器',
  `times` int(11) NOT NULL COMMENT '请求耗时（毫秒）',
  `create_time` datetime NOT NULL COMMENT '访问时间',
  `user_agent` varchar(2000) DEFAULT NULL COMMENT 'user-agent用户代理',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=497 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;
