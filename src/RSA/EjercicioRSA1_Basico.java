package RSA;

import java.security.*;
import javax.crypto.Cipher;
import java.util.Base64;

public class EjercicioRSA1_Basico {
    public static void main(String[] args) {
        try {
            // Generar un par de claves RSA de 2048 bits
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();

            // Mensaje a encriptar
            String mensaje = "Este es un mensaje secreto con RSA.";

            // Crear el objeto Cipher con RSA y padding PKCS1
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

            // Encriptar el mensaje con la clave pública
            cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
            byte[] mensajeEncriptado = cipher.doFinal(mensaje.getBytes("UTF-8"));
            String mensajeEncriptadoBase64 = Base64.getEncoder().encodeToString(mensajeEncriptado);
            System.out.println("Mensaje encriptado (Base64): " + mensajeEncriptadoBase64);

            // Desencriptar el mensaje con la clave privada
            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            byte[] mensajeDesencriptado = cipher.doFinal(mensajeEncriptado);
            System.out.println("Mensaje desencriptado: " + new String(mensajeDesencriptado, "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

