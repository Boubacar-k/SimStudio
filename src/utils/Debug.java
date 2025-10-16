package utils;

/**
 * Class for debug purpose.
 * 
 * @author Xav & Sirius
 * 
 */
public class Debug {
	/**
	 * Boolean that defines whether the debug mode is enabled or not.
	 */
	static protected boolean enabled_ = true;

	/**
	 * Constructor : prints a string if the debug mode is enabled.
	 * 
	 * @param chaine
	 *            String that will be printed.
	 */
	public Debug(String chaine) {
		if (enabled_ == true)
			System.out.println(chaine);
	}

	/**
	 * Enables the debug mode.
	 * 
	 */
	public static void enable() {
		enabled_ = true;
	}

	/**
	 * Disables the debug mode.
	 * 
	 */
	public static void disable() {
		enabled_ = false;
	}
}
