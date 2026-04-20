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
-- Table structure for table `travel_note`
--

DROP TABLE IF EXISTS `travel_note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `travel_note` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '游记ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `title` varchar(200) NOT NULL COMMENT '游记标题',
  `content` text NOT NULL COMMENT '游记内容',
  `cover` varchar(255) DEFAULT NULL COMMENT '封面图URL',
  `travel_time` varchar(100) DEFAULT NULL COMMENT '行程时间',
  `days` varchar(50) DEFAULT NULL COMMENT '行程天数',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='游记分享表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `travel_note`
--

LOCK TABLES `travel_note` WRITE;
/*!40000 ALTER TABLE `travel_note` DISABLE KEYS */;
INSERT INTO `travel_note` VALUES 
(1,2,'Незабываемое путешествие в Санкт-Петербург','<p>Провели в Санкт-Петербурге 5 дней. Посетили Эрмитаж, Петергоф и Царское Село. Особенно впечатлила Янтарная комната - невероятная красота! Белые ночи создали особую атмосферу. Рекомендую всем посетить этот удивительный город.</p><p>Совет: покупайте билеты в музеи заранее онлайн, чтобы избежать очередей.</p>',NULL,'2026-06-15','5','2026-04-03 19:18:45'),
(2,3,'Байкал - жемчужина Сибири','<p>Поездка на Байкал оставила неизгладимые впечатления. Чистейшая вода, величественные горы, уникальная природа. Катались на катере, пробовали омуля, любовались закатами.</p><p>Лучшее время для посещения - июль-август, когда вода прогревается.</p>',NULL,'2026-07-20','7','2026-04-05 10:30:22');
/*!40000 ALTER TABLE `travel_note` ENABLE KEYS */;
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
