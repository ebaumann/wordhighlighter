/*
 * @(#)Main.java    Created on 2010-06-25
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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;

/**
 * The {@link #main(java.lang.String[])} method starts this application.
 *
 * @author Elmar Baumann
 */
public class Main {
    public static void main(String[] args) {
        setSystemLookAndFeel();

        WordHighlighterDialog dlg = new WordHighlighterDialog(null, true);

        dlg.setVisible(true);
    }

    private static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Main() {}
}
