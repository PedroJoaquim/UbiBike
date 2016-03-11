package pt.ist.cmu.ubibike.httpserver.cipher;

import pt.ist.cmu.ubibike.httpserver.util.ResourceFileLoader;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

public class PrivateKeyReader {

    private static final String PRIVATE_KEY_CER_PATH = "keys/private_key.der";

    private static PrivateKey privateKey = null;

    public static PrivateKey getKey() {


        if(privateKey != null){
            return  privateKey;
        }

        DataInputStream dis = null;

        try{
            File f = ResourceFileLoader.getInstance().loadFile(PRIVATE_KEY_CER_PATH);
            dis = new DataInputStream(new FileInputStream(f));
            byte[] keyBytes = new byte[(int)f.length()];
            dis.readFully(keyBytes);

            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");

            privateKey = kf.generatePrivate(spec);
            return privateKey;

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
