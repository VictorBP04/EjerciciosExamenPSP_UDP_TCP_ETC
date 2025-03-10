package Tcp;

import javax.crypto.Cipher;
import java.io.*;
import java.net.*;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

public class TCPRSAClient {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 5005;
        // Clave pública del servidor en Base64 (obtenida del servidor)
        String publicKeyBase64 = "REEMPLAZAR_CON_LA_CLAVE_PUBLICA_DEL_SERVIDOR";
        try {
            // Convertir la clave pública de Base64 a PublicKey
            byte[] keyBytes = Base64.getDecoder().decode(publicKeyBase64);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey serverPublicKey = keyFactory.generatePublic(spec);

            String mensajeOriginal = "Mensaje secreto con RSA sobre TCP";
            // Cifrado RSA con la clave pública del servidor
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, serverPublicKey);
            byte[] mensajeCifrado = cipher.doFinal(mensajeOriginal.getBytes());
            String mensajeCifradoBase64 = Base64.getEncoder().encodeToString(mensajeCifrado);

            try (Socket socket = new Socket(host, port);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                out.println(mensajeCifradoBase64);
                System.out.println("Mensaje cifrado enviado: " + mensajeCifradoBase64);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
