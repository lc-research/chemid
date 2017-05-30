package org.chemid.structure.exception;

/**
 * Exception class
 */
public class ChemIDException extends Exception {
    /**
     * @param message
     * @param e
     */

    public ChemIDException(String message, Exception e) {
        super(message, e);
    }
}
