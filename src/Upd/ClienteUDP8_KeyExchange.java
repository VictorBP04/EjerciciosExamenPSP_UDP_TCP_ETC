package Upd;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class ClienteUDP8_KeyExchange {
    public static void main(String[] args) {
        final int PUERTO = 5710;
        final String HOST = "localhost";
        try (DatagramSocket socket = new DatagramSocket()) {
            // Recibir la clave pública RSA del servidor
            byte[] buffer = new byte[1024];
            DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
            socket.receive(paquete);
            String publicKeyBase64 = new String(paquete.getData(), 0, paquete.getLength(), "UTF-8");
            System.out.println("ClienteUDP8_KeyExchange: Clave pública recibida: " + publicKeyBase64);

            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
            java.security.KeyFactory keyFactory = java.security.KeyFactory.getInstance("RSA");
            var publicKey = keyFactory.generatePublic(spec);

            // Generar una clave AES aleatoria (128 bits)
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            SecretKey claveAES = keyGen.generateKey();

            // Cifrar la clave AES con la clave pública RSA del servidor
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] aesCifrado = cipher.doFinal(claveAES.getEncoded());

            // Enviar la clave AES cifrada al servidor
            InetAddress direccion = InetAddress.getByName(HOST);
            DatagramPacket paqueteEnvio = new DatagramPacket(aesCifrado, aesCifrado.length, direccion, PUERTO);
            socket.send(paqueteEnvio);
            System.out.println("ClienteUDP8_KeyExchange: Clave AES cifrada enviada al servidor.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

