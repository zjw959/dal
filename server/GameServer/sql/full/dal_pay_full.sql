/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50718
 Source Host           : 127.0.0.1:3306
 Source Schema         : dal_pay

 Target Server Type    : MySQL
 Target Server Version : 50718
 File Encoding         : 65001

 Date: 28/07/2018 19:51:09
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_pay_0
-- ----------------------------
DROP TABLE IF EXISTS `t_pay_0`;
CREATE TABLE `t_pay_0`  (
  `order_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '游戏内订单id',
  `channel_order_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道订单id',
  `channel_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道号',
  `channel_appid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '子渠道号',
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户账号名',
  `player_id` bigint(20) NULL DEFAULT NULL COMMENT '玩家id',
  `item_id` int(11) NULL DEFAULT NULL COMMENT '充值道具id',
  `sell_amount` int(11) NULL DEFAULT NULL COMMENT '道具出售金额（分）',
  `pay_amount` int(11) NULL DEFAULT NULL COMMENT '实际付款金额（分）',
  `status` smallint(11) NULL DEFAULT NULL COMMENT '订单状态',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '订单创建时间',
  `callback_time` datetime(0) NULL DEFAULT NULL COMMENT '渠道回调时间',
  `modify_time` datetime(0) NULL DEFAULT NULL COMMENT '订单状态变化时间',
  `extinfo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '额外信息',
  `htnonce` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '回调信息记录',
  `httoken` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '回调信息记录',
  PRIMARY KEY (`order_id`) USING BTREE,
  UNIQUE INDEX `order_id`(`order_id`) USING BTREE,
  INDEX `player_id`(`player_id`) USING BTREE,
  INDEX `channel_order_id`(`channel_order_id`) USING BTREE,
  INDEX `status`(`status`) USING BTREE,
  INDEX `status_2`(`status`, `modify_time`) USING BTREE,
  INDEX `create_time`(`create_time`) USING BTREE,
  INDEX `player_id_1`(`player_id`, `status`) USING BTREE,
  INDEX `channel_id`(`channel_id`) USING BTREE,
  INDEX `channel_id_2`(`channel_id`, `channel_appid`) USING BTREE,
  INDEX `user_name`(`user_name`) USING BTREE,
  INDEX `modify_time`(`modify_time`) USING BTREE,
  INDEX `status_3`(`status`, `create_time`) USING BTREE,
  INDEX `channel_id_3`(`channel_id`, `status`, `modify_time`) USING BTREE,
  INDEX `channel_id_4`(`channel_id`, `channel_appid`, `status`, `modify_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '平台充值回调后的订单记录表，都是有效订单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_pay_1
-- ----------------------------
DROP TABLE IF EXISTS `t_pay_1`;
CREATE TABLE `t_pay_1`  (
  `order_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '游戏内订单id',
  `channel_order_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道订单id',
  `channel_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道号',
  `channel_appid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '子渠道号',
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户账号名',
  `player_id` bigint(20) NULL DEFAULT NULL COMMENT '玩家id',
  `item_id` int(11) NULL DEFAULT NULL COMMENT '充值道具id',
  `sell_amount` int(11) NULL DEFAULT NULL COMMENT '道具出售金额（分）',
  `pay_amount` int(11) NULL DEFAULT NULL COMMENT '实际付款金额（分）',
  `status` smallint(11) NULL DEFAULT NULL COMMENT '订单状态',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '订单创建时间',
  `callback_time` datetime(0) NULL DEFAULT NULL COMMENT '渠道回调时间',
  `modify_time` datetime(0) NULL DEFAULT NULL COMMENT '订单状态变化时间',
  `extinfo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '额外信息',
  `htnonce` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '回调信息记录',
  `httoken` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '回调信息记录',
  PRIMARY KEY (`order_id`) USING BTREE,
  UNIQUE INDEX `order_id`(`order_id`) USING BTREE,
  INDEX `player_id`(`player_id`) USING BTREE,
  INDEX `channel_order_id`(`channel_order_id`) USING BTREE,
  INDEX `status`(`status`) USING BTREE,
  INDEX `status_2`(`status`, `modify_time`) USING BTREE,
  INDEX `create_time`(`create_time`) USING BTREE,
  INDEX `player_id_1`(`player_id`, `status`) USING BTREE,
  INDEX `channel_id`(`channel_id`) USING BTREE,
  INDEX `channel_id_2`(`channel_id`, `channel_appid`) USING BTREE,
  INDEX `user_name`(`user_name`) USING BTREE,
  INDEX `modify_time`(`modify_time`) USING BTREE,
  INDEX `status_3`(`status`, `create_time`) USING BTREE,
  INDEX `channel_id_3`(`channel_id`, `status`, `modify_time`) USING BTREE,
  INDEX `channel_id_4`(`channel_id`, `channel_appid`, `status`, `modify_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '平台充值回调后的订单记录表，都是有效订单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_pay_2
-- ----------------------------
DROP TABLE IF EXISTS `t_pay_2`;
CREATE TABLE `t_pay_2`  (
  `order_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '游戏内订单id',
  `channel_order_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道订单id',
  `channel_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道号',
  `channel_appid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '子渠道号',
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户账号名',
  `player_id` bigint(20) NULL DEFAULT NULL COMMENT '玩家id',
  `item_id` int(11) NULL DEFAULT NULL COMMENT '充值道具id',
  `sell_amount` int(11) NULL DEFAULT NULL COMMENT '道具出售金额（分）',
  `pay_amount` int(11) NULL DEFAULT NULL COMMENT '实际付款金额（分）',
  `status` smallint(11) NULL DEFAULT NULL COMMENT '订单状态',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '订单创建时间',
  `callback_time` datetime(0) NULL DEFAULT NULL COMMENT '渠道回调时间',
  `modify_time` datetime(0) NULL DEFAULT NULL COMMENT '订单状态变化时间',
  `extinfo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '额外信息',
  `htnonce` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '回调信息记录',
  `httoken` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '回调信息记录',
  PRIMARY KEY (`order_id`) USING BTREE,
  UNIQUE INDEX `order_id`(`order_id`) USING BTREE,
  INDEX `player_id`(`player_id`) USING BTREE,
  INDEX `channel_order_id`(`channel_order_id`) USING BTREE,
  INDEX `status`(`status`) USING BTREE,
  INDEX `status_2`(`status`, `modify_time`) USING BTREE,
  INDEX `create_time`(`create_time`) USING BTREE,
  INDEX `player_id_1`(`player_id`, `status`) USING BTREE,
  INDEX `channel_id`(`channel_id`) USING BTREE,
  INDEX `channel_id_2`(`channel_id`, `channel_appid`) USING BTREE,
  INDEX `user_name`(`user_name`) USING BTREE,
  INDEX `modify_time`(`modify_time`) USING BTREE,
  INDEX `status_3`(`status`, `create_time`) USING BTREE,
  INDEX `channel_id_3`(`channel_id`, `status`, `modify_time`) USING BTREE,
  INDEX `channel_id_4`(`channel_id`, `channel_appid`, `status`, `modify_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '平台充值回调后的订单记录表，都是有效订单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_pay_3
-- ----------------------------
DROP TABLE IF EXISTS `t_pay_3`;
CREATE TABLE `t_pay_3`  (
  `order_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '游戏内订单id',
  `channel_order_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道订单id',
  `channel_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道号',
  `channel_appid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '子渠道号',
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户账号名',
  `player_id` bigint(20) NULL DEFAULT NULL COMMENT '玩家id',
  `item_id` int(11) NULL DEFAULT NULL COMMENT '充值道具id',
  `sell_amount` int(11) NULL DEFAULT NULL COMMENT '道具出售金额（分）',
  `pay_amount` int(11) NULL DEFAULT NULL COMMENT '实际付款金额（分）',
  `status` smallint(11) NULL DEFAULT NULL COMMENT '订单状态',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '订单创建时间',
  `callback_time` datetime(0) NULL DEFAULT NULL COMMENT '渠道回调时间',
  `modify_time` datetime(0) NULL DEFAULT NULL COMMENT '订单状态变化时间',
  `extinfo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '额外信息',
  `htnonce` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '回调信息记录',
  `httoken` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '回调信息记录',
  PRIMARY KEY (`order_id`) USING BTREE,
  UNIQUE INDEX `order_id`(`order_id`) USING BTREE,
  INDEX `player_id`(`player_id`) USING BTREE,
  INDEX `channel_order_id`(`channel_order_id`) USING BTREE,
  INDEX `status`(`status`) USING BTREE,
  INDEX `status_2`(`status`, `modify_time`) USING BTREE,
  INDEX `create_time`(`create_time`) USING BTREE,
  INDEX `player_id_1`(`player_id`, `status`) USING BTREE,
  INDEX `channel_id`(`channel_id`) USING BTREE,
  INDEX `channel_id_2`(`channel_id`, `channel_appid`) USING BTREE,
  INDEX `user_name`(`user_name`) USING BTREE,
  INDEX `modify_time`(`modify_time`) USING BTREE,
  INDEX `status_3`(`status`, `create_time`) USING BTREE,
  INDEX `channel_id_3`(`channel_id`, `status`, `modify_time`) USING BTREE,
  INDEX `channel_id_4`(`channel_id`, `channel_appid`, `status`, `modify_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '平台充值回调后的订单记录表，都是有效订单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_pay_4
-- ----------------------------
DROP TABLE IF EXISTS `t_pay_4`;
CREATE TABLE `t_pay_4`  (
  `order_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '游戏内订单id',
  `channel_order_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道订单id',
  `channel_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道号',
  `channel_appid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '子渠道号',
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户账号名',
  `player_id` bigint(20) NULL DEFAULT NULL COMMENT '玩家id',
  `item_id` int(11) NULL DEFAULT NULL COMMENT '充值道具id',
  `sell_amount` int(11) NULL DEFAULT NULL COMMENT '道具出售金额（分）',
  `pay_amount` int(11) NULL DEFAULT NULL COMMENT '实际付款金额（分）',
  `status` smallint(11) NULL DEFAULT NULL COMMENT '订单状态',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '订单创建时间',
  `callback_time` datetime(0) NULL DEFAULT NULL COMMENT '渠道回调时间',
  `modify_time` datetime(0) NULL DEFAULT NULL COMMENT '订单状态变化时间',
  `extinfo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '额外信息',
  `htnonce` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '回调信息记录',
  `httoken` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '回调信息记录',
  PRIMARY KEY (`order_id`) USING BTREE,
  UNIQUE INDEX `order_id`(`order_id`) USING BTREE,
  INDEX `player_id`(`player_id`) USING BTREE,
  INDEX `channel_order_id`(`channel_order_id`) USING BTREE,
  INDEX `status`(`status`) USING BTREE,
  INDEX `status_2`(`status`, `modify_time`) USING BTREE,
  INDEX `create_time`(`create_time`) USING BTREE,
  INDEX `player_id_1`(`player_id`, `status`) USING BTREE,
  INDEX `channel_id`(`channel_id`) USING BTREE,
  INDEX `channel_id_2`(`channel_id`, `channel_appid`) USING BTREE,
  INDEX `user_name`(`user_name`) USING BTREE,
  INDEX `modify_time`(`modify_time`) USING BTREE,
  INDEX `status_3`(`status`, `create_time`) USING BTREE,
  INDEX `channel_id_3`(`channel_id`, `status`, `modify_time`) USING BTREE,
  INDEX `channel_id_4`(`channel_id`, `channel_appid`, `status`, `modify_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '平台充值回调后的订单记录表，都是有效订单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_pay_5
-- ----------------------------
DROP TABLE IF EXISTS `t_pay_5`;
CREATE TABLE `t_pay_5`  (
  `order_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '游戏内订单id',
  `channel_order_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道订单id',
  `channel_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道号',
  `channel_appid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '子渠道号',
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户账号名',
  `player_id` bigint(20) NULL DEFAULT NULL COMMENT '玩家id',
  `item_id` int(11) NULL DEFAULT NULL COMMENT '充值道具id',
  `sell_amount` int(11) NULL DEFAULT NULL COMMENT '道具出售金额（分）',
  `pay_amount` int(11) NULL DEFAULT NULL COMMENT '实际付款金额（分）',
  `status` smallint(11) NULL DEFAULT NULL COMMENT '订单状态',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '订单创建时间',
  `callback_time` datetime(0) NULL DEFAULT NULL COMMENT '渠道回调时间',
  `modify_time` datetime(0) NULL DEFAULT NULL COMMENT '订单状态变化时间',
  `extinfo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '额外信息',
  `htnonce` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '回调信息记录',
  `httoken` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '回调信息记录',
  PRIMARY KEY (`order_id`) USING BTREE,
  UNIQUE INDEX `order_id`(`order_id`) USING BTREE,
  INDEX `player_id`(`player_id`) USING BTREE,
  INDEX `channel_order_id`(`channel_order_id`) USING BTREE,
  INDEX `status`(`status`) USING BTREE,
  INDEX `status_2`(`status`, `modify_time`) USING BTREE,
  INDEX `create_time`(`create_time`) USING BTREE,
  INDEX `player_id_1`(`player_id`, `status`) USING BTREE,
  INDEX `channel_id`(`channel_id`) USING BTREE,
  INDEX `channel_id_2`(`channel_id`, `channel_appid`) USING BTREE,
  INDEX `user_name`(`user_name`) USING BTREE,
  INDEX `modify_time`(`modify_time`) USING BTREE,
  INDEX `status_3`(`status`, `create_time`) USING BTREE,
  INDEX `channel_id_3`(`channel_id`, `status`, `modify_time`) USING BTREE,
  INDEX `channel_id_4`(`channel_id`, `channel_appid`, `status`, `modify_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '平台充值回调后的订单记录表，都是有效订单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_pay_6
-- ----------------------------
DROP TABLE IF EXISTS `t_pay_6`;
CREATE TABLE `t_pay_6`  (
  `order_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '游戏内订单id',
  `channel_order_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道订单id',
  `channel_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道号',
  `channel_appid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '子渠道号',
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户账号名',
  `player_id` bigint(20) NULL DEFAULT NULL COMMENT '玩家id',
  `item_id` int(11) NULL DEFAULT NULL COMMENT '充值道具id',
  `sell_amount` int(11) NULL DEFAULT NULL COMMENT '道具出售金额（分）',
  `pay_amount` int(11) NULL DEFAULT NULL COMMENT '实际付款金额（分）',
  `status` smallint(11) NULL DEFAULT NULL COMMENT '订单状态',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '订单创建时间',
  `callback_time` datetime(0) NULL DEFAULT NULL COMMENT '渠道回调时间',
  `modify_time` datetime(0) NULL DEFAULT NULL COMMENT '订单状态变化时间',
  `extinfo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '额外信息',
  `htnonce` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '回调信息记录',
  `httoken` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '回调信息记录',
  PRIMARY KEY (`order_id`) USING BTREE,
  UNIQUE INDEX `order_id`(`order_id`) USING BTREE,
  INDEX `player_id`(`player_id`) USING BTREE,
  INDEX `channel_order_id`(`channel_order_id`) USING BTREE,
  INDEX `status`(`status`) USING BTREE,
  INDEX `status_2`(`status`, `modify_time`) USING BTREE,
  INDEX `create_time`(`create_time`) USING BTREE,
  INDEX `player_id_1`(`player_id`, `status`) USING BTREE,
  INDEX `channel_id`(`channel_id`) USING BTREE,
  INDEX `channel_id_2`(`channel_id`, `channel_appid`) USING BTREE,
  INDEX `user_name`(`user_name`) USING BTREE,
  INDEX `modify_time`(`modify_time`) USING BTREE,
  INDEX `status_3`(`status`, `create_time`) USING BTREE,
  INDEX `channel_id_3`(`channel_id`, `status`, `modify_time`) USING BTREE,
  INDEX `channel_id_4`(`channel_id`, `channel_appid`, `status`, `modify_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '平台充值回调后的订单记录表，都是有效订单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_pay_7
-- ----------------------------
DROP TABLE IF EXISTS `t_pay_7`;
CREATE TABLE `t_pay_7`  (
  `order_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '游戏内订单id',
  `channel_order_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道订单id',
  `channel_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道号',
  `channel_appid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '子渠道号',
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户账号名',
  `player_id` bigint(20) NULL DEFAULT NULL COMMENT '玩家id',
  `item_id` int(11) NULL DEFAULT NULL COMMENT '充值道具id',
  `sell_amount` int(11) NULL DEFAULT NULL COMMENT '道具出售金额（分）',
  `pay_amount` int(11) NULL DEFAULT NULL COMMENT '实际付款金额（分）',
  `status` smallint(11) NULL DEFAULT NULL COMMENT '订单状态',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '订单创建时间',
  `callback_time` datetime(0) NULL DEFAULT NULL COMMENT '渠道回调时间',
  `modify_time` datetime(0) NULL DEFAULT NULL COMMENT '订单状态变化时间',
  `extinfo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '额外信息',
  `htnonce` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '回调信息记录',
  `httoken` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '回调信息记录',
  PRIMARY KEY (`order_id`) USING BTREE,
  UNIQUE INDEX `order_id`(`order_id`) USING BTREE,
  INDEX `player_id`(`player_id`) USING BTREE,
  INDEX `channel_order_id`(`channel_order_id`) USING BTREE,
  INDEX `status`(`status`) USING BTREE,
  INDEX `status_2`(`status`, `modify_time`) USING BTREE,
  INDEX `create_time`(`create_time`) USING BTREE,
  INDEX `player_id_1`(`player_id`, `status`) USING BTREE,
  INDEX `channel_id`(`channel_id`) USING BTREE,
  INDEX `channel_id_2`(`channel_id`, `channel_appid`) USING BTREE,
  INDEX `user_name`(`user_name`) USING BTREE,
  INDEX `modify_time`(`modify_time`) USING BTREE,
  INDEX `status_3`(`status`, `create_time`) USING BTREE,
  INDEX `channel_id_3`(`channel_id`, `status`, `modify_time`) USING BTREE,
  INDEX `channel_id_4`(`channel_id`, `channel_appid`, `status`, `modify_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '平台充值回调后的订单记录表，都是有效订单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_pay_8
-- ----------------------------
DROP TABLE IF EXISTS `t_pay_8`;
CREATE TABLE `t_pay_8`  (
  `order_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '游戏内订单id',
  `channel_order_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道订单id',
  `channel_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道号',
  `channel_appid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '子渠道号',
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户账号名',
  `player_id` bigint(20) NULL DEFAULT NULL COMMENT '玩家id',
  `item_id` int(11) NULL DEFAULT NULL COMMENT '充值道具id',
  `sell_amount` int(11) NULL DEFAULT NULL COMMENT '道具出售金额（分）',
  `pay_amount` int(11) NULL DEFAULT NULL COMMENT '实际付款金额（分）',
  `status` smallint(11) NULL DEFAULT NULL COMMENT '订单状态',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '订单创建时间',
  `callback_time` datetime(0) NULL DEFAULT NULL COMMENT '渠道回调时间',
  `modify_time` datetime(0) NULL DEFAULT NULL COMMENT '订单状态变化时间',
  `extinfo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '额外信息',
  `htnonce` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '回调信息记录',
  `httoken` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '回调信息记录',
  PRIMARY KEY (`order_id`) USING BTREE,
  UNIQUE INDEX `order_id`(`order_id`) USING BTREE,
  INDEX `player_id`(`player_id`) USING BTREE,
  INDEX `channel_order_id`(`channel_order_id`) USING BTREE,
  INDEX `status`(`status`) USING BTREE,
  INDEX `status_2`(`status`, `modify_time`) USING BTREE,
  INDEX `create_time`(`create_time`) USING BTREE,
  INDEX `player_id_1`(`player_id`, `status`) USING BTREE,
  INDEX `channel_id`(`channel_id`) USING BTREE,
  INDEX `channel_id_2`(`channel_id`, `channel_appid`) USING BTREE,
  INDEX `user_name`(`user_name`) USING BTREE,
  INDEX `modify_time`(`modify_time`) USING BTREE,
  INDEX `status_3`(`status`, `create_time`) USING BTREE,
  INDEX `channel_id_3`(`channel_id`, `status`, `modify_time`) USING BTREE,
  INDEX `channel_id_4`(`channel_id`, `channel_appid`, `status`, `modify_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '平台充值回调后的订单记录表，都是有效订单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_pay_9
-- ----------------------------
DROP TABLE IF EXISTS `t_pay_9`;
CREATE TABLE `t_pay_9`  (
  `order_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '游戏内订单id',
  `channel_order_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道订单id',
  `channel_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道号',
  `channel_appid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '子渠道号',
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户账号名',
  `player_id` bigint(20) NULL DEFAULT NULL COMMENT '玩家id',
  `item_id` int(11) NULL DEFAULT NULL COMMENT '充值道具id',
  `sell_amount` int(11) NULL DEFAULT NULL COMMENT '道具出售金额（分）',
  `pay_amount` int(11) NULL DEFAULT NULL COMMENT '实际付款金额（分）',
  `status` smallint(11) NULL DEFAULT NULL COMMENT '订单状态',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '订单创建时间',
  `callback_time` datetime(0) NULL DEFAULT NULL COMMENT '渠道回调时间',
  `modify_time` datetime(0) NULL DEFAULT NULL COMMENT '订单状态变化时间',
  `extinfo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '额外信息',
  `htnonce` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '回调信息记录',
  `httoken` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '回调信息记录',
  PRIMARY KEY (`order_id`) USING BTREE,
  UNIQUE INDEX `order_id`(`order_id`) USING BTREE,
  INDEX `player_id`(`player_id`) USING BTREE,
  INDEX `channel_order_id`(`channel_order_id`) USING BTREE,
  INDEX `status`(`status`) USING BTREE,
  INDEX `status_2`(`status`, `modify_time`) USING BTREE,
  INDEX `create_time`(`create_time`) USING BTREE,
  INDEX `player_id_1`(`player_id`, `status`) USING BTREE,
  INDEX `channel_id`(`channel_id`) USING BTREE,
  INDEX `channel_id_2`(`channel_id`, `channel_appid`) USING BTREE,
  INDEX `user_name`(`user_name`) USING BTREE,
  INDEX `modify_time`(`modify_time`) USING BTREE,
  INDEX `status_3`(`status`, `create_time`) USING BTREE,
  INDEX `channel_id_3`(`channel_id`, `status`, `modify_time`) USING BTREE,
  INDEX `channel_id_4`(`channel_id`, `channel_appid`, `status`, `modify_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '平台充值回调后的订单记录表，都是有效订单' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
