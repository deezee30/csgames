/*
 * csgames
 * 
 * Created on 03 September 2016 at 11:18 PM.
 */

package com.maulss.csgames;

import com.maulss.csgames.match.Matches;
import com.maulss.csgames.util.Defaults;
import com.maulss.csgames.util.IOUtil;

import javax.swing.*;

public class Main {

	public static void main(String... args) {

		// initialize defaults
		Defaults.init();

		// generate CSGames main folder (%HOME%/Documents/csgames)
		try {
			IOUtil.generateMainFolder();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Was not able to generate default folders and files: " + e.getMessage());
			e.printStackTrace();
			return;
		}

		// obtain match info from flatfile
		try {
			Matches.getInstance();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Was not able to load matches from flatfile: " + e.getMessage());
			e.printStackTrace();
			return;
		}

		// initiate GamesFrame
		GamesFrame.getInstance();
	}
}