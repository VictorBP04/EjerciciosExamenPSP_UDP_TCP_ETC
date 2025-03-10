package Tcp;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.*;
import java.security.KeyPairGenerator;
import java.util.Base64;

public class MultiProtocolServer {
    public static void main(String[] args) {
        // Ejecutar la parte TCP
        new Thread(() -> runTCPServer()).start();
        // Ejecutar la parte UDP
        new Thread(() -> runUDPServer()).start();
    }

    // Servidor TCP que utiliza AES para cifrar/desencriptar mensajes
    public static void runTCPServer() {
        int tcpPort = 5010;
        String claveAES = "1234567890123456";
        SecretKeySpec aesKey = new SecretKeySpec(claveAES.getBytes(), "AES");
        try (ServerSocket serverSocket = new ServerSocket(tcpPort)) {
            System.out.println("Servidor TCP multi-protocolo en puerto " + tcpPort);
            try (Socket clientSocket = serverSocket.accept();
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                String mensajeCifradoBase64 = in.readLine();
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.DECRYPT_MODE, aesKey);
                byte[] mensajeCifrado = Base64.getDecoder().decode(mensajeCifradoBase64);
                byte[] mensajeDescifrado = cipher.doFinal(mensajeCifrado);
                System.out.println("TCP - Mensaje descifrado: " + new String(mensajeDescifrado));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Servidor UDP que utiliza RSA para cifrar/desencriptar mensajes
    public static void runUDPServer() {
        int udpPort = 6010;
        try {
            // Generar par de claves RSA para UDP
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(2048);
            var keyPair = keyPairGen.generateKeyPair();
            var privateKey = keyPair.getPrivate();
            var publicKey = keyPair.getPublic();

            // Mostrar la clave pública para el cliente
            String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            System.out.println("UDP - Clave pública del servidor: " + publicKeyBase64);

            DatagramSocket socket = new DatagramSocket(udpPort);
            byte[] buffer = new byte[2048];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            String mensajeCifradoBase64 = new String(packet.getData(), 0, packet.getLength());
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] mensajeCifrado = Base64.getDecoder().decode(mensajeCifradoBase64);
            byte[] mensajeDescifrado = cipher.doFinal(mensajeCifrado);
            System.out.println("UDP - Mensaje descifrado: " + new String(mensajeDescifrado));
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

