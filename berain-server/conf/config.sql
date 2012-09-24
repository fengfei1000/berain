CREATE TABLE `rain_config` (
  `id` bigint(64) unsigned NOT NULL AUTO_INCREMENT,
  `pid` bigint(64) unsigned NOT NULL DEFAULT '0',
  `path` varchar(760) NOT NULL,
  `skey` varchar(128) COLLATE utf8_bin NOT NULL,
  `svalue` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `smd5` varchar(32) COLLATE utf8_bin NOT NULL DEFAULT '',
  `create_at` datetime NOT NULL DEFAULT '2012-08-28 14:00:00',
  `update_at` datetime NOT NULL DEFAULT '2012-08-28 14:00:00',
  PRIMARY KEY (`id`),
  KEY `key_index` (`skey`),
  KEY `pid_index` (`pid`),
  KEY `parent_pid_idx` (`pid`),
  CONSTRAINT `parent_pid` FOREIGN KEY (`pid`) REFERENCES `rain_config` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
