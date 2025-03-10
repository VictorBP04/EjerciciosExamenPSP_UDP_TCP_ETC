package Tcp;

import javax.crypto.Cipher;
import java.io.*;
import java.net.*;
import java.security.*;
import java.util.Base64;

public class TCPRSAServer {
    public static void main(String[] args) {
        int port = 5005;
        try {
            // Generar par de claves RSA para el servidor
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(2048);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();

            // Mostrar clave pública en Base64 (para uso del cliente)
            String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            System.out.println("Clave pública del servidor (compartir con el cliente): " + publicKeyBase64);

            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("Servidor TCP RSA en puerto " + port);
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                    // Recibe mensaje cifrado en Base64
                    String mensajeCifradoBase64 = in.readLine();
                    // Descifrado RSA con clave privada
                    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                    cipher.init(Cipher.DECRYPT_MODE, privateKey);
                    byte[] mensajeCifrado = Base64.getDecoder().decode(mensajeCifradoBase64);
                    byte[] mensajeDescifrado = cipher.doFinal(mensajeCifrado);
                    System.out.println("Mensaje descifrado: " + new String(mensajeDescifrado));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

