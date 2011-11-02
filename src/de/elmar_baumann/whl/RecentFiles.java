package de.elmar_baumann.whl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class RecentFiles {

    private static final int ENTRY_LIMIT = 50;
    private final int maxEntries;
    private final JMenu menu;
    private final Set<RecentFileListener> listeners = new CopyOnWriteArraySet<RecentFileListener>();
    private static final String KEY_PREFIX = "RecentFiles.";

    public RecentFiles(int maxEntries, JMenu menu) {
        if ((maxEntries < 1) || (maxEntries > ENTRY_LIMIT)) {
            throw new IllegalArgumentException("Max entries of " + maxEntries + " not in range 1 - " + ENTRY_LIMIT);
        }

        if (menu == null) {
            throw new NullPointerException("menuItem == null");
        }

        this.maxEntries = maxEntries;
        this.menu = menu;
        setMenu();
    }

    private void setMenu() {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                menu.removeAll();

                for (File file : readStoredFiles()) {
                    JMenuItem item = new JMenuItem(file.getName());

                    item.addActionListener(new MenuItemListener(file, item));
                    menu.add(item);
                }
            }
        });
    }

    private LinkedList<File> readStoredFiles() {
        LinkedList<File> files = new LinkedList<File>();
        int index = 0;
        boolean fileStored = false;

        try {
            Preferences prefs =
                    Preferences.userNodeForPackage(RecentFiles.class);

            do {
                String path = prefs.get(KEY_PREFIX + Integer.toString(index++), null);

                fileStored = path != null;

                if (fileStored && !path.trim().isEmpty()) {
                    File file = new File(path.trim());

                    if (file.exists()) {
                        files.add(file);
                    }
                }
            } while (fileStored && (index < maxEntries));
        } catch (Exception ex) {
            Logger.getLogger(RecentFiles.class.getName()).log(Level.SEVERE,
                    null, ex);
        }

        return files;
    }

    private void storeFiles(Collection<? extends File> files) {
        assert files.size() <= ENTRY_LIMIT;

        try {
            Preferences prefs =
                    Preferences.userNodeForPackage(RecentFiles.class);
            int index = 0;

            for (File file : files) {
                prefs.put(KEY_PREFIX + Integer.toString(index++),
                        file.getAbsolutePath());
            }
        } catch (Exception ex) {
            Logger.getLogger(RecentFiles.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
    }

    public void addListener(RecentFileListener listener) {
        listeners.add(listener);
    }

    public void removeListener(RecentFileListener listener) {
        listeners.remove(listener);
    }

    private void notifySelected(File file) {
        for (RecentFileListener l : listeners) {
            l.selected(file);
        }
    }

    public void setMostRecentFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        LinkedList<File> files = readStoredFiles();

        files.remove(file);    // Avoiding duplicated files

        if (files.size() >= maxEntries) {
            files.removeLast();
        }

        files.addFirst(file);
        storeFiles(files);
        setMenu();
    }

    private class MenuItemListener implements ActionListener {

        private final File file;
        private final JMenuItem menuItem;

        private MenuItemListener(File file, JMenuItem menuItem) {
            this.file = file;
            this.menuItem = menuItem;
        }

        public void actionPerformed(ActionEvent e) {
            if (file.exists()) {
                notifySelected(file);
            } else {
                menu.remove(menuItem);
                Messages.errorMessage("RecentFiles.Error.FileNotExists");
            }
        }
    }
}
