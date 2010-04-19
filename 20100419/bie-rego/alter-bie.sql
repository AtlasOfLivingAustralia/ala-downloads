ALTER TABLE infosource add version int default 0;

CREATE TABLE `contact` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` int(11) DEFAULT NULL,
  `contact_role` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `geo_region` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `region_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `infosource_scientific_names` (
  `infosource` bigint(20) NOT NULL,
  `scientific_names` bigint(20) NOT NULL,
  PRIMARY KEY (`infosource`,`scientific_names`),
  KEY `FK69664AE85309096C` (`scientific_names`),
  KEY `FK69664AE8C3A956F8` (`infosource`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `scientific_name` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` int(11) DEFAULT NULL,
  `common_name` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `rank` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `infosource_states` (
  `infosource` bigint(20) NOT NULL,
  `states` bigint(20) NOT NULL,
  PRIMARY KEY (`infosource`,`states`),
  KEY `FKCD9844B8C3A956F8` (`infosource`),
  KEY `FKCD9844B89287D881` (`states`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
