package ru.maksirep.chatbot.chatbot.helptools;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.maksirep.chatbot.entity.ChatBot;
import ru.maksirep.chatbot.entity.ChatTtsButtons;
import ru.maksirep.chatbot.other.MessageConstClass;
import ru.maksirep.chatbot.service.ChatBotService;

import java.io.File;
import java.util.ArrayList;

public class KeyboardController {


    public ReplyKeyboardMarkup makeCommandsKeyboard(String chatId, ChatBotService chatBotService) {
        ChatBot chatBot = chatBotService.getChatBotEntity(chatId);
        if (chatBot != null) {
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
            replyKeyboardMarkup.setResizeKeyboard(true);
            replyKeyboardMarkup.setOneTimeKeyboard(false);
            ArrayList<KeyboardRow> keyboardRows = new ArrayList<>();
            switch (chatBot.getMenuPos()) {
                case (0) : {
                    KeyboardRow keyboardRow = new KeyboardRow();
                    keyboardRows.add(keyboardRow);
                    keyboardRow.add(new KeyboardButton(MessageConstClass.ABILITY_MESSAGE));
                    keyboardRow.add(new KeyboardButton(MessageConstClass.EMERGENCY_MESSAGE));
                    keyboardRow.add(new KeyboardButton(MessageConstClass.VOICE_COMMANDS_MESSAGE));
                    break;
                }
                case (1): {
                    KeyboardRow firstKeyboardRow = new KeyboardRow();
                    KeyboardRow secondKeyboardRow = new KeyboardRow();
                    KeyboardRow thirdKeyboardRow = new KeyboardRow();
                    KeyboardRow fourthKeyboardRow = new KeyboardRow();
                    keyboardRows.add(firstKeyboardRow);
                    keyboardRows.add(secondKeyboardRow);
                    keyboardRows.add(thirdKeyboardRow);
                    keyboardRows.add(fourthKeyboardRow);
                    firstKeyboardRow.add(new KeyboardButton(MessageConstClass.VOICE_COMMANDS_FIRE));
                    secondKeyboardRow.add(new KeyboardButton(MessageConstClass.VOICE_COMMANDS_POLICE));
                    thirdKeyboardRow.add(new KeyboardButton(MessageConstClass.VOICE_COMMANDS_AMBULANCE));
                    fourthKeyboardRow.add(new KeyboardButton(MessageConstClass.BACK_COMMAND));
                    break;
                }
                case 2: {
                    makeKeyboardRowsVoice(chatId, chatBotService, keyboardRows);
                    KeyboardRow backAddDelRows = new KeyboardRow();
                    backAddDelRows.add(new KeyboardButton(MessageConstClass.BACK_COMMAND));
                    backAddDelRows.add(new KeyboardButton(MessageConstClass.DELETE_VOICE_COMMAND));
                    backAddDelRows.add(new KeyboardButton(MessageConstClass.ADD_VOICE_COMMAND));
                    keyboardRows.add(backAddDelRows);
                    break;
                }
                case 4: {
                    makeKeyboardRowsVoice(chatId, chatBotService, keyboardRows);
                    KeyboardRow backRow = new KeyboardRow();
                    backRow.add(new KeyboardButton(MessageConstClass.BACK_COMMAND));
                    keyboardRows.add(backRow);
                }
            }
            replyKeyboardMarkup.setKeyboard(keyboardRows);
            return replyKeyboardMarkup;
        }
        return null;
    }

    private void makeKeyboardRowsVoice(String chatId, ChatBotService chatBotService, ArrayList<KeyboardRow> keyboardRows) {
        ArrayList<ChatTtsButtons> chatTtsButtons = chatBotService.getTtsCommands(chatId);
        int length = chatTtsButtons.size();
        for (int i = 0; i <= length/3; i ++) {
            KeyboardRow keyboardRow = new KeyboardRow();
            for (int j = 0; j < 3; j ++ ) {
                if (chatTtsButtons.size()!= 0 && (i * 3 + j < chatTtsButtons.size())) {
                    keyboardRow.add(new KeyboardButton(chatTtsButtons.get(i * 3 + j).getName()));
                } else {
                    break;
                }
            }
            if (keyboardRow.size() > 0) {
                keyboardRows.add(keyboardRow);
            }
        }
    }

