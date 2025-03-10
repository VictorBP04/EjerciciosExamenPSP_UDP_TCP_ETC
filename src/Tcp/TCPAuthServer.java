package Tcp;

import javax.crypto.Cipher;
import java.io.*;
import java.net.*;
import java.security.*;
import java.security.spec.*;
import java.util.Arrays;
import java.util.Base64;

public class TCPAuthServer {
    // Clave pública en formato X.509 (512 bits)
    public static final String RSA_PUBLIC_KEY_BASE64 =
            "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBALjn34Q79O5PK/yfIRs6OY5EFTglY8TnN75yZzH7dp9Zd/cNVPLh8mb/bR+a1vWvKBfA1lyuk0kZG8Cy7hwck88CAwEAAQ==";
    // Clave privada en formato PKCS#8 (512 bits)
    public static final String RSA_PRIVATE_KEY_BASE64 =
            "MIIBOgIBAAJBALjn34Q79O5PK/yfIRs6OY5EFTglY8TnN75yZzH7dp9Zd/cNVPLh8mb/bR+a1vWvKBfA1lyuk0kZG8Cy7hwck88CAwEAAQJBAIUrugKWeEOG8dOcwqjrQx2fHBM3VnYY+j+Z3t2ap+qsg4D+I6zJc8iABq89UyPjU3GLRoX8J6nUjzTZqeeQ6eECIQD1e6hHq9wOqF7VfR9gI6kNjnT2VfsFQyGwCl2UKTItJQIhAN03dM6q7Pq7+fn99i3+u6r7Rcl4+1+6j0iWKKI3fjInAiBTT1DEvIYtOPqXwkDq/C7Zr9tKo33Rq3H7A0aE7XjFNwIgCcP9X2G/JH48p8X8cDXCugx5PNTn3zStzJ1xP+8ZSGECIQCZA5h3sRk8NKJozgBwL7b0bX88W6sC0rknzRHXrxfvxA==";

    public static void main(String[] args) {
        int port = 5006;
        try {
            // Convertir la clave privada de Base64 a PrivateKey
            byte[] privateKeyBytes = Base64.getDecoder().decode(RSA_PRIVATE_KEY_BASE64);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey serverPrivateKey = kf.generatePrivate(keySpec);

            // Convertir la clave pública compartida de Base64 a PublicKey
            byte[] publicKeyBytes = Base64.getDecoder().decode(RSA_PUBLIC_KEY_BASE64);
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            PublicKey sharedPublicKey = kf.generatePublic(pubKeySpec);

            // Generar un reto (nonce) de 16 bytes
            byte[] challenge = new byte[16];
            SecureRandom random = new SecureRandom();
            random.nextBytes(challenge);

            // Cifrar el reto con la clave pública compartida
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, sharedPublicKey);
            byte[] encryptedChallenge = cipher.doFinal(challenge);
            String encryptedChallengeBase64 = Base64.getEncoder().encodeToString(encryptedChallenge);

            try (ServerSocket serverSocket = new ServerSocket(port);
                 Socket clientSocket = serverSocket.accept();
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                // Enviar el reto cifrado al cliente
                out.println(encryptedChallengeBase64);
                System.out.println("Enviado reto cifrado al cliente.");

                // Recibir la respuesta cifrada del cliente
                String encryptedResponseBase64 = in.readLine();
                cipher.init(Cipher.DECRYPT_MODE, serverPrivateKey);
                byte[] decryptedResponse = cipher.doFinal(Base64.getDecoder().decode(encryptedResponseBase64));

                // Verificar si la respuesta coincide con el reto original
                if (Arrays.equals(challenge, decryptedResponse)) {
                    System.out.println("Cliente autenticado con éxito.");
                } else {
                    System.out.println("Autenticación fallida.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
