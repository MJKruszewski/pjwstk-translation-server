package zad1.api.base;

import zad1.api.Commands;
import zad1.api.Server;
import zad1.api.base.commands.Shutdown;
import zad1.api.base.commands.Translate;
import zad1.api.dictionary.ServerConfiguration;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.List;

public final class BaseServer extends Server implements Runnable {

    private final List<ServerConfiguration> supportedLanguages;

    public BaseServer(InetSocketAddress inetSocketAddress, List<ServerConfiguration> supportedLanguages) {
        this.supportedLanguages = supportedLanguages;
        try {
            this.serverSocket = new ServerSocket();
            this.serverSocket.bind(inetSocketAddress);
            this.serverSocket.setSoTimeout(5000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void executeCommand(Commands command, String[] commandParts) {
        switch (command) {
            case SHUTDOWN:
                isServerRunning = false;
                Shutdown.execute(supportedLanguages, commandParts);
                break;
            case TRANSLATE:
                Translate.execute(supportedLanguages, commandParts);
                break;
        }
    }
}
