/*
 * csgames
 * 
 * Created on 09 September 2016 at 4:26 PM.
 */

package com.maulss.csgames.table;

import com.maulss.csgames.util.Defaults;
import com.maulss.csgames.util.IOUtil;
import com.maulss.csgames.match.Match;
import com.maulss.csgames.match.Matches;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;

public final class MouseTableListener extends MouseInputAdapter {

	private int rollOverRowIndex = -1;
	private final MatchTable table;

	public MouseTableListener(MatchTable table) {
		this.table = table;
	}

	@Override
	public void mouseExited(MouseEvent e) {
		rollOverRowIndex = -1;
		table.repaint();

		int row = table.rowAtPoint(e.getPoint());
		if (row == -1) return;

		Match match = Matches.getInstance().getMatchByIndex(row);
		if (match != null && match.isLive()) {
			table.setCursor(new Cursor(Cursor.HAND_CURSOR));
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		int row = table.rowAtPoint(e.getPoint());
		if (row == -1) return;
		if (row != rollOverRowIndex) {
			rollOverRowIndex = row;
			table.repaint();
		}

		Match match = Matches.getInstance().getMatchByIndex(row);
		if (match != null && !match.isLive()) {
			table.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() != MouseEvent.BUTTON1) {
			return;
		}

		Point point = e.getPoint();
		int col = table.columnAtPoint(point);
		int row = table.rowAtPoint(point);
		if (row == -1) return;

		switch (col) {
			// ID
			case 0: {
				Match match = Matches.getInstance().getMatchByIndex(row);
				if (match != null) {
					IOUtil.openMatchId(match.getId());
				}

				break;

			// team A
			} case 2: {
				Match match = Matches.getInstance().getMatchByIndex(row);
				if (match != null) {
					IOUtil.openSearchQuery(match.getTeamA());
				}

				break;

			// team B
			} case 3: {
				Match match = Matches.getInstance().getMatchByIndex(row);
				if (match != null) {
					IOUtil.openSearchQuery(match.getTeamB());
				}

				break;

			// event
			} case 4: {
				Match match = Matches.getInstance().getMatchByIndex(row);
				if (match != null) {
					String url = Defaults.getTwitchStreamLink(match.getEvent());
					if (url != null) {
						IOUtil.openUrl(url);
					}
				}

				break;
			}
		}
	}

	public MatchTable getTable() {
		return table;
	}

	public int getRollOverRowIndex() {
		return rollOverRowIndex;
	}
}