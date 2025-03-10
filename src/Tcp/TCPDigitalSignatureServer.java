package Tcp;

import java.io.*;
import java.net.*;
import java.security.*;
import java.util.Base64;

public class TCPDigitalSignatureServer {
    public static void main(String[] args) {
        int port = 5009;
        try {
            // Generar par de claves RSA para el servidor
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(2048);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            // Mostrar la clave pública para que el cliente la use
            String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            System.out.println("Clave pública del servidor para verificar firma: " + publicKeyBase64);

            try (ServerSocket serverSocket = new ServerSocket(port);
                 Socket clientSocket = serverSocket.accept();
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                // Recibe mensaje y firma digital del cliente separados por una línea
                String mensaje = in.readLine();
                String firmaDigitalBase64 = in.readLine();

                // Verificar la firma
                Signature verifier = Signature.getInstance("SHA256withRSA");
                verifier.initVerify(publicKey);
                verifier.update(mensaje.getBytes());
                boolean verificado = verifier.verify(Base64.getDecoder().decode(firmaDigitalBase64));
                if (verificado) {
                    System.out.println("Firma digital verificada. Mensaje: " + mensaje);
                } else {
                    System.out.println("Firma digital no verificada.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
