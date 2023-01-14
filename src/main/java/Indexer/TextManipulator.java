package Indexer;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextManipulator {
    protected static List<String> enStopWords;
    protected static List<String> deStopWords;
    public static HashMap<String, Integer> enCounts;
    public static HashMap<String, Integer> deCounts;

    public static void loadStopWords(){
        enStopWords = loadStopWordsLang("en_stopwords.txt");
        deStopWords = loadStopWordsLang("de_stopwords.txt");
    }

    public static List<String> loadStopWordsLang(String path) {
        List<String> stopWords = new Vector<String>();

        try{
            InputStream is = TextManipulator.class.getClassLoader().getResourceAsStream("en_stopwords.txt");

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            String word;
            while ((word = reader.readLine()) != null) {
                word = word.replaceAll("\\s*\\|.*", "");
                if(word.length() > 0){
                    stopWords.add(word);
                }
            }
            return stopWords;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stopWords;
    }

    public static void loadCountsWords(){
        enCounts = loadCountsWordsLang("en_counts.txt");
        deCounts = loadCountsWordsLang("de_counts.txt");
    }

    public static HashMap<String, Integer> loadCountsWordsLang(String path) {
        HashMap<String, Integer> map = new HashMap<>();

        try{
            InputStream is = TextManipulator.class.getClassLoader().getResourceAsStream(path);

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if(parts.length > 0){
                    map.put(parts[0], Integer.valueOf(parts[1]));
                }
            }
            return map;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
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

    public static List<String> convertToUpper(List<String> words) {
        for (int i = 0; i < words.size(); i++) {
            words.set(i, words.get(i).toUpperCase());
        }
        return words;
    }

    public static List<String> removeStopWords(List<String> words, String language) {
        switch (language){
            case "de":
                words.removeAll(deStopWords);
                break;
            case "en":
                words.removeAll(enStopWords);
                break;
        }
        return words;
    }

    public static List<String> stemming(List<String> words, String language) {
        Stemmer enStemmer = new Stemmer();
        Cistem deStemmer = new Cistem();
        // stem words in the list
        for (int i = 0; i < words.size(); i++) {
            String stemmedWord;

            if(language == "en"){
                stemmedWord = enStemmer.Stemming(words.get(i)); //stem the word
            } else {
                stemmedWord = deStemmer.stem(words.get(i)); //stem the word
            }
            words.set(i, stemmedWord); //get the stemmed word
        }
        return words;
    }

    public static String classify(List<String> words){
        List<String> upWords = convertToUpper(words);

        String allowedLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZÆØÅÄÖÜßÀÂÇÉÈÊËÎÏÔŒÙÛŸ";

        for (int i = 0; i < words.size(); i++) {
            upWords.set(i, words.get(i).replaceAll("[^" + allowedLetters + "]", ""));
        }

        Double deProb = computeLangProb(deCounts, upWords);
        Double enProb = computeLangProb(enCounts, upWords);

        if(deProb < enProb){
            return "en";
        } else {
            return "de";
        }
    }

    public static Double computeLangProb(HashMap<String, Integer> counts, List<String> words){
        Double prob = 0.0;

        for(int i=0; i < words.size(); i++){
            String word = words.get(i);

            Double count = 1.0;
            if(counts.get(word) != null){
                count = Double.valueOf(counts.get(word));
            }

            prob += Math.log(count);
        }

        return prob;
    }

    private static int minimum(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }

    public static int computeLevenshteinDistance(CharSequence lhs, CharSequence rhs) {
        int[][] distance = new int[lhs.length() + 1][rhs.length() + 1];

        for (int i = 0; i <= lhs.length(); i++)
            distance[i][0] = i;
        for (int j = 1; j <= rhs.length(); j++)
            distance[0][j] = j;

        for (int i = 1; i <= lhs.length(); i++)
            for (int j = 1; j <= rhs.length(); j++)
                distance[i][j] = minimum(
                        distance[i - 1][j] + 1,
                        distance[i][j - 1] + 1,
                        distance[i - 1][j - 1] + ((lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1));

        return distance[lhs.length()][rhs.length()];
    }
}
