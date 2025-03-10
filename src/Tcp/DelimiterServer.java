package Tcp;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.*;
import java.util.Base64;

public class DelimiterServer {
    public static void main(String[] args) {
        int port = 5200;
        String aesKeyStr = "abcdefghijklmnop"; // 16 bytes
        SecretKeySpec aesKey = new SecretKeySpec(aesKeyStr.getBytes(), "AES");
        String delimiter = "#END#";

        System.out.println("Servidor con delimitador en puerto " + port);
        try (ServerSocket serverSocket = new ServerSocket(port);
             Socket clientSocket = serverSocket.accept();
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null && !line.endsWith(delimiter)) {
                sb.append(line);
            }
            String encryptedMsgBase64 = sb.toString().replace(delimiter, "");

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            byte[] msgBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedMsgBase64));
            String message = new String(msgBytes);
            System.out.println("Mensaje recibido: " + message);

            // Responder confirmación con delimitador
            String response = "Mensaje recibido: " + message;
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            String encryptedResponse = Base64.getEncoder().encodeToString(cipher.doFinal(response.getBytes())) + delimiter;
            out.println(encryptedResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
