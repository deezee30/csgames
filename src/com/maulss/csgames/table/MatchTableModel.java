/*
 * csgames
 * 
 * Created on 06 September 2016 at 7:01 PM.
 */

package com.maulss.csgames.table;

import javax.swing.table.DefaultTableModel;
import java.util.Vector;

public class MatchTableModel extends DefaultTableModel {

	public MatchTableModel() {
		super();
	}

	public MatchTableModel(int rowCount, int columnCount) {
		super(rowCount, columnCount);
	}

	public MatchTableModel(Vector columnNames, int rowCount) {
		super(columnNames, rowCount);
	}

	public MatchTableModel(Object[] columnNames, int rowCount) {
		super(columnNames, rowCount);
	}

	public MatchTableModel(Vector data, Vector columnNames) {
		super(data, columnNames);
	}

	public MatchTableModel(Object[][] data, Object[] columnNames) {
		super(data, columnNames);
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
}