package ru.maksirep.chatbot.other;

import ru.maksirep.chatbot.chatbot.helptools.TtsClass;

import java.io.File;

public class DirectoryMaker {

    public void makeDir () {
        File mainFolder = new File(ConstClass.MAIN_FOLDER_PATH);
        if (!mainFolder.exists()){
            mainFolder.mkdirs();
            new File(ConstClass.CHAT_VALUES_PATH).mkdirs();
            new File(ConstClass.DICTIONARY_PATH).mkdirs();
            new File(ConstClass.MAIN_VALUES_PATH).mkdirs();
            TtsClass ttsClass = new TtsClass();
            try {
                ttsClass.recordTheText(MessageConstClass.VOICE_COMMANDS_FIRE , "-1");
                ttsClass.recordTheText(MessageConstClass.VOICE_COMMANDS_POLICE, "-1");
                ttsClass.recordTheText(MessageConstClass.VOICE_COMMANDS_AMBULANCE, "-1");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
