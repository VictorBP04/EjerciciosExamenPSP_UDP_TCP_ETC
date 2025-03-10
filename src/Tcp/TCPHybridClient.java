package Tcp;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.*;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

public class TCPHybridClient {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 5007;
        // La clave pública del servidor debe obtenerse de forma segura, aquí se asume que ya se conoce:
        String serverPublicKeyBase64 = "REEMPLAZAR_CON_LA_CLAVE_PUBLICA_DEL_SERVIDOR";
        try {
            // Convertir la clave pública
            byte[] keyBytes = Base64.getDecoder().decode(serverPublicKeyBase64);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey serverPublicKey = keyFactory.generatePublic(spec);

            // Generar clave AES
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            SecretKey aesKey = keyGen.generateKey();
            byte[] aesKeyBytes = aesKey.getEncoded();

            // Cifrar la clave AES con la clave pública del servidor
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, serverPublicKey);
            byte[] aesKeyCifrada = cipher.doFinal(aesKeyBytes);
            String aesKeyCifradaBase64 = Base64.getEncoder().encodeToString(aesKeyCifrada);

            try (Socket socket = new Socket(host, port);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                // Envía la clave AES cifrada al servidor
                out.println(aesKeyCifradaBase64);
                System.out.println("Clave AES cifrada enviada al servidor.");

                // Recibe mensaje cifrado del servidor
                String mensajeCifradoBase64 = in.readLine();
                cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(aesKey.getEncoded(), "AES"));
                byte[] mensajeCifrado = Base64.getDecoder().decode(mensajeCifradoBase64);
                byte[] mensajeDescifrado = cipher.doFinal(mensajeCifrado);
                System.out.println("Mensaje descifrado del servidor: " + new String(mensajeDescifrado));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
