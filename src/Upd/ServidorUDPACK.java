package Upd;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ServidorUDPACK {
    public static void main(String[] args) {
        final int PUERTO = 5556;
        final int NUM_PAQUETES = 5;
        try (DatagramSocket socket = new DatagramSocket(PUERTO)) {
            for (int i = 0; i < NUM_PAQUETES; i++) {
                byte[] buffer = new byte[512];
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                socket.receive(paquete);
                System.out.println("Paquete " + (i+1) + " recibido.");

                // Enviar ACK (mensaje fijo "ACK")
                byte[] ack = "ACK".getBytes("UTF-8");
                InetAddress address = paquete.getAddress();
                int port = paquete.getPort();
                DatagramPacket ackPacket = new DatagramPacket(ack, ack.length, address, port);
                socket.send(ackPacket);
                System.out.println("ACK enviado para el paquete " + (i+1));
            }
            System.out.println("Transferencia con ACK completada.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

