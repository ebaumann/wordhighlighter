package de.elmar_baumann.whl;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;

/**
 * @author Elmar Baumann
 */
public class Main {

    public static void main(String[] args) {
        setSystemLookAndFeel();

        WordHighlighterFrame dlg = new WordHighlighterFrame();

        dlg.setVisible(true);
    }

    private static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Main() {
    }
}
