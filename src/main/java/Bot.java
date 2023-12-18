import commands.AppBotCommand;
import commands.BotCommonCommands;
import functions.FilterOperation;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Bot extends TelegramLongPollingBot {
    ArrayList<String> photoPaths;
    Class[] commandClasses = new Class[] { BotCommonCommands.class };

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        try {
            SendMessage responseTextMessasge = runCommonCommand(message);
            if (responseTextMessasge != null) {
                execute(responseTextMessasge);
                return;
            }
        } catch (InvocationTargetException | IllegalAccessException | TelegramApiException e) {
            throw new RuntimeException(e);
        }
        /*try {
            String response = runCommand(message.getText());
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(response);
            execute(sendMessage);
        } catch (InvocationTargetException | IllegalAccessException | TelegramApiException e) {
            throw new RuntimeException(e);
        }*/
        /*try {
            photoPaths = new ArrayList<>(PhotoMessageUtils.savePhotos(getFilesByMessage(message), Main.getToken()));
            for (String path : photoPaths) {
                try {
                    String callData = "ЧБ";
                    PhotoMessageUtils.processingImage(path, callData);
                    execute(preparePhotoMessage(path, Long.parseLong(chatId)));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
    }

    private SendMessage runCommonCommand(Message message) throws InvocationTargetException, IllegalAccessException {
        String text = message.getText();
        BotCommonCommands commands = new BotCommonCommands();
        Method[] classMethods = commands.getClass().getDeclaredMethods();
        for (Method method : classMethods) {
            if (method.isAnnotationPresent(AppBotCommand.class)) {
                AppBotCommand command = method.getAnnotation(AppBotCommand.class);
                if (command.name().equals(text)) {
                    method.setAccessible(true);
                    String responseText = (String) method.invoke(commands);
                    if (responseText != null) {
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setChatId(message.getChatId());
                        sendMessage.setText(responseText);
                        return sendMessage;
                    }
                }
            }
        }
        return null;
    }

    /*private String runCommand(String text) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        for (int i = 0; i < commandClasses.length; i++) {
            Constructor<Class> constructor = Class.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            Class<?> instance = constructor.newInstance();
            Method[] classMethods = instance.getClass().getDeclaredMethods();
            for (Method method : classMethods) {
                if (method.isAnnotationPresent(AppBotCommand.class)) {
                    AppBotCommand command = method.getAnnotation(AppBotCommand.class);
                    if (command.name().equals(text)) {
                        method.setAccessible(true);
                        return (String) method.invoke(instance);
                    }
                }
            }
        }

        return null;
    }*/


    /*@Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasPhoto()) {
            String chatId = update.getMessage().getChatId().toString();
            try {
                photoPaths = new ArrayList<>(PhotoMessageUtils.savePhotos(getFilesByMessage(update.getMessage()), Main.getToken()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            SendMessage chooseMessage = chooseImageFilter(chatId);
            try {
                execute(chooseMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        } else if (update.hasCallbackQuery()) {
            String callData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            for (String path : photoPaths) {
                try {
                    PhotoMessageUtils.processingImage(path, callData);
                    execute(preparePhotoMessage(path, chatId));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }*/


    private static SendMessage chooseImageFilter(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Выберите фильтр для обработки фото: ");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInLine1 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Сепия");
        inlineKeyboardButton1.setCallbackData("СЕПИЯ");
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton2.setText("чб");
        inlineKeyboardButton2.setCallbackData("ЧБ");
        rowInLine1.add(inlineKeyboardButton1);
        rowInLine1.add(inlineKeyboardButton2);

        List<InlineKeyboardButton> rowInLine2 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
        inlineKeyboardButton3.setText("Только синий");
        inlineKeyboardButton3.setCallbackData("ТОЛЬКО СИНИЙ");
        InlineKeyboardButton inlineKeyboardButton4 = new InlineKeyboardButton();
        inlineKeyboardButton4.setText("Только красный");
        inlineKeyboardButton4.setCallbackData("ТОЛЬКО КРАСНЫЙ");
        rowInLine2.add(inlineKeyboardButton3);
        rowInLine2.add(inlineKeyboardButton4);

        rowsInline.add(rowInLine1);
        rowsInline.add(rowInLine2);

        markupInline.setKeyboard(rowsInline);
        sendMessage.setReplyMarkup(markupInline);

        return sendMessage;

    }

        private List<File> getFilesByMessage(Message message) {
        List<PhotoSize> photoSizes = message.getPhoto();
        ArrayList<File> files = new ArrayList<>();
        for (PhotoSize photoSize : photoSizes) {
            final String fileId = photoSize.getFileId();
            try {
                files.add(sendApiMethod(new GetFile(fileId)));
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
        return files;
    }
    private SendPhoto preparePhotoMessage(String localPath, long chatId) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setReplyMarkup(getKeyboard());
        InputFile newFile = new InputFile();
        newFile.setMedia(new java.io.File(localPath));
        sendPhoto.setPhoto(newFile);
        return sendPhoto;
    }

    public Bot(String botToken) {
        super(botToken);
    }

    @Override
    public String getBotUsername() {
        return "bulatjavabot";
    }

    private ReplyKeyboardMarkup getKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        ArrayList<KeyboardRow> allKeyboardRows = new ArrayList<>();
        //allKeyboardRows.addAll(getKeyboardRows(FilterOperation.class));
        allKeyboardRows.addAll(getKeyboardRows(BotCommonCommands.class));

        replyKeyboardMarkup.setKeyboard(allKeyboardRows);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        return replyKeyboardMarkup;
    }

    private ArrayList<KeyboardRow> getKeyboardRows(Class someClass) {
        Method[] classMethods = someClass.getMethods();
        ArrayList<AppBotCommand> commands = new ArrayList<>();
        for (Method method : classMethods) {
            if (method.isAnnotationPresent(AppBotCommand.class)) {
                commands.add(method.getAnnotation(AppBotCommand.class));
            }
        }
        ArrayList<KeyboardRow> keyboardRows = new ArrayList<>();
        int columnCount = 3;
        int rowsCount = commands.size() / columnCount + ((commands.size() % columnCount == 0) ? 0 : 1);
        for (int rowIndex = 0; rowIndex < rowsCount ; rowIndex++) {
            KeyboardRow row = new KeyboardRow();
            for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                int index = rowIndex * columnCount + columnIndex;
                if (index >= commands.size()) continue;
                AppBotCommand command = commands.get(index);
                KeyboardButton keyboardButton = new KeyboardButton(command.name());
                row.add(keyboardButton);
            }
            keyboardRows.add(row);
        }
        return keyboardRows;
    }


}
