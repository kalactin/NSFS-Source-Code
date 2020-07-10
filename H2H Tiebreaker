package com.ten93.nsfs.comparison;

import java.util.ArrayList;
import java.util.List;

import com.ten93.nsfs.Match;
import com.ten93.nsfs.Team;
import com.ten93.nsfs.league.LeagueSystem;
import com.ten93.nsfs.sports.Sport;

public class HeadToHeadTiebreaker implements Tiebreaker {
	private static HeadToHeadTiebreaker instance = null;
	private static Sport sport = null;
	private static final long serialVersionUID = 300;
	
	protected HeadToHeadTiebreaker() {
		// intentionally blank
	}
	
	public static Tiebreaker getInstance() {
		if(instance == null) {
			instance = new HeadToHeadTiebreaker();
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
		
		LeagueSystem system = team1.getLeague().getSystem();
		List<Tiebreaker> tiebreakers = system.getTiebreakers();
		ArrayList<Match> matches = team1.matchesAgainstTeamsWithSamePointTotal();
		
		Team team1Surrogate = new Team(team1.getName());
		Team team2Surrogate = new Team(team2.getName());
		
		for(Match m : matches) {
			System.out.println(m.scoreString());
			if(m.involves(team1)) {
				if(m.winningTeam() == team1) {
					team1Surrogate.addWin();
				}
				else if(m.losingTeam() == team1) {
					team1Surrogate.addLoss();
				}
				else {
					team1Surrogate.addDraw();
				}
				if(m.homeTeam() == team1) {
					team1Surrogate.addScoreFor(m.getHomeScore());
					team1Surrogate.addScoreAgainst(m.getAwayScore());
				}
				else if(m.awayTeam() == team1) {
					team1Surrogate.addScoreAgainst(m.getHomeScore());
					team1Surrogate.addScoreAgainst(m.getAwayScore());
				}
			}
			
			if(m.involves(team2)) {
				if(m.winningTeam() == team2) {
					team2Surrogate.addWin();
				}
				else if(m.losingTeam() == team2) {
					team2Surrogate.addLoss();
				}
				else {
					team2Surrogate.addDraw();
				}
				if(m.homeTeam() == team2) {
					team2Surrogate.addScoreFor(m.getHomeScore());
					team2Surrogate.addScoreAgainst(m.getAwayScore());
				}
				else if(m.awayTeam() == team2) {
					team2Surrogate.addScoreAgainst(m.getHomeScore());
					team2Surrogate.addScoreAgainst(m.getAwayScore());
				}
			}
		}
		
		for(Tiebreaker tb : tiebreakers) {
			if(tb == this) {
				continue;
			}
			//System.out.println("Inside head-to-head, using tiebreaker " + tb);
			if(tb.compare(team1Surrogate, team2Surrogate) != 0) {
				return tb.compare(team1Surrogate, team2Surrogate);
			}
		}
		
		return 0;
	}
	
	public String toString() {
		return "Matches between teams";
	}

}
