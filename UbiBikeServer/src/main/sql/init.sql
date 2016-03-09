
SET foreign_key_checks = 0;

DROP TABLE IF EXISTS users ;
CREATE TABLE users(
  uid 			INT NOT NULL,
  username VARCHAR (30) NULL,
  email VARCHAR (256) NULL,
  public_key VARCHAR(2048) NOT NULL,
  password BLOB NOT NULL,
  points INT NOT NULL DEFAULT 0,
  PRIMARY KEY (uid),
  UNIQUE (email, username)
);

DROP TABLE IF EXISTS trajectories;
CREATE TABLE trajectories(
  tid INT AUTO_INCREMENT NOT NULL,
  uid INT NOT NULL,
  coords_json VARCHAR(5000),
  points_earned INT NOT NULL,
  ride_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (tid)
);

DROP TABLE IF EXISTS points_transactions;
CREATE TABLE points_transactions(
  ptid INT NOT NULL AUTO_INCREMENT,
  sender_uid INT NOT NULL,
  receiver_uid INT NOT NULL,
  points INT NOT NULL,
  execution_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (ptid, sender_uid, receiver_uid),
  FOREIGN KEY (sender_uid) REFERENCES users(uid),
  FOREIGN KEY (receiver_uid) REFERENCES users(uid),
  UNIQUE (ptid)
);

SET foreign_key_checks = 1 ;




