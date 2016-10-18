/*
 * csgames
 * 
 * Created on 03 September 2016 at 11:44 PM.
 */

package com.maulss.csgames.match;

import com.maulss.csgames.util.DateTimeUtil;
import com.sun.istack.internal.NotNull;

import javax.json.JsonObject;
import javax.swing.*;
import java.text.ParseException;
import java.util.Date;
import java.util.Objects;

public final class Match implements Cloneable, Comparable<Match> {

	private final int id;
	private final long time;
	private final String teamA;
	private final String teamB;
	private final String winner; // null = undecided winner and "none" = no winner yet
	private final boolean closed;
	private final String event;
	private final short bestOf;

	private Match(int id, long time, String teamA, String teamB, String winner, boolean closed, String event, short bestOf) {
		this.id = id;
		this.time = time;
		this.teamA = teamA;
		this.teamB = teamB;
		this.winner = winner;
		this.closed = closed;
		this.event = event;
		this.bestOf = bestOf;
	}

	public int getId() {
		return id;
	}

	public Date getTime() {
		return new Date(time);
	}

	public long getTimeMillis() {
		return time;
	}

	public String getDynamicTime() {
		return DateTimeUtil.prettifyTime(time - System.currentTimeMillis());
	}

	public String getFormattedTimeShort() {
		return DateTimeUtil.DATE_FORMAT_SHORT.format(getTime());
	}

	public String getFormattedTime() {
		return DateTimeUtil.DATE_FORMAT_LONG.format(getTime());
	}

	public String getTeamA() {
		return teamA;
	}

	public String getTeamB() {
		return teamB;
	}

	public String getWinner() {
		return winner;
	}

	public boolean hasWinner() {
		return isWinnerA() || isWinnerB();
	}

	public boolean isWinnerA() {
		return teamA.equals(winner);
	}

	public boolean isWinnerB() {
		return teamB.equals(winner);
	}

	public ImageIcon getIconA() {
		return Matches.getInstance().getLogo(teamA);
	}

	public ImageIcon getIconB() {
		return Matches.getInstance().getLogo(teamB);
	}

	public boolean hasStarted() {
		return System.currentTimeMillis() > time;
	}

	public boolean isLive() {
		return hasStarted() && !isClosed();
	}

	public boolean isClosed() {
		return closed;
	}

	public String getEvent() {
		return event;
	}

	public short getBestOf() {
		return bestOf;
	}

	@Override
	public String toString() {
		return "Match{" +
				"id=" + id +
				", time=" + time +
				", teamA='" + teamA + '\'' +
				", teamB='" + teamB + '\'' +
				", winner='" + winner + '\'' +
				", closed=" + closed +
				", event='" + event + '\'' +
				", bestOf=" + bestOf +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Match match = (Match) o;
		return Objects.equals(id, match.id) &&
				Objects.equals(time, match.time) &&
				Objects.equals(closed, match.closed) &&
				Objects.equals(bestOf, match.bestOf) &&
				Objects.equals(teamA, match.teamA) &&
				Objects.equals(teamB, match.teamB) &&
				Objects.equals(winner, match.winner) &&
				Objects.equals(event, match.event);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, time, teamA, teamB, winner, closed, event, bestOf);
	}

	@Override
	public int compareTo(Match o) {
		return Integer.compare(id, o.id);
	}

	@NotNull
	public static Match fromJson(JsonObject jsonObject, boolean local) throws ParseException {
		final int id;
		long time;
		final String teamA = jsonObject.getString("a");
		final String teamB = jsonObject.getString("b");
		final String winner;
		final boolean closed;
		final String event = jsonObject.getString("event");
		final short bestOf;

		if (local) {
			id = jsonObject.getInt("id");
			time = Long.parseLong(jsonObject.get("time").toString());
			winner = jsonObject.getString("winner");
			closed = jsonObject.getBoolean("closed");
			bestOf = (short) jsonObject.getInt("bestof");
		} else {
			id = Integer.valueOf(jsonObject.getString("match"));
			time = DateTimeUtil.CSGOL_DATE_FORMAT.parse(jsonObject.getString("when") + " CEST").toInstant().toEpochMilli();
			closed = Integer.valueOf(jsonObject.getString("closed")) != 0;
			bestOf = Short.valueOf(jsonObject.getString("format"));
			switch (jsonObject.getString("winner")) {
				case "a":
					winner = teamB;
					break;
				case "b":
					winner = teamA;
					break;
				case "c":
					winner = "none";
					break;
				default:
					winner = null;
			}
		}

		return new Match(id, time, teamA, teamB, winner, closed, event, bestOf);
	}

	private float calculateOdds(int aWorth, int bWorth) {
		return 0f;
	}
}