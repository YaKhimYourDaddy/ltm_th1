package bai2_withGraphics;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Server extends JFrame {
    private JTextArea logTextArea;
    private ServerSocket serverSocket;
    private ExecutorService executorService;

    public Server() {
        setTitle("Server");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        logTextArea = new JTextArea();
        logTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logTextArea);
        add(scrollPane, BorderLayout.CENTER);

        executorService = Executors.newCachedThreadPool();

        try {
            serverSocket = new ServerSocket(12345);
            log("Server started...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                log("New client connected: " + clientSocket.getInetAddress().getHostAddress());
                executorService.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private DataInputStream dis;
        private DataOutputStream dos;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                dis = new DataInputStream(clientSocket.getInputStream());
                dos = new DataOutputStream(clientSocket.getOutputStream());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                String expression;
                while ((expression = dis.readUTF()) != null) {
                    log("Received from client: " + expression);
                    int result = evaluateExpression(expression);
                    dos.writeInt(result);
                    dos.flush();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        private int evaluateExpression(String expression) {
            try {
                return (int) new Object() {
                    int pos = -1, ch;

                    void nextChar() {
                        ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
                    }

                    boolean eat(int charToEat) {
                        while (ch == ' ') nextChar();
                        if (ch == charToEat) {
                            nextChar();
                            return true;
                        }
                        return false;
                    }

                    int parse() {
                        nextChar();
                        int x = parseExpression();
                        if (pos < expression.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                        return x;
                    }

                    // Grammar:
                    // expression = term | expression `+` term | expression `-` term
                    // term = factor | term `*` factor | term `/` factor
                    // factor = `+` factor | `-` factor | `(` expression `)` | number

                    int parseExpression() {
                        int x = parseTerm();
                        for (; ; ) {
                            if (eat('+')) x += parseTerm(); // addition
                            else if (eat('-')) x -= parseTerm(); // subtraction
                            else return x;
                        }
                    }

                    int parseTerm() {
                        int x = parseFactor();
                        for (; ; ) {
                            if (eat('*')) x *= parseFactor(); // multiplication
                            else if (eat('/')) x /= parseFactor(); // division
                            else return x;
                        }
                    }

                    int parseFactor() {
                        if (eat('+')) return parseFactor(); // unary plus
                        if (eat('-')) return -parseFactor(); // unary minus

                        int x;
                        int startPos = this.pos;
                        if (eat('(')) { // parentheses
                            x = parseExpression();
                            eat(')');
                        } else if ((ch >= '0' && ch <= '9')) { // numbers
                            while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                            x = Integer.parseInt(expression.substring(startPos, this.pos));
                        } else {
                            throw new RuntimeException("Unexpected: " + (char) ch);
                        }

                        return x;
                    }
                }.parse();
            } catch (Exception ex) {
                ex.printStackTrace();
                return Integer.MIN_VALUE; // Return a special value to indicate error
            }
        }
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> logTextArea.append(message + "\n"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Server().setVisible(true));
    }
}
