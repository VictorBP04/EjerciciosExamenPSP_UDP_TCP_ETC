package Tcp;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.*;
import java.security.*;
import java.util.Base64;

public class TCPHybridServer {
    public static void main(String[] args) {
        int port = 5007;
        try {
            // Generar par de claves RSA para el servidor
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(2048);
            KeyPair serverKeyPair = keyPairGen.generateKeyPair();
            PrivateKey serverPrivateKey = serverKeyPair.getPrivate();
            PublicKey serverPublicKey = serverKeyPair.getPublic();

            // Mostrar la clave pública para que el cliente la use
            String publicKeyBase64 = Base64.getEncoder().encodeToString(serverPublicKey.getEncoded());
            System.out.println("Clave pública del servidor: " + publicKeyBase64);

            try (ServerSocket serverSocket = new ServerSocket(port);
                 Socket clientSocket = serverSocket.accept();
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                // Recibe el AES key cifrado con RSA del cliente
                String aesKeyCifradaBase64 = in.readLine();
                Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                cipher.init(Cipher.DECRYPT_MODE, serverPrivateKey);
                byte[] aesKeyBytes = cipher.doFinal(Base64.getDecoder().decode(aesKeyCifradaBase64));
                SecretKeySpec aesKey = new SecretKeySpec(aesKeyBytes, "AES");
                System.out.println("Clave AES recibida y descifrada.");

                // Envío de mensaje cifrado con AES para confirmar el intercambio
                String mensaje = "Intercambio de clave exitoso";
                cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.ENCRYPT_MODE, aesKey);
                byte[] mensajeCifrado = cipher.doFinal(mensaje.getBytes());
                String mensajeCifradoBase64 = Base64.getEncoder().encodeToString(mensajeCifrado);
                out.println(mensajeCifradoBase64);
                System.out.println("Mensaje cifrado enviado al cliente: " + mensajeCifradoBase64);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

