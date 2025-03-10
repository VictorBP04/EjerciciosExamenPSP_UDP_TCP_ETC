package mIXTO;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class TCP_SecureComm_Client {
    public static void main(String[] args) {
        final String HOST = "localhost";
        final int PUERTO = 5570;
        try (Socket socket = new Socket(HOST, PUERTO);
             DataInputStream dis = new DataInputStream(socket.getInputStream());
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {

            // Recibir clave pública RSA del servidor
            String clavePublicaStr = dis.readUTF();
            byte[] clavePubBytes = Base64.getDecoder().decode(clavePublicaStr);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(clavePubBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            var clavePublica = keyFactory.generatePublic(spec);
            System.out.println("Clave pública RSA del servidor recibida.");

            // Generar clave AES aleatoria de 128 bits
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            SecretKey claveAES = keyGen.generateKey();

            // Cifrar la clave AES con la clave pública RSA del servidor
            Cipher cipherRSA = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipherRSA.init(Cipher.ENCRYPT_MODE, clavePublica);
            byte[] aesCifrado = cipherRSA.doFinal(claveAES.getEncoded());
            dos.writeInt(aesCifrado.length);
            dos.write(aesCifrado);
            System.out.println("Clave AES cifrada enviada al servidor.");

            // Preparar un mensaje y cifrarlo con AES
            String mensaje = "Comunicación segura vía TCP utilizando RSA para intercambio de claves y AES para cifrado.";
            Cipher cipherAES = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipherAES.init(Cipher.ENCRYPT_MODE, claveAES);
            byte[] mensajeCifrado = cipherAES.doFinal(mensaje.getBytes("UTF-8"));
            dos.writeInt(mensajeCifrado.length);
            dos.write(mensajeCifrado);
            System.out.println("Mensaje cifrado enviado al servidor.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
