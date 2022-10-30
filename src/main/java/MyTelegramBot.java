import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaVideo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class MyTelegramBot extends TelegramLongPollingBot {

    private MainController mainController;


    public MyTelegramBot() {
        mainController = new MainController(this);

    }

    @Override
    public String getBotUsername() {
        return "https://t.me/life_time_calculatebot";
    }

    @Override
    public String getBotToken() {
        return "5619367191:AAFCq5MlvE5CK-ddvACPeVPTu_V-tX9tu5s";
    }

    @Override
    public void onUpdateReceived(Update update) {

        try {

            if (update.hasMessage()) {

                Message message = update.getMessage();

                if (message.hasText()) {
                    mainController.handle(message);
                } else {
                    SendMessage sendMessage = new SendMessage();

                    sendMessage.setChatId(message.getChatId());
                    sendMessage.setText("Unknown");
                    send(sendMessage);

             
                }
            } else if (update.hasCallbackQuery()) {
                CallbackQuery callbackQuery = update.getCallbackQuery();
                Message message = callbackQuery.getMessage();
                String query = callbackQuery.getData();

                mainController.handelCallBack(message, query);

            }

        } catch (RuntimeException runtimeException) {
            runtimeException.getStackTrace();
        }


    }

    public void send(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }

    public void send(SendPhoto sms) {
        try {
            execute(sms);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }
    public void send(EditMessageText sms) {
        try {
            execute(sms);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }
    public void send(DeleteMessage sms) {
        try {
            execute(sms);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }
}
