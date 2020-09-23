package com.ten93.nsfs.league;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Vector;

import com.ten93.nsfs.NSFS;
import com.ten93.nsfs.Team;
import com.ten93.nsfs.sports.Sport;
import com.ten93.nsfs.comparison.*;

public class Division implements Serializable {
	private Vector<Team> teams;
	private String name;
	private Sport sport;
	private League league;
	private static final long serialVersionUID = 300;
	
	public Division(String divName, League lg, Sport sp) {
		name = divName;
		teams = new Vector<Team>();
		league = lg;
		sport = sp;
	}
	
	public void addTeam(Team t) {
		teams.add(t);
		t.setDivision(this);
		t.setLeague(league);
	}
	
	public void removeTeam(Team t) {
		teams.remove(t);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String n) {
		name = n;
	}
	
	public int size() {
		return teams.size();
	}
	
	public Team getTeam(int index) {
		return teams.get(index);
	}
	
	public Vector<Team> getTeams() {
		return teams;
	}
	
	public void printStandings(List<Tiebreaker> tiebreakers) {
		String newLine = System.getProperty("line.separator");
		
		NSFS.out.append(newLine + newLine);
		NSFS.out.append(name + " Standings");
		NSFS.out.append(newLine + newLine);
		Team[] sortedTeams = sortTeams(tiebreakers);
		
		int playedCharacters = playedCharacters();
		int winsCharacters = winsCharacters();
		int drawsCharacters = drawsCharacters();
		int lossesCharacters = lossesCharacters();
		int scoreForCharacters = scoreForCharacters();
		int scoreAgainstCharacters = scoreAgainstCharacters();
		int scoreDiffCharacters = scoreDiffCharacters();
		int pointsCharacters = pointsCharacters();
		
		for(int i = 0; i < Math.log10(sortedTeams.length); i++) {
			NSFS.out.append(" ");
		}
		NSFS.out.append("  Team");
		for(int i = 0; i < lengthOfLongestTeamName(); i++) {
			NSFS.out.append(" ");
		}
		NSFS.out.append(" ");
		
		for(int i = 0; i < playedCharacters; i++)
			NSFS.out.append(" ");
		NSFS.out.append("P ");
		
		for(int i = 0; i < winsCharacters; i++)
			NSFS.out.append(" ");
		NSFS.out.append("W ");
		
		// drawsAllowed detector is broken as of build 267, so display the column always
		//if(sport.drawsAllowed()) {
			for(int i = 0; i < drawsCharacters; i++)
				NSFS.out.append(" ");
			NSFS.out.append("D ");
		//}
		
		for(int i = 0; i < lossesCharacters; i++)
			NSFS.out.append(" ");
		NSFS.out.append("L ");
		
		for(int i = 0; i < scoreForCharacters - 2; i++)
			NSFS.out.append(" ");
		NSFS.out.append("For ");
		
		for(int i = 0; i < scoreAgainstCharacters - 1; i++)
			NSFS.out.append(" ");
		NSFS.out.append("Ag ");
		
		for(int i = 0; i < scoreDiffCharacters - 1; i++)
			NSFS.out.append(" ");
		NSFS.out.append("+/- ");
		
		for(int i = 0; i < pointsCharacters - 2; i++)
			NSFS.out.append(" ");
		NSFS.out.append("Pts");
		
		int index = 0;
		for(Team t : sortedTeams) {
			index++;
			int nameLength = t.getName().codePointCount(0, t.getName().length());

			NSFS.out.append(newLine);

			for(int j = (int)Math.log10(index); j < Math.log10(sortedTeams.length); j++)
				NSFS.out.append(" ");
			NSFS.out.append(index);
			NSFS.out.append(" ");
			
			NSFS.out.append(t.getName());
			for(int j = 0; j < lengthOfLongestTeamName() - nameLength + 5; j++) {
				NSFS.out.append(" ");
			}
			int played = t.getWins() + t.getDraws() + t.getLosses();
			for(int j = (played == 0 ? 0 : (int)Math.log10(played)); j < playedCharacters; j++)
				NSFS.out.append(" ");
			NSFS.out.append(played);
			NSFS.out.append(" ");

			for(int j = (t.getWins() == 0 ? 0 : (int)Math.log10(t.getWins())); j < winsCharacters; j++)
				NSFS.out.append(" ");
			NSFS.out.append(t.getWins());
			NSFS.out.append(" ");

			// drawsAllowed detector is broken as of build 267, so display the column always
			//if(sport.drawsAllowed()) {
				for(int j = (t.getDraws() == 0 ? 0 : (int)Math.log10(t.getDraws())); j < drawsCharacters; j++)
					NSFS.out.append(" ");
				NSFS.out.append(t.getDraws());
				NSFS.out.append(" ");
			//}

			for(int j = (t.getLosses() == 0 ? 0 : (int)Math.log10(t.getLosses())); j < lossesCharacters; j++)
				NSFS.out.append(" ");
			NSFS.out.append(t.getLosses());
			NSFS.out.append(" ");

			for(int j = (t.getScoreFor() == 0 ? 0 : (int)Math.log10(t.getScoreFor())); j < scoreForCharacters; j++)
				NSFS.out.append(" ");
			NSFS.out.append(t.getScoreFor());
			NSFS.out.append(" ");

			for(int j = (t.getScoreAgainst() == 0 ? 0 : (int)Math.log10(t.getScoreAgainst())); j < scoreAgainstCharacters; j++)
				NSFS.out.append(" ");
			NSFS.out.append(t.getScoreAgainst());
			NSFS.out.append(" ");

			int scoreDiff = t.getScoreFor() - t.getScoreAgainst();
			DecimalFormat nf = (DecimalFormat)DecimalFormat.getIntegerInstance();
			nf.setPositivePrefix("+");
			nf.setGroupingUsed(false);
			for(int j = (scoreDiff == 0 ? 0 : (int)Math.log10(Math.abs(scoreDiff))); j < scoreDiffCharacters; j++)
				NSFS.out.append(" ");
			NSFS.out.append(nf.format(scoreDiff));
			NSFS.out.append(" ");

			for(int j = (t.getPoints(sport) == 0 ? 0 : (int)Math.log10(t.getPoints(sport))); j < pointsCharacters; j++)
				NSFS.out.append(" ");
			NSFS.out.append(t.getPoints(sport));
		}
	}
	
