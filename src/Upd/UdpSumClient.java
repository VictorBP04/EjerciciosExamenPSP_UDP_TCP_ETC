package Upd;

import javax.crypto.Cipher;
import java.net.*;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

public class UdpSumClient {
    public static void main(String[] args) {
        // La clave pública del servidor se debe obtener de forma segura.
        // Para este demo, se debe reemplazar el siguiente string por la clave pública mostrada por el servidor.
        String serverPublicKeyBase64 = "REEMPLAZAR_CON_LA_CLAVE_PUBLICA_DEL_SERVIDOR";
        try {
            byte[] keyBytes = Base64.getDecoder().decode(serverPublicKeyBase64);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey serverPublicKey = keyFactory.generatePublic(spec);

            DatagramSocket socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName("localhost");
            int[] numbers = {10, 20, 30};
            for (int num : numbers) {
                Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                cipher.init(Cipher.ENCRYPT_MODE, serverPublicKey);
                byte[] encryptedNum = cipher.doFinal(Integer.toString(num).getBytes());
                String encryptedNumBase64 = Base64.getEncoder().encodeToString(encryptedNum);
                DatagramPacket packet = new DatagramPacket(encryptedNumBase64.getBytes(), encryptedNumBase64.length(), address, 5300);
                socket.send(packet);
                System.out.println("Número enviado: " + num);
            }
            // Recibir suma cifrada
            byte[] buffer = new byte[512];
            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
            socket.receive(response);
            String encryptedSum = new String(response.getData(), 0, response.getLength());
            // Descifrar con la clave pública (ya que el servidor cifró con su privada)
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, serverPublicKey);
            byte[] sumBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedSum));
            System.out.println("Suma recibida: " + new String(sumBytes));
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
