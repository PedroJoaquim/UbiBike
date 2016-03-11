package pt.ist.cmu.ubibike.httpserver.cipher;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;

/**
 * Created by ASUS on 10/03/2016.
 */
public class CipherManager {

    private static final String ALGORITHM = "RSA";

    public static byte[] cipher(byte[] plainData, Key key){

        try{
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            return cipher.doFinal(plainData);

        } catch (Exception e){
            return null;
        }
    }

    public static byte[] decipher(byte[] cipheredData, Key key){

        try{

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);

            return cipher.doFinal(cipheredData);

        } catch (Exception e){
            return null;
        }

    }


}
