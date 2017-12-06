package org.chemid.structure.exception;

/**
 * Exception class
 */
public class ChemIDStructureException extends Exception {
    /**
     * @param message
     * @param e
     */

    public ChemIDStructureException(String message, Exception e) {
        super(message, e);
    }
}
