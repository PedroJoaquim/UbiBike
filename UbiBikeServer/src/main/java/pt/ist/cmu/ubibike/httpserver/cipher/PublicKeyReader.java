package pt.ist.cmu.ubibike.httpserver.cipher;

import pt.ist.cmu.ubibike.httpserver.util.ResourceFileLoader;

import java.io.*;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

public class PublicKeyReader {

    private static final String PUBLIC_KEY_CER_PATH = "keys/public_key.der";

    private static PublicKey pKey = null;

    public static PublicKey getKey() {


        if(pKey != null){
            return pKey;
        }

        DataInputStream dis = null;

        try{

            File f = ResourceFileLoader.getInstance().loadFile(PUBLIC_KEY_CER_PATH);
            dis = new DataInputStream(new FileInputStream(f));
            byte[] keyBytes = new byte[(int)f.length()];
            dis.readFully(keyBytes);
            dis.close();

            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");

            pKey = kf.generatePublic(spec);
            return pKey;

        }catch (Exception e){
            return null;

        }finally {
            try{
                if(dis != null){
                    dis.close();
                }
            }
            catch (Exception e){
                //ignore
            }
        }
    }
}
