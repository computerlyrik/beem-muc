package net.java.otr4j;


public class OtrException extends Exception {
    private static final long serialVersionUID = 1L;

    public OtrException(Exception e){
	super(e);
    }
}