	/**
	 * 
	 * @return the length of the longest team name in the division
	 */
	private int lengthOfLongestTeamName() {
		int max = 0;
		for(Team t : teams) {
			if(t.getName().codePointCount(0, t.getName().length()) > max) {
				max = t.getName().codePointCount(0, t.getName().length());
			}
		}
		return max;
	}
	
	private int playedCharacters() {
		int max = 0;
		for(Team t : teams) {
			int pld = t.getWins() + t.getDraws() + t.getLosses();
			if(pld > max) {
				max = pld;
			}
		}
		if(max == 0)
			return 0;
		return (int)Math.ceil(Math.log10(max));
	}
	
	private int winsCharacters() {
		int max = 0;
		for(Team t : teams) {
			if(t.getWins() > max) {
				max = t.getWins();
			}
		}
		if(max == 0)
			return 0;
		return (int)Math.ceil(Math.log10(max));
	}
	
	private int drawsCharacters() {
		int max = 0;
		for(Team t : teams) {
			if(t.getDraws() > max) {
				max = t.getDraws();
			}
		}
		if(max == 0)
			return 0;
		return (int)Math.ceil(Math.log10(max));
	}
	
	private int lossesCharacters() {
		int max = 0;
		for(Team t : teams) {
			if(t.getLosses() > max) {
				max = t.getLosses();
			}
		}
		if(max == 0)
			return 0;
		return (int)Math.ceil(Math.log10(max));
	}
	
	private int scoreForCharacters() {
		int max = 0;
		for(Team t : teams) {
			if(t.getScoreFor() > max) {
				max = t.getScoreFor();
			}
		}
		if(max == 0)
			return 0;
		return (int)Math.ceil(Math.log10(max));
	}
	
	private int scoreAgainstCharacters() {
		int max = 0;
		for(Team t : teams) {
			if(t.getScoreAgainst() > max) {
				max = t.getScoreAgainst();
			}
		}
		if(max == 0)
			return 0;
		return (int)Math.ceil(Math.log10(max));
	}
	
	private int scoreDiffCharacters() {
		int max = 0;
		for(Team t : teams) {
			int scoreDiff = Math.abs(t.getScoreFor() - t.getScoreAgainst());
			if(scoreDiff > max) {
				max = scoreDiff;
			}
		}
		if(max == 0)
			return 0;
		return (int)Math.ceil(Math.log10(max));
	}
	
	private int pointsCharacters() {
		int max = 0;
		for(Team t : teams) {
			if(t.getPoints(sport) > max) {
				max = t.getPoints(sport);
			}
		}
		if(max == 0)
			return 0;
		return (int)Math.ceil(Math.log10(max));
	}
	
	public Team[] sortTeams(List<Tiebreaker> tiebreakers) {
		Team[] sortedTeams = teams.toArray(new Team[0]);
		PointsTiebreaker.getInstance().setSport(sport);
		for(int i = 1; i < sortedTeams.length; i++) {
			Team t = sortedTeams[i];
			int j = i - 1;
			boolean done = false;
			while(!done) {
				//System.out.println("i = " + i + ", j = " + j);
				//System.out.println("There are a total of " + tiebreakers.size() + " tiebreakers available.");
				for(Tiebreaker tb : tiebreakers) {
					//System.out.println("Using tiebreaker " + tb);
					done = false;
					if(tb.compare(sortedTeams[j], t) < 0) {
						//System.out.println(sortedTeams[j].getName() + "(" + sortedTeams[j].getPoints(sport) + ") v " + t.getName() + "(" + t.getPoints(sport) + ") < 0");
						sortedTeams[j+1] = sortedTeams[j];
						j--;
						if(j < 0) {
							done = true;
						}
						break;
					}
					else if(tb.compare(sortedTeams[j], t) > 0) {
						//System.out.println(sortedTeams[j].getName() + "(" + sortedTeams[j].getPoints(sport) + ") v " + t.getName() + "(" + t.getPoints(sport) + ") > 0");
						done = true;
						break;
					}
					else {
						//System.out.println(sortedTeams[j].getName() + "(" + sortedTeams[j].getPoints(sport) + ") v " + t.getName() + "(" + t.getPoints(sport) + ") = 0");
						done = true;
					}
				}
			}
			sortedTeams[j+1] = t;
		}
		return sortedTeams;
	}
	
	public String toString() {
		return name;
	}
	
	public void initialize() {
		for(Team t : teams) {
			t.initialize();
		}
	}
	
	public Sport getSport() {
		return sport;
	}
	
	public void shiftTeamDown(Team t) {
		int index = teams.indexOf(t);
		if(index == teams.size() - 1) {
			return;
		}
		teams.remove(t);
		teams.add(index + 1, t);
	}
	
	public void shiftTeamUp(Team t) {
		int index = teams.indexOf(t);
		if(index == 0) {
			return;
		}
		teams.remove(t);
		teams.add(index - 1, t);
	}
}
