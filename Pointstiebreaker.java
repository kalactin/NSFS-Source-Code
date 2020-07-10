package com.ten93.nsfs.comparison;

import com.ten93.nsfs.Team;
import com.ten93.nsfs.sports.Sport;

public class PointsTiebreaker implements Tiebreaker {
	private static PointsTiebreaker instance = null;
	private static Sport sport = null;
	private static final long serialVersionUID = 300;
	
	protected PointsTiebreaker() {
		// intentionally blank
	}
	
	public static Tiebreaker getInstance() {
		if(instance == null) {
			instance = new PointsTiebreaker();
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
		return team1.getPoints(sport) - team2.getPoints(sport);
	}
	
	public String toString() {
		return "Points";
	}

}
