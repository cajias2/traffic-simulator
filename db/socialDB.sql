SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

DROP SCHEMA IF EXISTS `socSimDB` ;
CREATE SCHEMA IF NOT EXISTS `socSimDB` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci ;
USE `socSimDB` ;

-- -----------------------------------------------------
-- Table `socSimDB`.`simulations`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `socSimDB`.`simulations` ;

CREATE  TABLE IF NOT EXISTS `socSimDB`.`simulations` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `time_start` MEDIUMTEXT  NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `socSimDB`.`nodes`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `socSimDB`.`nodes` ;

CREATE  TABLE IF NOT EXISTS `socSimDB`.`nodes` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `node` INT NOT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `socSimDB`.`graphs`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `socSimDB`.`graphs` ;

CREATE  TABLE IF NOT EXISTS `socSimDB`.`graphs` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `step` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`id`, `step`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `socSimDB`.`metrics`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `socSimDB`.`metrics` ;

CREATE  TABLE IF NOT EXISTS `socSimDB`.`metrics` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `avg_ci` FLOAT NULL ,
  `cpl` FLOAT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `socSimDB`.`communities`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `socSimDB`.`communities` ;

CREATE  TABLE IF NOT EXISTS `socSimDB`.`communities` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `socSimDB`.`community_members`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `socSimDB`.`community_members` ;

CREATE  TABLE IF NOT EXISTS `socSimDB`.`community_members` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `socSimDB`.`evolution`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `socSimDB`.`evolution` ;

CREATE  TABLE IF NOT EXISTS `socSimDB`.`evolution` (
  `id` INT NOT NULL ,
  `step` INT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
