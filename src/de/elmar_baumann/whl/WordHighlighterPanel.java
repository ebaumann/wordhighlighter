/*
 * @(#)WordHighlighterPanel.java    Created on 2010-06-25
 *
 * Copyright (C) 2010 by the Elmar Baumann <eb@elmar-baumann.de>.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package de.elmar_baumann.whl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.JFileChooser;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Panel with a text area highlighting words read from a wordbook.
 *
 * @author Elmar Baumann
 */
public class WordHighlighterPanel extends javax.swing.JPanel {
    private static final long serialVersionUID = 1L;
    private final TextHighlighter hl;
    private static final String KEY_WORDBOOK = "Wordbook";
    private static final String KEY_TEXTFILE_DIR = "TextfileDir";
    private static final String KEY_WORDBOOK_DIR = "WordbookDir";
    private static final ResourceBundle BUNDLE =
        ResourceBundle.getBundle("de/elmar_baumann/whl/Bundle");
    private File wordbookDir;
    private File textfileDir;
    private final Set<ContentChangeListener> contentChangeListeners =
            new CopyOnWriteArraySet<ContentChangeListener>();
    private boolean textfileRead;

    public WordHighlighterPanel() {
        initComponents();
        hl = new TextHighlighter(textArea, TextHighlighter.Option.IGNORE_CASE);
        readWordbookFromPrefs();
        textArea.getDocument().addDocumentListener(new ContentChangedListener());
    }

    public void addContentChangeListener(ContentChangeListener listener) {
        contentChangeListeners.add(listener);
    }

    public void removeContentChangeListener(ContentChangeListener listener) {
        contentChangeListeners.remove(listener);
    }

    private void notifyTextfileRead(File file) {
        for (ContentChangeListener l : contentChangeListeners) {
            l.textFileRead(file);
        }
    }

    private void notifyContentChanged() {
        for (ContentChangeListener l : contentChangeListeners) {
            l.contentChanged();
        }
    }

    private class ContentChangedListener implements DocumentListener {

        public void insertUpdate(DocumentEvent e) {
            checkNotify();
        }

        public void removeUpdate(DocumentEvent e) {
            checkNotify();
        }

        public void changedUpdate(DocumentEvent e) {
            checkNotify();
        }

        private void checkNotify() {
            if (!textfileRead) {
                notifyContentChanged();
            }
        }

    }

