package bai1_noGraphics;

import java.io.*;
import java.net.*;

public class Bai1Client_noGraphics {
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    public Bai1Client_noGraphics(String serverAddress, int serverPort) {
        try {
            socket = new Socket(serverAddress, serverPort);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            // Bắt đầu một luồng riêng để lắng nghe và xử lý dữ liệu từ Server
            Thread receivingThread = new Thread(() -> {
                try {
                    String receivedMessage;
                    while ((receivedMessage = dis.readUTF()) != null) {
                        System.out.println("Server: " + receivedMessage);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            receivingThread.start();

            // Đọc tin nhắn từ người dùng và gửi đến Server
            BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));
            String userInput;
            while ((userInput = userInputReader.readLine()) != null) {
                dos.writeUTF(userInput);
                dos.flush();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
                if (dis != null) {
                    dis.close();
                }
                if (dos != null) {
                    dos.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        String serverAddress = "localhost"; // Địa chỉ IP hoặc tên miền của Server
        int serverPort = 12345; // Cổng của Server
        new Bai1Client_noGraphics(serverAddress, serverPort);
    }
}
