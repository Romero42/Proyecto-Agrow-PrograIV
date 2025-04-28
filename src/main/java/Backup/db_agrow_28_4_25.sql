-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 28, 2025 at 06:59 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `db_agrow`
--

DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteSupplier` (IN `in_supplier_identification` INT(20))   BEGIN
  DELETE FROM supplier
    WHERE supplier_identification = in_supplier_identification;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllSupplier` ()   BEGIN
  SELECT
    supplier_identification,
    supplier_name,
    company_name,
    phone_num,
    email,
    registration_date,
    is_active,
    credit_limit
  FROM supplier;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `saveSupplier` (IN `in_supplier_identification` INT(20), IN `in_supplier_name` VARCHAR(70), IN `in_company_name` VARCHAR(45), IN `in_phone_num` INT, IN `in_email` VARCHAR(70), IN `in_registration_date` DATE, IN `in_is_active` TINYINT, IN `in_credit_limit` DOUBLE)   BEGIN
  INSERT INTO supplier
    (supplier_identification, supplier_name, company_name, phone_num, email, registration_date, is_active, credit_limit)
  VALUES
    (in_supplier_identification, in_supplier_name, in_company_name, in_phone_num, in_email, in_registration_date, in_is_active, in_credit_limit);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `spAddProducer` (IN `p_id` INT, IN `p_producerName` VARCHAR(30), IN `p_contactNumber` VARCHAR(8), IN `p_registrationDate` DATE, IN `p_producerType` VARCHAR(10), IN `p_email` VARCHAR(100), IN `p_city` VARCHAR(10), IN `p_address` VARCHAR(100), IN `p_isActive` TINYINT(1))   BEGIN
INSERT INTO `db_agrow`.`producer` (`id_producer`, `producerName`, `contactNumber`, `registrationDate`, `producerType`, `email`,`city`, `address`, `isActive`) 
VALUES (p_id, p_producerName, p_contactNumber, p_registrationDate, p_producerType, p_email, p_city, p_address, p_isActive);

END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `spDeleteProducer` (IN `p_producer` INT)   BEGIN
DELETE FROM `db_agrow`.`producer` WHERE (`id_producer` = p_producer);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `spGetProducer` (IN `p_idproducer` INT)   BEGIN

select * from producer  where id_producer = p_idproducer;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `spListCity` (IN `p_city` VARCHAR(10))   BEGIN

