package TresRaya;

import java.io.*;
import java.net.*;

public class TCPClientTicTacToe {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 14000;
        if (args.length >= 1) {
            host = args[0];
        }
        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch(NumberFormatException e) {
                System.out.println("El puerto debe ser numérico.");
                return;
            }
        }

        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {

            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("Servidor: " + line);
                if (line.contains("Tu turno") || line.contains("Ingresa tu movimiento")) {
                    System.out.print("Tu movimiento (fila columna): ");
                    String userMove = stdIn.readLine();
                    out.println(userMove);
                }
                if (line.contains("Ganaste") || line.contains("Perdiste") || line.contains("Empate"))
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
