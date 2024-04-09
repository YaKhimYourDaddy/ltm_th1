package bai3_withGraphics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Client extends JFrame {
    private JTextField messageTextField;
    private JButton sendButton;
    private JTextArea chatTextArea;
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private String clientName;

    public Client() {
        setTitle("Client");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel inputPanel = new JPanel(new BorderLayout());
        messageTextField = new JTextField();
        sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        inputPanel.add(messageTextField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.NORTH);

        chatTextArea = new JTextArea();
        chatTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatTextArea);
        add(scrollPane, BorderLayout.CENTER);

        clientName = JOptionPane.showInputDialog("Enter your name:");
        if (clientName == null || clientName.trim().isEmpty()) {
            clientName = "Anonymous";
        }

        try {
            socket = new Socket("localhost", 12345);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            // Send client's name to server
            dos.writeUTF(clientName);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Start a separate thread to listen for messages from the server
        Thread receivingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String message;
                    while ((message = dis.readUTF()) != null) {
                        chatTextArea.append(message + "\n");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        receivingThread.start();
    }

    private void sendMessage() {
        String message = messageTextField.getText();
        if (!message.isEmpty()) {
            try {
                dos.writeUTF(message);
                dos.flush();
                messageTextField.setText("");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Client().setVisible(true));
    }
}
