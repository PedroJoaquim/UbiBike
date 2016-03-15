package pt.ist.cmu.ubibike.httpserver.util;

import pt.ist.cmu.ubibike.httpserver.model.Coordinate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pedro Joaquim on 15-03-2016.
 */
public class CoordinatesParser {

    private static final String DELIMITER_COORDS = ";";
    private static final String DELIMITER_POINTS = "#";

    public static String toStoreFormat(Coordinate[] coords) {
        String result = "";

        for (int i = 0; i < coords.length; i++) {
            result += Double.toString(coords[i].getLat()) + DELIMITER_COORDS + Double.toString(coords[i].getLng());
            if(i != coords.length - 1) {result += DELIMITER_POINTS;}
        }

        return result;
    }



    public static Coordinate[] fromStoreFormat(String coords){

        List<Coordinate> result = new ArrayList<Coordinate>();

        String[] splitedPoints = coords.split(DELIMITER_POINTS);

        for(String point : splitedPoints){
            String[] splitedCoords = point.split(DELIMITER_COORDS);
            result.add(new Coordinate(Double.valueOf(splitedCoords[0]), Double.valueOf(splitedCoords[1])));
        }

        return result.toArray(new Coordinate[result.size()]);
    }
}
