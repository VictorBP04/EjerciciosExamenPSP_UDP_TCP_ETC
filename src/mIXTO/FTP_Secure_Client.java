package mIXTO;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class FTP_Secure_Client {
    public static void main(String[] args) {
        final String HOST = "localhost";
        final int PUERTO = 5800;
        final byte[] claveBytes = "ftpAESclave12345".substring(0,16).getBytes();
        SecretKeySpec claveAES = new SecretKeySpec(claveBytes, "AES");

        try (Socket socket = new Socket(HOST, PUERTO);
             DataInputStream dis = new DataInputStream(socket.getInputStream());
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             Scanner sc = new Scanner(System.in)) {

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

            System.out.println("Conectado al servidor FTP seguro.");
            while (true) {
                System.out.print("FTP> ");
                String comando = sc.nextLine();
                cipher.init(Cipher.ENCRYPT_MODE, claveAES);
                byte[] comandoCifrado = cipher.doFinal(comando.getBytes("UTF-8"));
                dos.writeInt(comandoCifrado.length);
                dos.write(comandoCifrado);

                if (comando.equalsIgnoreCase("QUIT"))
                    break;

                int tamResp = dis.readInt();
                byte[] respCifrada = new byte[tamResp];
                dis.readFully(respCifrada);
                cipher.init(Cipher.DECRYPT_MODE, claveAES);
                String respuesta = new String(cipher.doFinal(respCifrada), "UTF-8");
                System.out.println("Respuesta: " + respuesta);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

