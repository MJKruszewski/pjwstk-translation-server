package zad1.api.dictionary.commands;

import zad1.api.BaseLogger;
import zad1.api.dictionary.ServerConfiguration;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

public final class Translate {
    public static void execute(ServerConfiguration serverConfiguration, String[] commandParts) {
        try (
                Socket client = new Socket(commandParts[3].trim(), Integer.valueOf(commandParts[2]));
                PrintWriter clientWriter = new PrintWriter(client.getOutputStream(), true)
        ) {
            String translation = serverConfiguration.getDictionary().translate(commandParts[1]);
            clientWriter.println(translation);
            clientWriter.println("END");
            clientWriter.flush();

            BaseLogger.parentLogger.info("Sending response: Translated from " + commandParts[1] + " to " + translation);
        } catch (ConnectException i) {
            BaseLogger.parentLogger.debug(i);
        } catch (IOException i) {
            BaseLogger.parentLogger.error(i);
        }
    }
}
