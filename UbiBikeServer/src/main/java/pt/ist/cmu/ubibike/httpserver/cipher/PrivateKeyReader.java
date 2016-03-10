package pt.ist.cmu.ubibike.httpserver.cipher;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * Created by ASUS on 10/03/2016.
 */
public class PrivateKeyReader {

    private static final String PRIVATE_KEY_CER_PATH = "resource:/keys/private_key.der";

    public static PrivateKey getKey() {

        DataInputStream dis = null;

        try{
            File f = new File(PRIVATE_KEY_CER_PATH);
            dis = new DataInputStream(new FileInputStream(f));
            byte[] keyBytes = new byte[(int)f.length()];
            dis.readFully(keyBytes);

            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(spec);

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
