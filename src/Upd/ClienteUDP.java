package Upd;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ClienteUDP {
    public static void main(String[] args) {
        final int PUERTO = 9876;
        try (DatagramSocket socket = new DatagramSocket()) {
            String mensaje = "Hola, este es un mensaje UDP.";
            byte[] buffer = mensaje.getBytes("UTF-8");
            // Dirección local (localhost)
            InetAddress direccion = InetAddress.getByName("localhost");
            DatagramPacket paquete = new DatagramPacket(buffer, buffer.length, direccion, PUERTO);
            socket.send(paquete);
            System.out.println("Mensaje enviado: " + mensaje);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

