package Claculadora;

import java.io.*;
import java.net.*;

public class TCPServerCalculator {
    public static void main(String[] args) {
        int port = 16000;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor Calculadora TCP escuchando en el puerto " + port);
            Socket clientSocket = serverSocket.accept();
            System.out.println("Cliente conectado: " + clientSocket.getInetAddress().getHostAddress());

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            out.println("Bienvenido a la Calculadora Remota.");
            out.println("Envía expresiones en el formato: número operador número (ejemplo: 3 + 4).");
            out.println("Escribe 'salir' para finalizar.");

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                inputLine = inputLine.trim();
                if (inputLine.equalsIgnoreCase("salir")) {
                    out.println("Desconectando...");
                    break;
                }
                try {
                    double result = evaluateExpression(inputLine);
                    out.println("Resultado: " + result);
                } catch (Exception e) {
                    out.println("Error en la expresión. Formato correcto: número operador número (ejemplo: 3 + 4)");
                }
            }
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Evalúa una expresión en el formato "número operador número"
    private static double evaluateExpression(String expression) throws Exception {
        String[] tokens = expression.split("\\s+");
        if (tokens.length != 3) {
            throw new Exception("Formato incorrecto");
        }
        double num1 = Double.parseDouble(tokens[0]);
        String operator = tokens[1];
        double num2 = Double.parseDouble(tokens[2]);
        switch (operator) {
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

