SET SESSION sql_mode='NO_AUTO_VALUE_ON_ZERO';
SET foreign_key_checks = 0;

DROP TABLE IF EXISTS users;
CREATE TABLE users (
  uid        INT           NOT NULL AUTO_INCREMENT,
  username   VARCHAR(30)   NOT NULL,
  public_key VARCHAR(2048) NOT NULL,
  password   BINARY(32)    NOT NULL,
  points     INT           NOT NULL DEFAULT 500,
  logical_clock INT        NOT NULL DEFAULT 0,
  PRIMARY KEY (uid),
  UNIQUE (username)
);

DROP TABLE IF EXISTS points_transactions;
CREATE TABLE points_transactions (
  source_uid           VARCHAR(30)   NOT NULL,
  source_logical_clock INT NOT NULL,
  target_uid           VARCHAR(30)   NOT NULL,
  target_logical_clock INT NOT NULL,
  points               INT NOT NULL,
  transaction_timestamp LONG NOT NULL
);

DROP TABLE IF EXISTS pending_events;
CREATE TABLE pending_events (
  pe_id                INT NOT NULL AUTO_INCREMENT,
  source_uid           VARCHAR(30)   NOT NULL,
  source_logical_clock INT NOT NULL,
  target_uid           VARCHAR(30)   NOT NULL,
  target_logical_clock INT NOT NULL,
  points               INT NOT NULL,
  transaction_timestamp LONG NOT NULL,
  type                 INT NOT NULL,
  PRIMARY KEY (pe_id)
);

DROP TABLE IF EXISTS trajectories;
CREATE TABLE trajectories (
  tid            INT                NOT NULL DEFAULT 0,
  uid            INT                NOT NULL,
  start_sid      INT                NOT NULL,
  end_sid        INT                NOT NULL,
  coords_text    VARCHAR(5000)      NOT NULL,
  points_earned  INT                NOT NULL,
  user_tid       VARCHAR(124)       NOT NULL UNIQUE,
  distance       FLOAT              NOT NULL,
  ride_start_timestamp LONG  NOT NULL,
  ride_end_timestamp LONG  NOT NULL
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

DROP TABLE IF EXISTS bikes;
CREATE TABLE bikes (
  bid INT NOT NULL,
  bike_addr VARCHAR(40),
  PRIMARY KEY (bid)
);

DROP TABLE IF EXISTS bikes_stations;
CREATE TABLE bikes_stations (
  sid INT NOT NULL,
  bid INT NOT NULL,
  PRIMARY KEY (bid, sid),
  FOREIGN KEY (sid) REFERENCES stations (sid),
  FOREIGN KEY (bid) REFERENCES bikes (bid)
);

DROP TABLE IF EXISTS bookings;
CREATE TABLE bookings (
  booking_id INT NOT NULL AUTO_INCREMENT,
  bid INT NOT NULL,
  uid INT NOT NULL,
  source_sid INT NOT NULL,
  active BOOL NOT NULL DEFAULT FALSE,
  booking_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (booking_id, bid)
);

SET foreign_key_checks = 1;




