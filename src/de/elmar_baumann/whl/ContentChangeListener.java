package de.elmar_baumann.whl;

import java.io.File;

/**
 * @author Elmar Baumann
 */
public interface ContentChangeListener {

    public void textFileRead(File file);

    public void contentChanged();
}
