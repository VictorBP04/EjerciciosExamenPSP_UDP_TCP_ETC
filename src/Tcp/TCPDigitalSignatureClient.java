package Tcp;

import java.io.*;
import java.net.*;
import java.security.*;
import java.util.Base64;

public class TCPDigitalSignatureClient {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 5009;
        try {
            // Para la demo, generamos un par de claves para el servidor y usamos la privada para firmar
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(2048);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();
            // En la práctica, el servidor y el cliente intercambiarían claves de forma segura

            String mensaje = "Mensaje firmado digitalmente";
            // Firmar el mensaje con la clave privada
            Signature firma = Signature.getInstance("SHA256withRSA");
            firma.initSign(privateKey);
            firma.update(mensaje.getBytes());
            byte[] firmaDigital = firma.sign();
            String firmaDigitalBase64 = Base64.getEncoder().encodeToString(firmaDigital);

            try (Socket socket = new Socket(host, port);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                // Enviar mensaje y firma
                out.println(mensaje);
                out.println(firmaDigitalBase64);
                System.out.println("Mensaje y firma enviados.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
