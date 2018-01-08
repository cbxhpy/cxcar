/*
SQLyog 企业版 - MySQL GUI v8.14 
MySQL - 5.5.34 : Database - xxw
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`xxw` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `xxw`;

/*Table structure for table `eop_app` */

DROP TABLE IF EXISTS `eop_app`;

CREATE TABLE `eop_app` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `appid` varchar(50) DEFAULT NULL,
  `app_name` varchar(50) DEFAULT NULL,
  `author` varchar(50) DEFAULT NULL,
  `descript` longtext,
  `deployment` int(11) DEFAULT '1',
  `path` varchar(255) DEFAULT NULL,
  `authorizationcode` varchar(50) DEFAULT NULL,
  `installuri` varchar(255) DEFAULT NULL,
  `deleteflag` int(6) DEFAULT '0',
  `version` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

/*Data for the table `eop_app` */

insert  into `eop_app`(`id`,`appid`,`app_name`,`author`,`descript`,`deployment`,`path`,`authorizationcode`,`installuri`,`deleteflag`,`version`) values (1,'base','base应用',NULL,'base应用',0,'/core',NULL,NULL,0,'2.2.0'),(2,'cms','cms应用',NULL,'cms应用',0,'/cms',NULL,NULL,0,'2.2.0'),(3,'shop','shop应用',NULL,'shop应用',0,'/shop',NULL,NULL,0,'2.2.0');

/*Table structure for table `eop_ask` */

DROP TABLE IF EXISTS `eop_ask`;

