package Tcp;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.*;
import java.util.Base64;

public class TCPAESServer {
    public static void main(String[] args) {
        int port = 5004;
        // Clave AES fija (16 bytes para AES-128)
        String clave = "1234567890123456";
        SecretKeySpec aesKey = new SecretKeySpec(clave.getBytes(), "AES");

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor TCP AES en puerto " + port);
            try (Socket clientSocket = serverSocket.accept();
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                // Recibe el mensaje cifrado en Base64
                String mensajeCifradoBase64 = in.readLine();
                // Descifrado AES
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.DECRYPT_MODE, aesKey);
                byte[] mensajeCifrado = Base64.getDecoder().decode(mensajeCifradoBase64);
                byte[] mensajeDescifrado = cipher.doFinal(mensajeCifrado);
                System.out.println("Mensaje descifrado: " + new String(mensajeDescifrado));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