select *from producer where city = p_city;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `spListProducer` ()   BEGIN
select * from producer;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `spUpdateProducer` (IN `p_id` INT, IN `p_producerName` VARCHAR(30), IN `p_contactNumber` VARCHAR(8), IN `p_registrationDate` DATE, IN `p_producerType` VARCHAR(10), IN `p_email` VARCHAR(100), IN `p_city` VARCHAR(10), IN `p_address` VARCHAR(100), IN `p_isActive` TINYINT(1))   BEGIN
update `db_agrow`.`producer` set 
`registrationDate` = p_registrationDate, 
`producerType` = p_producerType, 
`email` = p_email, 
`city` = p_city,
`address`= p_address, 
`producerName` =p_producerName, 
`contactNumber` = p_contactNumber,
`isActive` = p_isActive
where (`id_producer` = p_id);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_AddProducer` (IN `in_idProducer` INT, IN `in_producerName` VARCHAR(100), IN `in_contactNumber` VARCHAR(8), IN `in_registrationDate` DATE, IN `in_producerType` VARCHAR(20), IN `in_email` VARCHAR(100), IN `in_city` VARCHAR(30), IN `in_address` VARCHAR(255), IN `in_isActive` TINYINT(1))   BEGIN
    INSERT INTO `producer` (`id_producer`, `producerName`, `contactNumber`, `registrationDate`, `producerType`, `email`, `city`, `address`, `isActive`)
    VALUES (in_idProducer, in_producerName, in_contactNumber, in_registrationDate, in_producerType, in_email, in_city, in_address, in_isActive);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_DecreaseStock` (IN `p_idSupply` INT(11), IN `p_quantity` DECIMAL(10,2))   BEGIN
    DECLARE current_stock DECIMAL(10,2);
    SELECT stock INTO current_stock FROM supply WHERE idSupply = p_idSupply;
    
    IF current_stock >= p_quantity THEN
        UPDATE supply
        SET stock = stock - p_quantity
        WHERE idSupply = p_idSupply;
        SELECT ROW_COUNT() AS rowsUpdated;
    ELSE
        SELECT 0 AS rowsUpdated;
    END IF;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_DecreaseSupplyStock` (IN `in_idSupply` INT, IN `in_quantity` DECIMAL(10,2))   BEGIN
    DECLARE current_stock DECIMAL(10,2);
    SELECT stock INTO current_stock FROM supply WHERE idSupply = in_idSupply;

    IF current_stock >= in_quantity THEN
        UPDATE supply SET stock = stock - in_quantity WHERE idSupply = in_idSupply;
        SELECT ROW_COUNT() AS rowsUpdated;
    ELSE
        SELECT 0 AS rowsUpdated; -- Indica que no se pudo decrementar
    END IF;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_DeleteProducer` (IN `in_idProducer` INT)   BEGIN
    DELETE FROM `producer` WHERE `id_producer` = in_idProducer;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_DeleteSupplier` (IN `in_supplierIdentification` INT)   BEGIN
    DELETE FROM `supplier`
    WHERE `supplierIdentification` = in_supplierIdentification;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_DeleteSupply` (IN `in_idSupply` INT)   BEGIN
    DELETE FROM `supply`
    WHERE `idSupply` = in_idSupply;
    SELECT ROW_COUNT() AS rowsDeleted; -- Opcional: devolver filas afectadas
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_FindSuppliers` (IN `in_searchTerm` VARCHAR(100))   BEGIN
    SELECT * FROM `supplier`
    WHERE `supplierIdentification` LIKE CONCAT('%', in_searchTerm, '%')
       OR `supplierName` LIKE CONCAT('%', in_searchTerm, '%')
       OR `companyName` LIKE CONCAT('%', in_searchTerm, '%')
    ORDER BY `companyName`;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_GetAllSuppliers` ()   BEGIN
    SELECT
        `supplierIdentification`,
        `supplierName`,
        `companyName`,
        `phoneNumber`,
        `email`,
        `registrationDate`,
        `isActive`,
        `creditLimit`
    FROM `supplier`
    ORDER BY `companyName`;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_GetAllSupplies` ()   BEGIN
    SELECT
        s.idSupply,
        s.name,
        s.category,
        s.stock,
        s.stockMinimo,
        s.unitType,
        s.pricePerUnit,
        s.expirationDate,
        s.supplierId,
        sp.companyName AS supplierName, -- Nombre del proveedor añadido
        CASE
            WHEN s.stock <= 0 THEN 'Agotado'
            WHEN s.stock <= s.stockMinimo THEN 'Bajo'
            ELSE 'Óptimo'
        END AS estado
    FROM `supply` s
    LEFT JOIN `supplier` sp ON s.supplierId = sp.supplierIdentification -- Unión con supplier
    ORDER BY s.category, s.name;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_GetExpiringSupplies` (IN `in_days` INT)   BEGIN
     SELECT
        s.idSupply,
        s.name,
        s.category,
        s.stock,
        s.stockMinimo,
        s.unitType,
        s.pricePerUnit,
        s.expirationDate,
        s.supplierId,
        sp.companyName AS supplierName,
        CASE
             WHEN s.stock <= 0 THEN 'Agotado'
             WHEN s.stock <= s.stockMinimo THEN 'Bajo'
             ELSE 'Óptimo'
        END AS estado
    FROM `supply` s
    LEFT JOIN `supplier` sp ON s.supplierId = sp.supplierIdentification
    WHERE s.expirationDate IS NOT NULL
      AND s.expirationDate BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL in_days DAY)
    ORDER BY s.expirationDate ASC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_GetFilteredSupplies` (IN `in_searchTerm` VARCHAR(100), IN `in_category` VARCHAR(50), IN `in_supplierId` INT)   BEGIN
    SELECT
        s.idSupply,
        s.name,
        s.category,
        s.stock,
        s.stockMinimo,
        s.unitType,
        s.pricePerUnit,
        s.expirationDate,
        s.supplierId,
        sp.companyName AS supplierName, -- Nombre del proveedor añadido
        CASE
             WHEN s.stock <= 0 THEN 'Agotado'
             WHEN s.stock <= s.stockMinimo THEN 'Bajo'
             ELSE 'Óptimo'
        END AS estado
    FROM `supply` s
    LEFT JOIN `supplier` sp ON s.supplierId = sp.supplierIdentification -- Unión con supplier
    WHERE (in_searchTerm IS NULL OR s.name LIKE in_searchTerm) -- Se asume que Java añade los '%'
      AND (in_category IS NULL OR in_category = '' OR s.category = in_category)
      AND (in_supplierId IS NULL OR s.supplierId = in_supplierId)
    ORDER BY s.name;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_GetLowStockSupplies` ()   BEGIN
    SELECT
        s.idSupply,
        s.name,
        s.category,
        s.stock,
        s.stockMinimo,
        s.unitType,
        s.pricePerUnit,
        s.expirationDate,
        s.supplierId,
        sp.companyName AS supplierName, -- Nombre del proveedor añadido
        'Bajo' AS estado -- Directamente 'Bajo' porque la condición es stock <= stockMinimo
    FROM `supply` s
    LEFT JOIN `supplier` sp ON s.supplierId = sp.supplierIdentification -- Unión con supplier
    WHERE s.stock <= s.stockMinimo AND s.stock > 0 -- Excluir agotados si se desea
    ORDER BY s.stock ASC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_GetProducerById` (IN `in_idProducer` INT)   BEGIN
    SELECT * FROM `producer` WHERE `id_producer` = in_idProducer;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_GetSupplierById` (IN `in_supplierIdentification` INT)   BEGIN
    SELECT * FROM `supplier` WHERE `supplierIdentification` = in_supplierIdentification;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_GetSuppliesByCategory` (IN `in_category` VARCHAR(50))   BEGIN
    SELECT
        s.idSupply,
        s.name,
        s.category,
        s.stock,
        s.stockMinimo,
        s.unitType,
        s.pricePerUnit,
        s.expirationDate,
        s.supplierId,
        sp.companyName AS supplierName, -- Nombre del proveedor añadido
        CASE
             WHEN s.stock <= 0 THEN 'Agotado'
             WHEN s.stock <= s.stockMinimo THEN 'Bajo'
             ELSE 'Óptimo'
        END AS estado
    FROM `supply` s
    LEFT JOIN `supplier` sp ON s.supplierId = sp.supplierIdentification -- Unión con supplier
    WHERE s.category = in_category
    ORDER BY s.name;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_GetSuppliesBySupplier` (IN `in_supplierId` INT)   BEGIN
    SELECT
        s.idSupply,
        s.name,
        s.category,
        s.stock,
        s.stockMinimo,
        s.unitType,
        s.pricePerUnit,
        s.expirationDate,
        s.supplierId,
        sp.companyName AS supplierName, -- Nombre del proveedor añadido
        CASE
             WHEN s.stock <= 0 THEN 'Agotado'
             WHEN s.stock <= s.stockMinimo THEN 'Bajo'
             ELSE 'Óptimo'
        END AS estado
    FROM `supply` s
    LEFT JOIN `supplier` sp ON s.supplierId = sp.supplierIdentification -- Unión con supplier
    WHERE s.supplierId = in_supplierId
    ORDER BY s.name; -- Ordenar por nombre de suministro
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_GetSupplyById` (IN `in_idSupply` INT)   BEGIN
    SELECT
        s.idSupply,
        s.name,
        s.category,
        s.stock,
        s.stockMinimo,
        s.unitType,
        s.pricePerUnit,
        s.expirationDate,
        s.supplierId,
        sp.companyName AS supplierName, -- Nombre del proveedor añadido
        CASE
             WHEN s.stock <= 0 THEN 'Agotado'
             WHEN s.stock <= s.stockMinimo THEN 'Bajo'
             ELSE 'Óptimo'
        END AS estado
    FROM `supply` s
    LEFT JOIN `supplier` sp ON s.supplierId = sp.supplierIdentification -- Unión con supplier
    WHERE s.idSupply = in_idSupply;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_IncreaseStock` (IN `p_idSupply` INT(11), IN `p_quantity` DECIMAL(10,2))   BEGIN
    UPDATE supply
    SET stock = stock + p_quantity
    WHERE idSupply = p_idSupply;
    
    SELECT ROW_COUNT() AS rowsUpdated;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_IncreaseSupplyStock` (IN `in_idSupply` INT, IN `in_quantity` DECIMAL(10,2))   BEGIN
    UPDATE supply SET stock = stock + in_quantity WHERE idSupply = in_idSupply;
    SELECT ROW_COUNT() AS rowsUpdated;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_InsertSupply` (IN `in_name` VARCHAR(100), IN `in_category` VARCHAR(50), IN `in_stock` DECIMAL(10,2), IN `in_stockMinimo` DECIMAL(10,2), IN `in_unitType` VARCHAR(30), IN `in_pricePerUnit` DECIMAL(10,2), IN `in_expirationDate` DATE, IN `in_supplierId` INT)   BEGIN
    INSERT INTO `supply` (`name`, `category`, `stock`, `stockMinimo`, `unitType`, `pricePerUnit`, `expirationDate`, `supplierId`)
    VALUES (in_name, in_category, in_stock, in_stockMinimo, in_unitType, in_pricePerUnit, in_expirationDate, in_supplierId);
    SELECT LAST_INSERT_ID() AS newIdSupply; -- Devuelve el ID generado
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_ListAllProducers` ()   BEGIN
    SELECT * FROM `producer`;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_ListProducersByCity` (IN `in_city` VARCHAR(30))   BEGIN
    SELECT * FROM `producer` WHERE `city` = in_city;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_SaveSupplier` (IN `in_supplierIdentification` INT, IN `in_supplierName` VARCHAR(100), IN `in_companyName` VARCHAR(100), IN `in_phoneNumber` INT, IN `in_email` VARCHAR(100), IN `in_registrationDate` DATE, IN `in_isActive` TINYINT(1), IN `in_creditLimit` DECIMAL(12,2))   BEGIN
    -- Opcional: Verificar si ya existe antes de insertar
    -- IF NOT EXISTS (SELECT 1 FROM supplier WHERE supplierIdentification = in_supplierIdentification) THEN
        INSERT INTO `supplier`
            (`supplierIdentification`, `supplierName`, `companyName`, `phoneNumber`, `email`, `registrationDate`, `isActive`, `creditLimit`)
        VALUES
            (in_supplierIdentification, in_supplierName, in_companyName, in_phoneNumber, in_email, in_registrationDate, in_isActive, in_creditLimit);
    -- ELSE
        -- Podrías manejar el error aquí si prefieres no hacerlo en Java
        -- SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Supplier ID already exists';
    -- END IF;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_SearchSuppliesByName` (IN `in_searchTerm` VARCHAR(100))   BEGIN
    SELECT
        s.idSupply,
        s.name,
        s.category,
        s.stock,
        s.stockMinimo,
        s.unitType,
        s.pricePerUnit,
        s.expirationDate,
        s.supplierId,
        sp.companyName AS supplierName, -- Nombre del proveedor añadido
        CASE
             WHEN s.stock <= 0 THEN 'Agotado'
             WHEN s.stock <= s.stockMinimo THEN 'Bajo'
             ELSE 'Óptimo'
        END AS estado
    FROM `supply` s
    LEFT JOIN `supplier` sp ON s.supplierId = sp.supplierIdentification -- Unión con supplier
    WHERE s.name LIKE CONCAT('%', in_searchTerm, '%')
    ORDER BY s.name;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_UpdateProducer` (IN `in_idProducer` INT, IN `in_producerName` VARCHAR(100), IN `in_contactNumber` VARCHAR(8), IN `in_registrationDate` DATE, IN `in_producerType` VARCHAR(20), IN `in_email` VARCHAR(100), IN `in_city` VARCHAR(30), IN `in_address` VARCHAR(255), IN `in_isActive` TINYINT(1))   BEGIN
    UPDATE `producer` SET
        `producerName` = in_producerName,
        `contactNumber` = in_contactNumber,
        `registrationDate` = in_registrationDate,
        `producerType` = in_producerType,
        `email` = in_email,
        `city` = in_city,
        `address` = in_address,
        `isActive` = in_isActive
    WHERE `id_producer` = in_idProducer;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_UpdateSupplier` (IN `in_supplierIdentification` INT, IN `in_supplierName` VARCHAR(100), IN `in_companyName` VARCHAR(100), IN `in_phoneNumber` INT, IN `in_email` VARCHAR(100), IN `in_registrationDate` DATE, IN `in_isActive` TINYINT(1), IN `in_creditLimit` DECIMAL(12,2))   BEGIN
    UPDATE `supplier` SET
        `supplierName` = in_supplierName,
        `companyName` = in_companyName,
        `phoneNumber` = in_phoneNumber,
        `email` = in_email,
        `registrationDate` = in_registrationDate,
        `isActive` = in_isActive,
        `creditLimit` = in_creditLimit
    WHERE `supplierIdentification` = in_supplierIdentification;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_UpdateSupply` (IN `in_idSupply` INT, IN `in_name` VARCHAR(100), IN `in_category` VARCHAR(50), IN `in_stock` DECIMAL(10,2), IN `in_stockMinimo` DECIMAL(10,2), IN `in_unitType` VARCHAR(30), IN `in_pricePerUnit` DECIMAL(10,2), IN `in_expirationDate` DATE, IN `in_supplierId` INT)   BEGIN
    UPDATE `supply` SET
        `name` = in_name,
        `category` = in_category,
        `stock` = in_stock,
        `stockMinimo` = in_stockMinimo,
        `unitType` = in_unitType,
        `pricePerUnit` = in_pricePerUnit,
        `expirationDate` = in_expirationDate,
        `supplierId` = in_supplierId
    WHERE `idSupply` = in_idSupply;
    -- SELECT ROW_COUNT() AS rowsUpdated; -- Opcional
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_UpdateSupplyStock` (IN `p_idSupply` INT(11), IN `p_stock` DECIMAL(10,2))   BEGIN
    UPDATE supply
    SET stock = p_stock
    WHERE idSupply = p_idSupply;
    
    SELECT ROW_COUNT() AS rowsUpdated;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `updateSupplier` (IN `in_supplier_identification` INT(20), IN `in_supplier_name` VARCHAR(70), IN `in_company_name` VARCHAR(45), IN `in_phone_num` INT, IN `in_email` VARCHAR(70), IN `in_registration_date` DATE, IN `in_is_active` TINYINT, IN `in_credit_limit` DOUBLE)   BEGIN
  UPDATE supplier
    SET
      supplier_name      = in_supplier_name,
      company_name       = in_company_name,
      phone_num          = in_phone_num,
      email              = in_email,
      registration_date  = in_registration_date,
      is_active          = in_is_active,
      credit_limit       = in_credit_limit
    WHERE supplier_identification = in_supplier_identification;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `producer`
