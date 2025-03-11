package Agenda;

import java.io.*;
import java.net.*;
import java.util.*;

class Appointment2 {
    int id;
    String date;
    String description;

    public Appointment2(int id, String date, String description) {
        this.id = id;
        this.date = date;
        this.description = description;
    }

    public String toString() {
        return id + ": " + date + " - " + description;
    }
}

public class UDPServerAgenda {
    public static void main(String[] args) {
        int port = 21000;
        List<Appointment> appointments = new ArrayList<>();
        int nextId = 1;
        byte[] buffer = new byte[2048];

        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("Servidor Agenda UDP escuchando en el puerto " + port);
            // Recibir paquete inicial para conocer la dirección y puerto del cliente
            DatagramPacket initPacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(initPacket);
            InetAddress clientAddress = initPacket.getAddress();
            int clientPort = initPacket.getPort();

            String welcome = "Bienvenido al sistema de Agenda Remota UDP.\nComandos: ADD yyyy-mm-dd descripcion, LIST, DELETE id, EXIT.";
            DatagramPacket welcomePacket = new DatagramPacket(
                    welcome.getBytes(), welcome.getBytes().length, clientAddress, clientPort);
            socket.send(welcomePacket);

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String command = new String(packet.getData(), 0, packet.getLength()).trim();
                String response = "";
                if (command.equalsIgnoreCase("EXIT")) {
                    response = "Desconectando...";
                    DatagramPacket responsePacket = new DatagramPacket(
                            response.getBytes(), response.getBytes().length, clientAddress, clientPort);
                    socket.send(responsePacket);
                    break;
                }
                if (command.toUpperCase().startsWith("ADD ")) {
                    String[] parts = command.split("\\s+", 3);
                    if (parts.length < 3) {
                        response = "Comando ADD inválido. Uso: ADD yyyy-mm-dd descripcion";
                    } else {
                        String date = parts[1];
                        String description = parts[2];
                        Appointment appt = new Appointment(nextId, date, description);
                        appointments.add(appt);
                        response = "Agregado: " + appt.toString();
                        nextId++;
                    }
                } else if (command.equalsIgnoreCase("LIST")) {
                    if (appointments.isEmpty()) {
                        response = "No hay citas.";
                    } else {
                        StringBuilder sb = new StringBuilder();
                        for (Appointment appt : appointments) {
                            sb.append(appt.toString()).append("\n");
                        }
                        response = sb.toString();
                    }
                } else if (command.toUpperCase().startsWith("DELETE ")) {
                    String[] parts = command.split("\\s+");
                    if (parts.length != 2) {
                        response = "Comando DELETE inválido. Uso: DELETE id";
                    } else {
                        try {
                            int id = Integer.parseInt(parts[1]);
                            boolean found = false;
                            Iterator<Appointment> iter = appointments.iterator();
                            while (iter.hasNext()) {
                                Appointment appt = iter.next();
                                if (appt.id == id) {
                                    iter.remove();
                                    found = true;
                                    response = "Eliminado: " + appt.toString();
                                    break;
                                }
                            }
                            if (!found) {
                                response = "No se encontró la cita con id " + id;
                            }
                        } catch (NumberFormatException e) {
                            response = "Id inválido.";
                        }
                    }
                } else {
                    response = "Comando no reconocido. Comandos: ADD, LIST, DELETE, EXIT.";
                }
                DatagramPacket responsePacket = new DatagramPacket(
                        response.getBytes(), response.getBytes().length, clientAddress, clientPort);
                socket.send(responsePacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

