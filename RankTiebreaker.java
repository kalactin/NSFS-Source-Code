package com.ten93.nsfs.comparison;

import com.ten93.nsfs.Team;
import com.ten93.nsfs.sports.Sport;

public class RankTiebreaker implements Tiebreaker {
	private static RankTiebreaker instance = null;
	private static Sport sport = null;
	private static final long serialVersionUID = 300;
	
	protected RankTiebreaker() {
		// intentionally blank
	}
	
	public static Tiebreaker getInstance() {
		if(instance == null) {
			instance = new RankTiebreaker();
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
		if(team1.getRank() > team2.getRank()) {
			return 1;
		}
		else if(team1.getRank() < team2.getRank()) {
			return -1;
		}
		else {
			return 0;
		}
	}
	
	public String toString() {
		return "Team ranking points";
	}

}
