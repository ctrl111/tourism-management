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
-- Table structure for table `notice`
--

DROP TABLE IF EXISTS `notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notice` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `user_id` int DEFAULT NULL COMMENT '接收用户ID(NULL表示全体用户)',
  `type_code` varchar(20) DEFAULT NULL COMMENT '通知类型',
  `title` varchar(200) NOT NULL COMMENT '通知标题',
  `content` text NOT NULL COMMENT '通知内容',
  `is_read` varchar(10) DEFAULT 'NO' COMMENT '是否已读: YES/NO',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统通知表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notice`
--

LOCK TABLES `notice` WRITE;
/*!40000 ALTER TABLE `notice` DISABLE KEYS */;
INSERT INTO `notice` VALUES 
(1,NULL,'SYSTEM','Добро пожаловать на платформу управления туризмом','Добро пожаловать на нашу платформу! Вы можете просматривать информацию о достопримечательностях, бронировать билеты, делиться впечатлениями и добавлять любимые места в избранное. Приятного путешествия!','NO','2026-04-03 18:50:18'),
(2,NULL,'SYSTEM','Обновление функционала платформы','Платформа обновлена: исправлены ошибки в функции избранного, улучшен пользовательский интерфейс.','NO','2026-04-03 18:50:18'),
(3,2,'PAYMENT','Пополнение счёта','Администратор пополнил Ваш счёт на 1000 ₽. Текущий баланс: 2000.00 ₽','NO','2026-04-09 17:24:10'),
(4,2,'ORDER','Заказ успешно создан','Ваш заказ создан. Номер заказа: 20260409163206447747. Сумма: 499.00 ₽. Пожалуйста, оплатите в течение 30 минут','NO','2026-04-09 17:32:06'),
(5,2,'PAYMENT','Оплата выполнена','Заказ 20260409163206447747 успешно оплачен. Сумма платежа: 499.00 ₽','NO','2026-04-09 17:32:26'),
(6,2,'PAYMENT','Возврат средств выполнен','По заказу 20260409163206447747 выполнен возврат. Сумма 499.00 ₽ зачислена на Ваш счёт','NO','2026-04-09 17:32:34'),
(7,2,'ORDER','Заказ успешно создан','Ваш заказ создан. Номер заказа: 20260409163309803432. Сумма: 499.00 ₽. Пожалуйста, оплатите в течение 30 минут','NO','2026-04-09 17:33:09'),
(8,2,'PAYMENT','Оплата выполнена','Заказ 20260409163309803432 успешно оплачен. Сумма платежа: 499.00 ₽','NO','2026-04-09 17:33:33'),
(9,2,'PAYMENT','Возврат средств выполнен','По заказу 20260409163309803432 выполнен возврат. Сумма 499.00 ₽ зачислена на Ваш счёт','NO','2026-04-09 17:34:32'),
(10,2,'ORDER','Заказ успешно создан','Ваш заказ создан. Номер заказа: 20260409163454142427. Сумма: 190.00 ₽. Пожалуйста, оплатите в течение 30 минут','NO','2026-04-09 17:34:54'),
(11,2,'PAYMENT','Оплата выполнена','Заказ 20260409163454142427 успешно оплачен. Сумма платежа: 190.00 ₽','NO','2026-04-09 17:35:01'),
(12,2,'PAYMENT','Возврат средств выполнен','По заказу 20260409163454142427 выполнен возврат. Сумма 190.00 ₽ зачислена на Ваш счёт','NO','2026-04-09 17:35:15'),
(13,2,'ORDER','Заказ успешно создан','Ваш заказ создан. Номер заказа: 20260409163545194189. Сумма: 60.00 ₽. Пожалуйста, оплатите в течение 30 минут','NO','2026-04-09 17:35:45'),
(14,2,'PAYMENT','Оплата выполнена','Заказ 20260409163545194189 успешно оплачен. Сумма платежа: 60.00 ₽','NO','2026-04-09 17:35:53'),
(15,2,'PAYMENT','Возврат средств выполнен','По заказу 20260409163545194189 выполнен возврат. Сумма 60.00 ₽ зачислена на Ваш счёт','NO','2026-04-09 17:35:57');
/*!40000 ALTER TABLE `notice` ENABLE KEYS */;
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
