package zad1.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;


public class Controller {

    @FXML
    public TextField word_from;
    @FXML
    public TextField word_to;
    @FXML
    public ChoiceBox<String> language;
    @FXML
    public TextField port;
    @FXML
    public TextField host;

    public void onButtonClick() {
        word_from.getStyleClass().remove("error");
        language.getStyleClass().remove("error");
        host.getStyleClass().remove("error");

        Integer portValue = ThreadLocalRandom.current().nextInt(6000, 7000);
        port.setText(portValue.toString());

        if (word_from.getText() == null || word_from.getText().isEmpty()) {
            word_from.getStyleClass().add("error");
            return;
        }

        if (host.getText() == null || host.getText().isEmpty()) {
            host.getStyleClass().add("error");
            return;
        }

        if (language.getValue() == null || language.getValue().isEmpty()) {
            language.getStyleClass().add("error");
            return;
        }

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        try {
            ServerSocket serverSocket = new ServerSocket(portValue);
            executorService.submit(() -> {
                try (
                        Socket socket = serverSocket.accept();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                ) {
                    String line;
                    if ((line = bufferedReader.readLine()) != null) {
                        word_to.setText(line);
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
            printWriter1.println(Commands.TRANSLATE + ";" + language.getValue() + ";" + word_from.getText() + ";" + portValue + ";" + host.getText());
            printWriter1.flush();
        } catch (IOException i) {
        }

        executorService.shutdown();

    }

    public void shutdown(ActionEvent actionEvent) {
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
