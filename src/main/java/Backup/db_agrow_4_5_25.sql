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
-- Table structure for table `machinaryrental`
--

DROP TABLE IF EXISTS `machinaryrental`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `machinaryrental` (
  `id_machinaryrental` int(11) NOT NULL AUTO_INCREMENT,
  `renterName` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `contactNumber` varchar(255) DEFAULT NULL,
  `rentStartDay` date NOT NULL,
  `rentFinalDay` date NOT NULL,
  `id_maquina` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_machinaryrental`),
  KEY `idx_id_maquina` (`id_maquina`),
  CONSTRAINT `fk_machinery_maquina` FOREIGN KEY (`id_maquina`) REFERENCES `tb_maquina` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `machinaryrental`
--

LOCK TABLES `machinaryrental` WRITE;
/*!40000 ALTER TABLE `machinaryrental` DISABLE KEYS */;
INSERT INTO `machinaryrental` VALUES (11,'Juan Perez','Heredia, Sarapiqui, Horquetas','87263514','2025-05-01','2025-08-15',13),(12,'Mario Canales','Heredia, Sarapiqui, Puerto Viejo','24586324','2025-01-06','2025-08-16',14),(13,'Ana González','San José, Escazú','88812345','2025-04-01','2025-04-15',15),(14,'Carlos Rojas','Alajuela, San Ramón','88561234','2025-04-05','2025-04-20',16),(15,'Lucía Jiménez','Cartago, Paraíso','89234567','2025-03-20','2025-04-10',17),(16,'Daniela Méndez','Puntarenas, Cóbano','89987654','2025-05-01','2025-05-18',20),(17,'Andrés Navarro','Limón, Siquirres','88123456','2025-01-10','2025-02-01',22),(18,'Pedro Salas','Heredia, Barva','89765432','2025-03-15','2025-04-01',25),(19,'Sofía Mora','Guanacaste, Santa Cruz','89456789','2025-02-10','2025-03-10',27),(20,'José Campos','San José, Desamparados','89321098','2025-06-01','2025-06-20',29),(21,'Gabriela Díaz','Cartago, Turrialba','88990011','2025-07-01','2025-07-15',30),(22,'Erick Vega','Alajuela, Grecia','89012345','2025-08-01','2025-08-20',32),(23,'Laura Torres','San José, Moravia','88999999','2025-03-10','2025-03-25',35),(24,'Marco Ramírez','Limón, Matina','88001122','2025-01-05','2025-01-20',89),(25,'Karen Fernández','Puntarenas, Quepos','88442211','2025-04-12','2025-04-25',876),(26,'Esteban Solís','San José, Tibás','88881234','2025-02-20','2025-03-05',987),(27,'Diana Araya','Guanacaste, Liberia','88998877','2025-06-10','2025-06-25',3434),(28,'Bryan Pineda','Cartago, Oreamuno','88776655','2025-05-05','2025-05-18',757),(29,'Isabel López','Heredia, San Pablo','88334455','2025-07-15','2025-07-30',500),(30,'Ricardo Méndez','Alajuela, Naranjo','88223311','2025-03-05','2025-03-20',332),(31,'Michelle Castro','Puntarenas, Esparza','88112233','2025-04-01','2025-04-15',87),(32,'Kevin Quesada','Limón, Talamanca','88009988','2025-02-10','2025-03-01',34),(33,'Valeria Ruiz','Guanacaste, Nicoya','87778899','2025-06-01','2025-06-14',33),(34,'Luis Rodríguez','San José, Santa Ana','87654321','2025-03-01','2025-03-17',31),(35,'Alejandra Vargas','Cartago, El Guarco','87543210','2025-01-15','2025-01-31',28),(36,'Cristian Jiménez','Heredia, Santo Domingo','87432109','2025-05-10','2025-05-25',26),(37,'Natalia Calderón','Alajuela, Palmares','87321098','2025-02-28','2025-03-15',24);
/*!40000 ALTER TABLE `machinaryrental` ENABLE KEYS */;
UNLOCK TABLES;

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
  `email` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
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
INSERT INTO `producer` VALUES (10001,'Juan Pérez','87654321','2024-02-01','Persona','juanp@gmail.com','Heredia','200m norte del parque central',0),(10002,'María Gómez','23456789','2024-02-05','Persona','mariag@gmail.com','Cartago','Barrio El Molino',1),(10003,'AgroTico S.A.','89456123','2024-03-12','Empresa','contacto@agrotico.com','Alajuela','Ruta 27, km 5',1),(10004,'Pedro Quesada','22445566','2024-03-15','Persona','pedroq@gmail.com','Limón','Frente al ICE',1),(10005,'Finca La Esperanza','88992211','2024-03-20','Empresa','fincaesperanza@outlook.com','Puntarenas','Del Ebais 300 este',0),(10006,'Laura Jiménez','83664422','2024-03-25','Persona','lauraj@gmail.com','San José','50m sur de la pulpería',1),(10007,'Verduras del Norte','89334455','2024-03-28','Empresa','info@verdurasnorte.com','Guanacaste','Zona industrial',1),(10008,'Carlos Mena','86663344','2024-03-29','Persona','carlosmena@gmail.com','Cartago','La Lima',0),(10009,'Finca Orgánica CR','85224466','2024-03-30','Empresa','organicoscr@gmail.com','San José','Sabanilla',1),(10010,'Josefa Vargas','89442233','2024-04-01','Persona','josefav@gmail.com','Heredia','Frente al colegio',1),(10011,'Exportaciones Tropico','89773344','2024-04-02','Empresa','ventas@tropico.com','Limón','Zona Franca',1),(10012,'Tomás Castro','87665544','2024-04-03','Persona','tomas.castro@hotmail.com','Puntarenas','El Roble',1),(10013,'BioFinca Verde','89993322','2024-04-04','Empresa','bioverde@cr.com','Cartago','La Unión',1),(10014,'Lucía Araya','83221144','2024-04-05','Persona','lucia.araya@gmail.com','Guanacaste','Centro de Liberia',0),(10015,'Finca Los Olivos','84445533','2024-04-06','Empresa','olivos@finca.cr','Heredia','Santa Bárbara',1),(10016,'Emilio Fernández','87889900','2024-04-07','Persona','emilio.fz@gmail.com','San José','Desamparados',1),(10017,'AgroExport CR','89776655','2024-04-08','Empresa','agroexport@gmail.com','Alajuela','Aeropuerto',1),(10018,'Marcela Soto','86554433','2024-04-09','Persona','marcela.soto@live.com','Cartago','Tierra Blanca',1),(10019,'Tierra Fértil','88997711','2024-04-10','Empresa','tierrafertil@cr.com','Puntarenas','Chacarita',1),(10020,'Daniela Campos','88884477','2024-04-11','Persona','danielac@gmail.com','San José','Escazú',1),(10021,'Finca Los Amigos','84332211','2024-04-12','Empresa','amigosfinca@correo.com','Guanacaste','Filadelfia',1),(10022,'Rafael Muñoz','86775588','2024-04-13','Persona','rafael.m@gmail.com','Alajuela','Centro',1),(10023,'Cultivos CR','89447733','2024-04-14','Empresa','cultivoscr@empresa.com','Cartago','Oreamuno',0),(10024,'Verónica Zúñiga','89112233','2024-04-15','Persona','veronica.zuniga@gmail.com','San José','Zapote',1),(10025,'Alimentos Naturales','86668899','2024-04-16','Empresa','naturales@agro.com','Heredia','San Rafael',1),(10026,'Erick Morales','87882211','2024-04-17','Persona','erick.morales@gmail.com','Limón','Guápiles',1),(10027,'Producciones Verdes','88773322','2024-04-18','Empresa','produccionesverdes@gmail.com','Puntarenas','Jacó',1),(10028,'Paola Sánchez','85994433','2024-04-19','Persona','paola.sanchez@gmail.com','San José','Uruca',1),(10029,'Frutas del Valle','83332244','2024-04-20','Empresa','frutasvalle@correo.com','Cartago','Paraíso',1),(10030,'Eduardo Herrera','87664455','2024-04-21','Persona','eduardo.herrera@gmail.com','Heredia','Barva',1),(58062,'Ana Maria','24585236','2024-03-04','Persona','anamaria@gmail.com','Guanacaste','300 metros estacion de trencito',0),(85758,'Elena Gonzales','87263514','2025-04-04','Persona','elena@gmail.com','Heredia','Heredia, Sarapiqui, Horquetas',1),(90106,'TicoFruts S.A','72723244','2024-03-19','Empresa','ticofruts@gmail.com','Guanacaste','300 metros estacion de tren',1);
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
  `id_producer` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_producer` (`id_producer`),
  CONSTRAINT `fk_producer` FOREIGN KEY (`id_producer`) REFERENCES `producer` (`id_producer`)
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_harvest`
--

LOCK TABLES `tb_harvest` WRITE;
/*!40000 ALTER TABLE `tb_harvest` DISABLE KEYS */;
INSERT INTO `tb_harvest` VALUES (13,'2024-04-15','Cosecha de temporada','Mercado local','Premium',230,_binary '','Tomates',10017),(14,'2024-04-18','Cosecha de temporada','Supermercado Regional','Premium',180,_binary '','Lechugas',58062),(15,'2024-04-20','Cosecha temprana','Restaurante El Campesino','Premium',150,_binary '','Zanahorias',10026),(16,'2024-04-22','Cosecha de primavera','Mercado central','Estandar',120,_binary '','Pimientos',58062),(17,'2024-04-25','Cosecha principal','Exportación','Premium',320,_binary '','Calabazas',90106),(18,'2024-04-27','Cosecha de temporada','Tienda de productos orgánicos','Premium',90,_binary '','Berenjenas',58062),(19,'2024-04-30','Cosecha principal','Restaurante Gourmet','Premium',210,_binary '','Pepinos',90106),(20,'2024-05-02','Cosecha de temporada','Mercado local','Secundaria',280,_binary '','Melones',58062),(21,'2024-05-04','Cosecha principal','Frutería del barrio','Estandar',350,_binary '','Sandías',90106),(22,'2024-05-06','Cosecha temprana','Pastelería artesanal','Premium',65,_binary '','Fresas',58062),(23,'2024-05-08','Cosecha de temporada','Exportación','Premium',45,_binary '','Frambuesas',90106),(24,'2024-05-10','Cosecha principal','Mercado de productores','Estandar',55,_binary '','Moras',58062),(25,'2024-05-12','Cosecha de temporada','Supermercado Regional','Premium',40,_binary '','Arándanos',90106),(26,'2024-05-14','Cosecha principal','Restaurante de comida local','Estandar',420,_binary '','Maíz',58062),(27,'2024-05-16','Cosecha de temporada','Mercado central','Estandar',560,_binary '','Papas',90106),(28,'2024-05-18','Cosecha principal','Restaurante El Campesino','Secundaria',180,_binary '','Cebollas',58062),(29,'2024-05-20','Cosecha de temporada','Mercado local','Estandar',75,_binary '','Ajos',90106),(30,'2024-05-22','Cosecha principal','Tienda de productos orgánicos','Premium',110,_binary '','Espinacas',58062),(31,'2024-05-24','Cosecha de temporada','Mercado de productores','Estandar',95,_binary '','Acelgas',90106),(32,'2024-05-26','Cosecha principal','Restaurante Gourmet','Premium',35,_binary '','Rúcula',58062),(33,'2024-05-28','Cosecha de temporada','Supermercado Regional','Premium',25,_binary '','Albahaca',90106),(34,'2024-05-30','Cosecha principal','Restaurante de comida local','Estandar',20,_binary '','Perejil',58062),(35,'2024-06-01','Cosecha de temporada','Mercado central','Estandar',30,_binary '','Cilantro',90106),(36,'2024-06-03','Cosecha principal','Exportación','Premium',170,_binary '','Brócoli',58062),(37,'2024-06-05','Cosecha de temporada','Mercado local','Estandar',155,_binary '','Coliflor',90106),(38,'2024-06-07','Cosecha principal','Restaurante El Campesino','Secundaria',190,_binary '','Repollo',58062),(39,'2024-06-09','Cosecha de temporada','Tienda de productos orgánicos','Premium',145,_binary '','Lechuga romana',90106),(40,'2024-06-11','Cosecha principal','Restaurante Gourmet','Premium',85,_binary '','Tomates cherry',58062),(41,'2024-06-13','Cosecha de temporada','Supermercado Regional','Estandar',135,_binary '','Pimientos morrones',90106),(42,'2024-06-15','Cosecha principal','Mercado de productores','Estandar',160,_binary '','Judías verdes',58062),(43,'2025-04-30','Kiwi del Prado','Guapiles','Premium',23,_binary '','Kiwi',10014);
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
INSERT INTO `tb_maquina` VALUES (13,'Topadora','Usada',0,'2020-04-12',230.4,'Cartago','Baja'),(14,'Grua Telescópica','Buena',0,'2022-12-15',375.6,'Heredia','Alta'),(15,'Barredora Vial','Usada',0,'2019-05-22',280,'Puntarenas','Media'),(16,'Perforadora','Nueva',0,'2023-03-10',290.75,'San José','Alta'),(17,'Fresadora','Reparación',0,'2020-11-25',310.5,'Alajuela','Baja'),(18,'Cargador Frontal','Usada',1,'2021-09-05',270.8,'Cartago','Media'),(19,'Hidroelevador','Buena',1,'2023-06-14',340,'Heredia','Alta'),(20,'Zanjadora','Usada',0,'2017-08-30',190,'Puntarenas','Baja'),(21,'Bomba de Agua','Buena',1,'2024-05-10',160.5,'San José','Media'),(22,'Cortadora de Concreto','Reparación',0,'2019-10-12',220.9,'Alajuela','Baja'),(23,'Autohormigonera','Usada',1,'2022-01-30',380,'Cartago','Alta'),(24,'Tuneladora','Buena',0,'2023-09-28',450.5,'Heredia','Alta'),(25,'Martillo Hidráulico','Usada',0,'2018-12-05',170,'Puntarenas','Media'),(26,'Trituradora','Buena',0,'2021-04-10',390.75,'San José','Alta'),(27,'Cosechadora','Reparación',0,'2020-06-22',260,'Alajuela','Baja'),(28,'Sembradora','Usada',0,'2022-11-08',210.4,'Cartago','Media'),(29,'Arado','Buena',0,'2023-07-18',150.9,'Heredia','Alta'),(30,'Rastra','Usada',0,'2019-03-15',190.5,'Puntarenas','Baja'),(31,'Empacadora','Nueva',0,'2024-02-14',340,'San José','Media'),(32,'Carretón','Reparación',0,'2020-09-12',125,'Alajuela','Baja'),(33,'Niveladora','Usada',0,'2021-12-20',410.6,'Cartago','Alta'),(34,'Cisterna','Buena',0,'2023-10-01',280,'Heredia','Alta'),(35,'Motocultor','Usada',0,'2018-07-23',195.3,'Puntarenas','Alta'),(87,'camion','Nueva',0,'2023-12-07',90000,'Liberia','Alta'),(89,'Chapulin','Nueva',0,'2023-12-06',5444,'Liberia','Alta'),(332,'perro','Nueva',0,'2023-12-15',2999,'Limon','alta'),(500,'machete','Usada',0,'2023-12-14',2345,'Cartago','Alta'),(757,'Martillo','Nueva',0,'2023-12-21',200000,'San Jose','Buena'),(876,'Chapulin','Nueva',0,'2023-12-05',5444,'Liberia','Alta'),(987,'carro','Nueva',0,'2023-12-14',5444,'Liberia','Alta'),(3434,'Tractor','Usada',0,'2023-12-06',8000,'San Jose','Alta');
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

-- Dump completed on 2025-05-04 17:32:34
