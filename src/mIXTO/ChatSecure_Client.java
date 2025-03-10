package mIXTO;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ChatSecure_Client {
    private static final byte[] claveBytes = "chatAESclave1234".substring(0,16).getBytes();
    private static final SecretKeySpec claveAES = new SecretKeySpec(claveBytes, "AES");

    public static void main(String[] args) {
        final String HOST = "localhost";
        final int PUERTO = 5905;
        try (Socket socket = new Socket(HOST, PUERTO);
             DataInputStream dis = new DataInputStream(socket.getInputStream());
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             Scanner sc = new Scanner(System.in)) {

            new Thread(() -> {
                try {
                    while (true) {
                        int tam = dis.readInt();
                        byte[] msgCifrado = new byte[tam];
                        dis.readFully(msgCifrado);
                        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                        cipher.init(Cipher.DECRYPT_MODE, claveAES);
                        String mensaje = new String(cipher.doFinal(msgCifrado), "UTF-8");
                        System.out.println("Mensaje recibido: " + mensaje);
                    }
                } catch (Exception e) {
                    System.out.println("Desconexión del servidor.");
                }
            }).start();

            System.out.println("Escribe tus mensajes:");
            while (true) {
                String mensaje = sc.nextLine();
                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, claveAES);
                byte[] msgCifrado = cipher.doFinal(mensaje.getBytes("UTF-8"));
                dos.writeInt(msgCifrado.length);
                dos.write(msgCifrado);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

