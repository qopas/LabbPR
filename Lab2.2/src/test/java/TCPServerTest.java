import org.junit.jupiter.api.*;
import java.io.*;
import java.net.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class TCPServerTest {

    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 12345;
    private static final int SERVER_STARTUP_DELAY_MS = 2000; // Delay to allow server startup

    private static Socket socket;
    private static BufferedReader input;
    private static PrintWriter output;

    private static final List<String> writtenMessages = Collections.synchronizedList(new ArrayList<>());

    @BeforeAll
    public static void startServer() throws IOException {
        // Start the server in a separate thread before tests
        new Thread(() -> {
            try {
                TCPServer.main(new String[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        // Give the server time to start (ensure it's ready)
        try {
            Thread.sleep(SERVER_STARTUP_DELAY_MS);  // Wait for server to be fully initialized
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    public void setUp() throws IOException {
        // Try to establish a socket connection to the server
        socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new PrintWriter(socket.getOutputStream(), true);
    }

    @AfterEach
    public void tearDown() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    @Test
    public void testConcurrency() throws InterruptedException, IOException {
        // Create multiple threads that send write and read commands to the server
        Thread writer1 = new Thread(() -> sendWriteCommand("First"));
        Thread writer2 = new Thread(() -> sendWriteCommand("Second"));
        Thread writer3 = new Thread(() -> sendWriteCommand("Third"));
        Thread reader = new Thread(this::sendReadCommand);

        // Start all threads
        writer1.start();
        writer2.start();
        writer3.start();

        // Ensure writers have started before reader starts
        writer1.join();
        writer2.join();
        writer3.join();

        reader.start();
        reader.join();

        // Verify the file content and the order of operations
        verifyFileContent();
    }

    private void sendWriteCommand(String message) {
        try {
            // Send the write command
            output.println("write " + message);
            String response = input.readLine();
            assertEquals("Message written to file.", response); // Expect success message from server
            writtenMessages.add(message); // Track the message
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendReadCommand() {
        try {
            // Send the read command
            output.println("read");

            // Read the response
            String response = input.readLine();
            assertEquals("File content:", response);

            // Read and verify the file content
            String line1 = input.readLine();
            String line2 = input.readLine();
            String line3 = input.readLine();
            assertNotNull(line1);
            assertNotNull(line2);
            assertNotNull(line3);

            // Check that the file contains all written messages, in any order
            List<String> fileContents = Arrays.asList(line1, line2, line3);
            assertTrue(fileContents.containsAll(writtenMessages));

            input.readLine(); // Should be "End of file."
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void verifyFileContent() throws IOException {
        // Open the file and verify its content
        try (BufferedReader fileReader = new BufferedReader(new FileReader("C:\\Users\\user\\IdeaProjects\\LabbPR\\sharedfile.txt"))) {
            Set<String> fileContents = new HashSet<>();
            String line;
            while ((line = fileReader.readLine()) != null) {
                fileContents.add(line);
            }
            assertTrue(fileContents.containsAll(writtenMessages));
        }
    }
}
