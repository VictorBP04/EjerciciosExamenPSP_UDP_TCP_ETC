package Claculadora;

import java.io.*;
import java.net.*;

public class UDPServerCalculator {
    public static void main(String[] args) {
        int port = 17000;
        byte[] buffer = new byte[1024];
        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("Servidor Calculadora UDP escuchando en el puerto " + port);

            // Esperar el primer paquete para obtener la dirección y puerto del cliente
            DatagramPacket initPacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(initPacket);
            InetAddress clientAddress = initPacket.getAddress();
            int clientPort = initPacket.getPort();

            String welcomeMsg = "Bienvenido a la Calculadora Remota UDP.\nEnvía expresiones en el formato: número operador número (ejemplo: 3 + 4).\nEscribe 'salir' para finalizar.";
            DatagramPacket welcomePacket = new DatagramPacket(welcomeMsg.getBytes(), welcomeMsg.getBytes().length, clientAddress, clientPort);
            socket.send(welcomePacket);

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String expression = new String(packet.getData(), 0, packet.getLength()).trim();
                String response;
                if (expression.equalsIgnoreCase("salir")) {
                    response = "Desconectando...";
                    DatagramPacket responsePacket = new DatagramPacket(response.getBytes(), response.getBytes().length, clientAddress, clientPort);
                    socket.send(responsePacket);
                    break;
                }
                try {
                    double result = evaluateExpression(expression);
                    response = "Resultado: " + result;
                } catch (Exception e) {
                    response = "Error en la expresión. Formato correcto: número operador número (ejemplo: 3 + 4)";
                }
                DatagramPacket responsePacket = new DatagramPacket(response.getBytes(), response.getBytes().length, clientAddress, clientPort);
                socket.send(responsePacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static double evaluateExpression(String expression) throws Exception {
        String[] tokens = expression.split("\\s+");
        if (tokens.length != 3) {
            throw new Exception("Formato incorrecto");
        }
        double num1 = Double.parseDouble(tokens[0]);
        String operator = tokens[1];
        double num2 = Double.parseDouble(tokens[2]);
        switch(operator) {
            case "+":
                return num1 + num2;
            case "-":
                return num1 - num2;
            case "*":
            case "x":
                return num1 * num2;
            case "/":
                if (num2 == 0)
                    throw new Exception("División por cero");
                return num1 / num2;
            default:
                throw new Exception("Operador no soportado");
        }
    }
}
