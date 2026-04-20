-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: localhost    Database: tourism_db
-- ------------------------------------------------------
-- Server version	8.0.42

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `scenic_info`
--

DROP TABLE IF EXISTS `scenic_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scenic_info` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '景点ID',
  `category_id` int NOT NULL COMMENT '景点分类ID',
  `name` varchar(100) NOT NULL COMMENT '景点名称',
  `original_price` decimal(10,2) DEFAULT NULL COMMENT '原价',
  `price` decimal(10,2) NOT NULL COMMENT '现价',
  `description` text COMMENT '景点描述',
  `stock` int DEFAULT '0' COMMENT '门票库存',
  `cover_image` varchar(255) DEFAULT NULL COMMENT '封面图URL',
  `detail_images` text COMMENT '详情图URL列表(逗号分隔)',
  `address` varchar(255) DEFAULT NULL COMMENT '景点地址',
  `opening_hours` varchar(100) DEFAULT NULL COMMENT '开放时间',
  `status` varchar(20) DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE/INACTIVE',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`),
  KEY `idx_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='景点信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `scenic_info`
--

LOCK TABLES `scenic_info` WRITE;
/*!40000 ALTER TABLE `scenic_info` DISABLE KEYS */;
INSERT INTO `scenic_info` VALUES 
(1,1,'Красная площадь',NULL,0.00,'Главная площадь Москвы и один из самых узнаваемых символов России. Здесь расположены Кремль, Собор Василия Блаженного, ГУМ и Мавзолей Ленина.',10000,'/images/scenic/red_square.jpg',NULL,'Москва, Красная площадь','Круглосуточно','ACTIVE','2026-04-03 18:50:18'),
(2,2,'Эрмитаж',800.00,800.00,'Один из крупнейших и наиболее значимых художественных и культурно-исторических музеев мира. Коллекция насчитывает около 3 миллионов экспонатов.',5000,'/images/scenic/hermitage.jpg',NULL,'Санкт-Петербург, Дворцовая площадь, 2','10:30-18:00','ACTIVE','2026-04-03 18:50:18'),
(3,2,'Петергоф',1000.00,1000.00,'Дворцово-парковый ансамбль на южном берегу Финского залива. Известен своими фонтанами и парками, часто называется «Русским Версалем».',3000,'/images/scenic/peterhof.jpg',NULL,'Санкт-Петербург, Петергоф','09:00-20:00','ACTIVE','2026-04-03 18:50:18'),
(4,1,'Московский Кремль',1000.00,1000.00,'Древнейшая часть Москвы, официальная резиденция Президента России. Включён в список Всемирного наследия ЮНЕСКО.',8000,'/images/scenic/kremlin.jpg',NULL,'Москва, Кремль','10:00-17:00','ACTIVE','2026-04-03 18:50:18'),
(5,4,'Храм Христа Спасителя',NULL,0.00,'Кафедральный собор Русской православной церкви. Самый высокий православный храм в мире.',5000,'/images/scenic/cathedral.jpg',NULL,'Москва, ул. Волхонка, 15','10:00-17:00','ACTIVE','2026-04-03 18:50:18'),
(6,4,'Собор Василия Блаженного',700.00,700.00,'Православный храм на Красной площади, один из самых ярких символов Москвы и России.',4000,'/images/scenic/basil.jpg',NULL,'Москва, Красная площадь, 2','11:00-17:00','ACTIVE','2026-04-03 18:50:18'),
(7,2,'Третьяковская галерея',500.00,500.00,'Художественный музей, основанный купцом Павлом Третьяковым. Крупнейшее собрание русского изобразительного искусства.',3500,'/images/scenic/tretyakov.jpg',NULL,'Москва, Лаврушинский пер., 10','10:00-18:00','ACTIVE','2026-04-03 18:50:18'),
(8,6,'Большой театр',3000.00,3000.00,'Один из крупнейших в мире театров оперы и балета. Символ русской культуры и искусства.',2000,'/images/scenic/bolshoi.jpg',NULL,'Москва, Театральная площадь, 1','11:00-19:00','ACTIVE','2026-04-03 18:50:18'),
(9,2,'Царское Село',700.00,700.00,'Музей-заповедник с Екатерининским дворцом и знаменитой Янтарной комнатой.',2500,'/images/scenic/tsarskoe.jpg',NULL,'Санкт-Петербург, г. Пушкин','10:00-18:00','ACTIVE','2026-04-03 18:50:18'),
(10,3,'Озеро Байкал',NULL,0.00,'Самое глубокое озеро на планете и крупнейший природный резервуар пресной воды. Объект Всемирного наследия ЮНЕСКО.',15000,'/images/scenic/baikal.jpg',NULL,'Иркутская область','Круглосуточно','ACTIVE','2026-04-03 18:50:18'),
(11,5,'Парк Горького',NULL,0.00,'Центральный парк культуры и отдыха Москвы. Популярное место для прогулок, спорта и культурных мероприятий.',8000,'/images/scenic/gorky_park.jpg',NULL,'Москва, Крымский Вал, 9','Круглосуточно','ACTIVE','2026-04-03 18:50:18'),
(12,1,'Золотое кольцо России',5000.00,4500.00,'Туристический маршрут по древним городам Северо-Восточной Руси. Включает Сергиев Посад, Владимир, Суздаль и другие города.',1000,'/images/scenic/golden_ring.jpg',NULL,'Владимирская и Ярославская области','09:00-18:00','ACTIVE','2026-04-03 18:50:18');
/*!40000 ALTER TABLE `scenic_info` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-12 14:55:32
