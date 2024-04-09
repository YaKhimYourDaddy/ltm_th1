package bai1_withGraphics;

import java.io.*;
import java.net.*;
import javax.swing.*;

public class Client extends JFrame {
    private JTextArea textArea;
    private JTextField inputField;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public Client() {
        super("Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textArea = new JTextArea();
        inputField = new JTextField();

        inputField.addActionListener(e -> {
            String message = inputField.getText();
            out.println(message);
            inputField.setText("");
        });

        add(new JScrollPane(textArea), BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);

        setSize(400, 300);
        setVisible(true);

        try {
            socket = new Socket("localhost", 12345);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String line;
            while ((line = in.readLine()) != null) {
                textArea.append(line + "\n");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Client();
    }
}
