/*
 Navicat Premium Data Transfer

 Source Server         : latest
 Source Server Type    : MySQL
 Source Server Version : 50722
 Source Host           : 39.106.212.32:3306
 Source Schema         : mallplus1

 Target Server Type    : MySQL
 Target Server Version : 50722
 File Encoding         : 65001

 Date: 16/08/2019 10:20:15
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for cms_topic
-- ----------------------------
DROP TABLE IF EXISTS `cms_topic`;
CREATE TABLE `cms_topic` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `category_id` bigint(20) DEFAULT NULL COMMENT '所属分类',
  `name` varchar(255) DEFAULT NULL COMMENT '标题',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `attend_count` int(11) DEFAULT '0' COMMENT '参与人数',
  `attention_count` int(11) DEFAULT '0' COMMENT '关注人数',
  `read_count` int(11) DEFAULT '0' COMMENT '点击人数',
  `award_name` varchar(100) DEFAULT NULL COMMENT '奖品名称',
  `attend_type` varchar(100) DEFAULT NULL COMMENT '参与方式',
  `content` text COMMENT '话题内容',
  `store_id` int(11) DEFAULT '1' COMMENT '所属店铺',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `atids` varchar(255) DEFAULT NULL COMMENT '参加的用户id',
  `area_id` bigint(20) DEFAULT NULL,
  `schoolId` bigint(20) DEFAULT NULL,
  `memberId` bigint(20) DEFAULT NULL,
  `areaName` varchar(255) DEFAULT NULL,
  `schoolName` varchar(255) DEFAULT NULL,
  `memberName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='话题表';

SET FOREIGN_KEY_CHECKS = 1;
