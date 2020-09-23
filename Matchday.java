package com.ten93.nsfs.league;

import java.io.Serializable;
import java.util.ArrayList;
import com.ten93.nsfs.Match;
import com.ten93.nsfs.NSFS;
import com.ten93.nsfs.Team;

public class Matchday implements Serializable {
	
	private ArrayList<Match> matches;
	private static final long serialVersionUID = 300;
	
	public Matchday() {
		matches = new ArrayList<Match>();
	}
	
	public void addMatch(Match m) {
		matches.add(m);
	}
	
	public ArrayList<Match> getMatches() {
		return matches;
	}
	
	public void scorinate() {
		for(Match m : matches) {
			m.scorinate();
			NSFS.out.append(m.scoreString() + System.getProperty("line.separator")); // temporary?
		}
	}
	
	public boolean isOpenFor(Team team1, Team team2) {
		for(Match m : matches) {
			if(m.involves(team1) || m.involves(team2)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 
	 * @param t
	 * @return The match, if any, involving the specified team. If no such match exists, returns null.
	 */
	public Match matchForTeam(Team t) {
		for(Match m : matches) {
			if(m.involves(t))
				return m;
		}
		return null;
	}
}
