-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: db_agrow
-- ------------------------------------------------------
-- Server version	5.5.5-10.4.32-MariaDB

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
-- Table structure for table `producer`
--

DROP TABLE IF EXISTS `producer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `producer` (
  `id_producer` int(11) NOT NULL,
  `producerName` varchar(100) NOT NULL,
  `contactNumber` varchar(8) NOT NULL,
  `registrationDate` date NOT NULL,
  `producerType` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `city` varchar(30) NOT NULL,
  `address` varchar(255) NOT NULL,
  `isActive` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id_producer`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `producer`
--

LOCK TABLES `producer` WRITE;
/*!40000 ALTER TABLE `producer` DISABLE KEYS */;
INSERT INTO `producer` VALUES (58062,'Ana Maria','24585236','2024-03-04','Persona','anamaria@gmail.com','Guanacaste','300 metros estacion de trencito',0),(90106,'TicoFruts S.A','72723244','2024-03-19','Empresa','ticofruts@gmail.com','Guanacaste','300 metros estacion de tren',1);
/*!40000 ALTER TABLE `producer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `supplier`
--

DROP TABLE IF EXISTS `supplier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `supplier` (
  `supplierIdentification` int(11) NOT NULL,
  `supplierName` varchar(100) NOT NULL,
  `companyName` varchar(100) NOT NULL,
  `phoneNumber` int(11) NOT NULL,
  `email` varchar(100) NOT NULL,
  `registrationDate` date NOT NULL,
  `isActive` tinyint(1) NOT NULL DEFAULT 1,
  `creditLimit` decimal(12,2) NOT NULL DEFAULT 0.00,
  PRIMARY KEY (`supplierIdentification`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `supplier`
--

LOCK TABLES `supplier` WRITE;
/*!40000 ALTER TABLE `supplier` DISABLE KEYS */;
INSERT INTO `supplier` VALUES (45612375,'Sara García M','Colono Agrícola',89889888,'sara@colono.com','2024-03-08',0,1588856.00),(45636520,'María Duarte Membreño','Fruty Corp',89889889,'maria.duarte@frutycorp.net','2024-04-04',1,1465156.00);
/*!40000 ALTER TABLE `supplier` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `supply`
--

DROP TABLE IF EXISTS `supply`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `supply` (
  `idSupply` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `category` varchar(50) NOT NULL,
  `stock` decimal(10,2) NOT NULL DEFAULT 0.00,
  `stockMinimo` decimal(10,2) NOT NULL DEFAULT 0.00,
  `unitType` varchar(30) NOT NULL,
  `pricePerUnit` decimal(10,2) NOT NULL,
  `expirationDate` date NOT NULL,
  `supplierId` int(11) NOT NULL,
  PRIMARY KEY (`idSupply`),
  KEY `fk_supply_supplier_idx` (`supplierId`),
  CONSTRAINT `fk_supply_supplier` FOREIGN KEY (`supplierId`) REFERENCES `supplier` (`supplierIdentification`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `supply`
--

LOCK TABLES `supply` WRITE;
/*!40000 ALTER TABLE `supply` DISABLE KEYS */;
INSERT INTO `supply` VALUES (14,'Pala Grande','Herramientas',3.00,5.00,'Unidad',9800.00,'2025-05-09',45636520),(15,'Semillas Girasol','Semillas',50.00,10.00,'Paquete',2500.00,'2025-12-31',45612375);
/*!40000 ALTER TABLE `supply` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_harvest`
--

DROP TABLE IF EXISTS `tb_harvest`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_harvest` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `date_harvested` date DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `destiny` varchar(255) DEFAULT NULL,
  `quality` varchar(255) DEFAULT NULL,
  `quantity_harvested` int(11) DEFAULT NULL,
  `registered_harvest` bit(1) NOT NULL,
  `type_harvest` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_harvest`
--

LOCK TABLES `tb_harvest` WRITE;
/*!40000 ALTER TABLE `tb_harvest` DISABLE KEYS */;
INSERT INTO `tb_harvest` VALUES (1,'2025-04-29','ddd','Guapiles','Premium',23,_binary '','Zanahoria'),(2,'2024-01-02','aaa','Jamaica','Secundaria',77,_binary '','Papaya'),(5,'2025-04-28','ddd','Limon','Premium',20,_binary '','Chayote'),(7,'2025-04-30','ddd','Pococi','Secundaria',7,_binary '','Fresas'),(8,'2002-02-12','dd','Guacimo','Estándar',20,_binary '','Cafe');
/*!40000 ALTER TABLE `tb_harvest` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_maquina`
--

DROP TABLE IF EXISTS `tb_maquina`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_maquina` (
  `id` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `condicion` varchar(50) NOT NULL,
  `disponibilidad` tinyint(1) NOT NULL,
  `dia_adquisicion` date NOT NULL,
  `costo_alquiler` double NOT NULL,
  `ubicacion` varchar(100) NOT NULL,
  `capacidad_trabajo` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_maquina`
--

LOCK TABLES `tb_maquina` WRITE;
/*!40000 ALTER TABLE `tb_maquina` DISABLE KEYS */;
INSERT INTO `tb_maquina` VALUES (13,'Topadora','Usada',1,'2020-04-12',230.4,'Cartago','Baja'),(14,'Grua Telescópica','Buena',1,'2022-12-15',375.6,'Heredia','Alta'),(15,'Barredora Vial','Usada',0,'2019-05-22',280,'Puntarenas','Media'),(16,'Perforadora','Nueva',0,'2023-03-10',290.75,'San José','Alta'),(17,'Fresadora','Reparación',0,'2020-11-25',310.5,'Alajuela','Baja'),(18,'Cargador Frontal','Usada',1,'2021-09-05',270.8,'Cartago','Media'),(19,'Hidroelevador','Buena',1,'2023-06-14',340,'Heredia','Alta'),(20,'Zanjadora','Usada',0,'2017-08-30',190,'Puntarenas','Baja'),(21,'Bomba de Agua','Buena',1,'2024-05-10',160.5,'San José','Media'),(22,'Cortadora de Concreto','Reparación',0,'2019-10-12',220.9,'Alajuela','Baja'),(23,'Autohormigonera','Usada',1,'2022-01-30',380,'Cartago','Alta'),(24,'Tuneladora','Buena',1,'2023-09-28',450.5,'Heredia','Alta'),(25,'Martillo Hidráulico','Usada',0,'2018-12-05',170,'Puntarenas','Media'),(26,'Trituradora','Buena',1,'2021-04-10',390.75,'San José','Alta'),(27,'Cosechadora','Reparación',0,'2020-06-22',260,'Alajuela','Baja'),(28,'Sembradora','Usada',1,'2022-11-08',210.4,'Cartago','Media'),(29,'Arado','Buena',1,'2023-07-18',150.9,'Heredia','Alta'),(30,'Rastra','Usada',0,'2019-03-15',190.5,'Puntarenas','Baja'),(31,'Empacadora','Buena',1,'2024-02-14',340,'San José','Media'),(32,'Carretón','Reparación',0,'2020-09-12',125,'Alajuela','Baja'),(33,'Niveladora','Usada',1,'2021-12-20',410.6,'Cartago','Alta'),(34,'Cisterna','Buena',1,'2023-10-01',280,'Heredia','Alta'),(35,'Motocultor','Usada',0,'2018-07-23',195.3,'Puntarenas','Alta'),(87,'camion','Nueva',1,'2023-12-07',90000,'Liberia','Alta'),(89,'Chapulin','Nueva',1,'2023-12-06',5444,'Liberia','Alta'),(332,'perro','Nueva',1,'2023-12-15',2999,'Limon','alta'),(500,'machete','Usada',1,'2023-12-14',2345,'Cartago','Alta'),(757,'Martillo','Nueva',1,'2023-12-21',200000,'San Jose','Buena'),(876,'Chapulin','Nueva',0,'2023-12-05',5444,'Liberia','Alta'),(987,'carro','Nueva',0,'2023-12-14',5444,'Liberia','Alta'),(3434,'Tractor','Usada',0,'2023-12-06',8000,'San Jose','Alta');
/*!40000 ALTER TABLE `tb_maquina` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-02 14:43:22
