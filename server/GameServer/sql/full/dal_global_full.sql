/*
Navicat MySQL Data Transfer

Source Server         : 内网
Source Server Version : 50722
Source Host           : 192.168.20.182:3306
Source Database       : dal_global

Target Server Type    : MYSQL
Target Server Version : 50722
File Encoding         : 65001

Date: 2018-08-18 18:52:36
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_activity
-- ----------------------------
DROP TABLE IF EXISTS `t_activity`;
CREATE TABLE `t_activity` (
  `id` int(11) NOT NULL COMMENT '活动id',
  `type` int(11) NOT NULL DEFAULT '0' COMMENT '活动类型',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '活动状态  0-无效1-强制生效2-自然时间检测',
  `title` varchar(255) DEFAULT '' COMMENT '活动标题',
  `subtitle` varchar(255) DEFAULT '' COMMENT '活动副标题',
  `beginTime` bigint(20) NOT NULL DEFAULT '0' COMMENT '开启时间',
  `endTime` bigint(20) NOT NULL DEFAULT '0' COMMENT '活动结束时间',
  `showBeginTime` bigint(20) NOT NULL DEFAULT '0' COMMENT '显示开始时间',
  `showEndTime` bigint(20) NOT NULL DEFAULT '0' COMMENT '显示结束时间',
  `createTime` bigint(20) NOT NULL DEFAULT '0' COMMENT '活动创建时间',
  `details` varchar(512) DEFAULT '' COMMENT '活动详情',
  `items` varchar(512) DEFAULT '' COMMENT '活动条目',
  `changeTime` bigint(20) NOT NULL DEFAULT '0' COMMENT '活动修改时间--游戏服务器根据这个时间判断运营是否修改过活动',
  `weight` int(11) DEFAULT NULL COMMENT '排序权重',
  `updateStatus` int(11) DEFAULT NULL COMMENT '二次确认字段  1是正在修改2是修改人员确认之后游戏服务器可读取 活动添加修改的时候  游戏服务器忽略该活动数据  ',
  `resetType` int(11) DEFAULT NULL COMMENT '是否重置  0不重置1重置',
  `resetCron` varchar(255) DEFAULT NULL COMMENT '重置表达式',
  `showIcon` varchar(255) DEFAULT NULL COMMENT '广告图',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='活动表';

-- ----------------------------
-- Table structure for t_function_switch
-- ----------------------------
DROP TABLE IF EXISTS `t_function_switch`;
CREATE TABLE `t_function_switch` (
  `type` int(11) NOT NULL COMMENT '功能开关类型',
  `status` tinyint(4) DEFAULT NULL COMMENT '状态 0-关闭 1-开启',
  PRIMARY KEY (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for t_item_shop
-- ----------------------------
DROP TABLE IF EXISTS `t_item_shop`;
CREATE TABLE `t_item_shop` (
  `id` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL COMMENT '商品名称',
  `rank` int(11) DEFAULT NULL COMMENT '排序权重',
  `limitType` int(11) DEFAULT '0' COMMENT '限购（0.不限购。1.刷新时间内限购。2.服务器时间本天内限购。3.永久限购。5.全服限购且刷新时间重置。6.全服限购夸天重置。7.全服永久限购）',
  `limitVal` int(11) DEFAULT NULL COMMENT '限购值',
  `serLimit` int(11) DEFAULT NULL COMMENT '全服时个人限购值',
  `limitDes` varchar(255) DEFAULT NULL COMMENT '限购描述',
  `goods` varchar(255) DEFAULT NULL COMMENT '道具',
  `price` varchar(255) DEFAULT NULL COMMENT '价格',
  `tag` varchar(255) DEFAULT NULL COMMENT '折扣标签',
  `batchBuy` int(11) DEFAULT NULL COMMENT '是否可以批量购买',
  `open` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_item_task
-- ----------------------------
DROP TABLE IF EXISTS `t_item_task`;
CREATE TABLE `t_item_task`  (
  `id` int(11) NOT NULL,
  `icon` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '任务图标',
  `rank` int(11) NULL DEFAULT NULL COMMENT '客户端显示顺序',
  `des` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '任务的文本描述，配置内容为string表中的id',
  `des2` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '任务的文本描述，配置内容为string表中的id',
  `resetType` int(11) NULL DEFAULT NULL COMMENT '重置类型',
  `finishCondId` int(11) NULL DEFAULT NULL COMMENT '完成条件Id',
  `finishParams` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '完成参数',
  `progress` int(11) NULL DEFAULT NULL COMMENT '当前进度',
  `reward` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '完成奖励',
  `jumpInterface` int(11) NULL DEFAULT NULL COMMENT '跳转',
  `open` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;
-- ----------------------------
-- Table structure for t_marquee
-- ----------------------------
DROP TABLE IF EXISTS `t_marquee`;
CREATE TABLE `t_marquee` (
  `id` bigint(20) NOT NULL,
  `body` varchar(1000) DEFAULT NULL COMMENT '内容',
  `weight` int(11) DEFAULT NULL COMMENT '权重（优先级）',
  `loop_count` int(11) DEFAULT NULL COMMENT '循环次数限制（0-不限制）',
  `interval_time` int(11) DEFAULT NULL COMMENT '循环展示间隔时间（秒）',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `begin_date` datetime DEFAULT NULL COMMENT '开始时间',
  `end_date` datetime DEFAULT NULL COMMENT '结束时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `time` (`create_date`) USING BTREE,
  KEY `begin_date` (`begin_date`,`end_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='remarks=跑马灯';
-- ----------------------------
-- Table structure for t_global
-- ----------------------------
CREATE TABLE `t_global`  (
  `id` int(11) NOT NULL,
  `longvalue` bigint(20) NULL DEFAULT 0,
  `stringvalue` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci;
