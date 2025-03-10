package mIXTO;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.*;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.ArrayList;
import java.util.List;

public class ChatSecure_Server {
    private static List<Socket> clientes = new ArrayList<>();
    private static final byte[] claveBytes = "chatAESclave1234".substring(0,16).getBytes();
    private static final SecretKeySpec claveAES = new SecretKeySpec(claveBytes, "AES");

    public static void main(String[] args) {
        final int PUERTO = 5905;
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            System.out.println("Servidor de chat seguro iniciado en el puerto " + PUERTO);
            while (clientes.size() < 3) { // Limitar a 3 clientes para este ejemplo
                Socket cliente = serverSocket.accept();
                clientes.add(cliente);
                new Thread(() -> manejarCliente(cliente)).start();
                System.out.println("Nuevo cliente conectado. Total: " + clientes.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void manejarCliente(Socket cliente) {
        try (DataInputStream dis = new DataInputStream(cliente.getInputStream());
             DataOutputStream dos = new DataOutputStream(cliente.getOutputStream())) {
            // Para este ejemplo se omite la autenticación RSA, se asume que el cliente está autenticado.
            while (true) {
                int tam = dis.readInt();
                byte[] msgCifrado = new byte[tam];
                dis.readFully(msgCifrado);
                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, claveAES);
                String mensaje = new String(cipher.doFinal(msgCifrado), "UTF-8");
                System.out.println("Mensaje recibido: " + mensaje);
                // Reenviar mensaje a todos los clientes
                reenviarMensaje(mensaje, cliente);
            }
        } catch (Exception e) {
            System.out.println("Cliente desconectado.");
        }
    }

    private static void reenviarMensaje(String mensaje, Socket remitente) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, claveAES);
            byte[] msgCifrado = cipher.doFinal(mensaje.getBytes("UTF-8"));
            for (Socket c : clientes) {
                if (c != remitente && !c.isClosed()) {
                    DataOutputStream dos = new DataOutputStream(c.getOutputStream());
                    dos.writeInt(msgCifrado.length);
                    dos.write(msgCifrado);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

