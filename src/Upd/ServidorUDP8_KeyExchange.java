package Upd;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

public class ServidorUDP8_KeyExchange {
    public static void main(String[] args) {
        final int PUERTO = 5710;
        try (DatagramSocket socket = new DatagramSocket(PUERTO)) {
            // Generar par de claves RSA
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();

            // Enviar la clave pública en Base64 al cliente
            String publicKeyBase64 = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            byte[] publicKeyBytes = publicKeyBase64.getBytes("UTF-8");
            InetAddress direccion = InetAddress.getByName("localhost");
            DatagramPacket paqueteEnvio = new DatagramPacket(publicKeyBytes, publicKeyBytes.length, direccion, PUERTO);
            socket.send(paqueteEnvio);
            System.out.println("ServidorUDP8_KeyExchange: Clave pública enviada: " + publicKeyBase64);



            // Recibir la clave AES cifrada del cliente
            byte[] buffer = new byte[256];
            DatagramPacket paqueteRecep = new DatagramPacket(buffer, buffer.length);
            socket.receive(paqueteRecep);
            int tam = paqueteRecep.getLength();
            byte[] aesCifrado = new byte[tam];
            System.arraycopy(buffer, 0, aesCifrado, 0, tam);

            // Descifrar la clave AES usando la clave privada RSA
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            byte[] aesBytes = cipher.doFinal(aesCifrado);
            SecretKeySpec claveAES = new SecretKeySpec(aesBytes, "AES");
            System.out.println("ServidorUDP8_KeyExchange: Clave AES recibida y descifrada.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

