package org.chemid.prefilter.exception;

/**
 * Exception class
 */
public class ChemIDPreFilterException extends Exception {
    /**
     * @param message
     * @param e
     */

    public ChemIDPreFilterException(String message, Exception e) {
        super(message, e);
    }
}
