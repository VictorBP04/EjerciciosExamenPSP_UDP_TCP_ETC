package mIXTO;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class RemoteCmd_Secure_Client {
    public static void main(String[] args) {
        final String HOST = "localhost";
        final int PUERTO = 5940;
        // Clave AES fija
        final byte[] claveBytes = "remotecmdAESclave".substring(0,16).getBytes();
        SecretKeySpec claveAES = new SecretKeySpec(claveBytes, "AES");

        try (Socket socket = new Socket(HOST, PUERTO);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             DataInputStream dis = new DataInputStream(socket.getInputStream())) {

            String comando = "date"; // Por ejemplo, listar la fecha del sistema (ajusta según OS)
            // Cifrar el comando con AES
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, claveAES);
            byte[] comandoCifrado = cipher.doFinal(comando.getBytes("UTF-8"));
            dos.writeInt(comandoCifrado.length);
            dos.write(comandoCifrado);
            System.out.println("Comando enviado.");

            // Recibir salida cifrada del servidor
            int tamSalida = dis.readInt();
            byte[] salidaCifrada = new byte[tamSalida];
            dis.readFully(salidaCifrada);
            cipher.init(Cipher.DECRYPT_MODE, claveAES);
            String salida = new String(cipher.doFinal(salidaCifrada), "UTF-8");
            System.out.println("Salida del comando:\n" + salida);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

