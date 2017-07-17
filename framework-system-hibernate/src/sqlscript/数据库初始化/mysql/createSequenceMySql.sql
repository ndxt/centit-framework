-- mysql -u tfzc -D tfzcpt -p <D:\Projects\j2eews\tfzcpt\src\sqlScript\createSequence.sql

-- use tfzcpt;

DROP TABLE IF EXISTS f_mysql_sequence;  
  

CREATE TABLE  f_mysql_sequence (
  name varchar(50) NOT NULL,  
  currvalue int(11) NOT NULL,  
  increment int(11) NOT NULL DEFAULT '1',  
   primary key (name)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='ÐòÁÐ±í£¬ÃüÃûs_[table_name]';  
  
DROP FUNCTION IF EXISTS sequence_currval;  

DELIMITER //  
  
CREATE  FUNCTION sequence_currval(seq_name VARCHAR(50)) RETURNS int(11)  
  
    READS SQL DATA  
  
    DETERMINISTIC  
  
BEGIN  
  
DECLARE cur_value INTEGER;  
  
SET cur_value = 0;  
  
SELECT currvalue INTO cur_value FROM f_mysql_sequence WHERE NAME = seq_name;  
  
RETURN cur_value;  
  
END//  
  
DELIMITER ;


DROP FUNCTION IF EXISTS sequence_nextval;  
  
DELIMITER //  
  
CREATE  FUNCTION sequence_nextval(seq_name VARCHAR(50)) RETURNS int(11)  
  
    DETERMINISTIC  
  
BEGIN  
DECLARE cur_value INTEGER;  

UPDATE f_mysql_sequence SET currvalue = currvalue + increment WHERE NAME = seq_name;  
  
SELECT currvalue INTO cur_value FROM f_mysql_sequence WHERE NAME = seq_name;  
  
RETURN cur_value;  
  
END//  
  
DELIMITER ;


DROP FUNCTION IF EXISTS sequence_setval;  
  
DELIMITER //  
  
CREATE  FUNCTION sequence_setval(seq_name VARCHAR(50),seq_value int(11)) RETURNS int(11)  
  
    DETERMINISTIC  
  
BEGIN 

UPDATE f_mysql_sequence SET currvalue = seq_value WHERE NAME = seq_name;  
RETURN seq_value;
END//  
  
DELIMITER ;


INSERT INTO f_mysql_sequence (name, currvalue , increment) VALUES    
('S_MSGCODE', 0, 1);

INSERT INTO f_mysql_sequence (name, currvalue , increment) VALUES    
('S_RECIPIENT', 0, 1);

INSERT INTO f_mysql_sequence (name, currvalue , increment) VALUES    
('S_UNITCODE', 0, 1);

INSERT INTO f_mysql_sequence (name, currvalue , increment) VALUES    
('S_USERCODE', 0, 1);

INSERT INTO f_mysql_sequence (name, currvalue , increment) VALUES    
('S_USER_UNIT_ID', 0, 1);

INSERT INTO f_mysql_sequence (name, currvalue , increment) VALUES    
('S_ADDRESSID', 0, 1);

INSERT INTO f_mysql_sequence (name, currvalue , increment) VALUES    
('S_OPTDEFCODE', 0, 1);


