package Tcp;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.*;
import java.util.Base64;

public class TCPAESClient {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 5004;
        String clave = "1234567890123456";
        SecretKeySpec aesKey = new SecretKeySpec(clave.getBytes(), "AES");

        String mensajeOriginal = "Mensaje secreto con AES sobre TCP";
        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            // Cifrado AES
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] mensajeCifrado = cipher.doFinal(mensajeOriginal.getBytes());
            String mensajeCifradoBase64 = Base64.getEncoder().encodeToString(mensajeCifrado);
            // Envía el mensaje cifrado
            out.println(mensajeCifradoBase64);
            System.out.println("Mensaje cifrado enviado: " + mensajeCifradoBase64);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

