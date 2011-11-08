SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

DROP SCHEMA IF EXISTS `socSimDB` ;
CREATE SCHEMA IF NOT EXISTS `socSimDB` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci ;
USE `socSimDB` ;

-- -----------------------------------------------------
-- Table `simulations`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `simulations` ;

CREATE  TABLE IF NOT EXISTS `simulations` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `nodes`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `nodes` ;

CREATE  TABLE IF NOT EXISTS `nodes` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `graphs`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `graphs` ;

CREATE  TABLE IF NOT EXISTS `graphs` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `step` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`id`, `step`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `metrics`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `metrics` ;

CREATE  TABLE IF NOT EXISTS `metrics` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `avg_ci` FLOAT NULL ,
  `cpl` FLOAT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `communities`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `communities` ;

CREATE  TABLE IF NOT EXISTS `communities` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `community_members`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `community_members` ;

CREATE  TABLE IF NOT EXISTS `community_members` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `evolution`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `evolution` ;

CREATE  TABLE IF NOT EXISTS `evolution` (
  `id` INT NOT NULL ,
  `step` INT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


CREATE USER `rolf` IDENTIFIED BY 'miramar';

CREATE USER `antonio` IDENTIFIED BY 'antonio';


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
