package mIXTO;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

public class TCP_SecureComm_Server {
    public static void main(String[] args) {
        final int PUERTO = 5570;
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            System.out.println("Servidor TCP esperando conexión...");
            Socket socket = serverSocket.accept();
            System.out.println("Cliente conectado.");

            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            // Generar par RSA para el servidor
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();

            // Enviar clave pública RSA en Base64 al cliente
            String clavePublica = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            dos.writeUTF(clavePublica);
            System.out.println("Clave pública RSA enviada al cliente.");

            // Recibir clave AES cifrada desde el cliente
            int tamAES = dis.readInt();
            byte[] aesCifrado = new byte[tamAES];
            dis.readFully(aesCifrado);

            // Descifrar la clave AES usando la clave privada RSA
            Cipher cipherRSA = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipherRSA.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            byte[] claveAESBytes = cipherRSA.doFinal(aesCifrado);
            SecretKeySpec claveAES = new SecretKeySpec(claveAESBytes, "AES");
            System.out.println("Clave AES establecida.");

            // Recibir mensaje cifrado con AES del cliente
            int tamMsg = dis.readInt();
            byte[] mensajeCifrado = new byte[tamMsg];
            dis.readFully(mensajeCifrado);

            // Descifrar el mensaje con AES
            Cipher cipherAES = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipherAES.init(Cipher.DECRYPT_MODE, claveAES);
            byte[] mensajeDescifrado = cipherAES.doFinal(mensajeCifrado);
            System.out.println("Mensaje recibido: " + new String(mensajeDescifrado, "UTF-8"));

            dis.close();
            dos.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

