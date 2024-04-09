package bai3_withGraphics;

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
        private String clientName;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                dis = new DataInputStream(clientSocket.getInputStream());
                dos = new DataOutputStream(clientSocket.getOutputStream());
                // Get client's name
                clientName = dis.readUTF();
                broadcast(clientName + " joined the chat");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                String message;
                while ((message = dis.readUTF()) != null) {
                    broadcast(clientName + ": " + message);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                    // Notify other clients when a client disconnects
                    broadcast(clientName + " left the chat");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        private void broadcast(String message) {
            log(message);
            // Broadcast message to all connected clients
            // Here you can send the message to clients
        }
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> logTextArea.append(message + "\n"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Server().setVisible(true));
    }
}
