package ghost.library.error;

public class GenericError extends Error {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8512316801580597409L;
	public int code;
	public String message;

	public GenericError(int code, String message)
	{
		// TODO Auto-generated constructor stub
		this.code = code;
		this.message = message;
	}

}
