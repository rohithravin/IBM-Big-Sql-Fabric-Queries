/**
 *
 */
package com.ibm.federation.fabric;

import java.util.Arrays;

/**
 * @author obernin
 *
 */
public class FabricWrapperLogging {

	public final static String ENTER_FMT 	= "ENTER %1$s";
	public final static String EXIT_FMT 		= "EXIT  %1$s";

	public final static String enterFormat(Object... parameters) {
		return String.format(FabricWrapperLogging.ENTER_FMT, Arrays.toString(parameters));
	}

	public final static String exitFormat() {
		return String.format(FabricWrapperLogging.EXIT_FMT, "");
	}

	public final static String exitFormat(Object returnValue) {
		return String.format(FabricWrapperLogging.EXIT_FMT, returnValue);
	}
}
