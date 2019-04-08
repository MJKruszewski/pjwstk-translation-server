package zad1.api;

import zad1.api.base.BaseServer;
import zad1.api.dictionary.DictionaryServer;
import zad1.api.dictionary.ServerConfiguration;
import zad1.api.dictionary.translations.files.LanguagesEnum;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Main {

    private final static List<ServerConfiguration> supportedLanguages = new ArrayList<>();

    public static void main(String[] args) {
        BaseLogger.parentLogger.info("Starting application");
        Properties properties = getProperties();

        final int baseServerThreads = Integer.valueOf(properties.getProperty("base.server.threads"));
        ExecutorService executorService = Executors.newFixedThreadPool(baseServerThreads);

        loadSupportedLanguages(properties);

        Future future = startBaseServer(properties, baseServerThreads, executorService);

        List<DictionaryServer> dictionaryServers = startDictionaryServers();
        waitUntilAllIsDone(future);

        shutdownExecutors(executorService, dictionaryServers);

        BaseLogger.parentLogger.info("Waiting to close application");
    }

    private static Future startBaseServer(Properties properties, int baseServerThreads, ExecutorService executorService) {
        BaseServer baseServer = new BaseServer(
                new InetSocketAddress(Integer.valueOf(properties.getProperty("base.server.port"))),
                supportedLanguages
        );

        Future future = null;

        for (int i = 0; i < baseServerThreads; i++) {
            future = executorService.submit(baseServer);
        }
        return future;
    }

    private static List<DictionaryServer> startDictionaryServers() {
        List<DictionaryServer> dictionaryServers = new ArrayList<>(supportedLanguages.size());
        supportedLanguages.forEach(serverConfiguration -> dictionaryServers.add(new DictionaryServer(serverConfiguration)));
        return dictionaryServers;
    }

    private static void waitUntilAllIsDone(Future future) {
        while (!future.isDone()) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                BaseLogger.parentLogger.error(e);
            }
        }
    }

    private static void shutdownExecutors(ExecutorService executorService, List<DictionaryServer> dictionaryServers) {
        executorService.shutdown();
        dictionaryServers.forEach(DictionaryServer::shutDown);
        try {
            executorService.awaitTermination(100L, TimeUnit.MILLISECONDS);
            dictionaryServers.forEach(dictionaryServer -> {
                try {
                    dictionaryServer.getExecutorService().awaitTermination(100L, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    BaseLogger.parentLogger.error(e);
                }
            });
        } catch (InterruptedException e) {
            BaseLogger.parentLogger.error(e);
        }
    }

    private static void loadSupportedLanguages(Properties properties) {
        Arrays.asList(properties.getProperty("dictionaries").split(";")).forEach(s -> {
            BaseLogger.parentLogger.info("Loading dictionary: " + s);
            supportedLanguages.add(new ServerConfiguration(
                            Integer.valueOf(properties.getProperty(s.trim().toUpperCase() + ".server.port")),
                            LanguagesEnum.valueOf(s.trim().toUpperCase()),
                            Integer.valueOf(properties.getProperty(s.trim().toUpperCase() + ".server.threads"))
                    )
            );
        });
    }

    private static Properties getProperties() {
        Properties properties = new Properties();

        try (InputStream inputStream = Main.class.getResourceAsStream("application.properties")) {
            BaseLogger.parentLogger.info("Loading properties");

            properties.load(inputStream);
        } catch (IOException i) {
            BaseLogger.parentLogger.error(i);
        }
        return properties;
    }
}
