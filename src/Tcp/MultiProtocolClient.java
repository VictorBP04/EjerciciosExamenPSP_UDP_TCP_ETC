package Tcp;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.*;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

public class MultiProtocolClient {
    public static void main(String[] args) {
        // Enviar mensaje por TCP (AES)
        sendTCPMessage();
        // Enviar mensaje por UDP (RSA)
        sendUDPMessage();
    }

    public static void sendTCPMessage() {
        String host = "localhost";
        int tcpPort = 5010;
        String claveAES = "1234567890123456";
        SecretKeySpec aesKey = new SecretKeySpec(claveAES.getBytes(), "AES");
        String mensaje = "Mensaje TCP seguro con AES";
        try (Socket socket = new Socket(host, tcpPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] mensajeCifrado = cipher.doFinal(mensaje.getBytes());
            String mensajeCifradoBase64 = Base64.getEncoder().encodeToString(mensajeCifrado);
            out.println(mensajeCifradoBase64);
            System.out.println("TCP - Mensaje cifrado enviado: " + mensajeCifradoBase64);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendUDPMessage() {
        // Para UDP, se requiere la clave pública del servidor (mostrarla en el servidor)
        // Para la demo, se asume que se conoce la clave pública.
        String serverPublicKeyBase64 = "REEMPLAZAR_CON_LA_CLAVE_PUBLICA_DEL_SERVIDOR_UDP";
        try {
            byte[] keyBytes = Base64.getDecoder().decode(serverPublicKeyBase64);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey serverPublicKey = keyFactory.generatePublic(spec);

            String mensaje = "Mensaje UDP seguro con RSA";
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, serverPublicKey);
            byte[] mensajeCifrado = cipher.doFinal(mensaje.getBytes());
            String mensajeCifradoBase64 = Base64.getEncoder().encodeToString(mensajeCifrado);

            byte[] buffer = mensajeCifradoBase64.getBytes();
            InetAddress address = InetAddress.getByName("localhost");
            int udpPort = 6010;
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, udpPort);
            socket.send(packet);
            System.out.println("UDP - Mensaje cifrado enviado: " + mensajeCifradoBase64);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

