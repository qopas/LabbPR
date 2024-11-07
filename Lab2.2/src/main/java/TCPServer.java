import java.io.*;
import java.net.*;

public class TCPServer {
    private static final int PORT = 12345;
    private static final String FILE_PATH = "sharedfile.txt";
    private static volatile boolean isWriting = false; // Volatile flag to indicate if writing is in progress

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);
            while (true) {
                // Accept new client connection and start a new thread for each client
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler extends Thread {
        private Socket socket;
        private BufferedReader input;
        private PrintWriter output;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                String command;
                System.out.println("Client " + socket.getInetAddress() + " is now in the handler thread.");
                while ((command = input.readLine()) != null) {
                    System.out.println("Received command: " + command);
                    if (command.startsWith("write ")) {
                        handleWriteCommand(command.substring(6));
                    } else if (command.equals("read")) {
                        handleReadCommand();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    System.out.println("Client disconnected: " + socket.getInetAddress());
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void handleWriteCommand(String message) {
            // Polling approach to check if a write operation is in progress
            synchronized (TCPServer.class) { // Ensure synchronized access to the isWriting flag
                while (isWriting) {
                    try {
                        System.out.println("Another write is in progress. Waiting...");
                        output.println("Error: Another write is in progress. Please wait.");
                        Thread.sleep(500); // Wait for a while before checking again
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }

                // Mark that a write operation is in progress
                isWriting = true;
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
                System.out.println("Writing message: " + message); // Log the message being written
                writer.write(message);
                writer.newLine();
                writer.flush();
                output.println("Message written to file.");
            } catch (IOException e) {
                e.printStackTrace();
                output.println("Error writing to file.");
            } finally {
                // Mark that the write operation is complete
                synchronized (TCPServer.class) {
                    isWriting = false;
                }
            }
        }

        private void handleReadCommand() {
            // Polling approach to check if a write operation is in progress
            while (isWriting) {
                try {
                    System.out.println("Waiting for write operation to finish...");
                    output.println("Waiting for the current write operation to finish...");
                    Thread.sleep(500); // Wait for a while before checking again
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Proceed with reading the file
            try (BufferedReader fileReader = new BufferedReader(new FileReader(FILE_PATH))) {
                String line;
                output.println("File content:");
                while ((line = fileReader.readLine()) != null) {
                    output.println(line);
                }
                output.println("End of file.");
            } catch (IOException e) {
                e.printStackTrace();
                output.println("Error reading from file.");
            }
        }
    }
}
