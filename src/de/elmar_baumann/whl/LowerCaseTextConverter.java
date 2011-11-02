package de.elmar_baumann.whl;

/**
 * @author Elmar Baumann
 */
public final class LowerCaseTextConverter implements TextConverter {

    @Override
    public String convert(String text) {
        return text.toLowerCase();
    }
}
