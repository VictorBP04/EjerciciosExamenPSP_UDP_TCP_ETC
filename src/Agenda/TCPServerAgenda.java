package Agenda;

import java.io.*;
import java.net.*;
import java.util.*;

class Appointment {
    int id;
    String date;
    String description;

    public Appointment(int id, String date, String description) {
        this.id = id;
        this.date = date;
        this.description = description;
    }

    public String toString() {
        return id + ": " + date + " - " + description;
    }
}

public class TCPServerAgenda {
    public static void main(String[] args) {
        int port = 20000;
        List<Appointment> appointments = new ArrayList<>();
        int nextId = 1;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor Agenda TCP escuchando en el puerto " + port);
            Socket clientSocket = serverSocket.accept();
            System.out.println("Cliente conectado: " + clientSocket.getInetAddress().getHostAddress());

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            out.println("Bienvenido al sistema de Agenda Remota.");
            out.println("Comandos: ADD yyyy-mm-dd descripcion, LIST, DELETE id, EXIT.");

            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.equalsIgnoreCase("EXIT")) {
                    out.println("Desconectando...");
                    break;
                }
                if (line.toUpperCase().startsWith("ADD ")) {
                    // Se espera: ADD fecha descripcion (la descripción puede contener espacios)
                    String[] parts = line.split("\\s+", 3);
                    if (parts.length < 3) {
                        out.println("Comando ADD inválido. Uso: ADD yyyy-mm-dd descripcion");
                        continue;
                    }
                    String date = parts[1];
                    String description = parts[2];
                    Appointment appt = new Appointment(nextId, date, description);
                    appointments.add(appt);
                    out.println("Agregado: " + appt.toString());
                    nextId++;
                } else if (line.equalsIgnoreCase("LIST")) {
                    if (appointments.isEmpty()) {
                        out.println("No hay citas.");
                    } else {
                        for (Appointment appt : appointments) {
                            out.println(appt.toString());
                        }
                    }
                } else if (line.toUpperCase().startsWith("DELETE ")) {
                    String[] parts = line.split("\\s+");
                    if (parts.length != 2) {
                        out.println("Comando DELETE inválido. Uso: DELETE id");
                        continue;
                    }
                    try {
                        int id = Integer.parseInt(parts[1]);
                        boolean found = false;
                        Iterator<Appointment> iter = appointments.iterator();
                        while (iter.hasNext()) {
                            Appointment appt = iter.next();
                            if (appt.id == id) {
                                iter.remove();
                                found = true;
                                out.println("Eliminado: " + appt.toString());
                                break;
                            }
                        }
                        if (!found) {
                            out.println("No se encontró la cita con id " + id);
                        }
                    } catch (NumberFormatException e) {
                        out.println("Id inválido.");
                    }
                } else {
                    out.println("Comando no reconocido. Comandos válidos: ADD, LIST, DELETE, EXIT.");
                }
            }
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

