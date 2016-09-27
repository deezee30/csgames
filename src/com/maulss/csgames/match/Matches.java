/*
 * csgames
 * 
 * Created on 04 September 2016 at 3:24 PM.
 */

package com.maulss.csgames.match;

import com.maulss.csgames.util.IOUtil;
import com.maulss.csgames.util.Timer;
import com.maulss.csgames.table.MatchTable;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonWriter;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public final class Matches implements Iterable<Match> {

	// Disable initialization
	private Matches() {}

	private static final Matches INSTANCE = new Matches();
	private static final String MATCH_SOURCE = "https://csgolounge.com/api/matches";
	private final List<MatchFilter> matchFilters = new ArrayList<>();
	private List<Match> matchCache = new ArrayList<>(MatchTable.DISPLAY_RESULTS);
	public final Map<String, ImageIcon> matchLogos = new HashMap<>();

	static {
		// Remove all games that aren't BO1, BO2, BO3, BO5 or BO7
		INSTANCE.registerMatchFilter(match -> {
			switch (match.getBestOf()) {
				case 1:
				case 2:
				case 3:
				case 5:
				case 7:
					return true;
				default:
					return false;
			}
		});

		// Remove all fake matches
		INSTANCE.registerMatchFilter(match -> {
			switch (match.getEvent().toLowerCase()) {
				case "predictions":
				case "giveaways":
					return false;
				default:
					return true;
			}
		});

		// Record the time it takes to download all matches
		Timer timer = new Timer().start();
		timer.onFinishExecute(() -> JOptionPane.showMessageDialog(null, String.format(
				"Successfully loaded %s matches in %ss",
				INSTANCE.matchCache.size(),
				(double) timer.getTime(TimeUnit.MILLISECONDS) / 1000D
		)));

		INSTANCE.matchCache.clear();

		// Load all match data
		try {
			INSTANCE.matchCache.addAll(INSTANCE.fromJson(IOUtil.readFromDataFile(), true));
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Could not load JSON data from flatfile: " + e.getMessage());
			e.printStackTrace();
		}

		// Load all icons for the matches
		for (Match match : INSTANCE) {
			INSTANCE.loadIcon(match.getTeamA());
			INSTANCE.loadIcon(match.getTeamB());
		}

		// Stop the timer and display success message
		timer.forceStop();
	}

	public void downloadMatches() {
		// Record the time it takes to download all matches
		Timer timer = new Timer().start();
		timer.onFinishExecute(() -> JOptionPane.showMessageDialog(null, String.format(
				"Successfully downloaded %s matches in %ss",
				INSTANCE.matchCache.size(),
				(double) timer.getTime(TimeUnit.MILLISECONDS) / 1000D
		)));

		String matchesJson;

		try {
			matchesJson = IOUtil.readPageContents(MATCH_SOURCE);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Could not connect to CSGO Lounge API: " + e.getMessage());
			e.printStackTrace();
			return;
		}

		matchCache.clear();

		try {
			matchCache.addAll(filterMatches(fromJson(matchesJson, false)));
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Could not process matches into a list: " + e.getMessage());
			e.printStackTrace();
			return;
		}

		try {
			IOUtil.writeToDataFile(toJson(matchCache));
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Could not update JSON data: " + e.getMessage());
			e.printStackTrace();
			return;
		}

		// Download the logos of the teams
		try {
			List<String> updatedTeams = new ArrayList<>();
			for (Match match : this) {
				String a = match.getTeamA();
				String b = match.getTeamB();

				if (!updatedTeams.contains(a) && match.getIconA() == null) {
					IOUtil.resizeAndDownloadImage(a);
					updatedTeams.add(a);
				}

				if (!updatedTeams.contains(b) && match.getIconB() == null) {
					IOUtil.resizeAndDownloadImage(b);
					updatedTeams.add(b);
				}
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Could not download team logos: " + e.getMessage());
			e.printStackTrace();
			return;
		}

		// Refresh the table to update the new data
		MatchTable.getInstance().update();

		// Stop the timer and display success message
		timer.forceStop();
	}

	public List<Match> getLoadedMatches() {
		// Create a copy of the cache and restrict modification
		return Collections.unmodifiableList(new ArrayList<>(matchCache));
	}

	public Match getMatchByIndex(int x) {
		List<Match> matches = new ArrayList<>(matchCache);
		Match m = null;
		try {
			m = matches.get(x);
		} catch (Exception ignored) {}
		return m;
	}

	public ImageIcon getLogo(String team) {
		return matchLogos.get(team);
	}

	public void setLogo(String team) {
		setLogo(team, new ImageIcon(IOUtil.IMAGES_PATH + "/" + team + ".gif"));
	}

	public void setLogo(String team, ImageIcon icon) {
		matchLogos.put(team, icon);
	}

	public void registerMatchFilter(MatchFilter filter) {
		matchFilters.add(filter);
	}

	private void loadIcon(String team) {
		matchLogos.put(team,new ImageIcon(IOUtil.IMAGES_PATH + "/" +
				(new File(IOUtil.IMAGES_PATH + "/" + team + ".gif").exists() ? team : "unknown")
			+ ".gif"));
	}

	private List<Match> filterMatches(List<Match> matches) {
		Collections.reverse(matches);
		List<Match> filteredMatches = new ArrayList<>();

		int x = 0;
		int y = 0;
		for (Match match : matches) {
			// Keep the size of the list no bigger than required
			if (x - y == MatchTable.DISPLAY_RESULTS) break;

			boolean add = true;
			for (MatchFilter filter : matchFilters) {
				if (!filter.accept(match)) {
					add = false;
					y++;
					break;
				}
			}

			if (add) filteredMatches.add(match);
			x++;
		}

		return filteredMatches;
	}

	private List<Match> fromJson(String json, boolean local) {
		return Json.createReader(new StringReader(json))
				.readArray()
				.stream()
				.map(value -> Match.fromJson(
						Json.createReader(new StringReader(value.toString())).readObject(),
						local
				)).collect(Collectors.toList());
	}

	private String toJson(List<Match> matches) {
		JsonArrayBuilder builder = Json.createArrayBuilder();
		for (Match match : matches) {
			String winner = match.getWinner();
			winner = winner == null ? "null" : winner;
			builder.add(Json.createObjectBuilder()
							.add("id", match.getId())
							.add("time", match.getTimeMillis())
							.add("a", match.getTeamA())
							.add("b", match.getTeamB())
							.add("winner", winner)
							.add("closed", match.isClosed())
							.add("event", match.getEvent())
							.add("bestof", match.getBestOf())
			);
		}

		JsonArray model = builder.build();

		StringWriter writer = new StringWriter();
		try (JsonWriter jsonWriter = Json.createWriter(writer)) {
			jsonWriter.writeArray(model);
		}

		return writer.toString();
	}

	@Override
	public Iterator<Match> iterator() {
		return matchCache.iterator();
	}

	public static Matches getInstance() {
		return INSTANCE;
	}
}