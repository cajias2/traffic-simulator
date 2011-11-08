SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

CREATE SCHEMA IF NOT EXISTS `socialSimBD` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci ;
USE `socialSimBD` ;

-- -----------------------------------------------------
-- Table `socialSimBD`.`simulations`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `socialSimBD`.`simulations` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `socialSimBD`.`nodes`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `socialSimBD`.`nodes` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  PRIMARY KEY (`id`) ,
  INDEX `sim_id` () ,
  CONSTRAINT `sim_id`
    FOREIGN KEY ()
    REFERENCES `socialSimBD`.`simulations` ()
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `socialSimBD`.`graphs`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `socialSimBD`.`graphs` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `step` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`id`, `step`) ,
  INDEX `sim_id` () ,
  INDEX `from_node` () ,
  INDEX `to_node` () ,
  CONSTRAINT `sim_id`
    FOREIGN KEY ()
    REFERENCES `socialSimBD`.`simulations` ()
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `from_node`
    FOREIGN KEY ()
    REFERENCES `socialSimBD`.`nodes` ()
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `to_node`
    FOREIGN KEY ()
    REFERENCES `socialSimBD`.`nodes` ()
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `socialSimBD`.`metrics`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `socialSimBD`.`metrics` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `avg_ci` FLOAT NULL ,
  `cpl` FLOAT NULL ,
  INDEX `graph_id` () ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `graph_id`
    FOREIGN KEY ()
    REFERENCES `socialSimBD`.`graphs` ()
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `socialSimBD`.`communities`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `socialSimBD`.`communities` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  PRIMARY KEY (`id`) ,
  INDEX `graph_id` () ,
  CONSTRAINT `graph_id`
    FOREIGN KEY ()
    REFERENCES `socialSimBD`.`graphs` ()
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `socialSimBD`.`community_members`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `socialSimBD`.`community_members` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  PRIMARY KEY (`id`) ,
  INDEX `com_id` () ,
  INDEX `node_id` () ,
  CONSTRAINT `com_id`
    FOREIGN KEY ()
    REFERENCES `socialSimBD`.`communities` ()
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `node_id`
    FOREIGN KEY ()
    REFERENCES `socialSimBD`.`nodes` ()
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `socialSimBD`.`evolution`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `socialSimBD`.`evolution` (
  `id` INT NOT NULL ,
  `step` INT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `from_com` () ,
  INDEX `to_com` () ,
  CONSTRAINT `from_com`
    FOREIGN KEY ()
    REFERENCES `socialSimBD`.`communities` ()
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `to_com`
    FOREIGN KEY ()
    REFERENCES `socialSimBD`.`communities` ()
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


CREATE USER `rolf` IDENTIFIED BY 'miramar';

CREATE USER `antonio` IDENTIFIED BY 'antonio';


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
