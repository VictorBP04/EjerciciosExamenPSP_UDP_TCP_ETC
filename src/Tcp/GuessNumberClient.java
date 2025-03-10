package Tcp;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.*;
import java.util.Base64;
import java.util.Scanner;

public class GuessNumberClient {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 5100;
        String aesKeyStr = "1234567890123456";
        SecretKeySpec aesKey = new SecretKeySpec(aesKeyStr.getBytes(), "AES");

        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            // Hasta 5 intentos
            for (int i = 0; i < 5; i++) {
                System.out.print("Ingresa tu conjetura (1-100): ");
                String guess = scanner.nextLine();

                // Cifrado de la conjetura
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.ENCRYPT_MODE, aesKey);
                byte[] encryptedBytes = cipher.doFinal(guess.getBytes());
                out.println(Base64.getEncoder().encodeToString(encryptedBytes));

                // Recibir y descifrar pista
                String encryptedHint = in.readLine();
                cipher.init(Cipher.DECRYPT_MODE, aesKey);
                byte[] hintBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedHint));
                String hint = new String(hintBytes);
                System.out.println("Pista: " + hint);
                if ("Correcto".equalsIgnoreCase(hint)) {
                    System.out.println("¡Has ganado!");
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
