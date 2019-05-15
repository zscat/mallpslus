/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50725
Source Host           : localhost:3306
Source Database       : mallplus

Target Server Type    : MYSQL
Target Server Version : 50725
File Encoding         : 65001

Date: 2019-05-15 09:23:26
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `pms_small_navicon_category`
-- ----------------------------
DROP TABLE IF EXISTS `pms_small_navicon_category`;
CREATE TABLE `pms_small_navicon_category` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '小程序首页分类ID',
  `title` varchar(200) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '分类名称',
  `icon` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '分类图标',
  `summary` varchar(200) CHARACTER SET utf8 DEFAULT NULL COMMENT '跳转页面',
  `content` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '跳转类型',
  `sort` int(10) DEFAULT '0' COMMENT '排序',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='小程序首页nav管理';

-- ----------------------------
-- Records of pms_small_navicon_category
-- ----------------------------
INSERT INTO `pms_small_navicon_category` VALUES ('1', '休闲食品', 'http://shlm-imagemanage.oss-cn-zhangjiakou.aliyuncs.com/mall/images/20190508/c6.png', null, null, '0');
INSERT INTO `pms_small_navicon_category` VALUES ('2', '乳品饮料', 'http://shlm-imagemanage.oss-cn-zhangjiakou.aliyuncs.com/mall/images/20190508/c4.png', null, null, '1');
INSERT INTO `pms_small_navicon_category` VALUES ('3', '实鲜水果', 'http://shlm-imagemanage.oss-cn-zhangjiakou.aliyuncs.com/mall/images/20190508/c3.png', null, null, '2');
INSERT INTO `pms_small_navicon_category` VALUES ('4', '生活用品', 'http://shlm-imagemanage.oss-cn-zhangjiakou.aliyuncs.com/mall/images/20190508/c5.png', null, null, '3');
INSERT INTO `pms_small_navicon_category` VALUES ('5', '电子数码', 'http://shlm-imagemanage.oss-cn-zhangjiakou.aliyuncs.com/mall/images/20190508/c7.png', null, null, '4');
