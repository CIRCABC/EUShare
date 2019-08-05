CREATE DATABASE easyshare DEFAULT CHARACTER SET utf8;
CREATE USER 'easyshare'@'%' IDENTIFIED BY 'easyshare';
GRANT ALL PRIVILEGES ON easyshare.* TO 'easyshare'@'%';
