package utils;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class SomeFuncsForHomeWork {

    public static SendMessage IntroduceYourSelf (String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        String text = "I am photo processing TelegramBot. I was made by Bulat Salikhov.";
        sendMessage.setText(text);
        return sendMessage;
    }

}
