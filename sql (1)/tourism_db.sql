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
-- Table structure for table `comment_info`
--

DROP TABLE IF EXISTS `comment_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment_info` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `type_code` varchar(20) NOT NULL COMMENT '评论类型: 景点/游记',
  `association_id` int NOT NULL COMMENT '关联ID',
  `content` text NOT NULL COMMENT '评论内容',
  `parent_id` int DEFAULT NULL COMMENT '父评论ID(用于回复)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_type_association` (`type_code`,`association_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='评论信息表(通用)';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment_info`
--

LOCK TABLES `comment_info` WRITE;
/*!40000 ALTER TABLE `comment_info` DISABLE KEYS */;
INSERT INTO `comment_info` VALUES (1,2,'SCENIC',5,'Эрмитаж - must see в Петербурге! Коллекция потрясающая, особенно залы с импрессионистами.',NULL,'2026-04-20 19:22:11'),(2,3,'SCENIC',6,'Фонтаны Петергофа работают только летом, учитывайте это при планировании поездки.',NULL,'2026-04-20 19:22:11'),(3,4,'SCENIC',1,'Красная площадь красива в любое время года, но зимой особенно сказочная!',NULL,'2026-04-20 19:22:11'),(8,2,'TRAVEL_NOTE',1,'12345',NULL,'2026-04-20 20:13:23');
/*!40000 ALTER TABLE `comment_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `favorite`
--

DROP TABLE IF EXISTS `favorite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `favorite` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `type_code` varchar(20) NOT NULL COMMENT '收藏类型: 景点/游记',
  `association_id` int NOT NULL COMMENT '关联ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_type_association` (`user_id`,`type_code`,`association_id`),
  KEY `idx_type_association` (`type_code`,`association_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='收藏信息表(通用)';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `favorite`
--

LOCK TABLES `favorite` WRITE;
/*!40000 ALTER TABLE `favorite` DISABLE KEYS */;
INSERT INTO `favorite` VALUES (1,3,'SCENIC',1,'2026-04-20 19:47:25'),(2,3,'SCENIC',2,'2026-04-20 19:47:29'),(3,3,'SCENIC',5,'2026-04-20 19:47:32'),(4,2,'SCENIC',1,'2026-04-20 19:48:06'),(5,2,'SCENIC',2,'2026-04-20 19:48:08'),(6,2,'SCENIC',3,'2026-04-20 19:53:53'),(7,2,'SCENIC',11,'2026-04-20 20:05:59'),(8,3,'SCENIC',11,'2026-04-20 20:07:23');
/*!40000 ALTER TABLE `favorite` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统通知表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notice`
--

LOCK TABLES `notice` WRITE;
/*!40000 ALTER TABLE `notice` DISABLE KEYS */;
INSERT INTO `notice` VALUES (1,NULL,'SYSTEM','Добро пожаловать в систему!','Добро пожаловать в систему управления туризмом! Откройте для себя красоту России!','NO','2026-04-20 19:22:11'),(2,NULL,'PROMOTION','Летняя распродажа билетов','Специальное предложение! Скидки до 30% на билеты в музеи Санкт-Петербурга. Акция действует до конца месяца.','NO','2026-04-20 19:22:11'),(3,NULL,'NEWS','Новые маршруты по Золотому кольцу','Мы добавили новые экскурсионные маршруты по городам Золотого кольца России. Забронируйте сейчас!','NO','2026-04-20 19:22:11'),(4,2,'PAYMENT','Оплата выполнена успешно','Ваш заказ на посещение Эрмитажа успешно оплачен. Приятного путешествия!','YES','2026-04-20 19:22:11'),(5,1,'Заказ','Заказ создан','Заказ создан. Номер: 20260420184326573041. Сумма: 12000.00 ₽. Оплатите в течение 30 минут','NO','2026-04-20 19:43:26'),(6,1,'Оплата','Оплата выполнена','Заказ 20260420184326573041 успешно оплачен. Сумма: 12000.00 ₽','NO','2026-04-20 19:43:30'),(7,3,'Заказ','Заказ создан','Заказ создан. Номер: 20260420194624736283. Сумма: 12000.00 ₽. Оплатите в течение 30 минут','NO','2026-04-20 20:46:24'),(8,3,'Оплата','Оплата выполнена','Заказ 20260420194624736283 успешно оплачен. Сумма: 12000.00 ₽','NO','2026-04-20 20:46:25'),(9,3,'Заказ','Заказ создан','Заказ создан. Номер: 20260420194706370860. Сумма: 400.00 ₽. Оплатите в течение 30 минут','NO','2026-04-20 20:47:06'),(10,2,'Заказ','Заказ создан','Заказ создан. Номер: 20260420200603392719. Сумма: 0.00 ₽. Оплатите в течение 30 минут','YES','2026-04-20 21:06:03'),(11,2,'Оплата','Оплата выполнена','Заказ 20260420200603392719 успешно оплачен. Сумма: 0.00 ₽','YES','2026-04-20 21:06:04'),(12,2,'Заказ','Заказ создан','Заказ создан. Номер: 20260420200646245509. Сумма: 500.00 ₽. Оплатите в течение 30 минут','YES','2026-04-20 21:06:46'),(13,2,'Оплата','Оплата выполнена','Заказ 20260420200646245509 успешно оплачен. Сумма: 500.00 ₽','YES','2026-04-20 21:06:48'),(14,2,'Заказ','Заказ создан','Заказ создан. Номер: 20260420200711857187. Сумма: 500.00 ₽. Оплатите в течение 30 минут','YES','2026-04-20 21:07:11'),(15,2,'Оплата','Оплата выполнена','Заказ 20260420200711857187 успешно оплачен. Сумма: 500.00 ₽','YES','2026-04-20 21:07:12'),(16,3,'Заказ','Заказ создан','Заказ создан. Номер: 20260420200728652910. Сумма: 0.00 ₽. Оплатите в течение 30 минут','NO','2026-04-20 21:07:28'),(17,3,'Оплата','Оплата выполнена','Заказ 20260420200728652910 успешно оплачен. Сумма: 0.00 ₽','NO','2026-04-20 21:07:30');
/*!40000 ALTER TABLE `notice` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_info`
--

LOCK TABLES `order_info` WRITE;
/*!40000 ALTER TABLE `order_info` DISABLE KEYS */;
INSERT INTO `order_info` VALUES (2,'20260420194624736283',3,12,'Долина гейзеров',1,12000.00,12000.00,'PAID','2026-04-29','2026-04-20 20:46:24'),(3,'20260420194706370860',3,9,'Суздальский кремль',1,400.00,400.00,'PENDING','2026-04-29','2026-04-20 20:47:06'),(4,'20260420200603392719',2,11,'Озеро Байкал',1,0.00,0.00,'PAID','2026-05-05','2026-04-20 21:06:03'),(5,'20260420200646245509',2,10,'Троице-Сергиева лавра',1,500.00,500.00,'PAID','2026-04-21','2026-04-20 21:06:46'),(6,'20260420200711857187',2,10,'Троице-Сергиева лавра',1,500.00,500.00,'PAID','2026-04-22','2026-04-20 21:07:11'),(7,'20260420200728652910',3,11,'Озеро Байкал',1,0.00,0.00,'PAID','2026-05-01','2026-04-20 21:07:28');
/*!40000 ALTER TABLE `order_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `scenic_category`
--

DROP TABLE IF EXISTS `scenic_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scenic_category` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='景点分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `scenic_category`
--

LOCK TABLES `scenic_category` WRITE;
/*!40000 ALTER TABLE `scenic_category` DISABLE KEYS */;
INSERT INTO `scenic_category` VALUES (1,'Исторические памятники','2026-04-20 19:22:11'),(2,'Музеи и галереи','2026-04-20 19:22:11'),(3,'Дворцы и усадьбы','2026-04-20 19:22:11'),(4,'Храмы и соборы','2026-04-20 19:22:11'),(5,'Природные парки','2026-04-20 19:22:11'),(6,'Театры и культура','2026-04-20 19:22:11'),(7,'Крепости и кремли','2026-04-20 19:22:11'),(8,'Современные достопримечательности','2026-04-20 19:22:11');
/*!40000 ALTER TABLE `scenic_category` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='景点信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `scenic_info`
--

LOCK TABLES `scenic_info` WRITE;
/*!40000 ALTER TABLE `scenic_info` DISABLE KEYS */;
INSERT INTO `scenic_info` VALUES (1,1,'Красная площадь',0.00,0.00,'Красная площадь - главная площадь Москвы, символ России. Здесь находятся Кремль, Собор Василия Блаженного, ГУМ и Мавзолей Ленина. Объект Всемирного наследия ЮНЕСКО.',9999,'http://localhost:1000/file/f04674b7d5184341a1571530d6bbf474.jpg','http://localhost:1000/file/70e075fbf81b401bbf4406bff5228f95.jpeg','Красная площадь, Москва, 109012','00:00-24:00','ACTIVE','2026-04-20 19:22:11'),(2,7,'Московский Кремль',1000.00,700.00,'Московский Кремль - крепость в центре Москвы, официальная резиденция Президента России. Включает Оружейную палату, Алмазный фонд и соборы XV-XVII веков.',500,'http://localhost:1000/file/e21a8f1606c54040b8d85043d52f1c12.jpg','http://localhost:1000/file/d710233c19d7427fbb5bac9dddb8120a.jpg','Кремль, Москва, 103073','10:00-17:00','ACTIVE','2026-04-20 19:22:11'),(3,4,'Храм Христа Спасителя',0.00,0.00,'Кафедральный собор Русской православной церкви, крупнейший храм России. Высота 103 метра. Восстановлен в 1990-х годах на историческом месте.',1000,'http://localhost:1000/file/b885c6d51c054282850fda92efb98266.jpg','http://localhost:1000/file/e89656670492463bafc3e3c5257452cc.jpg','ул. Волхонка, 15, Москва, 119019','10:00-17:00','ACTIVE','2026-04-20 19:22:11'),(4,2,'Третьяковская галерея',500.00,500.00,'Государственная Третьяковская галерея - крупнейший музей русского искусства в мире. Коллекция насчитывает более 180 000 произведений.',800,'http://localhost:1000/file/34d3aa094df5453fa05a74663a744e5d.jpg','http://localhost:1000/file/d3924d44aaf4460fac87346d30f8c45f.jpg','Лаврушинский пер., 10, Москва, 119017','10:00-18:00','ACTIVE','2026-04-20 19:22:11'),(5,3,'Эрмитаж',700.00,700.00,'Государственный Эрмитаж - один из крупнейших и наиболее значительных художественных и культурно-исторических музеев мира. Более 3 миллионов экспонатов.',1000,'http://localhost:1000/file/718d9361fbf2406f95136d4408547415.jpg','http://localhost:1000/file/59654bf3cfe14eeba0ce951621206de8.jpg','Дворцовая площадь, 2, Санкт-Петербург, 190000','10:30-18:00','ACTIVE','2026-04-20 19:22:11'),(6,3,'Петергоф',1000.00,900.00,'Петергоф - дворцово-парковый ансамбль на южном берегу Финского залива. Известен своими фонтанами и каскадами. \"Русский Версаль\".',1500,'http://localhost:1000/file/d3e90bb576264801b21d33a1f20f53a9.jpg','http://localhost:1000/file/76848dee601d4087a28ba5f67713670e.jpg','ул. Разводная, 2, Петергоф, Санкт-Петербург, 198516','09:00-20:00','ACTIVE','2026-04-20 19:22:11'),(7,4,'Исаакиевский собор',350.00,350.00,'Исаакиевский собор - крупнейший православный храм Санкт-Петербурга. Высота 101,5 метра. С колоннады открывается панорама города.',600,'http://localhost:1000/file/b00ff816261247b28d9989f9abc071a6.jpeg','http://localhost:1000/file/0c7030fa95924826b2daed23541f088b.jpg','Исаакиевская пл., 4, Санкт-Петербург, 190000','10:30-18:00','ACTIVE','2026-04-20 19:22:11'),(8,6,'Мариинский театр',2000.00,1500.00,'Мариинский театр - один из ведущих музыкальных театров России и мира. Здесь выступали Шаляпин, Нуриев, Барышников.',300,'http://localhost:1000/file/89556d7662e14a02a8224bb2e34ec906.jpg','http://localhost:1000/file/e2b96af927ea4e62af10190e499f3462.jpg','Театральная пл., 1, Санкт-Петербург, 190000','11:00-19:00','ACTIVE','2026-04-20 19:22:11'),(9,7,'Суздальский кремль',400.00,400.00,'Суздальский кремль - древнейшая часть города Суздаля, сохранившая архитектурный ансамбль XIII-XVIII веков. Объект Всемирного наследия ЮНЕСКО.',500,'http://localhost:1000/file/791927a5d7dc40e28bba9f24a1349339.jpg','http://localhost:1000/file/2928eb49ac92445f83fec6639040f7a4.jpg','ул. Кремлёвская, 20, Суздаль, Владимирская обл., 601293','09:00-19:00','ACTIVE','2026-04-20 19:22:11'),(10,4,'Троице-Сергиева лавра',500.00,500.00,'Троице-Сергиева лавра - крупнейший православный мужской монастырь России. Основан в 1337 году Сергием Радонежским. Объект ЮНЕСКО.',798,'http://localhost:1000/file/81dbc73eadbe4332872e905216a1a4fb.jpg','http://localhost:1000/file/374bbb1544514effb1e0c8d5d00db685.jpg','Сергиев Посад, Московская обл., 141300','05:00-21:00','ACTIVE','2026-04-20 19:22:11'),(12,5,'Долина гейзеров',15000.00,12000.00,'Долина гейзеров на Камчатке - одно из крупнейших гейзерных полей мира. Доступна только на вертолёте. Уникальный природный объект.',48,'http://localhost:1000/file/dca032eecb2b43cc849b199f0bf83a18.jpg','http://localhost:1000/file/29787a0e2f51404487223484a15129a6.jpg,http://localhost:1000/file/e6a74a8da9664825aecedfa8acee12e2.jpg,http://localhost:1000/file/798ef31292664ccfa8715599e87347df.jpg','Кроноцкий заповедник, Камчатский край','08:00-18:00','ACTIVE','2026-04-20 19:22:11');
/*!40000 ALTER TABLE `scenic_info` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='游记分享表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `travel_note`
--

LOCK TABLES `travel_note` WRITE;
/*!40000 ALTER TABLE `travel_note` DISABLE KEYS */;
INSERT INTO `travel_note` VALUES (1,2,'Незабываемые белые ночи в Петербурге','Санкт-Петербург летом - это что-то невероятное! Белые ночи, разведённые мосты, прогулки по Невскому проспекту до рассвета. Эрмитаж поразил своими масштабами - за один день невозможно всё осмотреть. Обязательно вернусь!','http://localhost:1000/file/03390f9eb11e4daaa7df67dbd92caf64.jpeg','2026-04-15','2','2026-04-20 19:22:11'),(2,3,'Петергоф - русский Версаль','Петергоф превзошёл все ожидания! Фонтаны работают с такой точностью, будто это современная техника, а не XVIII век. Большой каскад просто завораживает. Совет: приезжайте к открытию, чтобы избежать толп туристов.','/images/notes/peterhof_fountains.jpg',NULL,NULL,'2026-04-20 19:22:11'),(3,4,'Путешествие по Золотому кольцу','Провели неделю, объезжая города Золотого кольца. Суздаль особенно понравился - как будто попал в прошлое. Деревянные церкви, тишина, размеренная жизнь. Идеальное место для отдыха от городской суеты.','/images/notes/golden_ring.jpg',NULL,NULL,'2026-04-20 19:22:11'),(4,5,'Байкал - жемчужина Сибири','Байкал - это не просто озеро, это целый мир! Вода настолько чистая, что видно дно на глубине 40 метров. Закаты невероятные. Ездили на остров Ольхон - must see для всех, кто приезжает на Байкал.','/images/notes/baikal_sunset.jpg',NULL,NULL,'2026-04-20 19:22:11'),(5,6,'Московский Кремль и Красная площадь','Сердце России! Даже для москвича, как я, Кремль не перестаёт удивлять. Оружейная палата - это сокровищница истории. Рекомендую взять экскурсию с гидом, узнаете много интересного.','/images/notes/kremlin_tour.jpg',NULL,NULL,'2026-04-20 19:22:11'),(6,2,'Камчатка: земля огня и льда','Долина гейзеров - одно из чудес России! Вертолётная экскурсия недешёвая, но оно того стоит. Вулканы, горячие источники, медведи - всё это Камчатка. Незабываемые впечатления!','http://localhost:1000/file/66975df2429c4792a0c77fd562249138.jpg','2026-04-15','2','2026-04-20 19:22:11');
/*!40000 ALTER TABLE `travel_note` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) DEFAULT NULL COMMENT '用户名（可修改，初始为手机号或邮箱）',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号（可用于登录）',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱（可用于登录）',
  `avatar_url` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `balance` decimal(10,2) DEFAULT '0.00' COMMENT '账户余额',
  `status` varchar(20) DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-启用/INACTIVE-禁用',
  `role` varchar(20) NOT NULL DEFAULT 'USER' COMMENT '角色: USER-普通用户/ADMIN-管理员',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone` (`phone`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_username` (`username`),
  KEY `idx_role` (`role`),
  KEY `idx_status` (`status`),
  CONSTRAINT `chk_phone_or_email` CHECK (((`phone` is not null) or (`email` is not null)))
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','123456','+79001234567','admin@tourism.ru','http://localhost:1000/file/5f34a560175c4685af61a7562a05ab3d.png',38000.00,'ACTIVE','ADMIN','2026-04-20 19:22:11'),(2,'Александр','123456','+79001234568','alexander@mail.ru','',14000.00,'ACTIVE','USER','2026-04-20 19:22:11'),(3,'Екатерина','123456','+79001234569','ekaterina@mail.ru','',0.00,'ACTIVE','USER','2026-04-20 19:22:11'),(4,'Дмитрий','123456','+79001234570','dmitry@mail.ru','',8000.00,'ACTIVE','USER','2026-04-20 19:22:11'),(5,'Анастасия','123456','+79001234571','anastasia@mail.ru','',10000.00,'ACTIVE','USER','2026-04-20 19:22:11'),(6,'Михаил','123456','+79001234572','mikhail@mail.ru','',20000.00,'ACTIVE','USER','2026-04-20 19:22:11');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `v_user_scenic_score`
--

DROP TABLE IF EXISTS `v_user_scenic_score`;
/*!50001 DROP VIEW IF EXISTS `v_user_scenic_score`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_user_scenic_score` AS SELECT 
 1 AS `user_id`,
 1 AS `scenic_id`,
 1 AS `total_score`,
 1 AS `last_action_time`*/;
SET character_set_client = @saved_cs_client;

--
-- Final view structure for view `v_user_scenic_score`
--

/*!50001 DROP VIEW IF EXISTS `v_user_scenic_score`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_user_scenic_score` AS select `t`.`user_id` AS `user_id`,`t`.`scenic_id` AS `scenic_id`,sum(`t`.`score`) AS `total_score`,max(`t`.`action_time`) AS `last_action_time` from (select `favorite`.`user_id` AS `user_id`,`favorite`.`association_id` AS `scenic_id`,3.0 AS `score`,`favorite`.`create_time` AS `action_time` from `favorite` where (`favorite`.`type_code` = '景点') union all select `order_info`.`user_id` AS `user_id`,`order_info`.`scenic_id` AS `scenic_id`,5.0 AS `score`,`order_info`.`create_time` AS `action_time` from `order_info` where (`order_info`.`status` in ('PAID','COMPLETED','已支付','已完成'))) `t` group by `t`.`user_id`,`t`.`scenic_id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-22 15:58:27