    public String buttonController(String inputText, String chatId, ChatBotService chatBotService) {
        ChatBot chatBot = chatBotService.getChatBotEntity(chatId);
        switch (inputText) {
            case MessageConstClass.ABILITY_MESSAGE: {
                return MessageConstClass.BOT_ABILITY;
            }
            case MessageConstClass.EMERGENCY_MESSAGE: {
                chatBot.setMenuPos(1);
                chatBotService.updateChatBotEntity(chatBot, chatId);
                return MessageConstClass.EMERGENCY_ANSWER_MESSAGE;
            }
            case MessageConstClass.VOICE_COMMANDS_MESSAGE: {
                chatBot.setMenuPos(2);
                chatBotService.updateChatBotEntity(chatBot, chatId);
                return MessageConstClass.VOICE_COMMANDS_EDITOR_MESSAGE;
            }
            case MessageConstClass.BACK_COMMAND: {
                chatBot.setMenuPos(0);
                chatBotService.updateChatBotEntity(chatBot, chatId);
                return MessageConstClass.BACK_COMMAND;
            }
            case MessageConstClass.ADD_VOICE_COMMAND : {
                chatBot.setMenuPos(3);
                chatBotService.updateChatBotEntity(chatBot, chatId);
                return MessageConstClass.ADDING_VOICE_COMMAND;
            }
            case MessageConstClass.DELETE_VOICE_COMMAND : {
                chatBot.setMenuPos(4);
                chatBotService.updateChatBotEntity(chatBot, chatId);
                return MessageConstClass.DELETING_VOICE_COMMAND;
            }
        }
        return "";
    }

    public File buttonVoiceController (String inputText, String chatId, ChatBotService chatBotService) {
        ChatBot chatBot = chatBotService.getChatBotEntity(chatId);
        int menuPos = chatBot.getMenuPos();
        TtsClass ttsClass = new TtsClass();
        switch (menuPos) {
            case (1) : {
                if (inputText.equals(MessageConstClass.VOICE_COMMANDS_AMBULANCE) ||
                        inputText.equals(MessageConstClass.VOICE_COMMANDS_FIRE) ||
                        inputText.equals(MessageConstClass.VOICE_COMMANDS_POLICE)) {
                    return ttsClass.getTTSFile(inputText, "MainValues");
                }
                break;
            }
            case (2) : {
                ArrayList<ChatTtsButtons> chatTtsButtonsList = chatBotService.getTtsCommands(chatId);
                for (ChatTtsButtons value : chatTtsButtonsList) {
                    if (inputText.equals(value.getName())) {
                        return ttsClass.getTTSFile(inputText, chatBot.getChatId());
                    }
                }
            }
        }
        return null;
    }

    public String addRemoveVoiceCommands (String inputText, String chatId, ChatBotService chatBotService) {
        ChatBot chatBot = chatBotService.getChatBotEntity(chatId);
        int menuPos = chatBot.getMenuPos();
        switch (menuPos) {
            case (3) : {
                String res = chatBotService.addTtsCommands(inputText, chatId);
                chatBot.setMenuPos(2);
                chatBotService.updateChatBotEntity(chatBot, chatId);
                return res;
            }
            case (4) : {
                ArrayList<ChatTtsButtons> chatTtsButtonsList = chatBotService.getTtsCommands(chatId);
                for (ChatTtsButtons value : chatTtsButtonsList) {
                    if (inputText.equals(value.getName())) {
                        String res = chatBotService.deleteTtsCommands(inputText, chatId);
                        chatBot.setMenuPos(2);
                        chatBotService.updateChatBotEntity(chatBot, chatId);
                        return res;
                    }
                }
            }
        }
        return "";
    }
}
