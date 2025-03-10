package RSA;

import javax.crypto.Cipher;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

public class RSA_Challenge_Client {
    public static void main(String[] args) {
        final String HOST = "localhost";
        final int PUERTO = 6000;
        try (Socket socket = new Socket(HOST, PUERTO);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             DataInputStream dis = new DataInputStream(socket.getInputStream())) {

            System.out.println("Conectado al servidor.");

            // 1. Generar un par de claves RSA para el cliente
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();

            // 2. Enviar la clave pública del cliente en Base64 al servidor
            String clavePublicaB64 = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            dos.writeUTF(clavePublicaB64);
            System.out.println("Clave pública enviada al servidor.");

            // 3. Recibir el número cifrado del servidor
            int tam = dis.readInt();
            byte[] numeroCifrado = new byte[tam];
            dis.readFully(numeroCifrado);

            // 4. Descifrar el número utilizando la clave privada del cliente
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            byte[] numeroDescifradoBytes = cipher.doFinal(numeroCifrado);
            String numeroDescifrado = new String(numeroDescifradoBytes, "UTF-8");
            System.out.println("Número descifrado: " + numeroDescifrado);

            dos.close();
            dis.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

