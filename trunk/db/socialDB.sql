SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

DROP SCHEMA IF EXISTS `socSimDB` ;
CREATE SCHEMA IF NOT EXISTS `socSimDB` DEFAULT CHARACTER SET latin1 ;
USE `socSimDB` ;

-- -----------------------------------------------------
-- Table `socSimDB`.`communities`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `socSimDB`.`communities` ;

CREATE  TABLE IF NOT EXISTS `socSimDB`.`communities` (
  `graph_id` BIGINT NULL ,
  `sim_id` INT NULL ,
  `id` INT NOT NULL AUTO_INCREMENT ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `socSimDB`.`community_members`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `socSimDB`.`community_members` ;

CREATE  TABLE IF NOT EXISTS `socSimDB`.`community_members` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `comm_id` INT NULL ,
  `node_id` INT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `socSimDB`.`evolution`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `socSimDB`.`evolution` ;

CREATE  TABLE IF NOT EXISTS `socSimDB`.`evolution` (
  `id` INT(11) NOT NULL ,
  `step` INT(11) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `socSimDB`.`graphs`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `socSimDB`.`graphs` ;

CREATE  TABLE IF NOT EXISTS `socSimDB`.`graphs` (
  `step` INT NOT NULL ,
  `sim_id` INT(11) NOT NULL ,
  `created` BIGINT NULL ,
  PRIMARY KEY (`step`, `sim_id`) )
ENGINE = InnoDB
AUTO_INCREMENT = 116022
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `socSimDB`.`metrics`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `socSimDB`.`metrics` ;

CREATE  TABLE IF NOT EXISTS `socSimDB`.`metrics` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `avg_ci` FLOAT NULL DEFAULT NULL ,
  `cpl` FLOAT NULL DEFAULT NULL ,
  `graph_id` INT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `socSimDB`.`nodes`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `socSimDB`.`nodes` ;

CREATE  TABLE IF NOT EXISTS `socSimDB`.`nodes` (
  `id` INT(11) NOT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB
AUTO_INCREMENT = 10171
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `socSimDB`.`simulations`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `socSimDB`.`simulations` ;

CREATE  TABLE IF NOT EXISTS `socSimDB`.`simulations` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `time_start` BIGINT NULL DEFAULT NULL ,
  `agent_count` INT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB
AUTO_INCREMENT = 49
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `socSimDB`.`graph_edges`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `socSimDB`.`graph_edges` ;

CREATE  TABLE IF NOT EXISTS `socSimDB`.`graph_edges` (
  `graph_id` BIGINT NULL ,
  `from_node` INT NULL ,
  `to_node` INT NULL ,
  `is_create_edge` TINYINT(1)  NULL ,
  `id` INT NULL AUTO_INCREMENT ,
  `sim_id` INT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
