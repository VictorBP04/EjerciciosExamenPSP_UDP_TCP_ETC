package Upd;

import javax.crypto.Cipher;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

public class EjemploRSA {
    public static void main(String[] args) {
        try {
            // Generar par de claves RSA (2048 bits)
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(2048);
            KeyPair keyPair = keyPairGen.generateKeyPair();

            String mensaje = "Mensaje confidencial para RSA";

            // Cifrado con la clave pública
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
            byte[] mensajeCifrado = cipher.doFinal(mensaje.getBytes("UTF-8"));
            String mensajeCifradoBase64 = Base64.getEncoder().encodeToString(mensajeCifrado);
            System.out.println("Mensaje Cifrado (RSA, Base64): " + mensajeCifradoBase64);

            // Descifrado con la clave privada
            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            byte[] mensajeDescifrado = cipher.doFinal(mensajeCifrado);
            System.out.println("Mensaje Descifrado: " + new String(mensajeDescifrado, "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

