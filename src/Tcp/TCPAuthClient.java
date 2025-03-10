package Tcp;

import javax.crypto.Cipher;
import java.io.*;
import java.net.*;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

public class TCPAuthClient {
    // Usar las mismas claves RSA predefinidas que en el servidor
    public static final String RSA_PUBLIC_KEY_BASE64 =
            "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBALjn34Q79O5PK/yfIRs6OY5EFTglY8TnN75yZzH7dp9Zd/cNVPLh8mb/bR+a1vWvKBfA1lyuk0kZG8Cy7hwck88CAwEAAQ==";
    public static final String RSA_PRIVATE_KEY_BASE64 =
            "MIIBOgIBAAJBALjn34Q79O5PK/yfIRs6OY5EFTglY8TnN75yZzH7dp9Zd/cNVPLh8mb/bR+a1vWvKBfA1lyuk0kZG8Cy7hwck88CAwEAAQJBAIUrugKWeEOG8dOcwqjrQx2fHBM3VnYY+j+Z3t2ap+qsg4D+I6zJc8iABq89UyPjU3GLRoX8J6nUjzTZqeeQ6eECIQD1e6hHq9wOqF7VfR9gI6kNjnT2VfsFQyGwCl2UKTItJQIhAN03dM6q7Pq7+fn99i3+u6r7Rcl4+1+6j0iWKKI3fjInAiBTT1DEvIYtOPqXwkDq/C7Zr9tKo33Rq3H7A0aE7XjFNwIgCcP9X2G/JH48p8X8cDXCugx5PNTn3zStzJ1xP+8ZSGECIQCZA5h3sRk8NKJozgBwL7b0bX88W6sC0rknzRHXrxfvxA==";

    public static void main(String[] args) {
        String host = "localhost";
        int port = 5006;
        try {
            // Convertir la clave privada (del cliente) de Base64 a PrivateKey
            byte[] privateKeyBytes = Base64.getDecoder().decode(RSA_PRIVATE_KEY_BASE64);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey clientPrivateKey = kf.generatePrivate(keySpec);

            // Convertir la clave pública compartida de Base64 a PublicKey
            byte[] publicKeyBytes = Base64.getDecoder().decode(RSA_PUBLIC_KEY_BASE64);
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            PublicKey sharedPublicKey = kf.generatePublic(pubKeySpec);

            try (Socket socket = new Socket(host, port);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                // Recibir el reto cifrado desde el servidor
                String encryptedChallengeBase64 = in.readLine();
                Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                // Descifrar el reto con la clave privada del cliente
                cipher.init(Cipher.DECRYPT_MODE, clientPrivateKey);
                byte[] challenge = cipher.doFinal(Base64.getDecoder().decode(encryptedChallengeBase64));

                // Para responder, cifrar el mismo reto con la clave pública compartida
                cipher.init(Cipher.ENCRYPT_MODE, sharedPublicKey);
                byte[] encryptedResponse = cipher.doFinal(challenge);
                String encryptedResponseBase64 = Base64.getEncoder().encodeToString(encryptedResponse);
                out.println(encryptedResponseBase64);
                System.out.println("Enviado respuesta cifrada para autenticación.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
