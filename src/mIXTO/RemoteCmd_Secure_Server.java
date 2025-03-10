package mIXTO;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.util.Base64;

public class RemoteCmd_Secure_Server {
    public static void main(String[] args) {
        final int PUERTO = 5940;
        // Clave AES fija
        final byte[] claveBytes = "remotecmdAESclave".substring(0,16).getBytes();
        SecretKeySpec claveAES = new SecretKeySpec(claveBytes, "AES");

        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            System.out.println("Servidor de comandos remoto seguro iniciado.");
            Socket socket = serverSocket.accept();
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            // Recibir comando cifrado con AES
            int tam = dis.readInt();
            byte[] comandoCifrado = new byte[tam];
            dis.readFully(comandoCifrado);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, claveAES);
            String comando = new String(cipher.doFinal(comandoCifrado), "UTF-8");
            System.out.println("Comando recibido: " + comando);

            // Autenticación: se recibe una firma digital del comando (en este ejemplo se omite la verificación real)
            // Ejecutar el comando en el sistema
            Process process = Runtime.getRuntime().exec(comando);
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder salida = new StringBuilder();
            String linea;
            while ((linea = br.readLine()) != null) {
                salida.append(linea).append("\n");
            }
            // Cifrar la salida con AES y enviarla
            cipher.init(Cipher.ENCRYPT_MODE, claveAES);
            byte[] salidaCifrada = cipher.doFinal(salida.toString().getBytes("UTF-8"));
            dos.writeInt(salidaCifrada.length);
            dos.write(salidaCifrada);
            System.out.println("Salida del comando enviada.");
            dis.close();
            dos.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
