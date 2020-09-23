package com.ten93.nsfs.league;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;

import com.ten93.nsfs.sports.Sport;
import com.ten93.nsfs.Match;
import com.ten93.nsfs.NSFS;
import com.ten93.nsfs.Team;

public class LeagueSchedule implements Serializable {
	private League league;
	private Matchday[] matchdays;
	private Sport sport;
	private boolean neutral;
	private int lastCompletedMatchday = -1;
	private static final long serialVersionUID = 300;
	
	public LeagueSchedule(League lg, Sport sp, boolean neu) {
		league = lg;
		sport = sp;
		neutral = neu;
		//matchdays = new ArrayList<Matchday>();
		matchdays = new Matchday[calcNumMatchdays()];
		for(int i = 0; i < matchdays.length; i++) {
			matchdays[i] = new Matchday();
		}
	}
	
	private int calcNumMatchdays() {
		int divMatches = league.divisionMatches();
		int nonDivMatches = league.nonDivisionMatches();
		int divisionSize = league.divisionSize();
		int numDivisions = league.numDivisions();
		int evenDivisionSize;
		int evenNumDivisions;
		if(divisionSize % 2 == 0)
			evenDivisionSize = divisionSize;
		else
			evenDivisionSize = divisionSize + 1;
		if(numDivisions % 2 == 0)
			evenNumDivisions = numDivisions;
		else
			evenNumDivisions = numDivisions + 1;
		
		return divMatches * (evenDivisionSize - 1) + nonDivMatches * divisionSize * (evenNumDivisions - 1);
	}
	
	public void generate() {
		Matchday[][] intraDivisionMatches = new Matchday[league.numDivisions()][];
		for(int i = 0; i < intraDivisionMatches.length; i++) {
			intraDivisionMatches[i] = scheduleIntraDivision(league.getDivision(i));
		}

		for(int j = 0; j < intraDivisionMatches.length; j++) {
			for(int k = 0; k < intraDivisionMatches[j].length; k++) {
				ArrayList<Match> matches = intraDivisionMatches[j][k].getMatches();
				for(Match m : matches) {
					matchdays[k].addMatch(m);
				}
			}
		}
		
		Matchday[][] interDivisionMatches = new Matchday[league.numDivisions() * (league.numDivisions() - 1) / 2][];
		for(int i = 0, j = 0; j < league.numDivisions() - 1; j++) {
			for(int k = j + 1; k < league.numDivisions(); k++) {
				//System.out.println("Scheduling matches between divisions " + league.getDivision(j).getName() + " and " + league.getDivision(k).getName());
				interDivisionMatches[i++] = scheduleInterDivision(league.getDivision(j), league.getDivision(k));
				//System.out.println(interDivisionMatches[i].length);
			}
		}
		
		for(int l = 0; l < interDivisionMatches.length; l++) {
			//System.out.println(intraDivisionMatches[0].length); // temp
			//System.out.println(interDivisionMatches[0].length); // temp
			for(int n = 0; n < interDivisionMatches[l].length; n++) {
				ArrayList<Match> matches = interDivisionMatches[l][n].getMatches();
				Matchday currentMatchday = matchdays[intraDivisionMatches[0].length + n];
				for(Match m : matches) {
					if(currentMatchday.isOpenFor(m.homeTeam(), m.awayTeam())) {
						matchdays[intraDivisionMatches[0].length + n].addMatch(m);
					}
					else {
						Matchday newMD = getNextOpenMatchday(intraDivisionMatches[0].length + n, m.homeTeam(), m.awayTeam());
						newMD.addMatch(m);
					}
				}
			}
		}
	}
	
	private Matchday getNextOpenMatchday(int startIndex, Team team1, Team team2) {
		int index = startIndex;
		while(index < matchdays.length) {
			//System.out.println("Checking if matchday " + (index+1) + " is open for " + team1.getName() + " and " + team2.getName());
			if(matchdays[index].isOpenFor(team1, team2)) {
				return matchdays[index];
			}
			index++;
		}
		return null;
	}
	
	public void scorinate(int startDay, int numDays) {
		int endDay = startDay + numDays;
		if(numDays < 1) {
			endDay = matchdays.length;
		}
		if(endDay > matchdays.length) {
			endDay = matchdays.length;
		}
		for(int i = startDay; i < endDay; i++) {
			if(matchdays[i].getMatches().size() == 0) {
				continue;
			}
			NSFS.out.append("MATCHDAY " + (i+1) + System.getProperty("line.separator"));
			matchdays[i].scorinate();
			lastCompletedMatchday = i;
		}
	}
	
