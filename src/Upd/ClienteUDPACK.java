package Upd;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ClienteUDPACK {
    public static void main(String[] args) {
        final int PUERTO = 5556;
        final String HOST = "localhost";
        final int NUM_PAQUETES = 5;
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress direccion = InetAddress.getByName(HOST);
            for (int i = 0; i < NUM_PAQUETES; i++) {
                // Simular datos de cada paquete
                String mensaje = "Paquete " + (i+1);
                byte[] buffer = mensaje.getBytes("UTF-8");
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length, direccion, PUERTO);
                socket.send(paquete);
                System.out.println("Enviado: " + mensaje);

                // Esperar ACK (se usa un ciclo controlado: se espera 1 ACK por paquete)
                byte[] ackBuffer = new byte[10];
                DatagramPacket ackPacket = new DatagramPacket(ackBuffer, ackBuffer.length);
                socket.receive(ackPacket);
                String ack = new String(ackPacket.getData(), 0, ackPacket.getLength(), "UTF-8");
                System.out.println("Recibido: " + ack);
            }
            System.out.println("Todos los paquetes enviados y confirmados.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
