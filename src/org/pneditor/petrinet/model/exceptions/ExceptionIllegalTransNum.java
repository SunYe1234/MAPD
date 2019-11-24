package org.pneditor.petrinet.model.exceptions;


/**
 * Throw it when trying to create the network using a negative transition number
 */
public class ExceptionIllegalTransNum extends Exception {
    public ExceptionIllegalTransNum(String message) {
        super(message);
    }
}
