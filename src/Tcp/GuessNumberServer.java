package Tcp;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.*;
import java.util.Base64;
import java.util.Random;

public class GuessNumberServer {
    public static void main(String[] args) {
        int port = 5100;
        int maxAttempts = 5;
        int secretNumber = new Random().nextInt(100) + 1; // Número entre 1 y 100
        String aesKeyStr = "1234567890123456"; // Clave AES de 16 bytes
        SecretKeySpec aesKey = new SecretKeySpec(aesKeyStr.getBytes(), "AES");

        System.out.println("Servidor de adivinanza iniciado en el puerto " + port);
        System.out.println("Número secreto (depuración): " + secretNumber);

        try (ServerSocket serverSocket = new ServerSocket(port);
             Socket clientSocket = serverSocket.accept();
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            // Se permiten 5 intentos
            for (int i = 0; i < maxAttempts; i++) {
                // Recibe conjetura cifrada (Base64)
                String encryptedGuess = in.readLine();
                if (encryptedGuess == null) break;

                // Descifrado de la conjetura
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.DECRYPT_MODE, aesKey);
                byte[] guessBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedGuess));
                int guess = Integer.parseInt(new String(guessBytes));
                System.out.println("Intento " + (i+1) + ": " + guess);

                String hint;
                if (guess == secretNumber) {
                    hint = "Correcto";
                    cipher.init(Cipher.ENCRYPT_MODE, aesKey);
                    byte[] respBytes = cipher.doFinal(hint.getBytes());
                    out.println(Base64.getEncoder().encodeToString(respBytes));
                    break;
                } else if (guess < secretNumber) {
                    hint = "Mayor";
                } else {
                    hint = "Menor";
                }
                // Envío de la pista cifrada
                cipher.init(Cipher.ENCRYPT_MODE, aesKey);
                byte[] respBytes = cipher.doFinal(hint.getBytes());
                out.println(Base64.getEncoder().encodeToString(respBytes));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

