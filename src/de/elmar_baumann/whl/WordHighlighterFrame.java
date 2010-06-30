/*
 * @(#)WordHighlighterFrame.java    Created on 2010-06-25
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

import java.awt.Image;
import java.io.File;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 * Dialog with a {@link WordHighlighterPanel} and a menu for operations such as
 * reading a text file, displaying help and exiting the application.
 *
 * @author Elmar Baumann
 */
public class WordHighlighterFrame extends javax.swing.JFrame {
    private static final long serialVersionUID = 1L;
    private static final String KEY_WIDTH = "WordHighlighterFrame.Width";
    private static final String KEY_HEIGHT = "WordHighlighterFrame.Height";
    private static final String KEY_X = "WordHighlighterFrame.X";
    private static final String KEY_Y = "WordHighlighterFrame.Y";
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
                                                 "de/elmar_baumann/whl/Bundle");

    public WordHighlighterFrame() {
        initComponents();
        panel.addContentChangeListener(new TextfileDisplayer());
    }

    private class TextfileDisplayer implements ContentChangeListener {

        public void textFileRead(File file) {
            setTitle(Properties.APP_NAME + " - " + file.getName());
        }

        public void contentChanged() {
            setTitle(Properties.APP_NAME);
        }
    }

    @Override
    public List<Image> getIconImages() {
        return Arrays.asList(getIcon("book.png"), getIcon("book32.png"),
                             getIcon("book48.png"));
    }

    private Image getIcon(String name) {
        return new ImageIcon(getClass().getResource("/de/elmar_baumann/whl/"
                             + name)).getImage();
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            readFromPrefs();
        } else {
            writeToPrefs();
        }
        super.setVisible(visible);
    }

    private void readFromPrefs() {
        setSizeAndLocation();
        panel.readFromPrefs();
    }

    private void writeToPrefs() {
        writeSizeAndLocation();
        panel.writeToPrefs();
    }

    private void writeSizeAndLocation() {
        try {
            Preferences prefs =
                    Preferences.userNodeForPackage(WordHighlighterFrame.class);

            prefs.putInt(KEY_X, getX());
            prefs.putInt(KEY_Y, getY());
            prefs.putInt(KEY_WIDTH, getHeight());
            prefs.putInt(KEY_HEIGHT, getHeight());
        } catch (Exception ex) {
            Logger.getLogger(WordHighlighterFrame.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
    }

    private void setSizeAndLocation() {
        try {
            Preferences prefs =
                    Preferences.userNodeForPackage(WordHighlighterFrame.class);

            int x     = prefs.getInt(KEY_X, -1);
            int y     = prefs.getInt(KEY_Y, -1);
            int width = prefs.getInt(KEY_WIDTH, -1);
            int height = prefs.getInt(KEY_HEIGHT, -1);

            if (x >= 0 && y >= 0 && width > 0 && height > 0) {
                setBounds(x, y, width, height);
            } else {
                setLocationRelativeTo(null);
            }

        } catch (Exception ex) {
            Logger.getLogger(WordHighlighterFrame.class.getName()).log(
                    Level.SEVERE, null, ex);
            setLocationRelativeTo(null);
        }
    }

    private void quit() {
        setVisible(false);
        dispose();
    }

    private void displayHelp() {
        String title = MessageFormat.format(BUNDLE.getString(
                      "WordHighlighterFrame.Help.Title"), Properties.APP_NAME);
        String msg   = MessageFormat.format(
                      BUNDLE.getString("WordHighlighterFrame.Help.Text"),
                      Properties.APP_NAME);

        JOptionPane.showMessageDialog(this, msg, title,
                                      JOptionPane.PLAIN_MESSAGE);
    }

    private void displayAbout() {
        String title = MessageFormat.format(BUNDLE.getString(
                     "WordHighlighterFrame.About.Title"), Properties.APP_NAME);
        String msg   = MessageFormat.format(BUNDLE.getString(
                    "WordHighlighterFrame.About.Text"), Properties.APP_NAME,
                    Properties.APP_VERSION);

        JOptionPane.showMessageDialog(this, msg, title,
                                      JOptionPane.INFORMATION_MESSAGE);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel = new de.elmar_baumann.whl.WordHighlighterPanel();
        menuBar = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        menuItemReadTextfile = new javax.swing.JMenuItem();
        sep1 = new javax.swing.JPopupMenu.Separator();
        menuItemExit = new javax.swing.JMenuItem();
        menuHelp = new javax.swing.JMenu();
        menuItemHelp = new javax.swing.JMenuItem();
        menuItemAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(Properties.APP_NAME);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/elmar_baumann/whl/Bundle"); // NOI18N
        menuFile.setText(bundle.getString("WordHighlighterFrame.menuFile.text")); // NOI18N

        menuItemReadTextfile.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        menuItemReadTextfile.setText(bundle.getString("WordHighlighterFrame.menuItemReadTextfile.text")); // NOI18N
        menuItemReadTextfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemReadTextfileActionPerformed(evt);
            }
        });
        menuFile.add(menuItemReadTextfile);
        menuFile.add(sep1);

        menuItemExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        menuItemExit.setText(bundle.getString("WordHighlighterFrame.menuItemExit.text")); // NOI18N
        menuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemExitActionPerformed(evt);
            }
        });
        menuFile.add(menuItemExit);

        menuBar.add(menuFile);

        menuHelp.setText(bundle.getString("WordHighlighterFrame.menuHelp.text")); // NOI18N

        menuItemHelp.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        menuItemHelp.setText(bundle.getString("WordHighlighterFrame.menuItemHelp.text")); // NOI18N
        menuItemHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemHelpActionPerformed(evt);
            }
        });
        menuHelp.add(menuItemHelp);

        menuItemAbout.setText(bundle.getString("WordHighlighterFrame.menuItemAbout.text")); // NOI18N
        menuItemAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemAboutActionPerformed(evt);
            }
        });
        menuHelp.add(menuItemAbout);

        menuBar.add(menuHelp);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, 457, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        quit();
    }//GEN-LAST:event_formWindowClosing

    private void menuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemExitActionPerformed
        quit();
    }//GEN-LAST:event_menuItemExitActionPerformed

    private void menuItemHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemHelpActionPerformed
        displayHelp();
    }//GEN-LAST:event_menuItemHelpActionPerformed

    private void menuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemAboutActionPerformed
        displayAbout();
    }//GEN-LAST:event_menuItemAboutActionPerformed

    private void menuItemReadTextfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemReadTextfileActionPerformed
        panel.readTextfile();
    }//GEN-LAST:event_menuItemReadTextfileActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                WordHighlighterFrame dialog = new WordHighlighterFrame();
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenu menuHelp;
    private javax.swing.JMenuItem menuItemAbout;
    private javax.swing.JMenuItem menuItemExit;
    private javax.swing.JMenuItem menuItemHelp;
    private javax.swing.JMenuItem menuItemReadTextfile;
    private de.elmar_baumann.whl.WordHighlighterPanel panel;
    private javax.swing.JPopupMenu.Separator sep1;
    // End of variables declaration//GEN-END:variables
}