    private void readWordbookFromPrefs() {
        try {
            Preferences prefs  =
                    Preferences.userNodeForPackage(WordHighlighterPanel.class);
            String      wbPath = prefs.get(KEY_WORDBOOK, null);

            if (wbPath != null) {
                File wb = new File(wbPath);

                if (wb.exists() && wb.isFile()) {
                    readWordbook(wb);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(WordHighlighterPanel.class.getName()).log(
                             Level.SEVERE, null, ex);
            Messages.errorMessage("WordHighlighterPanel.Error.Preferences");
        }
    }

    private void readWordbook(File wb) {
        TextfileWordbook tfwb = new TextfileWordbook();
        try {
            tfwb.read(wb);
            Set<String> words = tfwb.getWords();
            hl.setHighlightWords(words,
                                 TextHighlighter.Convert.TO_LOWERCASE);
            labelWordbook.setText(wb.getName());
            addWordbookWordCount(words.size());
            Preferences.userNodeForPackage(WordHighlighterPanel.class).put(
                                           KEY_WORDBOOK, wb.getAbsolutePath());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WordHighlighterPanel.class.getName()).log(
                             Level.SEVERE, null, ex);
            Messages.errorMessage("WordHighlighterPanel.Error.ReadWordbook");
        }
    }

    private void addWordbookWordCount(int count) {
        String text = labelWordbook.getText();

        labelWordbook.setText(text + " (" + Integer.toString(count) + " W)");
    }

    private void readWordbook() {
        File selFile = selectTextfile(getWordbookDir());

        if (selFile != null) {
            wordbookDir = selFile.getParentFile();
            readWordbook(selFile);
        }
    }

    /**
     * Dislays a file chooser, reads the choosen text file into the thext area
     * and highlights words in the text.
     */
    public void readTextfile() {
        File selFile = selectTextfile(getTextfileDir());

        if (selFile != null) {
            InputStream is = null;
            Scanner     scanner = null;

            try {
                textfileDir = selFile.getParentFile();
                is          = new FileInputStream(selFile);
                scanner     = new Scanner(is, Properties.TEXT_ENCODING);

                StringBuilder text    = new StringBuilder();
                String        NL      = System.getProperty("line.separator");

                while (scanner.hasNextLine()){
                    text.append(scanner.nextLine()).append(NL);
                }

                textfileRead = true;
                textArea.setText(text.toString());
                notifyTextfileRead(selFile);
                textfileRead = false;
            } catch (Exception ex) {
                Logger.getLogger(WordHighlighterPanel.class.getName()).log(
                                 Level.SEVERE, null, ex);
                Messages.errorMessage("WordHighlighterPanel.Error.ReadTextfile");
            } finally {
                      scanner.close();
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ex) {
                        Logger.getLogger(WordHighlighterPanel.class.getName())
                                .log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
    
    private File getTextfileDir() {
        return textfileDir == null ? new File("") : textfileDir;
    }

    private File getWordbookDir() {
        return wordbookDir == null ? new File("") : wordbookDir;
    }

    private File selectTextfile(File currentDir) {
        JFileChooser fc = new JFileChooser(currentDir);

        fc.setMultiSelectionEnabled(false);
        fc.setDialogTitle(BUNDLE.getString("SelectTextfile.Title.Encoding"));

        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile();
        }

        return null;
    }

    /**
     * Does read persistent written fields, e.g. the wordbook or the directory
     * of the last opened text file.
     */
    public void readFromPrefs() {
        try {
            Preferences prefs =
                    Preferences.userNodeForPackage(WordHighlighterPanel.class);
            String      wbd   = prefs.get(KEY_WORDBOOK_DIR, null);
            String      tfd   = prefs.get(KEY_TEXTFILE_DIR, null);

            wordbookDir = new File(wbd == null ? "" : wbd);
            textfileDir = new File(tfd == null ? "" : tfd);

        } catch (Exception ex) {
            Logger.getLogger(WordHighlighterPanel.class.getName()).log(
                             Level.SEVERE, null, ex);
        }
    }

    /**
     * Writes all fields read by {@link #readFromPrefs()}.
     */
    public void writeToPrefs() {
        writeFileToPrefs(KEY_WORDBOOK_DIR, wordbookDir);
        writeFileToPrefs(KEY_TEXTFILE_DIR, textfileDir);
    }

    private void writeFileToPrefs(String key, File file) {
        try {
            Preferences prefs =
                    Preferences.userNodeForPackage(WordHighlighterPanel.class);

            if (file != null) {
                prefs.put(key, file.getAbsolutePath());
            }

        } catch (Exception ex) {
            Logger.getLogger(WordHighlighterPanel.class.getName()).log(
                             Level.SEVERE, null, ex);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelWordbookPrompt = new javax.swing.JLabel();
        labelWordbook = new javax.swing.JLabel();
        buttonReadWordbook = new javax.swing.JButton();
        scrollPane = new javax.swing.JScrollPane();
        textArea = new javax.swing.JTextArea();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/elmar_baumann/whl/Bundle"); // NOI18N
        labelWordbookPrompt.setText(bundle.getString("WordHighlighterPanel.labelWordbookPrompt.text")); // NOI18N

        labelWordbook.setText("-"); // NOI18N

        buttonReadWordbook.setText(bundle.getString("WordHighlighterPanel.buttonReadWordbook.text")); // NOI18N
        buttonReadWordbook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonReadWordbookActionPerformed(evt);
            }
        });

        textArea.setColumns(20);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        scrollPane.setViewportView(textArea);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scrollPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelWordbookPrompt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelWordbook, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonReadWordbook)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelWordbookPrompt)
                    .addComponent(labelWordbook)
                    .addComponent(buttonReadWordbook))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void buttonReadWordbookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonReadWordbookActionPerformed
        readWordbook();
    }//GEN-LAST:event_buttonReadWordbookActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonReadWordbook;
    private javax.swing.JLabel labelWordbook;
    private javax.swing.JLabel labelWordbookPrompt;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTextArea textArea;
    // End of variables declaration//GEN-END:variables
}
