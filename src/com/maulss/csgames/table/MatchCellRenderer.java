/*
 * csgames
 * 
 * Created on 09 September 2016 at 9:51 PM.
 */

package com.maulss.csgames.table;

import com.maulss.csgames.match.Match;
import com.maulss.csgames.match.Matches;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class MatchCellRenderer extends DefaultTableCellRenderer {

	public Component getTableCellRendererComponent(JTable table,
												   Object value,
												   boolean isSelected,
												   boolean hasFocus,
												   int row,
												   int col) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
		if (row == -1 || col < 2 || col > 3) return this;

		Match match = Matches.getInstance().getMatchByIndex(row);
		if (match != null) {
			if (col == 2) {
				setText(match.getTeamA());
				setIcon(match.getIconA());
			}

			if (col == 3) {
				setText(match.getTeamB());
				setIcon(match.getIconB());
			}
		}

		return this;
	}
}