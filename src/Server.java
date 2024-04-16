

import java.net.*;
import java.io.*;

public class Server {
    ServerSocket serverSocket = null;

    public Server() {
        try {
            serverSocket = new ServerSocket(22000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 22000.");
            System.exit(1);
        }
    }

    public void listen() throws IOException {
        Socket clientSocket = null;
        System.out.println("Waiting for connection.....");

        try {
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
        }

        System.out.println("Connection successful");
        System.out.println("Waiting for input.....");

        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),
                true);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            System.out.println("Server: " + inputLine);
        }

        out.close();
        in.close();
        clientSocket.close();
        serverSocket.close();
    }
}
