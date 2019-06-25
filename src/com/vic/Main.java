package com.vic;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Scanner;

public class Main {

    private static Map<String, String> wordsMap;
    private static DictionaryAPI dictionary;

    public static void main(String[] args) {
        // The program takes in one or more fies as an input and returns a
        // ranking of words by frequency

        // Setup
        wordsMap = new HashMap<String, String>();
        String key = "f30afd54-7f02-4809-8633-99426f5c5d36";
        dictionary = new DictionaryAPI(key);
        for (String f : args) {
            File file = new File(f);
            if (file.exists() && !file.isDirectory()) {
                System.out.println("Reading " + file.getPath());
                read(file);
                printResults();
            }
        }
        System.out.println("Do you want to continue? Press any key to continue");
        Scanner input = new Scanner(System.in);
        String line = input.nextLine();
        System.out.println("You said:" + line);

    }

    static void read(File file) {
        String line;

        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferReader = new BufferedReader(fileReader);

            while ((line = bufferReader.readLine()) != null) {
                analyze(line);
            }
            bufferReader.close();
            fileReader.close();
        } catch (FileNotFoundException fnf) {
            System.out.println("Unable to open file" + file.getPath());
        } catch (IOException io) {
            System.out.println("Error reading file " + file.getPath());
        }
    }

    static void printResults() {
        Iterator i = wordsMap.keySet().iterator();
        while (i.hasNext()) {
            String key = (String) i.next();
            System.out.println(key + ":" + wordsMap.get(key));
        }
    }

    static void analyze(String line) {
        for (String word : line.split(" ")) {

            word = cleanWord(word);

            if (!wordsMap.containsKey(word)) {
                wordsMap.put(word, dictionary.define(word));
            }
        }
        ;

    }

    static String cleanWord(String word) {
        // remove whitespaces, characters
        return word.trim();
    }

}