package RSA;

import javax.crypto.Cipher;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSA_Challenge_Server {
    public static void main(String[] args) {
        final int PUERTO = 6000; // Puerto de escucha del servidor
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            System.out.println("Servidor iniciado en el puerto " + PUERTO);

            // Aceptamos una única conexión para este ejemplo
            Socket socket = serverSocket.accept();
            System.out.println("Cliente conectado.");

            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            // 1. Recibir la clave pública del cliente (en Base64)
            String clavePublicaB64 = dis.readUTF();
            byte[] clavePublicaBytes = Base64.getDecoder().decode(clavePublicaB64);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(clavePublicaBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey clavePublicaCliente = keyFactory.generatePublic(spec);
            System.out.println("Clave pública del cliente recibida.");

            // 2. Generar un número aleatorio (por ejemplo, un entero entre 0 y 1000)
            SecureRandom random = new SecureRandom();
            int numero = random.nextInt(1000);
            System.out.println("Número generado: " + numero);

            // 3. Convertir el número a bytes (se usa la representación en String)
            byte[] numeroBytes = String.valueOf(numero).getBytes("UTF-8");

            // 4. Encriptar el número con RSA usando la clave pública del cliente
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, clavePublicaCliente);
            byte[] numeroCifrado = cipher.doFinal(numeroBytes);

            // 5. Enviar el número cifrado (se envía primero la longitud y luego los bytes)
            dos.writeInt(numeroCifrado.length);
            dos.write(numeroCifrado);
            System.out.println("Número cifrado enviado al cliente.");

            dis.close();
            dos.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

