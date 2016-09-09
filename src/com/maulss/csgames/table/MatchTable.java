/*
 * csgames
 * 
 * Created on 07 September 2016 at 1:13 AM.
 */

package com.maulss.csgames.table;

import com.maulss.csgames.match.Match;
import com.maulss.csgames.match.Matches;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;

public class MatchTable extends JTable {

	private static MatchTable INSTANCE = new MatchTable();
	private final MouseTableListener listener = new MouseTableListener(this);

	public static final int DISPLAY_RESULTS = 500;

	public MatchTable() {
		update();

		addMouseMotionListener(listener);
		addMouseListener(listener);

		// Settings
		setSelectionBackground(new Color(184, 184, 184));
		getTableHeader().setReorderingAllowed(false);
	}

	@Override
	public String getToolTipText(MouseEvent e) {
		String tip;

		Point p = e.getPoint();
		int rowIndex = rowAtPoint(p);
		int colIndex = columnAtPoint(p);

		switch (colIndex) {
			case 1:
				try {
					Match match = Matches.getInstance().getMatchByIndex(rowIndex);
					tip = match == null ? null : match.getFormattedTime();
					break;
				} catch (Exception ex) {
					// Cell doesn't exist
					tip = null;
				}
				break;
			default:
				try {
					tip = getValueAt(rowIndex, colIndex).toString();
				} catch (Exception ex) {
					// Cell doesn't exist
					tip = null;
				}
		}

		return tip;
	}

	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
		Component c = super.prepareRenderer(renderer, row, col);
		if (row == -1 || col == -1) return c;

		if (isRowSelected(row) || (row == listener.getRollOverRowIndex())) {
			c.setForeground(getSelectionForeground());
			c.setBackground(getSelectionBackground());
		} else {
			c.setForeground(getForeground());
			c.setBackground(getBackground());
		}

		Match match = Matches.getInstance().getMatchByIndex(row);

		if (match != null) {
			if (!match.isClosed()) {
				if (match.hasStarted()) {
					// Match is soon to be played
					c.setForeground(new Color(60, 168, 40));
				} else {
					// Match is currently live
					c.setForeground(new Color(2, 115, 168));
				}
			} else {
				if (col == 2) {
					if (match.isWinnerA()) {
						c.setBackground(new Color(76, 175, 80, 80));
						return c;
					}

					if (match.isWinnerB()) {
						c.setBackground(new Color(244, 47, 52, 80));
						return c;
					}
				}

				if (col == 3) {
					if (match.isWinnerA()) {
						c.setBackground(new Color(244, 47, 52, 80));
						return c;
					}

					if (match.isWinnerB()) {
						c.setBackground(new Color(76, 175, 80, 80));
						return c;
					}
				}
			}
		}

		return c;
	}

	public void update() {
		Object[] columns = {"#" , "Time" , "Team A" , "Team B" , "Event" , "Format"};

		Object[][] data = new Object[DISPLAY_RESULTS][columns.length];

		List<Match> matches = Matches.getInstance().getLoadedMatches();
		for (int x = 0; x < Math.min(DISPLAY_RESULTS, matches.size()); ++x) {
			Match match = matches.get(x);
			data[x][0] = match.getId();
			data[x][1] = match.getFormattedTimeShort();
			//data[x][2] = match.getTeamA();
			//data[x][3] = match.getTeamB();
			data[x][4] = match.getEvent();
			data[x][5] = "BO" + match.getBestOf();
		}

		setModel(new MatchTableModel(data, columns));

		TableColumnModel cols = getColumnModel();
		cols.getColumn(0).setMinWidth(45);
		cols.getColumn(0).setMaxWidth(45);
		cols.getColumn(1).setMinWidth(140);
		cols.getColumn(1).setMaxWidth(140);
		cols.getColumn(2).setMinWidth(120);
		cols.getColumn(2).setMaxWidth(120);
		cols.getColumn(3).setMinWidth(120);
		cols.getColumn(3).setMaxWidth(120);
		cols.getColumn(4).setMinWidth(140);
		cols.getColumn(4).setMaxWidth(170);
		cols.getColumn(5).setMinWidth(45);
		cols.getColumn(5).setMaxWidth(55);

		cols.getColumn(2).setCellRenderer(new MatchCellRenderer());
		cols.getColumn(3).setCellRenderer(new MatchCellRenderer());

		repaint();
	}

	public static MatchTable getInstance() {
		return INSTANCE;
	}
}