CREATE TABLE `eop_ask` (
  `askid` int(8) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  `content` longtext,
  `dateline` int(11) DEFAULT NULL,
  `isreply` smallint(1) DEFAULT NULL,
  `userid` int(8) DEFAULT NULL,
  `siteid` int(8) DEFAULT NULL,
  `domain` varchar(255) DEFAULT NULL,
  `sitename` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`askid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `eop_ask` */

/*Table structure for table `eop_data_log` */

DROP TABLE IF EXISTS `eop_data_log`;

CREATE TABLE `eop_data_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `content` longtext,
  `url` varchar(255) DEFAULT NULL,
  `pics` longtext,
  `sitename` varchar(255) DEFAULT NULL,
  `domain` varchar(255) DEFAULT NULL,
  `logtype` varchar(50) DEFAULT NULL,
  `optype` varchar(50) DEFAULT NULL,
  `dateline` int(11) DEFAULT NULL,
  `userid` bigint(20) DEFAULT NULL,
  `siteid` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=844 DEFAULT CHARSET=utf8;

/*Data for the table `eop_data_log` */


/*Table structure for table `eop_reply` */

DROP TABLE IF EXISTS `eop_reply`;

CREATE TABLE `eop_reply` (
  `replyid` int(8) NOT NULL AUTO_INCREMENT,
  `askid` int(8) DEFAULT NULL,
  `content` longtext,
  `username` varchar(255) DEFAULT NULL,
  `dateline` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`replyid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `eop_reply` */

/*Table structure for table `eop_site` */

DROP TABLE IF EXISTS `eop_site`;

CREATE TABLE `eop_site` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userid` int(11) DEFAULT NULL,
  `sitename` varchar(255) DEFAULT NULL,
  `productid` varchar(50) DEFAULT NULL,
  `descript` longtext,
  `icofile` varchar(255) DEFAULT NULL,
  `logofile` varchar(255) DEFAULT NULL,
  `deleteflag` int(6) DEFAULT '0',
  `keywords` varchar(255) DEFAULT NULL,
  `themepath` varchar(50) DEFAULT NULL,
  `adminthemeid` int(11) DEFAULT NULL,
  `themeid` int(11) DEFAULT NULL,
  `point` int(11) DEFAULT '0',
  `createtime` bigint(20) DEFAULT NULL,
  `lastlogin` bigint(20) DEFAULT NULL,
  `lastgetpoint` bigint(20) DEFAULT NULL,
  `logincount` int(11) DEFAULT NULL,
  `bkloginpicfile` varchar(255) DEFAULT NULL,
  `bklogofile` varchar(255) DEFAULT NULL,
  `sumpoint` bigint(20) DEFAULT '0',
  `sumaccess` bigint(20) DEFAULT '0',
  `title` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `usersex` int(11) DEFAULT NULL,
  `usertel` varchar(50) DEFAULT NULL,
  `usermobile` varchar(50) DEFAULT NULL,
  `usertel1` varchar(50) DEFAULT NULL,
  `useremail` varchar(50) DEFAULT NULL,
  `state` int(6) DEFAULT '1',
  `qqlist` varchar(255) DEFAULT '25106942:客户服务!comma;52560956:技术支持',
  `msnlist` varchar(255) DEFAULT NULL,
  `wwlist` varchar(255) DEFAULT NULL,
  `tellist` varchar(255) DEFAULT NULL,
  `worktime` varchar(255) DEFAULT '9:00到18:00',
  `siteon` int(6) DEFAULT '0',
  `closereson` varchar(255) DEFAULT NULL,
  `copyright` varchar(1000) DEFAULT 'Copyright &copy; 2010-2012 本公司版权所有',
  `icp` varchar(255) DEFAULT '京ICP备05037293号',
  `address` varchar(255) DEFAULT '北京市某区某街某号',
  `zipcode` varchar(50) DEFAULT '000000',
  `qq` int(11) DEFAULT '1',
  `msn` int(11) DEFAULT '0',
  `ww` int(11) DEFAULT '0',
  `tel` int(11) DEFAULT '0',
  `wt` int(11) DEFAULT '1',
  `linkman` varchar(255) DEFAULT '刘先生',
  `linktel` varchar(255) DEFAULT '010-61750491',
  `email` varchar(255) DEFAULT 'enation@126.com',
  `multi_site` smallint(1) DEFAULT '0',
  `relid` varchar(255) DEFAULT NULL,
  `sitestate` smallint(1) DEFAULT '0',
  `isimported` smallint(1) DEFAULT '0',
  `imptype` int(6) DEFAULT '0',
  `mobilesite` smallint(1) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Data for the table `eop_site` */

insert  into `eop_site`(`id`,`userid`,`sitename`,`productid`,`descript`,`icofile`,`logofile`,`deleteflag`,`keywords`,`themepath`,`adminthemeid`,`themeid`,`point`,`createtime`,`lastlogin`,`lastgetpoint`,`logincount`,`bkloginpicfile`,`bklogofile`,`sumpoint`,`sumaccess`,`title`,`username`,`usersex`,`usertel`,`usermobile`,`usertel1`,`useremail`,`state`,`qqlist`,`msnlist`,`wwlist`,`tellist`,`worktime`,`siteon`,`closereson`,`copyright`,`icp`,`address`,`zipcode`,`qq`,`msn`,`ww`,`tel`,`wt`,`linkman`,`linktel`,`email`,`multi_site`,`relid`,`sitestate`,`isimported`,`imptype`,`mobilesite`) values (1,1,'嘻嘻网','simple','嘻嘻网','fs:/attachment/spec/201510251100248982.png','fs:/attachment/spec/201510250923168560.png',0,'嘻嘻网','default',1,1,1000,1433256000,1448072091,0,225,NULL,'fs:/attachment/spec/201510250923168560.png',0,0,'嘻嘻网','admin',NULL,'010-12345678','13888888888',NULL,'youmail@domain.com',1,'25106942:客户服务!comma;52560956:技术支持',NULL,NULL,NULL,'9:00到18:00',1,'','Copyright &copy; 2010-2012 本公司版权所有','京ICP备05037293号','北京市某区某街某号','000000',0,0,0,0,0,'刘先生','010-61750491','enation@126.com',0,NULL,0,0,0,1);

/*Table structure for table `eop_sitedomain` */

DROP TABLE IF EXISTS `eop_sitedomain`;

CREATE TABLE `eop_sitedomain` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `domain` varchar(255) DEFAULT NULL,
  `domaintype` int(6) DEFAULT '0',
  `siteid` int(11) DEFAULT NULL,
  `userid` int(11) DEFAULT NULL,
  `status` int(6) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

/*Data for the table `eop_sitedomain` */

insert  into `eop_sitedomain`(`id`,`domain`,`domaintype`,`siteid`,`userid`,`status`) values (1,'localhost',0,1,1,0),(2,'27.154.54.34',0,1,1,0);

/*Table structure for table `eop_user` */

DROP TABLE IF EXISTS `eop_user`;

CREATE TABLE `eop_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT NULL,
  `companyname` varchar(50) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `legalrepresentative` varchar(50) DEFAULT NULL,
  `linkman` varchar(50) DEFAULT NULL,
  `tel` varchar(50) DEFAULT NULL,
  `mobile` varchar(50) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `logofile` varchar(255) DEFAULT NULL,
  `licensefile` varchar(255) DEFAULT NULL,
  `defaultsiteid` int(11) DEFAULT NULL,
  `deleteflag` int(6) DEFAULT '0',
  `usertype` int(6) DEFAULT NULL,
  `createtime` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Data for the table `eop_user` */

insert  into `eop_user`(`id`,`username`,`companyname`,`password`,`address`,`legalrepresentative`,`linkman`,`tel`,`mobile`,`email`,`logofile`,`licensefile`,`defaultsiteid`,`deleteflag`,`usertype`,`createtime`) values (1,'admin','单机版用户','21232f297a57a5a743894a0e4a801fc3','在这里输入详细地址',NULL,'在这里输入联系人信息','010-12345678','13888888888','youmail@domain.com',NULL,NULL,NULL,0,1,NULL);

/*Table structure for table `eop_userdetail` */

DROP TABLE IF EXISTS `eop_userdetail`;

CREATE TABLE `eop_userdetail` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userid` int(11) DEFAULT NULL,
  `bussinessscope` longtext,
  `regaddress` varchar(255) DEFAULT NULL,
  `regdate` bigint(20) DEFAULT NULL,
  `corpscope` int(6) DEFAULT '0',
  `corpdescript` longtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `eop_userdetail` */

/*Table structure for table `es_access` */

DROP TABLE IF EXISTS `es_access`;

CREATE TABLE `es_access` (
  `id` int(9) NOT NULL AUTO_INCREMENT,
  `ip` varchar(255) DEFAULT NULL,
  `url` varchar(1000) DEFAULT NULL,
  `page` varchar(255) DEFAULT NULL,
  `area` varchar(255) DEFAULT NULL,
  `access_time` int(11) DEFAULT NULL,
  `stay_time` int(11) DEFAULT NULL,
  `point` int(11) DEFAULT '0',
  `membername` varchar(255) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `es_access` */

/*Table structure for table `es_account_log` */

DROP TABLE IF EXISTS `es_account_log`;

CREATE TABLE `es_account_log` (
  `log_id` int(8) NOT NULL AUTO_INCREMENT,
  `user_id` int(8) DEFAULT NULL,
  `user_money` decimal(20,2) DEFAULT NULL,
  `frozen_money` decimal(20,2) DEFAULT NULL,
  `rank_points` int(9) DEFAULT NULL,
  `pay_points` decimal(20,2) DEFAULT NULL,
  `friend_points` decimal(20,2) DEFAULT NULL,
  `change_time` bigint(20) DEFAULT NULL,
  `change_desc` varchar(20) DEFAULT NULL,
  `change_type` smallint(1) DEFAULT NULL,
  `frozen_friend_points` int(11) DEFAULT NULL,
  `add_credit_account_money` decimal(20,2) DEFAULT NULL,
  PRIMARY KEY (`log_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `es_account_log` */

/*Table structure for table `es_adcolumn` */

DROP TABLE IF EXISTS `es_adcolumn`;

CREATE TABLE `es_adcolumn` (
  `acid` int(11) NOT NULL AUTO_INCREMENT,
  `cname` varchar(255) DEFAULT NULL,
  `width` varchar(50) DEFAULT NULL,
  `height` varchar(50) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `anumber` bigint(20) DEFAULT NULL,
  `atype` int(11) DEFAULT NULL,
  `arule` bigint(20) DEFAULT NULL,
  `disabled` varchar(5) DEFAULT 'false',
  PRIMARY KEY (`acid`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8;

/*Data for the table `es_adcolumn` */

insert  into `es_adcolumn`(`acid`,`cname`,`width`,`height`,`description`,`anumber`,`atype`,`arule`,`disabled`) values (1,'首页最上方广告','1920px','372px',NULL,NULL,0,NULL,'false'),(20,'1F 轮播广告','394','267',NULL,NULL,0,NULL,'false'),(21,'1F 小图片广告','210','255',NULL,NULL,0,NULL,'false'),(22,'1F 底部横幅广告','1200','90',NULL,NULL,0,NULL,'false'),(23,'2F 轮播广告','394','267',NULL,NULL,0,NULL,'false'),(24,'2F 小图片广告','210','255',NULL,NULL,0,NULL,'false'),(25,'2F 底部横幅广告','1200','90',NULL,NULL,0,NULL,'false'),(26,'3F 轮播广告','394','267','',NULL,0,NULL,'false'),(27,'3F 小图片广告','210','255','',NULL,0,NULL,'false'),(28,'3F 底部横幅广告','1200','90','',NULL,0,NULL,'false'),(29,'4F 轮播广告','394','267','',NULL,0,NULL,'false'),(30,'4F 小图片广告','210','255','',NULL,0,NULL,'false'),(31,'4F 底部横幅广告','1200','90','',NULL,0,NULL,'false'),(32,'5F 轮播广告','394','267','',NULL,0,NULL,'false'),(33,'5F 小图片广告','210','255','',NULL,0,NULL,'false'),(34,'5F 底部横幅广告','1200','90','',NULL,0,NULL,'false'),(35,'手机热卖推荐左边1张','','',NULL,NULL,0,NULL,'false'),(36,'手机热卖推荐右边2张','','',NULL,NULL,0,NULL,'false'),(37,'手机首页1F1张','','',NULL,NULL,0,NULL,'false'),(38,'手机首页2F1张','','',NULL,NULL,0,NULL,'false'),(39,'手机首页3F1张','','',NULL,NULL,0,NULL,'false'),(40,'手机横幅广告','','',NULL,NULL,0,NULL,'false');

/*Table structure for table `es_admintheme` */

DROP TABLE IF EXISTS `es_admintheme`;

CREATE TABLE `es_admintheme` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `appid` varchar(50) DEFAULT NULL,
  `siteid` int(11) DEFAULT NULL,
  `themename` varchar(50) DEFAULT NULL,
  `path` varchar(255) DEFAULT NULL,
  `userid` int(11) DEFAULT NULL,
  `author` varchar(50) DEFAULT NULL,
  `version` varchar(50) DEFAULT NULL,
  `framemode` int(6) DEFAULT '0',
  `deleteflag` int(6) DEFAULT '0',
  `thumb` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Data for the table `es_admintheme` */

insert  into `es_admintheme`(`id`,`appid`,`siteid`,`themename`,`path`,`userid`,`author`,`version`,`framemode`,`deleteflag`,`thumb`) values (1,NULL,NULL,'新模板','new',NULL,'enation','2.0',0,0,'preview.png');

/*Table structure for table `es_adminuser` */

DROP TABLE IF EXISTS `es_adminuser`;

CREATE TABLE `es_adminuser` (
  `userid` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `state` int(6) DEFAULT NULL,
  `realname` varchar(255) DEFAULT NULL,
  `userno` varchar(255) DEFAULT NULL,
  `userdept` varchar(255) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `dateline` int(11) DEFAULT NULL,
  `founder` smallint(1) DEFAULT NULL,
  `siteid` int(11) DEFAULT NULL,
  PRIMARY KEY (`userid`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

/*Data for the table `es_adminuser` */

insert  into `es_adminuser`(`userid`,`username`,`password`,`state`,`realname`,`userno`,`userdept`,`remark`,`dateline`,`founder`,`siteid`) values (1,'administrator','21232f297a57a5a743894a0e4a801fc3',1,'超级','','','',NULL,1,NULL),(2,'chanpin','e10adc3949ba59abbe56e057f20f883e',1,NULL,NULL,NULL,NULL,NULL,0,NULL),(3,'kuguan','e10adc3949ba59abbe56e057f20f883e',1,NULL,NULL,NULL,NULL,NULL,0,NULL),(4,'caiwu','e10adc3949ba59abbe56e057f20f883e',1,NULL,NULL,NULL,NULL,NULL,0,NULL),(5,'kefu','e10adc3949ba59abbe56e057f20f883e',1,NULL,NULL,NULL,NULL,NULL,0,NULL),(6,'admin','e00cf25ad42683b3df678c61f42c6bda',1,'管理员','','','',NULL,1,NULL),(7,'jiashijie','e10adc3949ba59abbe56e057f20f883e',1,'家视界','spgl','商品管理部','商品管理部',NULL,0,NULL),(8,'欧国青','c10367a48b25120d8d09d31696309b06',1,'欧国青','xx005','嘻嘻购总部','运营管理',NULL,1,NULL),(10,'袁熙尧','782869f3eebf1a862ccdd7fb3797e026',1,'袁熙尧','xx001','董事长','',NULL,0,NULL);

/*Table structure for table `es_adminuser_copy` */

DROP TABLE IF EXISTS `es_adminuser_copy`;

CREATE TABLE `es_adminuser_copy` (
  `userid` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `state` int(6) DEFAULT NULL,
  `realname` varchar(255) DEFAULT NULL,
  `userno` varchar(255) DEFAULT NULL,
  `userdept` varchar(255) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `dateline` int(11) DEFAULT NULL,
  `founder` smallint(1) DEFAULT NULL,
  `siteid` int(11) DEFAULT NULL,
  PRIMARY KEY (`userid`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

/*Data for the table `es_adminuser_copy` */

insert  into `es_adminuser_copy`(`userid`,`username`,`password`,`state`,`realname`,`userno`,`userdept`,`remark`,`dateline`,`founder`,`siteid`) values (1,'administrator','21232f297a57a5a743894a0e4a801fc3',1,'超级','','','',NULL,1,NULL),(2,'chanpin','e10adc3949ba59abbe56e057f20f883e',1,NULL,NULL,NULL,NULL,NULL,0,NULL),(3,'kuguan','e10adc3949ba59abbe56e057f20f883e',1,NULL,NULL,NULL,NULL,NULL,0,NULL),(4,'caiwu','e10adc3949ba59abbe56e057f20f883e',1,NULL,NULL,NULL,NULL,NULL,0,NULL),(5,'kefu','e10adc3949ba59abbe56e057f20f883e',1,NULL,NULL,NULL,NULL,NULL,0,NULL),(6,'admin','21232f297a57a5a743894a0e4a801fc3',1,'管理员','','','',NULL,0,NULL),(7,'jiashijie','e10adc3949ba59abbe56e057f20f883e',1,'家视界','spgl','商品管理部','商品管理部',NULL,0,NULL);

/*Table structure for table `es_adv` */

DROP TABLE IF EXISTS `es_adv`;

CREATE TABLE `es_adv` (
  `aid` bigint(20) NOT NULL AUTO_INCREMENT,
  `acid` bigint(20) DEFAULT NULL,
  `atype` int(11) DEFAULT NULL,
  `begintime` bigint(20) DEFAULT NULL,
  `endtime` bigint(20) DEFAULT NULL,
  `isclose` int(11) DEFAULT NULL,
  `attachment` varchar(50) DEFAULT NULL,
  `atturl` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `aname` varchar(255) DEFAULT NULL,
  `clickcount` int(11) DEFAULT '0',
  `linkman` varchar(50) DEFAULT NULL,
  `company` varchar(255) DEFAULT NULL,
  `contact` varchar(255) DEFAULT NULL,
  `disabled` varchar(5) DEFAULT 'false',
  PRIMARY KEY (`aid`)
) ENGINE=InnoDB AUTO_INCREMENT=71 DEFAULT CHARSET=utf8;

/*Data for the table `es_adv` */

insert  into `es_adv`(`aid`,`acid`,`atype`,`begintime`,`endtime`,`isclose`,`attachment`,`atturl`,`url`,`aname`,`clickcount`,`linkman`,`company`,`contact`,`disabled`) values (1,1,NULL,1328025600000,1614182400000,0,NULL,'fs:/attachment/adv/201510221432428635.jpg','/search-sort-8.html','一样的购买',0,'','','','false'),(2,1,NULL,1328025600000,1613750400000,0,NULL,'fs:/attachment/adv/201510221432272233.jpg','/search-sort-8.html','嘻嘻buy',0,'','','','false'),(3,1,NULL,1328025600000,1582041600000,0,NULL,'fs:/attachment/adv/201510221432108192.jpg','/search-sort-8.html','金山在手',0,'','','','false'),(4,1,NULL,1328025600000,1613059200000,0,NULL,'fs:/attachment/adv/201510221431412354.jpg','/search-sort-8.html','花本来该花的',1,'','','','false'),(30,20,NULL,1443628800,1451491200,0,NULL,'fs:/attachment/adv/201511161501181217.jpg','search-cat-1.html','吃货最幸福',0,'','','','false'),(31,20,NULL,1443628800,1477584000,0,NULL,'fs:/attachment/adv/201511161501010547.jpg','goods-381.html','超值特惠',0,'','','','false'),(32,21,NULL,1443628800,1477843200,0,NULL,'fs:/attachment/adv/201511161500342428.jpg','search-cat-1.html','1F小广告',0,'','','','false'),(33,22,NULL,1443628800,1509379200,0,NULL,'fs:/attachment/adv/201510241014343341.jpg','/register.html','1F底部横幅广告',0,'','','','false'),(37,33,NULL,1445270400,1477324800,0,NULL,'fs:/attachment/adv/201511161618449529.jpg','goods-346.html','海外精选，免税包邮',0,'','','','false'),(38,30,NULL,1445184000,1476979200,0,NULL,'fs:/attachment/adv/201511161459553575.jpg','http://www.xxw.cn/goods-299.html','养生壶',0,'','','','false'),(40,27,NULL,1444838400,1476201600,0,NULL,'fs:/attachment/adv/201511161557472197.jpg','http://www.xxw.cn/search-cat-38.html','洗发沐浴',0,'','','','false'),(41,32,NULL,1444579200,1476633600,0,NULL,'fs:/attachment/adv/201511161629041740.jpg','goods-360.html','西洋参胶原蛋白水凝保湿面膜',0,'','','','false'),(42,26,NULL,1445270400,1475424000,0,NULL,'fs:/attachment/adv/201510291334207857.jpg','search-cat-38.html','家居新世界',0,'','','','false'),(43,29,NULL,1443974400,1475510400,0,NULL,'fs:/attachment/adv/201511161442293545.jpg','http://www.xxw.cn/goods-302.html','大有净界',0,'','','','false'),(44,26,NULL,1444579200,1476806400,0,NULL,'fs:/attachment/adv/201511161620409793.jpg','goods-382.html','美好生活，从“齿”开始',0,'','','','false'),(45,29,NULL,1446048000,1477411200,0,NULL,'fs:/attachment/adv/201511161455272698.jpg','http://www.xxw.cn/goods-329.html','安全出行',0,'','','','false'),(46,32,NULL,1444060800,1476201600,0,NULL,'fs:/attachment/adv/201511161620542627.jpg','goods-351.html','博兰伊美',0,'','','','false'),(47,1,NULL,1444579200,1538496000,0,NULL,'fs:/attachment/adv/201510221433111338.jpg','','消费有惊喜',0,'','','','false'),(51,1,NULL,1445616000,1477670400,0,NULL,'fs:/attachment/adv/201510241410422315.jpg','http://www.xxw.cn/search-cat-128.html','嘻嘻',0,'','','','false'),(52,1,NULL,1445616000,1477670400,0,NULL,'fs:/attachment/adv/201510241541070392.jpg','http://www.xxw.cn/search-sort-8.html','上线',0,'','','','false'),(53,1,NULL,1445616000,1446220800,0,NULL,'http://120.25.63.142/statics/attachment/adv/201510241548225597.jpg','','1212嘻嘻商城隆重上线',0,'','','','false'),(54,25,NULL,1445702400,1477670400,0,NULL,'fs:/attachment/adv/201510271200326176.jpg','','家居用品',0,'','','','false'),(55,28,NULL,1445702400,1477670400,0,NULL,'fs:/attachment/adv/201510271203070078.gif','','数码电器',0,'','','','false'),(56,31,NULL,1445702400,1477670400,0,NULL,'fs:/attachment/adv/201510271201280947.jpg','','还原本质裸机色',0,'','','','false'),(57,1,NULL,1445961600,1477670400,0,NULL,'http://www.xxw.cn/statics/attachment/adv/201510281658377576.jpg','','12.12',0,'','','','false'),(59,35,NULL,1446307200,1448812800,0,NULL,'http://www.xxw.cn/statics/attachment/adv/201511011825290001.jpg','','1',0,'','','','false'),(60,36,NULL,1446307200,1448812800,0,NULL,'http://www.xxw.cn/statics/attachment/adv/201511011826085440.jpg','','2',0,'','','','false'),(61,36,NULL,1446307200,1448812800,0,NULL,'http://www.xxw.cn/statics/attachment/adv/201511011826262373.jpg','','3',0,'','','','false'),(62,37,NULL,1446307200,1448812800,0,NULL,'http://www.xxw.cn/statics/attachment/adv/201511011913155340.jpg','','1',0,'','','','false'),(63,38,NULL,1446307200,1448812800,0,NULL,'http://www.xxw.cn/statics/attachment/adv/201511011913505845.jpg','','2',0,'','','','false'),(64,39,NULL,1446307200,1448812800,0,NULL,'http://www.xxw.cn/statics/attachment/adv/201511011914100634.jpg','','1',0,'','','','false'),(65,40,NULL,1446307200,1448812800,0,NULL,'http://www.xxw.cn/statics/attachment/adv/201511012013311477.jpg','','1',0,'','','','false'),(66,40,NULL,1446307200,1446307200,0,NULL,'http://www.xxw.cn/statics/attachment/adv/201511012013547560.jpg','','2',0,'','','','false'),(67,40,NULL,1446307200,1448812800,0,NULL,'http://www.xxw.cn/statics/attachment/adv/201511012014116808.jpg','','3',0,'','','','false'),(68,23,NULL,1447430400,1510329600,0,NULL,'fs:/attachment/adv/201511161431115970.jpg','http://www.xxw.cn/goods-345.html','即炖燕窝',0,'','','','false'),(69,24,NULL,1447430400,1478188800,0,NULL,'fs:/attachment/adv/201511161112467958.png','http://www.xxw.cn/search-cat-4.html','健康生活踏青季',0,'','','','false'),(70,23,NULL,1447430400,1479484800,0,NULL,'fs:/attachment/adv/201511161402258109.jpg','http://www.xxw.cn/goods-354.html','优质生活每一天',0,'','','','false');

/*Table structure for table `es_adv_copy` */

DROP TABLE IF EXISTS `es_adv_copy`;

CREATE TABLE `es_adv_copy` (
  `aid` bigint(20) NOT NULL AUTO_INCREMENT,
  `acid` bigint(20) DEFAULT NULL,
  `atype` int(11) DEFAULT NULL,
  `begintime` bigint(20) DEFAULT NULL,
  `endtime` bigint(20) DEFAULT NULL,
  `isclose` int(11) DEFAULT NULL,
  `attachment` varchar(50) DEFAULT NULL,
  `atturl` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `aname` varchar(255) DEFAULT NULL,
  `clickcount` int(11) DEFAULT '0',
  `linkman` varchar(50) DEFAULT NULL,
  `company` varchar(255) DEFAULT NULL,
  `contact` varchar(255) DEFAULT NULL,
  `disabled` varchar(5) DEFAULT 'false',
  PRIMARY KEY (`aid`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8;

/*Data for the table `es_adv_copy` */

insert  into `es_adv_copy`(`aid`,`acid`,`atype`,`begintime`,`endtime`,`isclose`,`attachment`,`atturl`,`url`,`aname`,`clickcount`,`linkman`,`company`,`contact`,`disabled`) values (1,1,NULL,1328025600000,1614182400000,0,NULL,'fs:/attachment/adv/201509211640332452.jpg','/search-cat-35.html','潮人双肩包69元起',0,'','','','false'),(2,1,NULL,1328025600000,1613750400000,0,NULL,'fs:/attachment/adv/201509211640379479.jpg','/search-cat-86.html','男士POLO衫特价68元',0,'','','','false'),(3,1,NULL,1328025600000,1582041600000,0,NULL,'fs:/attachment/adv/201509211640424515.jpg','/search-cat-87.html','真空压缩袋节省空间67%',0,'','','','false'),(4,1,NULL,1328025600000,1613059200000,0,NULL,'fs:/attachment/adv/201509211640512233.jpg','/search-cat-86.html','早春运动针织长裤69元起',1,'','','','false'),(7,2,NULL,1328025600000,1582128000000,0,NULL,'http://static.v4.javamall.com.cn/attachment/adv/201202281306593822.jpg','/search-cat-86.html','多彩帆布鞋把春天叫醒',0,'','','','false'),(8,3,NULL,1328025600000,1613491200000,0,NULL,'http://static.v4.javamall.com.cn/attachment/adv/201202281306167304.jpg','/search-cat-86.html','皮质帆船鞋经典至极',0,'','','','false'),(9,4,NULL,1328025600000,1614096000000,0,NULL,'http://static.v4.javamall.com.cn/attachment/adv/201202281435584831.jpg','/search-cat-86.html','换季女装畅销款',0,'','','','false'),(10,5,NULL,1328112000000,1614441600000,0,NULL,'http://static.v4.javamall.com.cn/attachment/adv/201202281448180726.jpg','/','商品列表横幅',0,'','','','false'),(11,6,NULL,1328112000000,1614441600000,0,NULL,'fs:/attachment/adv/201206281222116564.jpg','/','1',0,'','','','false'),(12,6,NULL,1328112000000,1614441600000,0,NULL,'fs:/attachment/adv/201206281224515367.jpg','/','2',0,'','','','false'),(13,6,NULL,1328112000000,1614441600000,0,NULL,'fs:/attachment/adv/201206281225338663.jpg','/','3',0,'','','','false'),(14,7,NULL,1338480000000,1624982400000,0,NULL,'fs:/attachment/adv/201207041909541698.jpg','#','闪光玻璃纱性感文胸',0,'','','','false'),(15,7,NULL,1338480000000,1624982400000,0,NULL,'fs:/attachment/adv/201207041920088824.jpg','#','柔美针织开衫',0,'','','','false'),(16,7,NULL,1338480000000,1624982400000,0,NULL,'fs:/attachment/adv/201207041921117839.jpg','','大热性感女鞋系列',0,'','','','false'),(17,7,NULL,1338480000000,1624982400000,0,NULL,'fs:/attachment/adv/201207041922031419.jpg','','莫代尔弹力花边吊带',0,'','','','false'),(18,7,NULL,1338480000000,1624982400000,0,NULL,'fs:/attachment/adv/201207041922550628.jpg','#','精美富贵花中跟凉拖',0,'','','','false'),(19,8,NULL,1346428800000,1598889600000,1,NULL,'http://localhost:8080/javamall/statics/attachment/adv/201209301144199462.jpg','','首页小广告一',0,'','','','false'),(20,8,NULL,1346428800000,1598889600000,0,NULL,'fs:/attachment/adv/201507011000083635.png','','首页小广告二',0,'','','','false'),(21,8,NULL,1346428800000,1598889600000,0,NULL,'fs:/attachment/adv/201510161357003036.jpg','','首页小广告三',0,'','','','false'),(22,10,NULL,1435939200,1468425600,0,NULL,'fs:/attachment/adv/201509221040326743.jpg','','1F茶壶广告',0,'','','','false'),(23,9,NULL,1435939200,1468425600,0,NULL,'fs:/attachment/adv/201509221041074323.jpg','','2F智能广告',0,'','','','false'),(24,11,NULL,1435852800,1468425600,0,NULL,'http://120.25.63.142/statics/attachment/adv/201510161354415406.jpg','','3F家居广告',0,'','','','false'),(25,12,NULL,1435766400,1468425600,0,NULL,'fs:/attachment/adv/201509221042388995.jpg','','4F建材广告',0,'','','','false'),(26,13,NULL,1435852800,1468425600,0,NULL,'fs:/attachment/adv/201509221042523939.jpg','','5F瓷砖广告',0,'','','','false'),(27,14,NULL,1435852800,1458057600,0,NULL,'fs:/attachment/adv/201509221044076308.jpg','','商品分类-家具',0,'','','','false'),(28,15,NULL,1435852800,1458057600,0,NULL,'fs:/attachment/adv/201507040959179089.png','','商品分类-建材',0,'','','','false'),(29,1,NULL,1436889600,1438704000,1,NULL,'http://localhost:8080/xxw/statics/attachment/adv/201507232341227134.jpg','www.tmall.com','首页小广告4',0,'','','','false');

/*Table structure for table `es_advance_logs` */

DROP TABLE IF EXISTS `es_advance_logs`;

CREATE TABLE `es_advance_logs` (
  `log_id` int(8) NOT NULL AUTO_INCREMENT,
  `member_id` int(8) NOT NULL,
  `money` decimal(20,2) NOT NULL,
  `message` varchar(255) DEFAULT NULL,
  `mtime` bigint(20) NOT NULL,
  `payment_id` varchar(20) DEFAULT NULL,
  `order_id` varchar(20) DEFAULT NULL,
  `paymethod` varchar(100) DEFAULT NULL,
  `memo` varchar(100) DEFAULT NULL,
  `import_money` decimal(20,2) NOT NULL DEFAULT '0.00',
  `explode_money` decimal(20,2) NOT NULL DEFAULT '0.00',
  `member_advance` decimal(20,2) NOT NULL DEFAULT '0.00',
  `shop_advance` decimal(20,2) NOT NULL DEFAULT '0.00',
  `disabled` varchar(5) NOT NULL DEFAULT 'false',
  PRIMARY KEY (`log_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;

/*Data for the table `es_advance_logs` */

insert  into `es_advance_logs`(`log_id`,`member_id`,`money`,`message`,`mtime`,`payment_id`,`order_id`,`paymethod`,`memo`,`import_money`,`explode_money`,`member_advance`,`shop_advance`,`disabled`) values (7,78,'0.10',NULL,1446687397,'1','21054221424375137778','alipay','充值','0.10','0.00','0.00','0.00','false'),(8,78,'0.10',NULL,1446688577,'1','90874333524962324918','alipay','充值','0.10','0.00','0.00','0.00','false'),(9,78,'0.01',NULL,1446689272,'1','20124570612162554243','alipay','充值','0.01','0.00','0.00','0.00','false'),(10,79,'1000.00',NULL,1446715124,'1','70535766486403600658','alipay','充值','1000.00','0.00','0.00','0.00','false'),(11,79,'1.00',NULL,1446715204,'1','72056122818374584985','alipay','充值','1.00','0.00','0.00','0.00','true'),(12,5,'100.00',NULL,1447494428,'1','44592664223099804170','alipay','充值','100.00','0.00','0.00','0.00','false'),(13,5,'100.00',NULL,1447748161,'1','96625809570310824275','alipay','充值','100.00','0.00','0.00','0.00','false');

/*Table structure for table `es_agent` */

DROP TABLE IF EXISTS `es_agent`;

CREATE TABLE `es_agent` (
  `agentid` int(11) NOT NULL AUTO_INCREMENT,
  `parentid` int(11) DEFAULT NULL,
  `username` varchar(50) DEFAULT NULL,
  `tel` varchar(50) DEFAULT NULL,
  `mobile` varchar(50) DEFAULT NULL,
  `sex` int(6) DEFAULT '0',
  `zip` varchar(50) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `city` varchar(50) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `qq` varchar(50) DEFAULT NULL,
  `ww` varchar(50) DEFAULT NULL,
  `msn` varchar(50) DEFAULT NULL,
  `id_card` varchar(50) DEFAULT NULL,
  `bank_account` varchar(50) DEFAULT NULL,
  `bank_username` varchar(50) DEFAULT NULL,
  `bank_name` varchar(50) DEFAULT NULL,
  `bank_city` varchar(50) DEFAULT NULL,
  `shop_url` varchar(50) DEFAULT NULL,
  `state` int(6) DEFAULT '0',
  `dateline` int(11) DEFAULT NULL,
  PRIMARY KEY (`agentid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `es_agent` */

/*Table structure for table `es_agent_transfer` */

DROP TABLE IF EXISTS `es_agent_transfer`;

CREATE TABLE `es_agent_transfer` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `memberid` int(11) DEFAULT NULL,
  `price` decimal(20,2) DEFAULT NULL,
  `is_transfer` smallint(1) DEFAULT NULL,
  `blank_account` varchar(50) DEFAULT NULL,
  `blank_username` varchar(50) DEFAULT NULL,
  `blank_name` varchar(50) DEFAULT NULL,
  `blank_city` varchar(50) DEFAULT NULL,
  `apply_time` int(11) DEFAULT NULL,
  `transfer_time` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `es_agent_transfer` */

/*Table structure for table `es_agent_yongjin_history` */

DROP TABLE IF EXISTS `es_agent_yongjin_history`;

CREATE TABLE `es_agent_yongjin_history` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `member_id` int(11) DEFAULT NULL,
  `agent_level` int(11) DEFAULT NULL COMMENT '1区县代理\r\n            2市级代理\r\n            3省级代理\r\n            4全国代理\r\n            5一星董事\r\n            6二星董事\r\n            7三星董事',
  `reason` varchar(100) DEFAULT NULL,
  `xxw_month` varchar(7) DEFAULT NULL,
  `yongjin` decimal(20,2) DEFAULT NULL,
  `create_time` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

/*Data for the table `es_agent_yongjin_history` */

insert  into `es_agent_yongjin_history`(`id`,`member_id`,`agent_level`,`reason`,`xxw_month`,`yongjin`,`create_time`) values (1,5,1,'2015-11月分红嘻币','2015-11','5561.24',1447427430),(2,4,1,'2015-11月分红嘻币','2015-11','7069.81',1447427430),(3,3,1,'2015-11月分红嘻币','2015-11','7997.69',1447427430),(4,1,2,'2015-11月分红嘻币','2015-11','62171.26',1447427430);

/*Table structure for table `es_allocation_item` */

DROP TABLE IF EXISTS `es_allocation_item`;

CREATE TABLE `es_allocation_item` (
  `allocationid` int(11) NOT NULL AUTO_INCREMENT,
  `itemid` int(11) DEFAULT NULL,
  `orderid` int(11) DEFAULT NULL,
  `depotid` int(11) DEFAULT NULL,
  `goodsid` int(11) DEFAULT NULL,
  `productid` int(11) DEFAULT NULL,
  `num` int(11) DEFAULT NULL,
  `other` longtext,
  `iscmpl` smallint(1) DEFAULT '0',
  PRIMARY KEY (`allocationid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `es_allocation_item` */

/*Table structure for table `es_article` */

DROP TABLE IF EXISTS `es_article`;

CREATE TABLE `es_article` (
  `id` int(8) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  `content` longtext,
  `create_time` bigint(20) DEFAULT NULL,
  `cat_id` int(8) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `es_article` */

/*Table structure for table `es_article_cat` */

DROP TABLE IF EXISTS `es_article_cat`;

CREATE TABLE `es_article_cat` (
  `cat_id` int(8) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) DEFAULT NULL,
  `parent_id` int(8) DEFAULT NULL,
  `cat_path` varchar(200) DEFAULT NULL,
  `cat_order` int(5) DEFAULT NULL,
  PRIMARY KEY (`cat_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `es_article_cat` */

/*Table structure for table `es_auth_action` */

DROP TABLE IF EXISTS `es_auth_action`;

CREATE TABLE `es_auth_action` (
  `actid` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `objvalue` longtext,
  `choose` int(11) DEFAULT NULL,
  PRIMARY KEY (`actid`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

/*Data for the table `es_auth_action` */

insert  into `es_auth_action`(`actid`,`name`,`type`,`objvalue`,`choose`) values (1,'超级管理员','menu','1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,127,128,129,31,32,33,34,35,36,37,38,39,40,41,42,43,44,133,134,45,46,47,48,49,50,51,52,130,131,132,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,90,91,92,93,135,136,137,138,139,140,141,142',1),(2,'商品管理权限','menu','1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,36,37,38,39,40,41,49,50,51,52,53',0),(3,'订单管理权限','menu','15,16,17,18,19,20,21,22,23,24,25',1),(4,'库管权限','menu','1,2,3,5,6,15,16,19,23',1),(5,'财务权限','menu','1,2,6,15,16,18,21,22,84,85',0),(6,'客服权限','menu','15,16,17,26,27,28,29,30,31,32',1),(7,'普通管理员','menu','1,2,3,4,5,6,7,8,9,10,11,86,87,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,81,84,85,90,91,92',0);

/*Table structure for table `es_bonus_goods` */

DROP TABLE IF EXISTS `es_bonus_goods`;

CREATE TABLE `es_bonus_goods` (
  `rel_id` int(11) NOT NULL AUTO_INCREMENT,
  `bonus_type_id` int(11) DEFAULT NULL,
  `goods_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`rel_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `es_bonus_goods` */

/*Table structure for table `es_bonus_type` */

DROP TABLE IF EXISTS `es_bonus_type`;

CREATE TABLE `es_bonus_type` (
  `type_id` int(8) NOT NULL AUTO_INCREMENT,
  `type_name` varchar(60) DEFAULT NULL,
  `type_money` decimal(10,2) DEFAULT NULL,
  `send_type` smallint(1) DEFAULT NULL,
  `min_amount` decimal(10,2) DEFAULT NULL,
  `max_amount` decimal(10,2) DEFAULT NULL,
  `send_start_date` int(11) DEFAULT NULL,
  `send_end_date` int(11) DEFAULT NULL,
  `use_start_date` int(11) DEFAULT NULL,
  `use_end_date` int(11) DEFAULT NULL,
  `min_goods_amount` decimal(10,2) DEFAULT NULL,
  `recognition` varchar(20) DEFAULT NULL,
  `create_num` int(11) DEFAULT NULL,
  `use_num` int(11) DEFAULT '0',
  PRIMARY KEY (`type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

/*Data for the table `es_bonus_type` */

insert  into `es_bonus_type`(`type_id`,`type_name`,`type_money`,`send_type`,`min_amount`,`max_amount`,`send_start_date`,`send_end_date`,`use_start_date`,`use_end_date`,`min_goods_amount`,`recognition`,`create_num`,`use_num`) values (1,'优惠劵','10.00',0,NULL,NULL,1433260800,1435852800,1433260800,1435852800,'200.00','AA',3,0),(2,'年中促销','40.00',0,NULL,NULL,1436889600,1439568000,1436889600,1439568000,'100.00','Doad.cn',36,0);

/*Table structure for table `es_brand` */

DROP TABLE IF EXISTS `es_brand`;

CREATE TABLE `es_brand` (
  `brand_id` int(8) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `logo` varchar(255) DEFAULT NULL,
  `keywords` longtext,
  `brief` longtext,
  `url` varchar(255) DEFAULT NULL,
  `disabled` smallint(1) DEFAULT NULL,
  PRIMARY KEY (`brand_id`)
) ENGINE=InnoDB AUTO_INCREMENT=109 DEFAULT CHARSET=utf8;

/*Data for the table `es_brand` */


/*Table structure for table `es_cart` */

DROP TABLE IF EXISTS `es_cart`;

CREATE TABLE `es_cart` (
  `cart_id` int(8) NOT NULL AUTO_INCREMENT,
  `goods_id` int(9) DEFAULT NULL,
  `product_id` int(8) DEFAULT NULL,
  `itemtype` int(8) DEFAULT '0',
  `num` int(8) DEFAULT NULL,
  `weight` decimal(20,2) DEFAULT NULL,
  `session_id` varchar(50) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `price` decimal(20,2) DEFAULT NULL,
  `addon` longtext,
  PRIMARY KEY (`cart_id`)
) ENGINE=InnoDB AUTO_INCREMENT=625 DEFAULT CHARSET=utf8;

/*Data for the table `es_cart` */

insert  into `es_cart`(`cart_id`,`goods_id`,`product_id`,`itemtype`,`num`,`weight`,`session_id`,`name`,`price`,`addon`) values (210,294,421,0,1,'300.00','D4DD5508618E4524B471EF53D9B64364','抹茶曲奇饼干 下午茶美食品办公休闲零食 无添加健康手工饼干','18.00',NULL),(220,296,424,0,20,'0.00','FBCF128A057C1EC784B23B495F1E1317','新款大V领毛衣 女式学院风减龄休闲针织衫','1.00',NULL),(346,296,424,0,2,'0.00','3713F9BF7575479567C6C9CCF58D71B6','新款大V领毛衣 女式学院风减龄休闲针织衫','1.00',NULL),(347,296,424,0,1,'0.00','62746BC42C2D6A145A53157E4329E7BB','新款大V领毛衣 女式学院风减龄休闲针织衫','1.00',NULL),(353,294,421,0,1,'300.00','BC85ED3313651333CBC3DF68586B6265','抹茶曲奇饼干 下午茶美食品办公休闲零食 无添加健康手工饼干','10.00',NULL),(540,292,419,0,8,'500.00','3F68274E6CECD802C244F4F50DD9A3A3','紫燕之韵嫩白修复液','1.00',NULL),(607,319,450,0,2,'0.00','64627578F585D1ED4674033EBE5EA745','金日禾野麦片巧克力酥脆棒铁盒装','1.00',NULL),(612,292,419,0,1,'500.00','C8C8CD33DB9A0D313769E9B16F1CEC87','紫燕之韵嫩白修复液','1.00',NULL),(613,320,451,0,1,'400.00','32DF98A176FB92F82440CA9017FE40B1','金日禾野 果仁麦片巧克力铁盒装','50.00',NULL),(614,292,419,0,1,'500.00','C1B6D40B6B64F2AE185D19136E699627','紫燕之韵嫩白修复液','1.00',NULL),(615,292,419,0,1,'500.00','31F78A2A1781E7FF0CF225A355C08452','紫燕之韵嫩白修复液','1.00',NULL),(619,322,453,0,1,'37.80','F6D3396BC5B6C9901A79342847DBCEF4','金日禾野 黑芝麻糊','25.50',NULL),(620,318,449,0,1,'168.00','F49D4804F13AE2305AD5E71A3E5AAFC3','金日禾野 纤麦酥','16.50',NULL),(624,296,424,0,1,'0.00','C0635456247F2BCC7EA4BF006B6D8A59','新款大V领毛衣 女式学院风减龄休闲针织衫','1.00',NULL);

/*Table structure for table `es_comments` */

DROP TABLE IF EXISTS `es_comments`;

CREATE TABLE `es_comments` (
  `comment_id` int(8) NOT NULL AUTO_INCREMENT,
  `for_comment_id` int(8) DEFAULT NULL,
  `object_id` int(8) NOT NULL,
  `object_type` varchar(50) NOT NULL DEFAULT 'ask',
  `author_id` int(8) DEFAULT NULL,
  `author` varchar(100) DEFAULT NULL,
  `levelname` varchar(50) DEFAULT NULL,
  `contact` varchar(255) DEFAULT NULL,
  `mem_read_status` varchar(5) NOT NULL DEFAULT 'false',
  `adm_read_status` varchar(5) NOT NULL DEFAULT 'false',
  `time` bigint(20) DEFAULT NULL,
  `lastreply` bigint(20) DEFAULT NULL,
  `reply_name` varchar(100) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `acomment` longtext,
  `ip` varchar(15) DEFAULT NULL,
  `display` varchar(5) NOT NULL DEFAULT 'false',
  `p_index` varchar(2) DEFAULT NULL,
  `disabled` varchar(5) DEFAULT 'false',
  `commenttype` varchar(50) DEFAULT NULL,
  `grade` int(11) DEFAULT NULL,
  `img` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `es_comments` */

/*Table structure for table `es_component` */

DROP TABLE IF EXISTS `es_component`;

CREATE TABLE `es_component` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `componentid` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `install_state` smallint(1) DEFAULT '0',
  `enable_state` smallint(1) DEFAULT '0',
  `version` varchar(50) DEFAULT NULL,
  `author` varchar(255) DEFAULT NULL,
  `javashop_version` varchar(50) DEFAULT NULL,
  `description` longtext,
  `error_message` longtext,
  `sort_order` int(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8;

/*Data for the table `es_component` */


/*Table structure for table `es_coupons` */

DROP TABLE IF EXISTS `es_coupons`;

CREATE TABLE `es_coupons` (
  `cpns_id` int(8) NOT NULL AUTO_INCREMENT,
  `cpns_name` varchar(255) DEFAULT NULL,
  `lv_ids` longtext,


