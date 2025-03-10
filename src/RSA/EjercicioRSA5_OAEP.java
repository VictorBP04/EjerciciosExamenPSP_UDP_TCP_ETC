package RSA;

import java.security.*;
import javax.crypto.Cipher;
import java.util.Base64;

public class EjercicioRSA5_OAEP {
    public static void main(String[] args) {
        try {
            // Generar un par de claves RSA de 2048 bits
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();

            String mensaje = "Mensaje usando RSA con OAEP padding";

            // Usar RSA con OAEP y SHA-256
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");

            // Encriptar el mensaje con la clave pública
            cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
            byte[] mensajeEncriptado = cipher.doFinal(mensaje.getBytes("UTF-8"));
            String mensajeEncriptadoBase64 = Base64.getEncoder().encodeToString(mensajeEncriptado);
            System.out.println("Mensaje encriptado (OAEP, Base64): " + mensajeEncriptadoBase64);

            // Desencriptar el mensaje con la clave privada
            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            byte[] mensajeDesencriptado = cipher.doFinal(mensajeEncriptado);
            System.out.println("Mensaje desencriptado: " + new String(mensajeDesencriptado, "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

