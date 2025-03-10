package mIXTO;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.Arrays;

public class UDP_Auth_Server {
    public static void main(String[] args) {
        final int PUERTO = 5580;
        try (DatagramSocket socket = new DatagramSocket(PUERTO)) {
            // Generar par RSA para el servidor
            KeyPairGenerator keyGenServer = KeyPairGenerator.getInstance("RSA");
            keyGenServer.initialize(2048);
            KeyPair keyPairServer = keyGenServer.generateKeyPair();

            // Simular que conocemos la clave pública del cliente (para el ejemplo, la generamos aquí)
            KeyPairGenerator keyGenClient = KeyPairGenerator.getInstance("RSA");
            keyGenClient.initialize(2048);
            KeyPair keyPairClient = keyGenClient.generateKeyPair();

            // Generar un reto (nonce)
            byte[] reto = new byte[16];
            new SecureRandom().nextBytes(reto);
            System.out.println("Servidor: Reto generado: " + Arrays.toString(reto));

            // Cifrar el reto con la clave pública del cliente
            Cipher cipherRSA = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipherRSA.init(Cipher.ENCRYPT_MODE, keyPairClient.getPublic());
            byte[] retoCifrado = cipherRSA.doFinal(reto);

            // Enviar reto cifrado al cliente (usando el puerto y dirección del paquete recibido previamente)
            // Para simplificar, enviamos a localhost
            DatagramPacket paqueteEnvio = new DatagramPacket(retoCifrado, retoCifrado.length,
                    java.net.InetAddress.getByName("localhost"), PUERTO);
            socket.send(paqueteEnvio);
            System.out.println("Servidor: Reto cifrado enviado al cliente.");

            // Recibir la respuesta del cliente (se espera que el cliente firme el reto)
            byte[] buffer = new byte[256];
            DatagramPacket paqueteRespuesta = new DatagramPacket(buffer, buffer.length);
            socket.receive(paqueteRespuesta);
            int tam = paqueteRespuesta.getLength();
            byte[] respuestaCifrada = new byte[tam];
            System.arraycopy(buffer, 0, respuestaCifrada, 0, tam);

            // Descifrar la respuesta con la clave privada del servidor
            cipherRSA.init(Cipher.DECRYPT_MODE, keyPairServer.getPrivate());
            byte[] respuesta = cipherRSA.doFinal(respuestaCifrada);

            // Verificar que la respuesta sea una firma válida del reto usando la clave pública del cliente
            Signature verificador = Signature.getInstance("SHA256withRSA");
            verificador.initVerify(keyPairClient.getPublic());
            verificador.update(reto);
            boolean autenticado = verificador.verify(respuesta);
            System.out.println("Servidor: Autenticación " + (autenticado ? "exitosa" : "fallida"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
