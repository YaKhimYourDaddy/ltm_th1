package bai1_noGraphics;
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
        private DataInputStream dis;
        private DataOutputStream dos;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                dis = new DataInputStream(socket.getInputStream());
                dos = new DataOutputStream(socket.getOutputStream());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                String line;
                while ((line = dis.readUTF()) != null) {
                    // Thực hiện tất cả các công việc và gửi kết quả trả về cho client
                    StringBuilder result = new StringBuilder();
                    result.append("Reversed string: ").append(new StringBuilder(line).reverse()).append("\n");
                    result.append("Uppercase string: ").append(line.toUpperCase()).append("\n");
                    result.append("Lowercase string: ").append(line.toLowerCase()).append("\n");

                    StringBuilder toggled = new StringBuilder();
                    for (char c : line.toCharArray()) {
                        if (Character.isUpperCase(c)) {
                            toggled.append(Character.toLowerCase(c));
                        } else {
                            toggled.append(Character.toUpperCase(c));
                        }
                    }
                    result.append("Toggled case string: ").append(toggled).append("\n");

                    int wordCount = line.split("\\s+").length;
                    int vowelCount = line.replaceAll("[^aeiouAEIOU]", "").length();
                    result.append("Word count: ").append(wordCount).append("\n");
                    result.append("Vowel count: ").append(vowelCount);

                    dos.writeUTF(result.toString());
                    dos.flush();
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
