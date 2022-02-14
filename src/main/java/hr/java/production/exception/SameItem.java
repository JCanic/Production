package hr.java.production.exception;

/**
 * is thrown when user enters Item that has already been entered
 */

public class SameItem extends Exception{

    /**
     * contains Exception description
     * @param message describes thrown exception
     */
    public SameItem(String message){
        super (message);
    }

    /**
     * contains Exception cause
     * @param cause presents cause for throwing Exception
     */
    public SameItem (Throwable cause){
        super (cause);
    }

    /**
     * contains Exception description and cause
     * @param message describes thrown exception
     * @param cause presents cause for throwing Exception
     */
    public SameItem (String message, Throwable cause){
        super(message,cause);
    }
}
