package pt.ulisboa.tecnico.cmu.ubibike.utils;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public final class DigitalSignature {

    public static byte[] signData(byte[] buffer, PrivateKey privateKey) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature dsa = Signature.getInstance("SHA256withRSA");
        dsa.initSign(privateKey);
        dsa.update(buffer);
        return dsa.sign();
    }

    public static boolean verifySign(byte[] data, byte[] signature, PublicKey public_key) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, InvalidKeySpecException {
        boolean verifies = false;
        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(public_key.getEncoded());
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
        java.security.Signature sig = java.security.Signature.getInstance("SHA256withRSA");
        sig.initVerify(pubKey);
        sig.update(data);
        verifies = sig.verify(signature);
        return verifies;
    }
}
