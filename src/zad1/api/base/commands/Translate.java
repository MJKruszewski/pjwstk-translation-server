package zad1.api.base.commands;

import zad1.api.BaseLogger;
import zad1.api.Commands;
import zad1.api.dictionary.ServerConfiguration;
import zad1.api.dictionary.translations.files.LanguagesEnum;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Translate {
    public static void execute(List<ServerConfiguration> supportedLanguages, String[] commandParts) {
        AtomicBoolean serverFound = new AtomicBoolean(false);


        supportedLanguages.forEach(serverConfiguration -> {
            if (LanguagesEnum.valueOf(commandParts[1]) != serverConfiguration.getLanguageCode()) {
                return;
            }

            try (
                    Socket client = new Socket("localhost", serverConfiguration.getInetSocketAddress().getPort());
                    PrintWriter clientWriter = new PrintWriter(client.getOutputStream(), true)
            ) {
                serverFound.set(false);

                String message = Commands.TRANSLATE
                        + ";" + commandParts[2].trim().toLowerCase()
                        + ";" + Integer.valueOf(commandParts[3])
                        + ";" + commandParts[4].trim();

                clientWriter.println(message);
                clientWriter.println("END");
                clientWriter.flush();

                BaseLogger.parentLogger.info("Sending message: " + message);
            } catch (IOException i) {
                BaseLogger.parentLogger.error(i);
            }
        });

        try (
                Socket client = new Socket("localhost", supportedLanguages.get(0).getInetSocketAddress().getPort());
                PrintWriter clientWriter = new PrintWriter(client.getOutputStream(), true)
        ) {
            if (!serverFound.get()) {
                String message = Commands.TRANSLATE
                        + ";XXXXXXXXXXXXXXXX"
                        + ";" + Integer.valueOf(commandParts[3])
                        + ";" + commandParts[4].trim();

                clientWriter.println(message);
                clientWriter.println("END");
                clientWriter.flush();

                BaseLogger.parentLogger.info("Sending message: " + message);
            }
        } catch (IOException i) {
            BaseLogger.parentLogger.error(i);
        }


    }
}
