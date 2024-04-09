package bai2_withGraphics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Client extends JFrame {
    private JTextField expressionTextField;
    private JButton sendButton;
    private JTextArea resultTextArea;
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    public Client() {
        setTitle("Client");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel inputPanel = new JPanel(new BorderLayout());
        expressionTextField = new JTextField();
        sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendExpression();
            }
        });
        inputPanel.add(expressionTextField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.NORTH);

        resultTextArea = new JTextArea();
        resultTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultTextArea);
        add(scrollPane, BorderLayout.CENTER);

        try {
            socket = new Socket("localhost", 12345);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void sendExpression() {
        String expression = expressionTextField.getText();
        if (!expression.isEmpty()) {
            try {
                dos.writeUTF(expression);
                dos.flush();
                int result = dis.readInt();
                if (result != Integer.MIN_VALUE) {
                    resultTextArea.append("Result: " + result + "\n");
                } else {
                    resultTextArea.append("Invalid expression\n");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Client().setVisible(true));
    }
}
