CREATE DATABASE easyshare DEFAULT CHARACTER SET utf8;
DROP USER ''@'localhost';
CREATE USER 'easyshare'@'%' IDENTIFIED BY 'easyshare';
GRANT ALL PRIVILEGES ON easyshare.* TO 'easyshare'@'%';
