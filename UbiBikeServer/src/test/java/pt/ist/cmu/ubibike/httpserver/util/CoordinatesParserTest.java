package pt.ist.cmu.ubibike.httpserver.util;

import org.junit.Assert;
import org.junit.Test;
import pt.ist.cmu.ubibike.httpserver.model.Coordinate;

import static org.junit.Assert.*;

/**
 * Created by Pedro Joaquim on 15-03-2016.
 */
public class CoordinatesParserTest {

    private static final String coords = "1.0;1.0#-12.47;0.34#2.988;67.0";


    @Test
    public void testToStoreFormat() throws Exception {
        Coordinate[] coordinates = {new Coordinate(1,1), new Coordinate(-12.47,0.34), new Coordinate(2.988,67)};

        String storeFormat = CoordinatesParser.toStoreFormat(coordinates);

        Assert.assertEquals(coords, storeFormat);
    }

    @Test
    public void testFromStoreFormat() throws Exception {

        Coordinate[] coordinates = {new Coordinate(1,1), new Coordinate(-12.47,0.34), new Coordinate(2.988,67)};

        Coordinate[] coordinates2 = CoordinatesParser.fromStoreFormat(coords);


        Assert.assertEquals(coordinates.length, coordinates2.length);
        Assert.assertArrayEquals(coordinates, coordinates2);

    }
}