package RSA;

import java.security.*;
import javax.crypto.Cipher;
import java.util.Base64;

public class EjercicioRSA10_ComparacionPadding {
    public static void main(String[] args) {
        try {
            // Generar un par de claves RSA
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();

            String mensaje = "Comparando RSA con diferentes paddings.";

            // Encriptar con PKCS1Padding
            Cipher cipherPKCS1 = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipherPKCS1.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
            byte[] encriptadoPKCS1 = cipherPKCS1.doFinal(mensaje.getBytes("UTF-8"));
            String encriptadoPKCS1Base64 = Base64.getEncoder().encodeToString(encriptadoPKCS1);
            System.out.println("Encriptado con PKCS1Padding: " + encriptadoPKCS1Base64);

            // Encriptar con OAEPWithSHA-256AndMGF1Padding
            Cipher cipherOAEP = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            cipherOAEP.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
            byte[] encriptadoOAEP = cipherOAEP.doFinal(mensaje.getBytes("UTF-8"));
            String encriptadoOAEPBase64 = Base64.getEncoder().encodeToString(encriptadoOAEP);
            System.out.println("Encriptado con OAEPWithSHA-256AndMGF1Padding: " + encriptadoOAEPBase64);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
