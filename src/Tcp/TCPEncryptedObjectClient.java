package Tcp;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.*;
import java.util.Base64;

public class TCPEncryptedObjectClient {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 5008;
        String clave = "1234567890123456";
        SecretKeySpec aesKey = new SecretKeySpec(clave.getBytes(), "AES");
        String mensajeOriginal = "Objeto serializado y cifrado con AES";
        try (Socket socket = new Socket(host, port);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
            // Cifrar el mensaje
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] mensajeCifrado = cipher.doFinal(mensajeOriginal.getBytes());
            String mensajeCifradoBase64 = Base64.getEncoder().encodeToString(mensajeCifrado);
            // Enviar el mensaje cifrado como objeto
            oos.writeObject(mensajeCifradoBase64);
            System.out.println("Objeto cifrado enviado: " + mensajeCifradoBase64);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
