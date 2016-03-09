
SET foreign_key_checks = 0;

DROP TABLE IF EXISTS users ;
CREATE TABLE users(
  uid 			VARCHAR (256) NOT NULL,
  name VARCHAR (30) NULL,
  email VARCHAR (256) NULL,
  public_key BLOB NOT NULL,
  PRIMARY KEY (uid),
  UNIQUE (email)
);

DROP TABLE IF EXISTS private_info ;
CREATE TABLE private_info(
  private_key BLOB NOT NULL,
  public_key BLOB NOT NULL
);


SET foreign_key_checks = 1 ;




