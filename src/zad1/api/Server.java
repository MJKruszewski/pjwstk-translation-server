package zad1.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public abstract class Server implements Runnable {
    protected static Boolean isServerRunning = true;
    protected ServerSocket serverSocket;

    @Override
    public void run() {
        String line;
        Commands commands = Commands.IDK;

        while (isServerRunning) {
            try (Socket socket = serverSocket.accept()) {
                BaseLogger.parentLogger.info("Accepted connection from " + socket.getInetAddress().getHostAddress());

                try (
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true)
                ) {
                    while ((line = bufferedReader.readLine()) != null) {
                        commands = Commands.IDK;
                        String[] commandParts = line.split(";");

                        if (commandParts.length <= 0 || commandParts[0].equals("END")) {
                            continue;
                        }

                        try {
                            commands = Commands.valueOf(commandParts[0]);
                        } catch (IllegalArgumentException e) {
                            BaseLogger.parentLogger.error(e);
                        }

                        BaseLogger.parentLogger.info("Executing command: " + commands);
                        executeCommand(commands, commandParts);
                    }

                    printWriter.println("END");
                    printWriter.flush();
                } catch (IOException i) {
                }
            } catch (SocketTimeoutException socketTimeoutException) {
                BaseLogger.parentLogger.debug("Socket timeout due to lack of connection");
            } catch (IOException e) {
                BaseLogger.parentLogger.error(e);
            }
        }

        closeServerSocket();
    }

    protected abstract void executeCommand(Commands command, String[] commandParts);

    private void closeServerSocket() {
        try {
            if (!serverSocket.isClosed()) {
                Thread.sleep(10001);
            }
            serverSocket.close();
        } catch (IOException | InterruptedException e) {
            BaseLogger.parentLogger.error(e);
        }
    }
}
