package org.pneditor.petrinet.model.exceptions;


/**
 * Throw it when trying to delete more tokens than a place actually has
 */
public class ExceptionDeleteTokens extends  Exception {

    public ExceptionDeleteTokens(String message) {
        super(message);
    }


}
