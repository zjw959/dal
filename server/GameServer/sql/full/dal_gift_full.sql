/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.20.182
 Source Server Type    : MySQL
 Source Server Version : 50722
 Source Host           : 192.168.20.182:3306
 Source Schema         : dal_gift

 Target Server Type    : MySQL
 Target Server Version : 50722
 File Encoding         : 65001

 Date: 09/08/2018 21:38:21
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for hibernate_sequence
-- ----------------------------
DROP TABLE IF EXISTS `hibernate_sequence`;
CREATE TABLE `hibernate_sequence`  (
  `next_val` bigint(20) NULL DEFAULT NULL
) ENGINE = MyISAM CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Fixed;

-- ----------------------------
-- Table structure for t_u_chinese_code
-- ----------------------------
DROP TABLE IF EXISTS `t_u_chinese_code`;
CREATE TABLE `t_u_chinese_code`  (
  `id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '中文礼包码',
  `package_id` int(11) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_u_forever_got
-- ----------------------------
DROP TABLE IF EXISTS `t_u_forever_got`;
CREATE TABLE `t_u_forever_got`  (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '礼包码',
  `role_id` int(11) NOT NULL DEFAULT 0 COMMENT '领取的角色ID',
  `package_id` int(11) NOT NULL DEFAULT 0 COMMENT '礼包配置表ID，外键关联t_u_package表格的ID',
  `got` tinyint(4) NOT NULL DEFAULT 0 COMMENT '被领取次数',
  `got_time` datetime(0) NULL DEFAULT NULL COMMENT '领取时间',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`, `role_id`) USING BTREE,
  INDEX `idx_package_id`(`package_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '礼包码记录表，一次性礼包码' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_u_invit_code
-- ----------------------------
DROP TABLE IF EXISTS `t_u_invit_code`;
CREATE TABLE `t_u_invit_code`  (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '礼包码',
  `package_id` int(11) NOT NULL DEFAULT 0 COMMENT '礼包配置表ID，外键关联t_u_package表格的ID',
  `got` tinyint(4) NOT NULL DEFAULT 0 COMMENT '被领取次数',
  `role_id` int(11) NULL DEFAULT 0 COMMENT '领取的角色ID',
  `got_time` datetime(0) NULL DEFAULT NULL COMMENT '领取时间',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_role_id`(`role_id`) USING BTREE,
  INDEX `idx_package_id`(`package_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '礼包码记录表，一次性礼包码' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_u_invit_code_chinese
-- ----------------------------
DROP TABLE IF EXISTS `t_u_invit_code_chinese`;
CREATE TABLE `t_u_invit_code_chinese`  (
  `role_id` int(11) NOT NULL DEFAULT 0 COMMENT '领取的角色ID',
  `package_id` int(11) NOT NULL,
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '礼包码',
  `got_time` datetime(0) NULL DEFAULT NULL COMMENT '领取时间',
  PRIMARY KEY (`role_id`, `id`) USING BTREE,
  UNIQUE INDEX `ids`(`id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_u_invit_code_forever
-- ----------------------------
DROP TABLE IF EXISTS `t_u_invit_code_forever`;
CREATE TABLE `t_u_invit_code_forever`  (
  `id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '礼包码',
  `package_id` int(11) NOT NULL DEFAULT 0 COMMENT '礼包配置表ID，外键关联t_u_package表格的ID',
  `got` int(11) NOT NULL DEFAULT 0 COMMENT '被领取次数',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`, `package_id`) USING BTREE,
  INDEX `fk_pack_id`(`package_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '永久性礼包码' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_u_package
-- ----------------------------
DROP TABLE IF EXISTS `t_u_package`;
CREATE TABLE `t_u_package`  (
  `id` int(11) NOT NULL,
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `expression` varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '礼包物品，表达式：resType,resId,resNum|……',
  `channel` varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '有效渠道，空或者空字符串表示不限制。否则通过表达式设定有效渠道：channel1,channel2……',
  `server_id` varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '有效服务器ID，空或者空字符串表示任意服务器有效。特定服务器有效通过表达式进行定义：server_1,server_2……',
  `begin_time` datetime(0) NULL DEFAULT NULL COMMENT '有效时间，起始时间，空表示不限制',
  `end_time` datetime(0) NULL DEFAULT NULL COMMENT '有效时间，结束时间，空表示不限制',
  `count` int(11) NOT NULL DEFAULT 1 COMMENT '单个礼包最多领取次数',
  `day_interval` int(11) NULL DEFAULT 0 COMMENT '时间间隔（时间间隔必须为整天0表示无时间间隔）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '礼包配置表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
