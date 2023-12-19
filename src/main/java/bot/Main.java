package bot;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main {
    private static String token;
    public static void main(String[] args) throws TelegramApiException {
        try {
            Properties properties = new Properties();
            String FILE_NAME = "config.properties";
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            try (InputStream inputStream = loader.getResourceAsStream(FILE_NAME)) {
                properties.load(inputStream);
            } catch (IOException e) {
                throw new IOException(String.format("Error loading properties file '%s'", FILE_NAME));
            }
            token = properties.getProperty("token");
            if (token == null) {
                throw new RuntimeException("Token value is null");
            }
        } catch (RuntimeException | IOException e) {
            throw new RuntimeException("bot.Bot initialization error: " + e.getMessage());
        }

        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        BotSession botSession =  api.registerBot(new Bot(token));
    }

    public static String getToken() {
        return token;
    }
}
