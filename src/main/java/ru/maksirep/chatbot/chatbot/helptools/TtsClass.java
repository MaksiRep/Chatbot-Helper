package ru.maksirep.chatbot.chatbot.helptools;

import com.voicerss.tts.*;
import ru.maksirep.chatbot.other.ConstClass;

import java.io.File;
import java.io.FileOutputStream;

public class TtsClass {

    public String recordTheText(String text, String chatId) throws Exception {

        if (chatId.equals("-1")) {
            return ConstClass.MAIN_VALUES_PATH + "/" + text + ".mp3";
        }
        VoiceProvider tts = new VoiceProvider("136ab37136254ab3a44c0bd6a2df5ded"); //API

        VoiceParameters params = new VoiceParameters(text, Languages.Russian);
        params.setCodec(AudioCodec.WAV);
        params.setFormat(AudioFormat.Format_44KHZ.AF_44khz_16bit_stereo);
        params.setBase64(false);
        params.setSSML(false);
        params.setRate(0);

        byte[] voice = tts.speech(params);

        FileOutputStream fos = new FileOutputStream(ConstClass.CHAT_VALUES_PATH + "/" + chatId + "/" + text + ".mp3");
        fos.write(voice, 0, voice.length);
        fos.flush();
        fos.close();
        return ConstClass.CHAT_VALUES_PATH + "/" + chatId + "/" + text + ".mp3";
    }

    public File getTTSFile (String command, String chatId) {
        File ttsFile =  new File(ConstClass.CHAT_VALUES_PATH + "/" + chatId + "/" + command + ".mp3");
        if (ttsFile.exists()) {
            return ttsFile;
        } else {
            try {
                recordTheText(command, chatId);
                return ttsFile;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
