package client;

import javafx.concurrent.Task;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SendFileTask extends Task<Void> {
    private File file;
    private int bufferSize = 64;
    private String serverAddress = "127.0.0.1";
    private int port = 50000;
    private static Logger log = Logger.getLogger(SendFileTask.class.getCanonicalName());

    protected SendFileTask(File file) {
        this.file = file;
    }

    @Override protected Void call() {
        updateMessage("Connecting to server on address " + serverAddress);

        try (Socket server = new Socket(serverAddress, port);
             ObjectOutputStream output = new ObjectOutputStream(new BufferedOutputStream(server.getOutputStream()));
             BufferedInputStream input = new BufferedInputStream(Files.newInputStream(file.toPath()))) {

            updateMessage("Connection initialized. Sending file.");

            output.writeUTF(file.getName());
            byte[] buffer = new byte[bufferSize];
            int readBytes, sentBytes = 0;

            while ((readBytes = input.read(buffer)) != -1) {
                output.write(buffer, 0, readBytes);
                sentBytes += readBytes;
                updateProgress(sentBytes, file.length());
            }

            updateMessage("File sent successfully.");
        } catch (ConnectException e) {
            updateMessage("Couldn't connect to server: " + serverAddress + ":" + port);
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }

        return null;
    }
}