	public int numMatchdays() {
		return matchdays.length;
	}
	
	public int getLastCompletedMatchday() {
		return lastCompletedMatchday;
	}
	
	private Matchday[] scheduleInterDivision(Division div1, Division div2) {
		if(div1.size() != div2.size()) {
			System.err.println("Attempted to schedule matches between divisions of size " + div1.size() + " and " + div2.size());
			return null;
		}
		Matchday[] fixtures = new Matchday[div1.size() * league.nonDivisionMatches()];
		for(int i = 0; i < fixtures.length; i++) {
			fixtures[i] = new Matchday();
			for(int j = 0; j < div1.size(); j++) {
				if(i % 2 == 0) {
					fixtures[i].addMatch(new Match(div1.getTeam(j), div2.getTeam((j+i) % div2.size()), sport, neutral));
				}
				else {
					fixtures[i].addMatch(new Match(div2.getTeam((j+i) % div2.size()), div1.getTeam(j), sport, neutral));
				}
			}
		}
		return fixtures;
	}
	
	private Matchday[] scheduleIntraDivision(Division div) {
		int evenDivisionSize;
		if(div.size() % 2 == 0)
			evenDivisionSize = div.size();
		else
			evenDivisionSize = div.size() + 1;
		Matchday[] fixtures = new Matchday[(evenDivisionSize - 1) * league.divisionMatches()];
		Team fixedCompetitor = div.getTeam(0);
		LinkedList<Team> rotatingCompetitors = new LinkedList<Team>();
		for(int i = 1; i < div.getTeams().size(); i++) { // yes, i = 1 is intentional, we don't want the first one
			rotatingCompetitors.add(div.getTeam(i));
		}
		if(rotatingCompetitors.size() % 2 == 0) {
			rotatingCompetitors.add(null);
		}
		
		for(int i = 0; i < fixtures.length; i++) {
			fixtures[i] = new Matchday();
			int lastIndex = rotatingCompetitors.size() - 1;
			if(rotatingCompetitors.get(lastIndex) != null) {
				if(i % 2 == 0) {
					fixtures[i].addMatch(new Match(fixedCompetitor, rotatingCompetitors.get(lastIndex), sport, neutral));
				}
				else {
					fixtures[i].addMatch(new Match(rotatingCompetitors.get(lastIndex), fixedCompetitor, sport, neutral));
				}
			}
			for(int j = 1; j < evenDivisionSize / 2; j++) { // j=1 is intentional
				if(rotatingCompetitors.get(j - 1) != null && rotatingCompetitors.get(lastIndex - j) != null) {
					if(i % 2 == 0) {
						fixtures[i].addMatch(new Match(rotatingCompetitors.get(j - 1), rotatingCompetitors.get(lastIndex - j), sport, neutral));
					}
					else {
						fixtures[i].addMatch(new Match(rotatingCompetitors.get(lastIndex - j), rotatingCompetitors.get(j - 1), sport, neutral));
					}
				}
			}
			rotatingCompetitors.addFirst(rotatingCompetitors.removeLast());
		}
		return fixtures;
	}
	
	public ArrayList<Match> matchesInvolvingTeam(Team t) {
		ArrayList<Match> matches = new ArrayList<Match>();
		for(int i = 0; i < matchdays.length; i++) {
			Match m = matchdays[i].matchForTeam(t);
			if(m != null) {
				matches.add(m);
			}
		}
		return matches;
	}
	
	// we'll want to have this on hand to help with head-to-head tiebreakers
	public ArrayList<Match> matchesBetweenTeams(Team team1, Team team2) {
		ArrayList<Match> matches = matchesInvolvingTeam(team1);
		ArrayList<Match> finalMatches = new ArrayList<Match>();
		if(team1 == team2) {
			return finalMatches;
		}
		for(Match m : matches) {
			if(m.involves(team2)) {
				finalMatches.add(m);
			}
		}
		return finalMatches;
	}
	
	public ArrayList<Match> matchesAmongTeams(Vector<Team> teams) {
		ArrayList<Match> matches = new ArrayList<Match>();
		for(Team t : teams) {
			for(Team u : teams) {
				for(Match m : matchesBetweenTeams(t, u)) {
					if(!matches.contains(m)) {
						matches.add(m);
					}
				}
			}
		}
		return matches;
	}

}
