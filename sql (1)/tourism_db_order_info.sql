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
-- Table structure for table `order_info`
--

DROP TABLE IF EXISTS `order_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_info` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(50) NOT NULL COMMENT '订单号',
  `user_id` int NOT NULL COMMENT '用户ID',
  `scenic_id` int NOT NULL COMMENT '景点ID',
  `scenic_name` varchar(100) NOT NULL COMMENT '景点名称',
  `quantity` int NOT NULL DEFAULT '1' COMMENT '购票数量',
  `unit_price` decimal(10,2) NOT NULL COMMENT '单价',
  `total_amount` decimal(10,2) NOT NULL COMMENT '订单总金额',
  `status` varchar(20) DEFAULT 'PENDING' COMMENT '支付状态: PENDING/PAID/CANCELLED/REFUNDED',
  `visit_date` date DEFAULT NULL COMMENT '游玩日期',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_scenic_id` (`scenic_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_info`
--

LOCK TABLES `order_info` WRITE;
/*!40000 ALTER TABLE `order_info` DISABLE KEYS */;
INSERT INTO `order_info` VALUES 
(1,'20260409163206447747',2,2,'Эрмитаж',2,800.00,1600.00,'PAID','2026-06-15','2026-04-09 17:32:06'),
(2,'20260409163309803432',2,3,'Петергоф',1,1000.00,1000.00,'PAID','2026-06-16','2026-04-09 17:33:09'),
(3,'20260409163343240019',3,4,'Московский Кремль',1,1000.00,1000.00,'PAID','2026-05-20','2026-04-09 17:33:43'),
(4,'20260409163454142427',2,6,'Собор Василия Блаженного',2,700.00,1400.00,'REFUNDED','2026-05-21','2026-04-09 17:34:54'),
(5,'20260409163545194189',3,7,'Третьяковская галерея',1,500.00,500.00,'PAID','2026-05-25','2026-04-09 17:35:45'),
(6,'20260409164949963964',2,9,'Царское Село',2,700.00,1400.00,'PAID','2026-06-17','2026-04-09 17:49:49'),
(7,'20260409165420809087',3,5,'Храм Христа Спасителя',1,0.00,0.00,'PAID','2026-05-22','2026-04-09 17:54:20'),
(8,'20260409165503914012',2,8,'Большой театр',1,3000.00,3000.00,'CANCELLED','2026-06-20','2026-04-09 17:55:03');
/*!40000 ALTER TABLE `order_info` ENABLE KEYS */;
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
