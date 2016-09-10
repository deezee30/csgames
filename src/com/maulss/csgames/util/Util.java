/*
 * csgames
 * 
 * Created on 07 September 2016 at 12:51 AM.
 */

package com.maulss.csgames.util;

import java.util.GregorianCalendar;

public final class Util {

	public static int TIMEZONE_OFFSET = new GregorianCalendar().getTimeZone().getRawOffset();

	// Disable initialization
	private Util() {}
}