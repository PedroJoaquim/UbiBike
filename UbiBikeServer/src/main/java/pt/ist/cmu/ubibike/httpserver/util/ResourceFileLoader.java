package pt.ist.cmu.ubibike.httpserver.util;

import pt.ist.cmu.ubibike.httpserver.Server;

import java.io.File;

/**
 * Created by Pedro Joaquim on 11-03-2016.
 */
public class ResourceFileLoader {

    private static ResourceFileLoader singleton = null;

    public static ResourceFileLoader getInstance(){

        if(singleton == null){
            singleton = new ResourceFileLoader();
        }

        return singleton;
    }

    public File loadFile(String filePath){
        ClassLoader classLoader = this.getClass().getClassLoader();
        return new File(classLoader.getResource(filePath).getFile());
    }
}
