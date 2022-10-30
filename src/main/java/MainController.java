import org.checkerframework.checker.units.qual.K;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.util.*;

public class MainController {
    private MyTelegramBot myTelegramBot;

    private HashMap<Long, Profile> userMap = new HashMap<>();
    private String[] monthList = {"January", "February", "March", "April", "May", "June", "July", "August", "September",
            "October", "November", "December"};


    public MainController(MyTelegramBot myTelegramBot) {
        this.myTelegramBot = myTelegramBot;
    }

    public void handle(Message message) {

        String text = message.getText();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());

        if (text.equals("/start")) {

            userMap.put(message.getChatId(), new Profile(message.getChatId(), UserStep.NAME));

            sendMessage.setText("Insmingni kirit bro");
            myTelegramBot.send(sendMessage);

        } else if (userMap.containsKey(message.getChatId())) {

            Profile profile = userMap.get(message.getChatId());

            if (userMap.get(message.getChatId()).getUserStep() == UserStep.NAME) {



                profile.setName(text);
                profile.setUserStep(UserStep.YEAR);

                sendMessage.setText("Choose your birth year!");
                sendMessage.setReplyMarkup(makeYearKeyBoard(1990));

                myTelegramBot.send(sendMessage);
            } else if (profile.getUserStep().equals(UserStep.MONTH)) {
                int month = getMonthIndex(text);
                if (month == -1) {
                    //
                    return;
                }
                profile.setMonth(month + 1);
                profile.setUserStep(UserStep.DAY);

                ReplyKeyboardRemove remove = new ReplyKeyboardRemove();
                remove.setRemoveKeyboard(true);

                sendMessage.setText("Ma'lumotlarni kriitshda davom eting!");
                sendMessage.setReplyMarkup(remove);

                myTelegramBot.send(sendMessage);
                sendMessage = new SendMessage();
                sendMessage.setChatId(message.getChatId());
                sendMessage.setText("Choose your birth day");

                sendMessage.setReplyMarkup(makeDayButton(profile)); //  day count as button list

                myTelegramBot.send(sendMessage);
            } else if (profile.getUserStep().equals(UserStep.CALCULATE)) {

                profile.setUserStep(UserStep.FINISH);
                sendMessage.setText(calculate(profile.getYear(), profile.getMonth(), profile.getDay(), profile.getName()));
                ReplyKeyboardRemove remove = new ReplyKeyboardRemove();
                remove.setRemoveKeyboard(true);
                sendMessage.setReplyMarkup(remove);

                myTelegramBot.send(sendMessage);

            }


//            sendMessage.setText("Welcome" + message.getText());
//            myTelegramBot.send(sendMessage);

        } else {

            sendMessage.setText("Mavjud emas");
        }


    }

    private String calculate(int year, int month, int day, String name) {


        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.M.dd");

        LocalDate ldStart = LocalDate.parse(year + "." + month + "." + day, dateTimeFormatter);
        System.out.println(ldStart);
        LocalDate ldEnd = LocalDate.now();


        long y = ldStart.until(ldEnd, ChronoUnit.YEARS);

        long m = ldStart.until(ldEnd, ChronoUnit.MONTHS) % 12;

        int maxDay = getMaxDay(month,day);
        int d = 0;

        Calendar calendar = Calendar.getInstance();

        if (calendar.get(Calendar.DAY_OF_MONTH) > day) {
            d = calendar.get(Calendar.DAY_OF_MONTH) - day;
        } else if (calendar.get(Calendar.DAY_OF_MONTH) < day) {
            d = maxDay - day + calendar.get(Calendar.DAY_OF_MONTH);
        }

        long week = d / 7;
        d = d % 7;

        String text = name + " siz " + y + " yil " + m + " oy " + week + " hafta " + d + " kun " + calendar.get(Calendar.HOUR_OF_DAY) + " soat " +
                calendar.get(Calendar.MINUTE) + " daqiqa " +
                calendar.get(Calendar.SECOND) + " soniya " + calendar.get(Calendar.MILLISECOND) + " ms yashagansiz";

        return text;
    }

    private InlineKeyboardMarkup makeDayButton(Profile profile) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> keyboard = new LinkedList<>();
        int year = profile.getYear();
        int month = profile.getMonth();

        int maxDay = getMaxDay(month, year);

        int count = 1;

        int maxRow = maxDay == 31 ? 6 : 5;
        for (int i = 0; i < maxRow; i++) {
            List<InlineKeyboardButton> row = new LinkedList<>();
            for (int j = 0; j < 6 && count != maxDay + 1; j++) {
                row.add(button(count + "", "/day/" + count));
                count++;
            }
            keyboard.add(row);
        }
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;

    }

    private int getMaxDay(int month, int year) {
        int maxDay = 0;
        if (month == 2) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.M");
            LocalDate localDate = LocalDate.parse(year + "." + month, dateTimeFormatter);
            if (localDate.isLeapYear()) {
                maxDay = 29;
            } else {
                maxDay = 28;
            }
        } else if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
            maxDay = 31;
        } else {
            maxDay = 30;
        }
        return maxDay;
    }

    public void handelCallBack(Message message, String text) {
        String[] arr = text.split("/");
        if (arr[1].equals("year")) {
            switch (arr[2]) {
                case "prev":
                    int year = Integer.parseInt(arr[3]);

                    EditMessageText editMessageText = new EditMessageText();
                    editMessageText.setChatId(message.getChatId());
                    editMessageText.setMessageId(message.getMessageId());
                    editMessageText.setText("Choose your birth year!");
                    editMessageText.setReplyMarkup(makeYearKeyBoard(year - 16));

                    myTelegramBot.send(editMessageText);
                    break;
                case "next":
                    int year2 = Integer.parseInt(arr[3]);

                    EditMessageText editMessageText2 = new EditMessageText();
                    editMessageText2.setChatId(message.getChatId());
                    editMessageText2.setMessageId(message.getMessageId());
                    editMessageText2.setText("Choose your birth year!");
                    editMessageText2.setReplyMarkup(makeYearKeyBoard(year2));

                    myTelegramBot.send(editMessageText2);

                    break;
                default:
                    int y = Integer.parseInt(arr[2]);
                    Profile profile = userMap.get(message.getChatId());
                    profile.setYear(y);
                    profile.setUserStep(UserStep.MONTH);

                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setText("Choose your  birth month");
                    sendMessage.setChatId(message.getChatId());
                    sendMessage.setReplyMarkup(makeMonthButton());
                    myTelegramBot.send(sendMessage);

            }
        } else if (arr[1].equals("day")) {

            int day = Integer.parseInt(arr[2]);
            Profile profile = userMap.get(message.getChatId());
            profile.setDay(day);
            profile.setUserStep(UserStep.CALCULATE);

            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setMessageId(message.getMessageId());
            deleteMessage.setChatId(message.getChatId());

            myTelegramBot.send(deleteMessage);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(message.getChatId());
            sendMessage.setReplyMarkup(calculateButton());
            sendMessage.setText("Click Calculate");

            myTelegramBot.send(sendMessage);
        }

    }

    private ReplyKeyboardMarkup calculateButton() {
        KeyboardButton keyboardButton = new KeyboardButton("CALCULATE");
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(keyboardButton);

        List<KeyboardRow> keyboard = new LinkedList<>();
        keyboard.add(keyboardRow);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup makeMonthButton() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> list = new ArrayList<>();
        int index = 0;
        for (int i = 0; i < 3; i++) {
            KeyboardRow row = new KeyboardRow();
            for (int j = 0; j < 4; j++) {
                KeyboardButton btn = new KeyboardButton();
                btn.setText(monthList[index++]);
                row.add(btn);
            }
            list.add(row);
        }
        replyKeyboardMarkup.setKeyboard(list);

        return replyKeyboardMarkup;
    }

    public InlineKeyboardMarkup makeYearKeyBoard(int year) {


        int initYear = year;

        List<List<InlineKeyboardButton>> rowList = new LinkedList<>();
        for (int i = 0; i < 4; i++) {
            List<InlineKeyboardButton> row = new LinkedList<>();
            for (int j = 0; j < 4; j++) {

                row.add(button(String.valueOf(year), "/year/" + year));
                year++;
            }
            rowList.add(row);

        }

        List<InlineKeyboardButton> lasRow = new LinkedList<>();


        lasRow.add(button("<<", "/year/prev/" + initYear));
        lasRow.add(button(">>", "/year/next/" + year));
        rowList.add(lasRow);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }

    public InlineKeyboardButton button(String text, String callBackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callBackData);
        return button;

    }

    public int getMonthIndex(String month) {
        int index = 0;
        for (String s : monthList) {
            if (s.equals(month)) {
                return index;
            }
            index++;
        }
        return -1;
    }
}
