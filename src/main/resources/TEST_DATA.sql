CREATE DATABASE mydbmonster;


use mydbmonster;



DROP TABLE IF EXISTS `test_data`;
CREATE TABLE `test_data` (
  `key_id` int(11) NOT NULL AUTO_INCREMENT,
  `INT_ID` int(11) DEFAULT NULL,
  `IPADDR` varchar(255) DEFAULT NULL,
  `COMPRESS_DAY` datetime DEFAULT NULL,
  `contractnum` varchar(16) DEFAULT NULL,
  `sex` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`key_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5002 DEFAULT CHARSET=utf8;