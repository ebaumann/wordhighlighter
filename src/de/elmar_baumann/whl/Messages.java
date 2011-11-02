package de.elmar_baumann.whl;

import java.util.ResourceBundle;

import javax.swing.JOptionPane;

/**
 * @author Elmar Baumann
 */
public final class Messages {

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("de/elmar_baumann/whl/Bundle");

    public static void errorMessage(String bundleKey) {
        if (bundleKey == null) {
            throw new NullPointerException("bundleKey == null");
        }

        String title = BUNDLE.getString("ErrorMessage.Title");
        String msg = BUNDLE.getString(bundleKey);

        JOptionPane.showMessageDialog(null, msg, title, JOptionPane.ERROR_MESSAGE);
    }

    private Messages() {
    }
}
