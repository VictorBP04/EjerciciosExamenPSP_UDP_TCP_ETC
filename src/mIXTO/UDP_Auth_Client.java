package mIXTO;

import javax.crypto.Cipher;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;

public class UDP_Auth_Client {
    public static void main(String[] args) {
        final int PUERTO = 5580;
        final String HOST = "localhost";
        try (DatagramSocket socket = new DatagramSocket()) {
            // Generar par RSA para el cliente
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPairCliente = keyGen.generateKeyPair();

            // Simular que conocemos la clave pública del servidor (se genera para el ejemplo)
            KeyPairGenerator keyGenServer = KeyPairGenerator.getInstance("RSA");
            keyGenServer.initialize(2048);
            KeyPair keyPairServer = keyGenServer.generateKeyPair();

            // Recibir el reto cifrado del servidor
            byte[] buffer = new byte[256];
            DatagramPacket paqueteRecibido = new DatagramPacket(buffer, buffer.length);
            socket.receive(paqueteRecibido);
            int tam = paqueteRecibido.getLength();
            byte[] retoCifrado = new byte[tam];
            System.arraycopy(buffer, 0, retoCifrado, 0, tam);

            // Descifrar el reto con la clave privada del cliente
            Cipher cipherRSA = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipherRSA.init(Cipher.DECRYPT_MODE, keyPairCliente.getPrivate());
            byte[] reto = cipherRSA.doFinal(retoCifrado);
            System.out.println("Cliente: Reto recibido y descifrado.");

            // Firmar el reto con la clave privada del cliente
            Signature firma = Signature.getInstance("SHA256withRSA");
            firma.initSign(keyPairCliente.getPrivate());
            firma.update(reto);
            byte[] firmaDigital = firma.sign();

            // Cifrar la firma con la clave pública del servidor (simulado)
            cipherRSA.init(Cipher.ENCRYPT_MODE, keyPairServer.getPublic());
            byte[] respuestaCifrada = cipherRSA.doFinal(firmaDigital);

            // Enviar la respuesta al servidor
            DatagramPacket paqueteEnvio = new DatagramPacket(respuestaCifrada, respuestaCifrada.length,
                    java.net.InetAddress.getByName(HOST), PUERTO);
            socket.send(paqueteEnvio);
            System.out.println("Cliente: Respuesta cifrada enviada al servidor.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
