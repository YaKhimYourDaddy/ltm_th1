package bai1_noGraphics;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class Client extends JFrame {
    private JTextArea textArea;
    private JTextField inputField;
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    public Client() {
        super("Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textArea = new JTextArea();
        inputField = new JTextField();

        inputField.addActionListener(e -> {
            String message = inputField.getText();
            try {
                dos.writeUTF(message);
                dos.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            inputField.setText("");
        });

        add(new JScrollPane(textArea), BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);

        setSize(400, 300);
        setVisible(true);

        try {
            socket = new Socket("localhost", 12345);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            String line;
            while ((line = dis.readUTF()) != null) {
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
