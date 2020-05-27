/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.20.182
 Source Server Type    : MySQL
 Source Server Version : 50722
 Source Host           : 192.168.20.182:3306
 Source Schema         : dal_login

 Target Server Type    : MySQL
 Target Server Version : 50722
 File Encoding         : 65001

 Date: 18/08/2018 19:57:05
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_s_server_group
-- ----------------------------
DROP TABLE IF EXISTS `t_s_server_group`;
CREATE TABLE `t_s_server_group`  (
  `id` int(11) NOT NULL COMMENT '游戏服分组id',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '游戏服分组名',
  `info` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '游戏服分组说明',
  `mail_databases_ip` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `mail_databases_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `mail_databases_port` int(11) NULL DEFAULT NULL,
  `mail_databases_user` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `mail_databases_password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `pay_databases_ip` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `pay_databases_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `pay_databases_port` int(11) NULL DEFAULT NULL,
  `pay_databases_user` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `pay_databases_password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `global_databases_ip` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `global_databases_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `global_databases_port` int(11) NULL DEFAULT NULL,
  `global_databases_user` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `global_databases_password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `mark` int(11) NOT NULL DEFAULT 0 COMMENT '是否是维护状态  1是0否',
  `maintenance_notice` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '维护公告',
  `game_databases_ip` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `game_databases_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `game_databases_port` int(11) NULL DEFAULT NULL,
  `game_databases_user` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `game_databases_password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `csv_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `gift_code_verify_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_s_server_list
-- ----------------------------
DROP TABLE IF EXISTS `t_s_server_list`;
CREATE TABLE `t_s_server_list`  (
  `id` int(11) NOT NULL COMMENT '服务器id',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '服务器名称',
  `server_group` int(11) NULL DEFAULT 1 COMMENT '服务器分组',
  `mark` int(11) NULL DEFAULT 0 COMMENT '服务器是否启用  1是0否',
  `game_server_external_ip` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '服务器外网ip',
  `game_server_internal_ip` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '服务器内网ip',
  `game_server_tcp_port` int(11) NULL DEFAULT NULL COMMENT '服务器外内端口',
  `game_server_http_port` int(11) NULL DEFAULT NULL COMMENT '服务器内网端口',
  `open_time` datetime(0) NULL DEFAULT NULL COMMENT '服务器开启时间',
  `end_time` datetime(0) NULL DEFAULT NULL COMMENT '服务器关闭时间',
  `last_update_time` datetime(0) NULL DEFAULT NULL COMMENT '服务器信息最后一次被修改时间',
  `max_online_num` int(11) NULL DEFAULT NULL COMMENT '服务器最大在线人数',
  `split_flow_num` int(11) NULL DEFAULT 0 COMMENT '服务器分流阀值',
  `is_test` tinyint(4) NULL DEFAULT 1 COMMENT '是否是测试服',
  `type` tinyint(4) NULL DEFAULT NULL COMMENT '服务器类型 1-游戏服 2-战斗服',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '服务器信息列表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for t_u_account
-- ----------------------------
DROP TABLE IF EXISTS `t_u_account`;
CREATE TABLE `t_u_account`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `player_id` int(11) NULL DEFAULT NULL,
  `account_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `channel_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `server_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0',
  `create_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `column_account_channel`(`account_id`, `channel_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 123599189 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for t_u_account_permission
-- ----------------------------
DROP TABLE IF EXISTS `t_u_account_permission`;
CREATE TABLE `t_u_account_permission`  (
  `player_id` int(11) NOT NULL,
  `type` int(11) NOT NULL,
  `start_time` datetime(0) NULL DEFAULT NULL,
  `end_time` datetime(0) NULL DEFAULT NULL,
  `reason` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `operator` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`player_id`, `type`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
