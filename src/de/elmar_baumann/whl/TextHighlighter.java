package de.elmar_baumann.whl;

import java.awt.Color;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Set;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

/**
 * @author Elmar Baumann
 */
public final class TextHighlighter implements DocumentListener {

    private final Highlighter hilit = new DefaultHighlighter();
    private Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
    private final JTextComponent tc;
    private final Set<String> hlWords = new HashSet<String>();
    private final Set<Option> options = EnumSet.noneOf(Option.class);
    private boolean inWord;
    private int wordStartIndex = -1;

    public enum Option { NO_OPTON, IGNORE_CASE,}

    public TextHighlighter(JTextComponent tc, Option... options) {
        if (tc == null) {
            throw new NullPointerException("tc == null");
        }

        this.tc = tc;
        this.options.addAll(Arrays.asList(options));
        tc.setHighlighter(hilit);
        tc.getDocument().addDocumentListener(this);
    }

    public synchronized void setHighlightColor(Color color) {
        if (color == null) {
            throw new NullPointerException("color == null");
        }

        painter = new DefaultHighlighter.DefaultHighlightPainter(color);
    }

    public enum Convert { NONE, TO_LOWERCASE,}

    /**
     * Sets the words to highlight and highlights the text of the text
     * component.
     * <p>
     * Only complete words will be highlighted or substrings starting at the
     * beginning of the word, not substrings within a word. E.g. if
     * <code>words</code> containing <code>"the"</code>, then the
     * <code>"the"</code> itself and in <code>"theology"</code> will
     * be highlighted, but <em>not</em> in <code>"leather"</code>).
     *
     * @param words   words
     * @param convert conversion <code>words</code>. If the option
     *                {@link Option#IGNORE_CASE} <code>words</code> contains
     *                words with uppercase characters, use
     *                {@link Convert#TO_LOWERCASE}
     */
    public synchronized void setHighlightWords(Set<String> words, Convert convert) {
        if (words == null) {
            throw new NullPointerException("words == null");
        }

        if (convert == null) {
            throw new NullPointerException("convert == null");
        }

        hlWords.clear();

        if (options.contains(Option.IGNORE_CASE) && convert.equals(Convert.TO_LOWERCASE)) {
            addAsLowercase(words);
        } else {
            hlWords.addAll(words);
        }

        highlight();
    }

    private synchronized void addAsLowercase(Set<String> wds) {
        for (String word : wds) {
            hlWords.add(word.toLowerCase());
        }
    }

    public synchronized void highlight() {
        highlight(0);
    }

    // Bug: Does only support adding words. Using offset 0 (zero) if words
    // were removed
    private synchronized void highlight(int offset) {
        if (offset < 1) {
            hilit.removeAllHighlights();
            wordStartIndex = -1;
        }

        String text = tc.getText();
        int len = text.length();
        String currentWord = "";

        for (int i = offset; i < len; i++) {
            inWord = Character.isLetterOrDigit(text.charAt(i));

            if (inWord && (wordStartIndex == -1)) {
                wordStartIndex = i;
            } else if (!inWord) {
                wordStartIndex = -1;
            }

            if (inWord) {
                currentWord = text.substring(wordStartIndex, i + 1);

                if (options.contains(Option.IGNORE_CASE)) {
                    currentWord = currentWord.toLowerCase();
                }

                if (hlWords.contains(currentWord)) {
                    try {
                        hilit.addHighlight(wordStartIndex, i + 1, painter);
                    } catch (BadLocationException ex) {
                        Logger.getLogger(TextHighlighter.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        highlight(0);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        highlight(0);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        highlight(0);
    }
}
