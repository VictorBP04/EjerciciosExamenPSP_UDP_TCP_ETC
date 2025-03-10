package Tcp;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.*;
import java.util.Base64;

public class TCPEncryptedObjectServer {
    public static void main(String[] args) {
        int port = 5008;
        // Clave AES fija
        String clave = "1234567890123456";
        SecretKeySpec aesKey = new SecretKeySpec(clave.getBytes(), "AES");

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor para objeto cifrado en puerto " + port);
            try (Socket clientSocket = serverSocket.accept();
                 ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream())) {
                // Recibe un objeto de tipo String que está cifrado (en Base64)
                String objetoCifradoBase64 = (String) ois.readObject();
                // Descifrar el objeto
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.DECRYPT_MODE, aesKey);
                byte[] objetoCifrado = Base64.getDecoder().decode(objetoCifradoBase64);
                byte[] objetoDescifrado = cipher.doFinal(objetoCifrado);
                String mensajeRecibido = new String(objetoDescifrado);
                System.out.println("Objeto recibido y descifrado: " + mensajeRecibido);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

