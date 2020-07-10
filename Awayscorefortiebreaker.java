package com.ten93.nsfs.comparison;

import com.ten93.nsfs.Team;
import com.ten93.nsfs.sports.Sport;

public class AwayScoreForTiebreaker implements Tiebreaker {
	private static AwayScoreForTiebreaker instance = null;
	private static Sport sport = null;
	private static final long serialVersionUID = 300;
	
	protected AwayScoreForTiebreaker() {
		// intentionally blank
	}
	
	public static Tiebreaker getInstance() {
		if(instance == null) {
			instance = new AwayScoreForTiebreaker();
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
		return team1.getAwayScoreFor() - team2.getAwayScoreFor();
	}
	
	public String toString() {
		return "Score for away from home";
	}

}
