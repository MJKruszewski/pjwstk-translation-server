package zad1.gui;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientTest {
    public static void main(String[] args) {
//        shutdown();
//        translate();
    }

    private static void translate() {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        try {
            ServerSocket serverSocket = new ServerSocket(9768);
            executorService.submit(() -> {
                try (
                        Socket socket = serverSocket.accept();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                ) {
                    String line;
                    if ((line = bufferedReader.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException i) {
                    i.printStackTrace();
                }

                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException i) {
            i.printStackTrace();
        }

        try (
                Socket client = new Socket("localhost", 8000);
                PrintWriter printWriter1 = new PrintWriter(client.getOutputStream(), true)
        ) {
            printWriter1.println(Commands.TRANSLATE + ";" + "EN" + ";" + "kodń" + ";" + 9768);
            printWriter1.flush();
        } catch (IOException i) {
        }

        executorService.shutdown();
    }

    private static void shutdown() {
        try (
                Socket client = new Socket("localhost", 8000);
                PrintWriter printWriter1 = new PrintWriter(client.getOutputStream(), true)
        ) {
            printWriter1.println(Commands.SHUTDOWN);
            printWriter1.flush();
        } catch (IOException i) {
        }
    }
}
