package Indexer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextManipulator {
    protected static List<String> stopWords;

    protected static void loadStopWords() {
        try{
            BufferedReader reader = new BufferedReader(new FileReader("stopwords.txt"));
            stopWords = new Vector<String>();
            String word;
            while ((word = reader.readLine()) != null) {
                word = word.replaceAll("\\s*\\|.*", "");
                if(word.length() > 0){
                    stopWords.add(word);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> splitWords(String text) {
        List<String> wordsList = new <String>Vector();
        Pattern pattern = Pattern.compile("\\w+");
        Matcher match = pattern.matcher(text);
        while (match.find()) {
            wordsList.add(match.group());
        }
        return wordsList;
    }

    public static List<String> convertToLower(List<String> words) {
        for (int i = 0; i < words.size(); i++) {
            words.set(i, words.get(i).toLowerCase());
        }
        return words;
    }

    public static List<String> removeStopWords(List<String> words) {
        words.removeAll(stopWords);
        return words;
    }

    public static List<String> stemming(List<String> words) {
        Stemmer stemmer = new Stemmer();
        // stem words in the list
        for (int i = 0; i < words.size(); i++) {
            String stemmedWord = stemmer.Stemming(words.get(i)); //stem the word
            words.set(i, stemmedWord); //get the stemmed word
        }
        return words;
    }
}
