package Upd;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;

public class EjemploAES {
    public static void main(String[] args) {
        try {
            // Generar la clave AES (128 bits)
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128); // tamaño de la clave en bits
            SecretKey secretKey = keyGen.generateKey();

            // Mensaje original a cifrar
            String mensaje = "Este es un mensaje secreto.";

            // Crear el cifrador para AES en modo ECB con PKCS5Padding
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

            // Cifrado: inicializar el cifrador en modo encriptación con la clave
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] mensajeCifrado = cipher.doFinal(mensaje.getBytes("UTF-8"));
            String mensajeCifradoBase64 = Base64.getEncoder().encodeToString(mensajeCifrado);
            System.out.println("Mensaje Cifrado (Base64): " + mensajeCifradoBase64);

            // Descifrado: inicializar el cifrador en modo desencriptación con la misma clave
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] mensajeDescifrado = cipher.doFinal(mensajeCifrado);
            System.out.println("Mensaje Descifrado: " + new String(mensajeDescifrado, "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

