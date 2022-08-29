package ru.maksirep.chatbot.chatbot.helptools;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.json.JSONObject;
import ru.maksirep.chatbot.other.ConstClass;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PhotoReader {

    public String readPhoto (String chatId, String fileId) throws IOException {
        downloadFile(chatId, fileId);
        PhotoHelper photoHelper = new PhotoHelper();
        String pathForImprove = ConstClass.CHAT_VALUES_PATH + "/" + chatId + "/" + chatId + "_" + fileId + ".jpg";
        String pathForScan = ConstClass.CHAT_VALUES_PATH + "/" + chatId + "/" + chatId + "_" + fileId + ".jpg" + "improved.jpg";
        if (ImageIO.read(new File(pathForImprove)) == null)
            return "";
        photoHelper.photoImprovement(pathForImprove);
        java.io.File file = new File(pathForScan);
        Tesseract tesseract = new Tesseract();
        String text = "";
        try {
            tesseract.setDatapath("tessdata");
            tesseract.setLanguage("rus");
            text = tesseract.doOCR(file);
            Files.delete(Paths.get(pathForImprove));
            Files.delete(Paths.get(pathForScan));
        } catch (TesseractException e) {
            e.printStackTrace();
        }
        TextParser textParser = new TextParser();
        if (!text.equals("")) {
            System.out.println(text);
            return textParser.parse(text);
        } else {
            return text;
        }
    }

//TODO : Убрать паблик
    private void downloadFile (String chatId, String fileId) throws IOException {
        URL url = new URL("https://api.telegram.org/bot"+ ConstClass.BOT_TOKEN +"/getFile?file_id="+fileId);
        BufferedReader in = new BufferedReader(new InputStreamReader( url.openStream()));
        String res = in.readLine();
        JSONObject jresult = new JSONObject(res);
        JSONObject path = jresult.getJSONObject("result");
        String file_path = path.getString("file_path");
        URL download = new URL("https://api.telegram.org/file/bot" + ConstClass.BOT_TOKEN + "/" + file_path);
        FileOutputStream fos = new FileOutputStream(ConstClass.CHAT_VALUES_PATH + "/" + chatId + "/" + chatId + "_" + fileId + ".jpg");
        ReadableByteChannel rbc = Channels.newChannel(download.openStream());
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
    }
}
