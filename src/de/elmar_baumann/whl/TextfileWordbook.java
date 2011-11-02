package de.elmar_baumann.whl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Scanner;
import java.util.Set;

/**
 * @author Elmar Baumann
 */
public final class TextfileWordbook {

    private boolean read;
    private final Set<String> words = new HashSet<String>(250);
    private final List<TextConverter> converters = new ArrayList<TextConverter>();

    /**
     * Adds a converter for earch read word. Multiple converters are called in
     * the order as they has been added.
     *
     * @param converter converter. Default: Words will not be converted.
     */
    public synchronized void addConverter(TextConverter converter) {
        if (converter == null) {
            throw new NullPointerException("converter == null");
        }

        converters.add(converter);
    }

    /**
     * Reads the words from a text file, each line contains a single word.
     *
     * @param file text file
     * @throws  FileNotFoundException
     */
    public synchronized void read(File file) throws FileNotFoundException {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        words.clear();

        FileInputStream fis = new FileInputStream(file);
        Scanner scanner = new Scanner(fis, Properties.TEXT_ENCODING);

        try {
            while (scanner.hasNextLine()) {
                words.add(convert(scanner.nextLine().trim()));
            }

            read = true;
        } finally {
            scanner.close();

            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ex) {
                    Logger.getLogger(TextfileWordbook.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private String convert(String word) {
        String convertedWord = word;

        for (TextConverter converter : converters) {
            convertedWord = converter.convert(word);
        }

        return convertedWord;
    }

    /**
     * Returns the read words.
     *
     * @return Words
     * @throws IllegalStateException if the file hasn't been read
     */
    public synchronized Set<String> getWords() {
        if (!read) {
            throw new IllegalStateException("File has not been read!");
        }

        return Collections.unmodifiableSet(words);
    }

    /**
     * Returns the count of words in this wordbook.
     *
     * @return word count
     */
    public synchronized int getWordCount() {
        return words.size();
    }
}
