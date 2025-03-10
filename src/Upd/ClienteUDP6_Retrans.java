package Upd;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ClienteUDP6_Retrans {
    public static void main(String[] args) {
        final int PUERTO = 5610;
        final String HOST = "localhost";
        final int NUM_PAQUETES = 5;
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress direccion = InetAddress.getByName(HOST);
            for (int i = 0; i < NUM_PAQUETES; i++) {
                String mensaje = "Paquete " + (i+1);
                byte[] buffer = mensaje.getBytes("UTF-8");
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length, direccion, PUERTO);
                socket.send(paquete);
                System.out.println("ClienteUDP6_Retrans: Enviado: " + mensaje);
                byte[] ackBuffer = new byte[20];
                DatagramPacket ackPacket = new DatagramPacket(ackBuffer, ackBuffer.length);
                socket.receive(ackPacket);
                String ack = new String(ackPacket.getData(), 0, ackPacket.getLength(), "UTF-8");
                System.out.println("ClienteUDP6_Retrans: Recibido: " + ack);
                // Simular retransmisión para el paquete 3 si el ACK no es correcto
                if (i == 2 && !ack.equals("ACK3")) {
                    System.out.println("ClienteUDP6_Retrans: Retransmitiendo paquete 3...");
                    socket.send(paquete);
                    ackPacket = new DatagramPacket(ackBuffer, ackBuffer.length);
                    socket.receive(ackPacket);
                    ack = new String(ackPacket.getData(), 0, ackPacket.getLength(), "UTF-8");
                    System.out.println("ClienteUDP6_Retrans: Recibido tras retransmisión: " + ack);
                }
            }
            System.out.println("ClienteUDP6_Retrans: Todos los paquetes enviados y confirmados.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
