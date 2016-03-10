package pt.ist.cmu.ubibike.httpserver.cipher;

import java.io.*;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;


public class PublicKeyReader {

    private static final String PUBLIC_KEY_CER_PATH = "resource:/keys/public_key.der";

    public static PublicKey getKey() {

        DataInputStream dis = null;

        try{

            File f = new File(PUBLIC_KEY_CER_PATH);
            dis = new DataInputStream(new FileInputStream(f));
            byte[] keyBytes = new byte[(int)f.length()];
            dis.readFully(keyBytes);
            dis.close();

            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");

            return kf.generatePublic(spec);

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
