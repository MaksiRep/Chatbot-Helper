package ru.maksirep.chatbot.chatbot.helptools;

import ru.maksirep.chatbot.other.ConstClass;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.*;

public class TextParser {

    public String parse(String inputString) {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        ArrayList<Future<String>> resultList = new ArrayList<>();
        String[] str = inputString.split(" ");
        String[] stringForParse = firstStringFix(str);
        int stringForParseLength = stringForParse.length;
        for (int j = 0; j < stringForParseLength; j++) {
            Callable<String> worker = new LivenshtainParsing(stringForParse[j]);
            Future<String> result = executor.submit(worker);
            resultList.add(result);
        }
        StringBuilder stringBuilder = new StringBuilder();
        String punctuations = "-()\n";
        for (Future<String> result : resultList) {
            try {
                if (result != null && !result.get().equals("")) {
                    if (punctuations.contains(result.get())) {
                        if (stringBuilder.length() != 0 && !result.get().equals("(") && stringBuilder.charAt(stringBuilder.length() - 1) == ' ')
                            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                        stringBuilder.append(result.get());
                    } else {
                        stringBuilder.append(result.get()).append(" ");
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();

        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    private String[] firstStringFix(String[] stringsArray) {
        ArrayList<String> arrayList = new ArrayList<>();
        int stringArrayLength = stringsArray.length;
        int stringArrayElemLength;
        for (int i = 0; i < stringArrayLength; i++) {
            if (stringsArray[i].contains("\n") || stringsArray[i].contains("-")
                    || stringsArray[i].contains("(") || stringsArray[i].contains(")")) {
                StringBuilder stringBuilder = new StringBuilder();
                stringArrayElemLength = stringsArray[i].length();
                for (int j = 0; j < stringArrayElemLength; j++) {
                    Character ch = stringsArray[i].charAt(j);
                    if (ch == '\n' || ch == '-' || ch == '(' || ch == ')') {
                        arrayList.add(stringBuilder.toString());
                        arrayList.add(ch.toString());
                        stringBuilder.delete(0, stringBuilder.length());
                    } else {
                        stringBuilder.append(ch);
                    }
                }
                if (!arrayList.isEmpty()) {
                    arrayList.add(stringBuilder.toString());
                    stringBuilder.delete(0, stringBuilder.length());
                }
            } else {
                arrayList.add(stringsArray[i]);
            }
        }
        return arrayList.toArray(new String[0]);
    }


    private class LivenshtainParsing implements Callable<String> {

        private String word;

        private int minMetric = 4;

        public LivenshtainParsing(String word) {
            this.word = word;
        }

        @Override
        public String call() throws Exception {

            String checkedWord = checkTheWord(word);

            if (checkedWord.equals("")) {
                return "";
            }
            if (checkedWord.contains("digit")) {
                return checkedWord.substring(5);
            }
            if (checkedWord.contains("punctuation")) {
                return checkedWord.substring(11);
            }
            char lastCh = checkedWord.charAt(checkedWord.length() - 1);
            char firstCh = checkedWord.charAt(0);
            StringBuilder stringBuilder = new StringBuilder();
            if (!Character.isLetter(lastCh)) {
                stringBuilder.insert(0, checkedWord).deleteCharAt(checkedWord.length() - 1);
                checkedWord = stringBuilder.toString();
                stringBuilder.delete(0, stringBuilder.length());
            }
            String endString = findDictWord(checkedWord, 0);
            if (minMetric == 3) {
                endString = findDictWord(checkedWord, 1);
            }
            if (minMetric == 4) {
                return "";
            }
            if (!Character.isLetter(lastCh)) {
                endString = endString + lastCh;
            }
            if (!Character.isLetterOrDigit(firstCh)) {
                endString = firstCh + endString;
            }
            if (Character.isUpperCase(firstCh)) {
                endString = Character.toUpperCase(endString.charAt(0)) + endString.substring(1).toLowerCase();
            }

            return endString;
        }

        private String findDictWord (String checkedWord, int charPos) throws IOException {
            String endString = new String(checkedWord);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(ConstClass.DICTIONARY_PATH + "/" + Character.toUpperCase(checkedWord.charAt(charPos)) + "russian.txt"));
            for (String line; (line = bufferedReader.readLine()) != null; ) {
                int newMetric = livenshtain(checkedWord.toLowerCase(Locale.ROOT), line);
                if (newMetric < minMetric || (minMetric == -1)) {
                    minMetric = newMetric;
                    endString = line;
                }
            }
            return endString;
        }

        private String checkTheWord(String word) {

            String punctuations = ".,!?-:()\n";
            String cValues = "мсгнк";
            if (word.equals(""))
                return "";
            char firstChar = word.charAt(0);
            if (Character.toUpperCase(firstChar) == 'Ь' || Character.toUpperCase(firstChar) == 'Ъ')
                return "";
            if (Character.isDigit(firstChar)) {
                for (int i = 0; i < word.length(); i++) {
                    char ch = word.charAt(i);
                    boolean cValuesContains = cValues.contains(Character.toString(ch));
                    if (!Character.isDigit(ch) && ch != '.' && ch != ',' && !cValuesContains)
                        return "";
                }
                return "digit" + word;
            } else {
                boolean punctuationContains = punctuations.contains(Character.toString(firstChar));
                if (!Character.isLetterOrDigit(firstChar)) {
                    if (punctuationContains) {
                        if ((word.length() == 1)) {
                            return "punctuation" + word;
                        } else {
                            return "";
                        }
                    } else {
                        return "";
                    }
                }
            }

            return word;
        }

        private static int livenshtain(String firstString, String secondString) {
            int[][] mat = new int[firstString.length() + 1][secondString.length() + 1];

            for (int i = 0; i <= firstString.length(); i++) {
                for (int j = 0; j <= secondString.length(); j++) {
                    if (i == 0) {
                        mat[i][j] = j;
                    } else if (j == 0) {
                        mat[i][j] = i;
                    } else {
                        mat[i][j] = findMin(mat[i - 1][j - 1] + costOfSubstitution(firstString.charAt(i - 1), secondString.charAt(j - 1)),
                                mat[i - 1][j] + 1,
                                mat[i][j - 1] + 1);
                    }
                }
            }

            return mat[firstString.length()][secondString.length()];
        }

        private static int costOfSubstitution(char firstLetter, char secondLetter) {
            return firstLetter == secondLetter ? 0 : 1;
        }

        private static int findMin(int... values) {
            return Arrays.stream(values)
                    .min().orElse(Integer.MAX_VALUE);
        }
    }
}
