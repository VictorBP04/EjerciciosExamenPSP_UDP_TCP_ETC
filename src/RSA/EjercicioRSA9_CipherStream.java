package RSA;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.CipherInputStream;
import java.io.*;
import java.security.*;

public class EjercicioRSA9_CipherStream {
    public static void main(String[] args) {
        try {
            // Generar un par de claves RSA
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();

            String mensaje = "Mensaje usando CipherOutputStream y CipherInputStream con RSA.";

            // Configurar Cipher para encriptación RSA
            Cipher cipherEnc = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipherEnc.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());

            // Escribir el mensaje en un archivo cifrado
            try (FileOutputStream fos = new FileOutputStream("mensaje_cifrado.dat");
                 CipherOutputStream cos = new CipherOutputStream(fos, cipherEnc)) {
                cos.write(mensaje.getBytes("UTF-8"));
            }

            // Configurar Cipher para desencriptación RSA
            Cipher cipherDec = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipherDec.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());

            // Leer el mensaje cifrado desde el archivo
            try (FileInputStream fis = new FileInputStream("mensaje_cifrado.dat");
                 CipherInputStream cis = new CipherInputStream(fis, cipherDec);
                 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[256];
                int bytesRead;
                while ((bytesRead = cis.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }
                String mensajeDesencriptado = new String(baos.toByteArray(), "UTF-8");
                System.out.println("Mensaje desencriptado: " + mensajeDesencriptado);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
