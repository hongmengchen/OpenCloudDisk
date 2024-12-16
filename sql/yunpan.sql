
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for file
-- ----------------------------
DROP TABLE IF EXISTS `file`;
CREATE TABLE `file` (
  `fileId` int NOT NULL AUTO_INCREMENT,
  `userName` varchar(255) DEFAULT NULL,
  `filePath` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`fileId`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of file
-- ----------------------------
INSERT INTO `file` VALUES ('16', 'gxh', '\\\\image\\aaa.docx');
INSERT INTO `file` VALUES ('29', '1234', '\\\\1542611785840044218.png');

-- ----------------------------
-- Table structure for office
-- ----------------------------
DROP TABLE IF EXISTS `office`;
CREATE TABLE `office` (
  `officeid` varchar(32) NOT NULL,
  `officeMd5` varchar(32) NOT NULL,
  PRIMARY KEY (`officeMd5`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of office
-- ----------------------------
INSERT INTO `office` VALUES ('doc-mhrgmat8g5b34sp', '213DD25B7F08175BAB9118FB419D6AA7');
INSERT INTO `office` VALUES ('doc-gkjraanw4f89uu5', '3AC0DD267D32943CF839077793B7CEB5');
INSERT INTO `office` VALUES ('doc-mhrgj0i6jy7bkym', '7EE154BC4F08E825288CFD8D31637854');
INSERT INTO `office` VALUES ('doc-mhrgaywhc6zcvgt', '90524FF11CE2AA032FCFB7BE60FE32CE');
INSERT INTO `office` VALUES ('doc-mhrgu6gqp50g3cu', 'A0296CDAEC3F061C9C128EDD0BC50B23');
INSERT INTO `office` VALUES ('doc-mhrgsfmntcww6v3', 'AEA45C33ED0F609DDD8D7D688A4CD917');
INSERT INTO `office` VALUES ('doc-gkjraanw4f89uu5', 'C9F575B30E794F8A10DE898ACFFC906A');
INSERT INTO `office` VALUES ('doc-gkjraanw4f89uu5', 'E82BC0449F096AD446B20F2516DAE912');
INSERT INTO `office` VALUES ('doc-mhfd9ka8xfwku6q', 'E96654BD394B95C52DE538ACD02BC63E');
INSERT INTO `office` VALUES ('doc-mhrgpwsjcnx6qty', 'ECFE027348E05A99ABA6E54DB917F200');

-- ----------------------------
-- Table structure for share
-- ----------------------------
DROP TABLE IF EXISTS `share`;
CREATE TABLE `share` (
  `shareId` int NOT NULL AUTO_INCREMENT,
  `shareUrl` varchar(32) NOT NULL,
  `path` varchar(255) NOT NULL,
  `shareUser` varchar(20) NOT NULL,
  `status` tinyint DEFAULT '1' COMMENT '1公开 2加密 -1已取消',
  `command` varchar(4) DEFAULT NULL COMMENT '提取码',
  PRIMARY KEY (`shareId`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of share
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `password` varchar(32) DEFAULT NULL,
  `countSize` varchar(20) DEFAULT '0.0B',
  `totalSize` varchar(20) DEFAULT '10.0GB',
  PRIMARY KEY (`id`,`username`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', '1234', '81DC9BDB52D04DC20036DBD8313ED055', '14.2MB', '10.0GB');
INSERT INTO `user` VALUES ('17', 'admin', '81DC9BDB52D04DC20036DBD8313ED055', '0.0B', '10.0GB');
INSERT INTO `user` VALUES ('18', 'aaa', '202CB962AC59075B964B07152D234B70', '0.0B', '10.0GB');
INSERT INTO `user` VALUES ('20', 'gxh', '202CB962AC59075B964B07152D234B70', '7.5MB', '10.0GB');
INSERT INTO `user` VALUES ('21', '13', '532B7CBE070A3579F424988A040752F2', '0.0B', '10.0GB');
