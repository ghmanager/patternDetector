package controller;

/**
 * This class is an enumeration of input types, where every input type contains his own explanation for the corresponding label.
 * @author root
 *
 */
public enum InputType {

	CONFIG(new String[] {"the config file (absolute path) of"}),
	URL(new String[] {"the url of"}),
	TOKEN(new String[] {"the url of", "your authentication token to identify to"}),
	USERPASSWORD(new String[] {"the url of", "your username to identify to", "your password to identify to"});
	
	/**
	 * The describing string of the input type
	 */
	private String[] authdata;
	
	/**
	 * Sets the describing string of the input type
	 * @param authdata the string to set
	 */
	private InputType(String[] authdata) {
		this.authdata = authdata;
	}
	
	/**
	 * Gets the describing string of the input type
	 * @return the describing string
	 */
	public String[] getAuthData() {
		return authdata;
	}
	
	@Override
	public String toString() {
	    return super.toString().toLowerCase();
	}
}
