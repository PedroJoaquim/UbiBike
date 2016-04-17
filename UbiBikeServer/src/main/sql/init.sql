SET foreign_key_checks = 0;

DROP TABLE IF EXISTS users;
CREATE TABLE users (
  uid        INT           NOT NULL AUTO_INCREMENT,
  username   VARCHAR(30)   NOT NULL,
  public_key VARCHAR(2048) NOT NULL,
  password   BINARY(32)    NOT NULL,
  points     INT           NOT NULL DEFAULT 0,
  PRIMARY KEY (uid),
  UNIQUE (username)
);

DROP TABLE IF EXISTS trajectories;
CREATE TABLE trajectories (
  tid            INT AUTO_INCREMENT NOT NULL,
  uid            INT                NOT NULL,
  start_sid      INT                NOT NULL,
  end_sid        INT                NOT NULL,
  coords_text    VARCHAR(5000)      NOT NULL,
  points_earned  INT                NOT NULL,
  user_tid       VARCHAR(124)       NOT NULL UNIQUE,
  distance       FLOAT              NOT NULL,
  ride_start_timestamp LONG  NOT NULL,
  ride_end_timestamp LONG  NOT NULL,
  PRIMARY KEY (tid)
);

DROP TABLE IF EXISTS points_transactions;
CREATE TABLE points_transactions (
  ptid                INT NOT NULL AUTO_INCREMENT,
  sender_uid          INT NOT NULL,
  receiver_uid        INT NOT NULL,
  points              INT NOT NULL,
  execution_timestamp TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (ptid, sender_uid, receiver_uid),
  FOREIGN KEY (sender_uid) REFERENCES users (uid),
  FOREIGN KEY (receiver_uid) REFERENCES users (uid),
  UNIQUE (ptid)
);

DROP TABLE IF EXISTS sessions;
CREATE TABLE sessions (
  uid             INT NOT NULL,
  session_id      INT NOT NULL,
  start_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (session_id)
);

DROP TABLE IF EXISTS stations;
CREATE TABLE stations (
  sid          INT         NOT NULL AUTO_INCREMENT,
  station_name VARCHAR(25) NOT NULL,
  lat          DOUBLE      NOT NULL,
  lng          DOUBLE      NOT NULL,
  PRIMARY KEY (sid),
  UNIQUE (station_name)
);

DROP TABLE IF EXISTS bikes_stations;
CREATE TABLE bikes_stations (
  sid INT NOT NULL,
  bid INT NOT NULL,
  PRIMARY KEY (bid, sid),
  FOREIGN KEY (sid) REFERENCES stations (sid),
  UNIQUE (bid)
);

DROP TABLE IF EXISTS bookings;
CREATE TABLE bookings (
  booking_id INT NOT NULL AUTO_INCREMENT,
  bid INT NOT NULL,
  uid INT NOT NULL,
  active BOOL NOT NULL DEFAULT FALSE,
  booking_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (booking_id, bid)
);

SET foreign_key_checks = 1;




