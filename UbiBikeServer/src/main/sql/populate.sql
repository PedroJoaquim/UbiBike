INSERT INTO users(username, public_key, password, points) VALUES ('pedro', 'pedro123123', CONV('2702cb34ee041711b9df0c67a8d5c9de02110c80e3fc966ba8341456dbc9ef2b', 16, 2), 0);


INSERT INTO stations(station_name, lat, lng) VALUES   ('Odivelas Station', 38.793017, -9.173086),
                                                      ('Ameixoeira', 38.779865, -9.159804),
                                                      ('Campo Grande Station', 38.759601, -9.157925),
                                                      ('Alvalade Station', 38.753040, -9.143829),
                                                      ('Entrecampos Station', 38.747692, -9.148506),
                                                      ('Alameda Station', 38.737073, -9.133582),
                                                      ('Arco do Cego Station', 38.735361, -9.142362),
                                                      ('Parque Station', 38.729628, -9.150012),
                                                      ('Avenida Station', 38.719981, -9.145588),
                                                      ('Rossio Station', 38.719981, -9.145588),
                                                      ('Indendente Station', 38.722029, -9.135263),
                                                      ('Arroios Station', 38.737073, -9.133582),
                                                      ('Chelas Station', 38.755019, -9.114212),
                                                      ('Oriente Station', 38.768527, -9.099648),
                                                      ('Moscavide Station', 38.768527, -9.099648),
                                                      ('Laranjeiras', 38.748300, -9.172612),
                                                      ('Colegio Militar Station', 38.753195, -9.188162),
                                                      ('Pontinha Station', 38.762259, -9.196830),
                                                      ('Cais do Sodré Station', 38.705734, -9.144241),
                                                      ('Martim Moniz Station', 38.716798, -9.135628);

Delimiter //

DROP PROCEDURE IF EXISTS initBikeStations//
CREATE PROCEDURE initBikeStations()
  BEGIN
    DECLARE num_stations int;
    DECLARE num_cicles int;
    DECLARE bike_id int;
    DECLARE station_id int;

    SET num_stations = (SELECT COUNT(*) FROM stations);
    SET bike_id = 1;
    SET station_id = 1;

    WHILE station_id <= num_stations DO
      SET num_cicles = 10;
      WHILE num_cicles > 0 DO
        INSERT INTO bikes_stations (sid, bid) VALUES (station_id, bike_id);
        SET num_cicles = num_cicles -1;
        SET bike_id = bike_id +1;
      END WHILE;
      SET station_id = station_id +1;
    END WHILE;
  END //

Delimiter ;

CALL initBikeStations();
