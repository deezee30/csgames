/*
 * csgames
 * 
 * Created on 03 September 2016 at 11:19 PM.
 */

package com.maulss.csgames;

import com.maulss.csgames.match.Matches;
import com.maulss.csgames.table.MatchTable;
import com.maulss.csgames.util.IOUtil;

import javax.swing.*;
import java.awt.*;

public class GamesFrame extends JFrame {

	private static final GamesFrame INSTANCE = new GamesFrame();
	private static final String VERSION = "0.1";

	private GamesFrame() {
		super("CSGames v" + VERSION);

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		// Disable resizing
		setResizable(false);

		// Setting the look and feel of the interface
		try {
			for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if (info.getName().equals("Nimbus")) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {

			// If Nimbus is not available, fall back to cross-platform
			try {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			} catch (Exception ex) {
				// not worth my time
			}
		}

		JPanel panel1 = new JPanel();
		panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));

		JLabel title = new JLabel("Displaying " + MatchTable.DISPLAY_RESULTS + " latest matches");
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setFont(new Font("Verdana", Font.BOLD, 20));
		panel1.add(title, BorderLayout.BEFORE_FIRST_LINE);

		MatchTable table = MatchTable.getInstance();
		JScrollPane scrollPane = new JScrollPane(table);
		panel1.add(scrollPane, BorderLayout.CENTER);

		JPanel panel2 = new JPanel(new FlowLayout());

		JButton button1 = new JButton("Refresh");
		button1.addActionListener(e -> Matches.getInstance().downloadMatches());
		panel2.add(button1);

		JButton button2 = new JButton("HLTV.org");
		button2.addActionListener(e -> IOUtil.openUrl("http://hltv.org"));
		panel2.add(button2);

		JButton button3 = new JButton("Local Folder");
		button3.addActionListener(e -> IOUtil.openDirectory(IOUtil.MAIN_FOLDER_PATH));
		panel2.add(button3);

		panel1.add(panel2);

		add(panel1);

		setSize(670, (int) getPreferredSize().getHeight());

		setVisible(true);
	}

	public static GamesFrame getInstance() {
		return INSTANCE;
	}
}