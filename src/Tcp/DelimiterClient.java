package Tcp;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.*;
import java.util.Base64;

public class DelimiterClient {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 5200;
        String aesKeyStr = "abcdefghijklmnop";
        SecretKeySpec aesKey = new SecretKeySpec(aesKeyStr.getBytes(), "AES");
        String delimiter = "#END#";

        String message = "Este es un mensaje largo con delimitador";
        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            String encryptedMsg = Base64.getEncoder().encodeToString(cipher.doFinal(message.getBytes())) + delimiter;
            out.println(encryptedMsg);

            StringBuilder responseSB = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null && !line.endsWith(delimiter)) {
                responseSB.append(line);
            }
            String encryptedResponse = responseSB.toString().replace(delimiter, "");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            byte[] responseBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedResponse));
            String response = new String(responseBytes);
            System.out.println("Respuesta del servidor: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
