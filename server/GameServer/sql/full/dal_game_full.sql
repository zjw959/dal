/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 50717
 Source Host           : 127.0.0.1:3306
 Source Schema         : dal_game

 Target Server Type    : MySQL
 Target Server Version : 50717
 File Encoding         : 65001

 Date: 11/06/2018 16:54:04
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_player
-- ----------------------------
CREATE TABLE `t_player`  (
  `playerid` int(20) NOT NULL,
  `playername` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色名',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账号名',
  `channelid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道ID',
  `channelappid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道appId',
  `currentserver` int(11) NULL DEFAULT NULL COMMENT '当前登录服务器ID',
  `createtime` bigint(20) NULL DEFAULT NULL COMMENT '创建角色时间',
  `isforbid` int(11) NULL DEFAULT NULL COMMENT '是否封号',
  `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `level` int(11) NULL DEFAULT NULL COMMENT '角色等级',
  `viplevel` int(11) NULL DEFAULT 0 COMMENT 'VIP等级',
  `exp` bigint(20) NULL DEFAULT 0 COMMENT '经验值',
  `gmlevel` int(11) NULL DEFAULT 0 COMMENT 'GM等级',
  `data` json NULL COMMENT '其他数据',
  `logintime` bigint(20) NULL DEFAULT NULL COMMENT '等级时间',
  `offlinetime` bigint(20) NULL DEFAULT NULL COMMENT '上次离线时间',
  `onlinetime` bigint(20) NULL DEFAULT NULL COMMENT '总在线时长',
  `lastlogintime` bigint(20) NULL DEFAULT NULL COMMENT '上次登录时间',
  `gold` bigint(20) NULL DEFAULT NULL COMMENT '金币',
  `systemdiamond` bigint(20) NULL DEFAULT NULL COMMENT '系统钻石',
  `rechargediamond` bigint(20) NULL DEFAULT NULL COMMENT '充值钻石',
  `strength` int(11) NULL DEFAULT NULL COMMENT '角色体力',
  `fightpower` bigint(20) NULL DEFAULT NULL COMMENT '战斗力',
  INDEX `index_lvl` (`level`),
  PRIMARY KEY (`playerid`) USING BTREE,
  UNIQUE INDEX `playerid`(`playerid`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci;

SET FOREIGN_KEY_CHECKS = 1;
