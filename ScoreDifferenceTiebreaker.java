package com.ten93.nsfs.comparison;

import com.ten93.nsfs.Team;
import com.ten93.nsfs.sports.Sport;

public class ScoreDifferenceTiebreaker implements Tiebreaker {
	private static ScoreDifferenceTiebreaker instance = null;
	private static Sport sport = null;
	private static final long serialVersionUID = 300;
	
	protected ScoreDifferenceTiebreaker() {
		// intentionally blank
	}
	
	public static Tiebreaker getInstance() {
		if(instance == null) {
			instance = new ScoreDifferenceTiebreaker();
		}
		return instance;
	}
	
	public void setSport(Sport sp) {
		sport = sp;
	}

	public int compare(Team team1, Team team2) {
		if(sport == null) {
			throw new IllegalStateException();
		}
		int diff1 = team1.getScoreFor() - team1.getScoreAgainst(); 
		int diff2 = team2.getScoreFor() - team2.getScoreAgainst();
		return diff1 - diff2;
	}
	
	public String toString() {
		return "Score difference";
	}

}
