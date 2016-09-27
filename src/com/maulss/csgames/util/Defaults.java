/*
 * csgames
 * 
 * Created on 10 September 2016 at 1:23 PM.
 */

package com.maulss.csgames.util;

import java.util.HashMap;
import java.util.Map;

public final class Defaults {

	private static final String TWITCH_URL = "https://www.twitch.tv/";

	private static final Defaults DEFAULTS = new Defaults();
	private final Map<String, String> streams = new HashMap<String, String>() {{
		put("Starladder", "starladder_cs_en");
		put("StarSeries", "starladder_cs_en");
		put("King of Nordic", "kingofnordic");
		put("DreamLeague", "dreamleague");
		put("ESL", "esl_csgo");
		put("ELeague", "eleaguetv");
		put("ECS", "faceittv");
		put("Northern Arena", "northernarena");
		put("ESWC", "eswc");
		put("Dreamhack", "dreamhackcs");
		put("MLG", "mlg");
		put("WESG", "wesg_main");
		put("Epicenter", "epicenter_en1");
		put("99League", "99damage");
		put("ESEA", "esea");
	}};

	public static String getTwitchStreamLink(String event) {
		String twitchId = DEFAULTS.streams.get(event);
		return twitchId == null ? null : TWITCH_URL + twitchId;
	}

	public static void init() {}
}