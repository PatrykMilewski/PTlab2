package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;

public class DataServer {
    private int port, bufferSize;

    private static Logger log = Logger.getLogger(DataServer.class.getCanonicalName());
    private ExecutorService executor = Executors.newFixedThreadPool(4);

    private DataServer(int port, int bufferSize) {
        this.port = port;
        this.bufferSize = bufferSize;
    }

    private void startServer() {
        try (ServerSocket server = new ServerSocket(port)) {
            log.info(format("Listening on port %d", port));

            while (true) {
                final Socket clientSocket = server.accept();
                executor.submit(() -> handleClient(clientSocket));
            }
        }
        catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private void handleClient(Socket client) {
        log.info(format("New connection from %s", client.getInetAddress().getHostAddress()));

        try (ObjectInputStream input = new ObjectInputStream(new BufferedInputStream(client.getInputStream()))) {
            String fileName = input.readUTF();
            log.info(format("Receiving file %s", fileName));

            try (BufferedOutputStream output = new BufferedOutputStream(Files.newOutputStream(Paths.get(fileName)))) {
                byte[] buffer = new byte[bufferSize];
                int readSize;
                while ((readSize = input.read(buffer)) != -1)
                    output.write(buffer, 0, readSize);
            }
        }
        catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        DataServer server = new DataServer(50000, 64);
        server.startServer();
    }
}