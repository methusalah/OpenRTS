package exception;

/**
 * 
 * throw if there are some in-game problems like unexpected states or something like that
 *
 */
public class FunctionalException extends RuntimeException {

	private static final long serialVersionUID = 1672251038884875668L;

	public FunctionalException(String message) {
        super(message);
    }
	
	public FunctionalException(Throwable cause) {
		super(cause);
	}

}
