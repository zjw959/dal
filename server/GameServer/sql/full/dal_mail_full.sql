/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50718
 Source Host           : 127.0.0.1:3306
 Source Schema         : dal_mail

 Target Server Type    : MySQL
 Target Server Version : 50718
 File Encoding         : 65001

 Date: 16/07/2018 12:09:18
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_player_mail
-- ----------------------------
CREATE TABLE `t_player_mail`  (
  `id` bigint(20) NOT NULL,
  `sender_id` bigint(20) NULL DEFAULT NULL COMMENT '发送者',
  `receiver_id` bigint(20) NOT NULL COMMENT '收件人id',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮件标题',
  `body` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮件正文',
  `items` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '附件',
  `status` smallint(4) NULL DEFAULT NULL COMMENT '状态：0未读,1已读,2已收,3已删',
  `info` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮件来源信息',
  `create_date` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `modify_date` datetime(0) NULL DEFAULT NULL COMMENT '最近修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `receiverId_idx`(`receiver_id`) USING BTREE,
  INDEX `receiverId_status`(`receiver_id`, `status`) USING BTREE,
  INDEX `time`(`create_date`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'remarks=邮件' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_server_mail
-- ----------------------------
CREATE TABLE `t_server_mail`  (
  `id` bigint(20) NOT NULL,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标题',
  `body` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '内容',
  `items` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '附件',
  `create_date` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `modify_date` datetime(0) NULL DEFAULT NULL COMMENT '最近修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `time`(`create_date`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'remarks=全服邮件' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
