package bai1_noGraphics;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Bai1Server_noGraphics {
    private ServerSocket serverSocket;

    public Bai1Server_noGraphics() {
        try {
            serverSocket = new ServerSocket(12345);
            System.out.println("Server started...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket.getInetAddress().getHostAddress());
                ClientHandler clientHandler = new ClientHandler(socket);
                clientHandler.start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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
                    StringBuilder result = new StringBuilder();
                    result.append("Reversed string: ").append(reverseString(line)).append("\n");
                    result.append("Uppercase string: ").append(uppercaseString(line)).append("\n");
                    result.append("Lowercase string: ").append(lowercaseString(line)).append("\n");
                    result.append("Toggled case string: ").append(toggleCaseString(line)).append("\n");
                    result.append("Word count: ").append(countWords(line)).append("\n");
                    result.append("Vowel count: ").append(countVowels(line));

                    dos.writeUTF(result.toString());
                    dos.flush();
                }

                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        private String reverseString(String str) {
            return new StringBuilder(str).reverse().toString();
        }

        private String uppercaseString(String str) {
            return str.toUpperCase();
        }

        private String lowercaseString(String str) {
            return str.toLowerCase();
        }

        private String toggleCaseString(String str) {
            StringBuilder toggled = new StringBuilder();
            for (char c : str.toCharArray()) {
                if (Character.isUpperCase(c)) {
                    toggled.append(Character.toLowerCase(c));
                } else {
                    toggled.append(Character.toUpperCase(c));
                }
            }
            return toggled.toString();
        }

        private int countWords(String str) {
            /*
            int wordCount = 0;
            boolean inWord = false;

            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);

                if (!Character.isWhitespace(c) && !inWord) {
                    wordCount++;
                    inWord = true;
                }

                if (Character.isWhitespace(c)) {
                    inWord = false;
                }
            }

            return wordCount;
             */

            /*
            line.split("\\s+"):
            This part of the line is splitting the string stored in the variable line
            into an array of substrings based on the regular expression "\s+".
            The "\s+" regular expression means "one or more whitespace characters",
            where "\s" matches any whitespace character (space, tab, newline, etc.)
            and "+" specifies one or more occurrences.
            Therefore, the split("\\s+") method separates the string into substrings
             it encounters one or more whitespace characters.
             */
            return str.split("\\s+").length;
        }

        private int countVowels(String str) {
            /*
            int vowelCount = 0;
            String vowels = "aeiouAEIOU";

            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                if (vowels.indexOf(c) != -1) {
                    vowelCount++;
                }
            }

            return vowelCount;
             */

            /*
            line.replaceAll("[^aeiouAEIOU]", ""):
            This part of the line uses the replaceAll() method of the String class
            to replace all characters in the string line that are not vowels
            (both lowercase and uppercase) with an empty string "".
            The regular expression [^aeiouAEIOU] matches any character that is not a vowel.
            The replaceAll() method then replaces all such characters with an empty string,
            effectively removing them from the original string. After this operation,
            the resulting string contains only the vowels from the original string.
             */
            return str.replaceAll("[^aeiouAEIOU]", "").length();
        }
    }

    public static void main(String[] args) {
        new Bai1Server_noGraphics();
    }
}
