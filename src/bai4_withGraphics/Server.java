package bai4_withGraphics;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class Server extends JFrame {
    private JTextArea logTextArea;
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private Map<String, String> database;

    public Server() {
        setTitle("Server");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        logTextArea = new JTextArea();
        logTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logTextArea);
        add(scrollPane, BorderLayout.CENTER);

        executorService = Executors.newCachedThreadPool();
        database = new HashMap<>();
        database.put("user1", "password1");
        database.put("user2", "password2");

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
                String username = dis.readUTF();
                String password = dis.readUTF();
                if (authenticate(username, password)) {
                    dos.writeUTF("Authentication successful");
                } else {
                    dos.writeUTF("Authentication failed");
                }
                dos.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        private boolean authenticate(String username, String password) {
            return database.containsKey(username) && database.get(username).equals(password);
        }
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> logTextArea.append(message + "\n"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Server().setVisible(true));
    }
}