--

CREATE TABLE `producer` (
  `id_producer` int(11) NOT NULL,
  `producerName` varchar(100) NOT NULL,
  `contactNumber` varchar(8) NOT NULL,
  `registrationDate` date NOT NULL,
  `producerType` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `city` varchar(30) NOT NULL,
  `address` varchar(255) NOT NULL,
  `isActive` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `producer`
--

INSERT INTO `producer` (`id_producer`, `producerName`, `contactNumber`, `registrationDate`, `producerType`, `email`, `city`, `address`, `isActive`) VALUES
(58062, 'Ana Maria', '24585236', '2024-03-04', 'Persona', 'anamaria@gmail.com', 'Guanacaste', '300 metros estacion de trencito', 0),
(90106, 'TicoFruts S.A', '72723244', '2024-03-19', 'Empresa', 'ticofruts@gmail.com', 'Guanacaste', '300 metros estacion de tren', 1);

-- --------------------------------------------------------

--
-- Table structure for table `supplier`
--

CREATE TABLE `supplier` (
  `supplierIdentification` int(11) NOT NULL,
  `supplierName` varchar(100) NOT NULL,
  `companyName` varchar(100) NOT NULL,
  `phoneNumber` int(11) NOT NULL,
  `email` varchar(100) NOT NULL,
  `registrationDate` date NOT NULL,
  `isActive` tinyint(1) NOT NULL DEFAULT 1,
  `creditLimit` decimal(12,2) NOT NULL DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `supplier`
--

INSERT INTO `supplier` (`supplierIdentification`, `supplierName`, `companyName`, `phoneNumber`, `email`, `registrationDate`, `isActive`, `creditLimit`) VALUES
(45612375, 'Sara García M', 'Colono Agrícola', 89889888, 'sara@colono.com', '2024-03-08', 0, 1588856.00),
(45636520, 'María Duarte Membreño', 'Fruty Corp', 89889889, 'maria.duarte@frutycorp.net', '2024-04-04', 1, 1465156.00);

-- --------------------------------------------------------

--
-- Table structure for table `supply`
--

CREATE TABLE `supply` (
  `idSupply` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `category` varchar(50) NOT NULL,
  `stock` decimal(10,2) NOT NULL DEFAULT 0.00,
  `stockMinimo` decimal(10,2) NOT NULL DEFAULT 0.00,
  `unitType` varchar(30) NOT NULL,
  `pricePerUnit` decimal(10,2) NOT NULL,
  `expirationDate` date NOT NULL,
  `supplierId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `supply`
--

INSERT INTO `supply` (`idSupply`, `name`, `category`, `stock`, `stockMinimo`, `unitType`, `pricePerUnit`, `expirationDate`, `supplierId`) VALUES
(14, 'Pala Grande', 'Herramientas', 3.00, 5.00, 'Unidad', 9800.00, '2025-05-09', 45636520),
(15, 'Semillas Girasol', 'Semillas', 50.00, 10.00, 'Paquete', 2500.00, '2025-12-31', 45612375);

-- --------------------------------------------------------

--
-- Table structure for table `tb_maquina`
--

CREATE TABLE `tb_maquina` (
  `id` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `condicion` varchar(50) NOT NULL,
  `disponibilidad` tinyint(1) NOT NULL,
  `dia_adquisicion` date NOT NULL,
  `costo_alquiler` double NOT NULL,
  `ubicacion` varchar(100) NOT NULL,
  `capacidad_trabajo` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tb_maquina`
--

INSERT INTO `tb_maquina` (`id`, `nombre`, `condicion`, `disponibilidad`, `dia_adquisicion`, `costo_alquiler`, `ubicacion`, `capacidad_trabajo`) VALUES
(13, 'Topadora', 'Usada', 1, '2020-04-12', 230.4, 'Cartago', 'Baja'),
(14, 'Grua Telescópica', 'Buena', 1, '2022-12-15', 375.6, 'Heredia', 'Alta'),
(15, 'Barredora Vial', 'Usada', 0, '2019-05-22', 280, 'Puntarenas', 'Media'),
(16, 'Perforadora', 'Nueva', 0, '2023-03-10', 290.75, 'San José', 'Alta'),
(17, 'Fresadora', 'Reparación', 0, '2020-11-25', 310.5, 'Alajuela', 'Baja'),
(18, 'Cargador Frontal', 'Usada', 1, '2021-09-05', 270.8, 'Cartago', 'Media'),
(19, 'Hidroelevador', 'Buena', 1, '2023-06-14', 340, 'Heredia', 'Alta'),
(20, 'Zanjadora', 'Usada', 0, '2017-08-30', 190, 'Puntarenas', 'Baja'),
(21, 'Bomba de Agua', 'Buena', 1, '2024-05-10', 160.5, 'San José', 'Media'),
(22, 'Cortadora de Concreto', 'Reparación', 0, '2019-10-12', 220.9, 'Alajuela', 'Baja'),
(23, 'Autohormigonera', 'Usada', 1, '2022-01-30', 380, 'Cartago', 'Alta'),
(24, 'Tuneladora', 'Buena', 1, '2023-09-28', 450.5, 'Heredia', 'Alta'),
(25, 'Martillo Hidráulico', 'Usada', 0, '2018-12-05', 170, 'Puntarenas', 'Media'),
(26, 'Trituradora', 'Buena', 1, '2021-04-10', 390.75, 'San José', 'Alta'),
(27, 'Cosechadora', 'Reparación', 0, '2020-06-22', 260, 'Alajuela', 'Baja'),
(28, 'Sembradora', 'Usada', 1, '2022-11-08', 210.4, 'Cartago', 'Media'),
(29, 'Arado', 'Buena', 1, '2023-07-18', 150.9, 'Heredia', 'Alta'),
(30, 'Rastra', 'Usada', 0, '2019-03-15', 190.5, 'Puntarenas', 'Baja'),
(31, 'Empacadora', 'Buena', 1, '2024-02-14', 340, 'San José', 'Media'),
(32, 'Carretón', 'Reparación', 0, '2020-09-12', 125, 'Alajuela', 'Baja'),
(33, 'Niveladora', 'Usada', 1, '2021-12-20', 410.6, 'Cartago', 'Alta'),
(34, 'Cisterna', 'Buena', 1, '2023-10-01', 280, 'Heredia', 'Alta'),
(35, 'Motocultor', 'Usada', 0, '2018-07-23', 195.3, 'Puntarenas', 'Alta'),
(87, 'camion', 'Nueva', 1, '2023-12-07', 90000, 'Liberia', 'Alta'),
(89, 'Chapulin', 'Nueva', 1, '2023-12-06', 5444, 'Liberia', 'Alta'),
(332, 'perro', 'Nueva', 1, '2023-12-15', 2999, 'Limon', 'alta'),
(500, 'machete', 'Usada', 1, '2023-12-14', 2345, 'Cartago', 'Alta'),
(757, 'Martillo', 'Nueva', 1, '2023-12-21', 200000, 'San Jose', 'Buena'),
(876, 'Chapulin', 'Nueva', 0, '2023-12-05', 5444, 'Liberia', 'Alta'),
(987, 'carro', 'Nueva', 0, '2023-12-14', 5444, 'Liberia', 'Alta'),
(3434, 'Tractor', 'Usada', 0, '2023-12-06', 8000, 'San Jose', 'Alta');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `producer`
--
ALTER TABLE `producer`
  ADD PRIMARY KEY (`id_producer`);

--
-- Indexes for table `supplier`
--
ALTER TABLE `supplier`
  ADD PRIMARY KEY (`supplierIdentification`);

--
-- Indexes for table `supply`
--
ALTER TABLE `supply`
  ADD PRIMARY KEY (`idSupply`),
  ADD KEY `fk_supply_supplier_idx` (`supplierId`);

--
-- Indexes for table `tb_maquina`
--
ALTER TABLE `tb_maquina`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `supply`
--
ALTER TABLE `supply`
  MODIFY `idSupply` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `supply`
--
ALTER TABLE `supply`
  ADD CONSTRAINT `fk_supply_supplier` FOREIGN KEY (`supplierId`) REFERENCES `supplier` (`supplierIdentification`) ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
