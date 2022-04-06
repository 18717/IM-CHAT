/*
 Navicat Premium Data Transfer

 Source Server         : 本地
 Source Server Type    : MySQL
 Source Server Version : 80028
 Source Host           : 127.0.0.1:3306
 Source Schema         : im_chat

 Target Server Type    : MySQL
 Target Server Version : 80028
 File Encoding         : 65001

 Date: 29/03/2022 23:54:28
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for friend_list
-- ----------------------------
DROP TABLE IF EXISTS `friend_list`;
CREATE TABLE `friend_list`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `username` char(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
  `friends` varchar(400) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '好友列表',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`, `username`) USING BTREE,
  INDEX `username`(`username`) USING BTREE,
  CONSTRAINT `friend_list_ibfk_1` FOREIGN KEY (`username`) REFERENCES `user` (`username`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '好友列表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for group_info
-- ----------------------------
DROP TABLE IF EXISTS `group_info`;
CREATE TABLE `group_info`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `gid` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '群组唯一识别码',
  `group_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '群名',
  `leader` char(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '群主用户名',
  `group_describe` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '群描述',
  `members` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '成员',
  `member_num` int NOT NULL COMMENT '成员数',
  `is_deleted` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`, `gid`) USING BTREE,
  INDEX `master_username`(`leader`) USING BTREE,
  INDEX `gid`(`gid`) USING BTREE,
  CONSTRAINT `group_info_ibfk_1` FOREIGN KEY (`leader`) REFERENCES `user` (`username`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '群基本信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for group_list
-- ----------------------------
DROP TABLE IF EXISTS `group_list`;
CREATE TABLE `group_list`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `username` char(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
  `gids` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT 'gid列表',
  `found_num` int NOT NULL DEFAULT 0 COMMENT '创建的群数量',
  `join_num` int NOT NULL DEFAULT 0 COMMENT '加入的群数量',
  `total_num` int NOT NULL DEFAULT 0 COMMENT '用户所有的群数量',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`, `username`) USING BTREE,
  INDEX `username`(`username`) USING BTREE,
  CONSTRAINT `group_list_ibfk_1` FOREIGN KEY (`username`) REFERENCES `user` (`username`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '群组列表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for group_message
-- ----------------------------
DROP TABLE IF EXISTS `group_message`;
CREATE TABLE `group_message`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `gid` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '群组唯一识别码',
  `sender` char(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
  `content_type` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '内容类型',
  `content` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '内容',
  `file_url` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件URL',
  `send_time` datetime NOT NULL COMMENT '发送时间',
  `is_self` tinyint NOT NULL COMMENT '是否是当前登录用户发送出去的',
  PRIMARY KEY (`id`, `gid`) USING BTREE,
  INDEX `message_content_type`(`content_type`) USING BTREE,
  INDEX `sender`(`sender`) USING BTREE,
  CONSTRAINT `group_message_ibfk_1` FOREIGN KEY (`sender`) REFERENCES `user` (`username`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '群消息汇总' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `mkey` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '消息记录标识',
  `sender` char(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发送者用户名',
  `receiver` char(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '接收者用户名',
  `content_type` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '消息内容类型',
  `content` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '文本内容',
  `file_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件URL',
  `send_time` datetime NULL DEFAULT NULL COMMENT '发送时间',
  `is_self` tinyint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `message_content_type`(`content_type`) USING BTREE,
  INDEX `send_username`(`sender`) USING BTREE,
  CONSTRAINT `message_ibfk_2` FOREIGN KEY (`sender`) REFERENCES `user` (`username`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '私聊消息汇总' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for notice_friend
-- ----------------------------
DROP TABLE IF EXISTS `notice_friend`;
CREATE TABLE `notice_friend`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `sender` char(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '发送者用户名',
  `receiver` char(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '接收者用户名',
  `title` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '标题',
  `content` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '内容',
  `is_confirm` tinyint UNSIGNED NOT NULL COMMENT '是否确认',
  `is_add` tinyint NOT NULL COMMENT '是否是添加好友',
  `is_del` tinyint NOT NULL COMMENT '是否是删除好友',
  `is_verified` tinyint NOT NULL COMMENT '是否验证',
  `is_flag` tinyint NOT NULL COMMENT '验证过后的消息',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `send_username`(`sender`) USING BTREE,
  CONSTRAINT `notice_friend_ibfk_1` FOREIGN KEY (`sender`) REFERENCES `user` (`username`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '好友通知' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for notice_group
-- ----------------------------
DROP TABLE IF EXISTS `notice_group`;
CREATE TABLE `notice_group`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `sender` char(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '发送者用户名',
  `receiver` char(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '接收者用户名',
  `title` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '标题',
  `content` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '备注内容',
  `gid` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '群号GID',
  `is_confirm` tinyint UNSIGNED NOT NULL COMMENT '是否确认',
  `is_join` tinyint NOT NULL COMMENT '是否是添加群聊',
  `is_quit` tinyint NOT NULL COMMENT '是否是退出群聊',
  `is_force_quit` tinyint NOT NULL COMMENT '是否是强制退出群聊',
  `is_dismiss` tinyint NOT NULL COMMENT '是否是解散群聊',
  `is_verified` tinyint NOT NULL COMMENT '是否验证',
  `is_flag` tinyint NOT NULL COMMENT '是否是反馈信息',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `send_username`(`sender`) USING BTREE,
  INDEX `receive_username`(`receiver`) USING BTREE,
  INDEX `gid`(`gid`) USING BTREE,
  CONSTRAINT `notice_group_ibfk_1` FOREIGN KEY (`sender`) REFERENCES `user` (`username`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `notice_group_ibfk_2` FOREIGN KEY (`receiver`) REFERENCES `user` (`username`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `notice_group_ibfk_3` FOREIGN KEY (`gid`) REFERENCES `group_info` (`gid`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '好友通知' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for notice_server
-- ----------------------------
DROP TABLE IF EXISTS `notice_server`;
CREATE TABLE `notice_server`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `title` char(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '标题',
  `notice_type` char(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '【服务器通知】' COMMENT '通知类型',
  `sender` char(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建者用户名',
  `receiver` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '接收的用户',
  `file_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '文件名称',
  `file_url` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件链接',
  `content` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '内容',
  `push_num` int NOT NULL COMMENT '推送量',
  `send_time` datetime NOT NULL COMMENT '发送时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `notice_type`(`notice_type`) USING BTREE,
  INDEX `sender`(`sender`) USING BTREE,
  CONSTRAINT `notice_server_ibfk_1` FOREIGN KEY (`sender`) REFERENCES `user` (`username`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '推送通知' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '计数id',
  `uid` char(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户唯一识别码',
  `username` char(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '登录名',
  `pwd` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '登录密码',
  `avatar` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '头像URL',
  `nickname` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '昵称',
  `gender` char(5) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '性别',
  `email` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '邮箱',
  `create_time` datetime NOT NULL COMMENT '注册时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `is_admin` tinyint UNSIGNED NOT NULL COMMENT '管理员',
  `is_login` tinyint UNSIGNED NOT NULL COMMENT '登录状态',
  `is_disable` tinyint UNSIGNED NOT NULL COMMENT '账号状态',
  `is_deleted` tinyint UNSIGNED NOT NULL COMMENT '逻辑删除',
  PRIMARY KEY (`id`, `uid`) USING BTREE,
  INDEX `username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户基本信息' ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
