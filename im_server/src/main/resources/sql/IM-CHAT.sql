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

 Date: 09/03/2022 21:42:13
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for group_info
-- ----------------------------
DROP TABLE IF EXISTS `group_info`;
CREATE TABLE `group_info`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `gid` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '群组唯一识别码',
  `group_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '群名',
  `master_username` char(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '群主用户名',
  `group_describe` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '群描述',
  `members` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '成员',
  `member_num` int NULL DEFAULT NULL COMMENT '成员数',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`, `gid`) USING BTREE,
  INDEX `master_username`(`master_username`) USING BTREE,
  CONSTRAINT `group_info_ibfk_1` FOREIGN KEY (`master_username`) REFERENCES `user_info` (`username`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '群基本信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of group_info
-- ----------------------------
INSERT INTO `group_info` VALUES (2, 'nl27838vn196', '通角八速百', 'li2n', '响得名精是查前求很业他准动。动制极什难改油里严果候设林没观导容。量便值因机热收始事青质说自。音结般则都共月今许风得圆引。量其转有中体连队全质节入米信。下意适更构方千时求统等证内定值选。', 'li2n,', 1, '2022-03-09 19:42:21', '2022-03-09 19:42:21');
INSERT INTO `group_info` VALUES (3, 'nln638273883', '用内张身', 'li2n', '白素近员和便度要志农细了点各府已月。有持众真八度交件从天题却七飞世可个。据现院写走况品传两因里权往级重速。青油办办很应军或地任西关往精。算团管布中清走维联数更命。', 'li2n,Eric,', 2, '2022-03-09 19:45:36', '2022-03-09 20:36:50');
INSERT INTO `group_info` VALUES (4, '22l164d4b271', '料明北家亲给', 'li2n', '布管门关影王给称提了工用种院图领。角部验合委且起界经们单近市线。证干各程拉龙院制易在天响期正构。经用律就张五众七老小去先加与边报交。步多走设除布组通物步此为九常则其。通类算教家系入大法铁状及学整义近流。', 'li2n,', 1, '2022-03-09 19:47:12', '2022-03-09 19:47:57');

-- ----------------------------
-- Table structure for group_list
-- ----------------------------
DROP TABLE IF EXISTS `group_list`;
CREATE TABLE `group_list`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `username` char(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
  `gids` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '用户所在的所以群组gid',
  `found_num` int NOT NULL DEFAULT 0 COMMENT '创建的群数量',
  `join_num` int NOT NULL DEFAULT 0 COMMENT '加入的群数量',
  `total_num` int NOT NULL DEFAULT 0 COMMENT '用户所有的群数量',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`, `username`) USING BTREE,
  INDEX `username`(`username`) USING BTREE,
  CONSTRAINT `group_list_ibfk_1` FOREIGN KEY (`username`) REFERENCES `user_info` (`username`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '群组列表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of group_list
-- ----------------------------
INSERT INTO `group_list` VALUES (1, 'li2n', 'nl27838vn196,nln638273883,22l164d4b271,', 3, 0, 3, '2022-03-09 00:46:21', '2022-03-09 19:48:56');
INSERT INTO `group_list` VALUES (2, 'Eric', 'nl27838vn196,nln638273883,', 0, 2, 2, '2022-03-09 01:55:57', '2022-03-09 20:36:52');
INSERT INTO `group_list` VALUES (4, 'Elizabeth', '', 0, 0, 0, '2022-03-09 01:56:27', '2022-03-09 01:56:27');
INSERT INTO `group_list` VALUES (5, 'Cynthia', '', 0, 0, 0, '2022-03-09 01:56:28', '2022-03-09 01:56:28');
INSERT INTO `group_list` VALUES (6, 'Paul', '', 0, 0, 0, '2022-03-09 01:56:30', '2022-03-09 01:56:30');
INSERT INTO `group_list` VALUES (7, 'Ronald', '', 0, 0, 0, '2022-03-09 01:56:31', '2022-03-09 01:56:31');
INSERT INTO `group_list` VALUES (8, 'Maria', '', 0, 0, 0, '2022-03-09 01:56:32', '2022-03-09 01:56:32');
INSERT INTO `group_list` VALUES (9, 'Sharon', '', 0, 0, 0, '2022-03-09 01:56:33', '2022-03-09 01:56:33');
INSERT INTO `group_list` VALUES (10, 'Robert', '', 0, 0, 0, '2022-03-09 10:48:31', '2022-03-09 10:48:31');

-- ----------------------------
-- Table structure for message_content_type
-- ----------------------------
DROP TABLE IF EXISTS `message_content_type`;
CREATE TABLE `message_content_type`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `type_describe` char(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '消息内容类型',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `describe`(`type_describe`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '消息内容类型' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of message_content_type
-- ----------------------------
INSERT INTO `message_content_type` VALUES (1, 'text', '2022-03-08 01:51:08', '2022-03-08 01:51:10');
INSERT INTO `message_content_type` VALUES (2, 'file', '2022-03-08 01:52:09', '2022-03-08 01:52:11');
INSERT INTO `message_content_type` VALUES (3, 'img', '2022-03-08 01:52:48', '2022-03-08 01:52:50');

-- ----------------------------
-- Table structure for message_group
-- ----------------------------
DROP TABLE IF EXISTS `message_group`;
CREATE TABLE `message_group`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `gid` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '群组唯一识别码',
  `send_username` char(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发送者用户名',
  `message_content_type` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '内容类型',
  `content` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '内容',
  `file_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件URL',
  `send_time` datetime NULL DEFAULT NULL COMMENT '发送时间',
  `is_self` tinyint NULL DEFAULT NULL COMMENT '是否是当前登录用户发送出去的',
  PRIMARY KEY (`id`, `gid`) USING BTREE,
  INDEX `message_content_type`(`message_content_type`) USING BTREE,
  CONSTRAINT `message_group_ibfk_1` FOREIGN KEY (`message_content_type`) REFERENCES `message_content_type` (`type_describe`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '群消息汇总' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of message_group
-- ----------------------------

-- ----------------------------
-- Table structure for message_offline
-- ----------------------------
DROP TABLE IF EXISTS `message_offline`;
CREATE TABLE `message_offline`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `send_nickname` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发送者昵称',
  `send_username` char(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发送者用户名',
  `receive_username` char(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '接收者用户名',
  `message_content_type` char(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '消息内容类型',
  `content` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文本内容',
  `file_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件URL',
  `send_time` datetime NULL DEFAULT NULL COMMENT '发送时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `message_content_type`(`message_content_type`) USING BTREE,
  CONSTRAINT `message_offline_ibfk_1` FOREIGN KEY (`message_content_type`) REFERENCES `message_content_type` (`type_describe`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 60 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '私聊离线消息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of message_offline
-- ----------------------------

-- ----------------------------
-- Table structure for message_total
-- ----------------------------
DROP TABLE IF EXISTS `message_total`;
CREATE TABLE `message_total`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `mkey` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '消息记录标识',
  `send_username` char(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发送者用户名',
  `receive_username` char(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '接收者用户名',
  `message_content_type` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '消息内容类型',
  `content` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文本内容',
  `file_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件URL',
  `send_time` datetime NULL DEFAULT NULL COMMENT '发送时间',
  `is_online` tinyint NULL DEFAULT NULL COMMENT '是否是在线消息',
  `is_self` tinyint NULL DEFAULT NULL COMMENT '该消息是否是发送出去的',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `message_content_type`(`message_content_type`) USING BTREE,
  CONSTRAINT `message_total_ibfk_1` FOREIGN KEY (`message_content_type`) REFERENCES `message_content_type` (`type_describe`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '私聊消息汇总' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of message_total
-- ----------------------------

-- ----------------------------
-- Table structure for notice_friend
-- ----------------------------
DROP TABLE IF EXISTS `notice_friend`;
CREATE TABLE `notice_friend`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `send_username` char(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发送者用户名',
  `receive_username` char(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '接收者用户名',
  `title` char(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标题',
  `content` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '内容',
  `is_confirm` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '是否确认',
  `is_add` tinyint NULL DEFAULT NULL COMMENT '是否是添加好友',
  `is_del` tinyint NULL DEFAULT NULL COMMENT '是否是删除好友',
  `is_online` tinyint NULL DEFAULT NULL COMMENT '目标是否是离线状态',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `title`(`title`) USING BTREE,
  CONSTRAINT `notice_friend_ibfk_1` FOREIGN KEY (`title`) REFERENCES `notice_type` (`type_describe`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '好友通知' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of notice_friend
-- ----------------------------

-- ----------------------------
-- Table structure for notice_server
-- ----------------------------
DROP TABLE IF EXISTS `notice_server`;
CREATE TABLE `notice_server`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `title` char(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标题',
  `send_username` char(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者用户名',
  `content` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '内容',
  `message_content_type` char(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型',
  `receive_username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '接收的用户',
  `push_num` int NULL DEFAULT NULL COMMENT '推送量',
  `send_time` datetime NULL DEFAULT NULL COMMENT '发送时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `title`(`title`) USING BTREE,
  INDEX `message_content_type`(`message_content_type`) USING BTREE,
  CONSTRAINT `notice_server_ibfk_1` FOREIGN KEY (`title`) REFERENCES `notice_type` (`type_describe`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `notice_server_ibfk_2` FOREIGN KEY (`message_content_type`) REFERENCES `message_content_type` (`type_describe`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '推送通知' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of notice_server
-- ----------------------------

-- ----------------------------
-- Table structure for notice_type
-- ----------------------------
DROP TABLE IF EXISTS `notice_type`;
CREATE TABLE `notice_type`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `type_describe` char(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '通知类型',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `describe`(`type_describe`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '通知类型' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notice_type
-- ----------------------------
INSERT INTO `notice_type` VALUES (1, '【服务器通知】', '2022-03-08 01:56:24', '2022-03-08 01:56:26');
INSERT INTO `notice_type` VALUES (2, '【好友通知】', '2022-03-08 01:56:42', '2022-03-08 01:56:44');

-- ----------------------------
-- Table structure for user_friend_list
-- ----------------------------
DROP TABLE IF EXISTS `user_friend_list`;
CREATE TABLE `user_friend_list`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `username` char(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
  `friends` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '好友列表',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`, `username`) USING BTREE,
  INDEX `username`(`username`) USING BTREE,
  CONSTRAINT `user_friend_list_ibfk_1` FOREIGN KEY (`username`) REFERENCES `user_info` (`username`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '好友列表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_friend_list
-- ----------------------------
INSERT INTO `user_friend_list` VALUES (3, 'li2n', 'Eric,Maria', '2022-03-09 00:46:21', '2022-03-09 12:05:46');
INSERT INTO `user_friend_list` VALUES (4, 'Eric', 'li2n', '2022-03-09 01:55:57', '2022-03-09 01:55:57');
INSERT INTO `user_friend_list` VALUES (6, 'Elizabeth', '', '2022-03-09 01:56:27', '2022-03-09 01:56:27');
INSERT INTO `user_friend_list` VALUES (7, 'Cynthia', '', '2022-03-09 01:56:28', '2022-03-09 01:56:28');
INSERT INTO `user_friend_list` VALUES (8, 'Paul', '', '2022-03-09 01:56:30', '2022-03-09 01:56:30');
INSERT INTO `user_friend_list` VALUES (9, 'Ronald', '', '2022-03-09 01:56:31', '2022-03-09 01:56:31');
INSERT INTO `user_friend_list` VALUES (10, 'Maria', 'li2n', '2022-03-09 01:56:32', '2022-03-09 12:05:46');
INSERT INTO `user_friend_list` VALUES (11, 'Sharon', '', '2022-03-09 01:56:33', '2022-03-09 01:56:33');
INSERT INTO `user_friend_list` VALUES (12, 'Robert', '', '2022-03-09 10:48:31', '2022-03-09 10:48:31');

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '计数id',
  `uid` char(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户唯一识别码',
  `username` char(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '登录名',
  `pwd` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '登录密码',
  `avatar` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '头像URL',
  `nickname` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `gender` char(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '性别',
  `email` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `create_time` datetime NULL DEFAULT NULL COMMENT '注册时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_admin` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '管理员',
  `is_login` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '登录状态',
  `is_disable` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '账号状态',
  `is_deleted` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '逻辑删除',
  PRIMARY KEY (`id`, `uid`) USING BTREE,
  INDEX `username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户基本信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_info
-- ----------------------------
INSERT INTO `user_info` VALUES (3, '3eenli95990vmsu5', 'li2n', '$2a$10$koMX20K382EG9o73KE8zH.xZ3xXjR4YdSNsjF1A9GNsVOuNjFHfdm', 'http://dummyimage.com/100x100', '一杯香梨', '男', 'e.ucuwzaici@qq.com', '2022-03-09 00:46:21', '2022-03-09 02:07:47', 1, 0, 0, 0);
INSERT INTO `user_info` VALUES (4, 'eee41416ric3lwep', 'Eric', '$2a$10$fAO17/3hoXWrw1DrMPHcN.4bRLid7P0nFBgssWewielbiy55Iccem', 'http://iwrlpmf.cm/muxugy', '困宝宝', '女', 'c.kbwxxd@ajmfssab.net', '2022-03-09 01:55:57', '2022-03-09 11:27:47', 0, 0, 1, 0);
INSERT INTO `user_info` VALUES (6, '347blh12262uphgq', 'Elizabeth', '$2a$10$lLqDwAjUNbPB.elWxNr8Ge628QYY187sRGoU8g0amKRcljKHbD46u', 'http://vjpixethyw.il/ofwg', '萧秀兰', '女', 'g.weupbgtjw@vxzj.mr', '2022-03-09 01:56:27', '2022-03-09 11:28:02', 0, 0, 0, 0);
INSERT INTO `user_info` VALUES (7, 'aht682614f1z8iug', 'Cynthia', '$2a$10$vHWWr1q4uQY0L6QEQvhF9.eN1qYzd89L8XBgmhK.GoO9Ht/7tFoc.', 'http://fgnqvp.sb/fhyn', '刘秀兰', '男', 'a.ubls@befur.kz', '2022-03-09 01:56:28', '2022-03-09 11:28:09', 0, 0, 0, 0);
INSERT INTO `user_info` VALUES (8, 'all73eptvs069121', 'Paul', '$2a$10$YO2RL9GyWt2VU2e9ni1k7ePm7i2lm8ePrWKQxZRx.7w7A3FvG5QWy', 'http://uvc.cd/gsyzkztggh', '沈平', '男', 'p.lsfpw@pxekrct.ne', '2022-03-09 01:56:30', '2022-03-09 11:28:14', 0, 0, 0, 0);
INSERT INTO `user_info` VALUES (9, 'nwil2nolee865479', 'Ronald', '$2a$10$RM72nUjJ7zuT7v.HPu/ENusev4ugMuNg8fTdzY/oxkHs6CV0tcMGy', 'http://sfpakvfhq.to/kgixe', '徐勇', '男', 'f.qtyejh@rblrk.cq', '2022-03-09 01:56:31', '2022-03-09 11:28:21', 0, 0, 0, 0);
INSERT INTO `user_info` VALUES (10, '79714aire75nfmhg', 'Maria', '$2a$10$PALY7RjmicPLaFzLa7O8d.1N7osD1LBGgBJdc/ah1YFKeDk.n0EEi', 'http://zfegm.ad/tumcilv', '陈伟', '男', 'd.kbiyy@wpbbkgd.lv', '2022-03-09 01:56:32', '2022-03-09 11:28:34', 0, 0, 0, 0);
INSERT INTO `user_info` VALUES (11, '2thnj72616153aaa', 'Sharon', '$2a$10$ipcaZgOagRFcdCL0chaZie.1j2a1EF2upXdrFSNEU1xZt1j9PYrr6', 'http://dummyimage.com/88x31', '侯静', '女', 'r.bfrtn@buslpvru.gn', '2022-03-09 01:56:33', '2022-03-09 11:32:28', 0, 0, 0, 0);
INSERT INTO `user_info` VALUES (12, '4w1xo66917obR853', 'Robert', '$2a$10$X/TSOvi9/Yg3eUjo4qaFRec32Emqm8dpXLdxsqjxzXvBvmdsvNoRW', 'http://ddxleoleq.pk/vrdxjnglv', '江敏', '女', 'c.mdjkl@ddpdyjv.ir', '2022-03-09 10:48:31', '2022-03-09 11:28:45', 0, 0, 0, 1);

SET FOREIGN_KEY_CHECKS = 1;
