INSERT INTO stations(sid, station_name, lat, lng) VALUES (1, "cenas", -20, -10);
INSERT INTO stations(sid, station_name, lat, lng) VALUES (2, "coisas", -30, -20);

INSERT INTO bikes_stations(sid, bid) VALUES (1, 1), (1, 2), (1, 3), (2,4), (2, 5);

INSERT INTO bookings(bid, uid) VALUES (2, 1), (5, 1);