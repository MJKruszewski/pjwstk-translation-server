package zad1.api.dictionary;

import zad1.api.BaseLogger;
import zad1.api.Commands;
import zad1.api.Server;
import zad1.api.dictionary.commands.Translate;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class DictionaryServer extends Server implements Runnable {

    private final ExecutorService executorService;
    private final ServerConfiguration serverConfiguration;

    public DictionaryServer(ServerConfiguration serverConfiguration) {
        this.serverConfiguration = serverConfiguration;

        try {
            this.serverSocket = new ServerSocket();
            this.serverSocket.bind(this.serverConfiguration.getInetSocketAddress());
            this.serverSocket.setSoTimeout(5000);
        } catch (IOException ignored) {
        }

        executorService = Executors.newFixedThreadPool(this.serverConfiguration.getThreadsNumbers());

        for (int i = 0; i < this.serverConfiguration.getThreadsNumbers(); i++) {
            executorService.submit(this);
        }
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void shutDown() {
        this.executorService.shutdownNow();
        try {
            this.executorService.awaitTermination(100L, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
        }
    }


    @Override
    protected void executeCommand(Commands command, String[] commandParts) {
        switch (command) {
            case SHUTDOWN:
                isServerRunning = false;
                BaseLogger.parentLogger.info("Translation Server SHUTDOWN");
                break;
            case TRANSLATE:
                Translate.execute(this.serverConfiguration, commandParts);
                break;
        }
    }
}
