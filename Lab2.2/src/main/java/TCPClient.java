import java.io.*;
import java.net.*;
import java.util.Scanner;

public class TCPClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter serverOutput = new PrintWriter(socket.getOutputStream(), true);
             Scanner userInput = new Scanner(System.in)) {

            System.out.println("Connected to server. Type 'write <message>' to write or 'read' to read from file.");

            String command;
            while (true) {
                System.out.print("Enter command: ");
                command = userInput.nextLine();

                // Send the command to the server
                serverOutput.println(command);

                // If the command is "exit", break the loop
                if (command.equalsIgnoreCase("exit")) {
                    break;
                }

                // Receive the response from the server and print it
                String response;
                while ((response = serverInput.readLine()) != null) {
                    System.out.println(response);
                    if (response.equals("End of file.") || response.equals("Message written to file.")) {
                        break;  // End the loop for 'read' or 'write' response
                    }
                }

                // Only ask for input again if the command is 'read'
                if (!command.startsWith("write ")) {
                    System.out.print("Enter command: ");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
