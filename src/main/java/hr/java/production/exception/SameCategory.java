package hr.java.production.exception;

/**
 * is thrown when user enters a category that has already been entered
 */

public class SameCategory extends RuntimeException{

    /**
     * contains Exception description
     * @param message describes thrown exception
     */
    public SameCategory (String message){
        super (message);
    }


    /**
     * contains Exception cause
     * @param cause presents cause for throwing Exception
     */
    public SameCategory (Throwable cause){
        super (cause);
    }

    /**
     * contains Exception description and cause
     * @param message describes thrown exception
     * @param cause presents cause for throwing Exception
     */
    public SameCategory (String message, Throwable cause){
        super (message, cause);
    }


}
