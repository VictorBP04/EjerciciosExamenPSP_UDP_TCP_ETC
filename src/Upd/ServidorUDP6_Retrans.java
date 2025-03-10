package Upd;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ServidorUDP6_Retrans {
    public static void main(String[] args) {
        final int PUERTO = 5610;
        final int NUM_PAQUETES = 5;
        try (DatagramSocket socket = new DatagramSocket(PUERTO)) {
            for (int i = 0; i < NUM_PAQUETES; i++) {
                byte[] buffer = new byte[512];
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                socket.receive(paquete);
                System.out.println("ServidorUDP6_Retrans: Paquete " + (i+1) + " recibido.");
                // Enviar ACK para cada paquete
                String ackMsg = "ACK" + (i+1);
                byte[] ack = ackMsg.getBytes("UTF-8");
                InetAddress address = paquete.getAddress();
                int port = paquete.getPort();
                DatagramPacket ackPacket = new DatagramPacket(ack, ack.length, address, port);
                socket.send(ackPacket);
                System.out.println("ServidorUDP6_Retrans: Enviado " + ackMsg);
            }
            System.out.println("ServidorUDP6_Retrans: Transferencia completa.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

