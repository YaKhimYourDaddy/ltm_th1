package bai1_withGraphics;

import java.io.*;
import java.net.*;
import javax.swing.*;

public class Server extends JFrame {
    private JTextArea textArea;
    private ServerSocket serverSocket;

    public Server() {
        super("Server");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textArea = new JTextArea();
        add(new JScrollPane(textArea));

        setSize(400, 300);
        setVisible(true);

        try {
            serverSocket = new ServerSocket(12345);
            appendToTextArea("Server started...");

            while (true) {
                Socket socket = serverSocket.accept();
                appendToTextArea("New client connected: " + socket.getInetAddress().getHostAddress());
                ClientHandler clientHandler = new ClientHandler(socket);
                clientHandler.start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void appendToTextArea(String text) {
        textArea.append(text + "\n");
    }

    private class ClientHandler extends Thread {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                String line;
                while ((line = in.readLine()) != null) {
                    // Xử lý yêu cầu từ client ở đây
                    // Ví dụ: In ra chuỗi đảo ngược
                    String reversedString = new StringBuilder(line).reverse().toString();
                    out.println("Reversed string: " + reversedString);
                }

                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}
