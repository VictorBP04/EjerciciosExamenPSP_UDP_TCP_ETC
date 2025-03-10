package Upd;

import javax.crypto.Cipher;
import java.net.*;
import java.security.*;
import java.util.Base64;

public class UdpSumServer {
    public static void main(String[] args) {
        int port = 5300;
        try {
            // Generar par RSA
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();

            System.out.println("Clave pública (UDP): " + Base64.getEncoder().encodeToString(publicKey.getEncoded()));

            DatagramSocket socket = new DatagramSocket(port);
            int sum = 0;
            InetAddress clientAddress = null;
            int clientPort = -1;
            // Recibir 3 números
            for (int i = 0; i < 3; i++) {
                byte[] buffer = new byte[512];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                clientAddress = packet.getAddress();
                clientPort = packet.getPort();
                String encryptedNumber = new String(packet.getData(), 0, packet.getLength());
                Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                cipher.init(Cipher.DECRYPT_MODE, privateKey);
                byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedNumber));
                int num = Integer.parseInt(new String(decrypted));
                System.out.println("Número recibido: " + num);
                sum += num;
            }
            // Enviar suma cifrada usando la clave privada (para que el cliente descifre con la pública)
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPrivate());
            byte[] sumBytes = cipher.doFinal(Integer.toString(sum).getBytes());
            String encryptedSum = Base64.getEncoder().encodeToString(sumBytes);
            DatagramPacket responsePacket = new DatagramPacket(encryptedSum.getBytes(), encryptedSum.length(), clientAddress, clientPort);
            socket.send(responsePacket);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

