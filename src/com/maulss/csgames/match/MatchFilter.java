/*
 * csgames
 * 
 * Created on 07 September 2016 at 4:05 PM.
 */

package com.maulss.csgames.match;

public interface MatchFilter {

	boolean accept(Match match);
}