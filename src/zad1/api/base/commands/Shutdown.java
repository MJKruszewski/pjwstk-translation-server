package zad1.api.base.commands;

import zad1.api.BaseLogger;
import zad1.api.Commands;
import zad1.api.dictionary.ServerConfiguration;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public final class Shutdown {

    public static void execute(List<ServerConfiguration> supportedLanguages, String[] commandParts) {
        supportedLanguages.forEach(serverConfiguration -> {
            try (
                    Socket client = new Socket("localhost", serverConfiguration.getInetSocketAddress().getPort());
                    PrintWriter printWriter1 = new PrintWriter(client.getOutputStream(), true)
            ) {
                printWriter1.println(Commands.SHUTDOWN);
                printWriter1.flush();
            } catch (IOException i) {
                BaseLogger.parentLogger.error(i);
            }
        });

        BaseLogger.parentLogger.info("Base Server SHUTDOWN");
    }
}
