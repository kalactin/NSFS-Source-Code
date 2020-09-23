package com.ten93.nsfs.knockout;

import java.io.Serializable;

import com.ten93.nsfs.Match;
import com.ten93.nsfs.NSFS;
import com.ten93.nsfs.Team;
import com.ten93.nsfs.sports.Sport;

public class Series implements Serializable {	
	private static final long serialVersionUID = 301;
	
	public static final int AGGREGATE_SCORE = 1;
	public static final int BEST_OF_X = 2;
	
	private Sport sport;
	private Team homeTeam, awayTeam;
	private int homeTeamAgg, awayTeamAgg; // for aggregate score series
	private int homeAwayAgg, awayAwayAgg; // for away goals tiebreaker in aggregate score series
	private int homeWins, awayWins; // for best-of-X series
	private int seriesType;
	private Match[] matches;
	private boolean awayGoalsRule;
	
	public Series(Sport sp, Team home, Team away, int numMatches, boolean neutral, int type, boolean agr) {
		sport = sp;
		homeTeam = home;
		awayTeam = away;
		seriesType = type;
		matches = new Match[numMatches];
		awayGoalsRule = agr;
		for(int i = 0; i < numMatches; i++) {
			if(i % 2 == 0) {
				if(seriesType == AGGREGATE_SCORE) {
					matches[i] = new Match(home, away, sport, neutral, true, agr);
				}
				else if(seriesType == BEST_OF_X) {
					matches[i] = new Match(home, away, sport, neutral, false, false);
				}
			}
			else {
				if(seriesType == AGGREGATE_SCORE) {
					matches[i] = new Match(away, home, sport, neutral, true, agr);
				}
				else if(seriesType == BEST_OF_X) {
					matches[i] = new Match(away, home, sport, neutral, false, false);
				}
			}
		}
	}
	
	public void scorinate() {
		for(int i = 0; i < matches.length && !seriesOver(i); i++) {
			Match m = matches[i];
			if(seriesType == AGGREGATE_SCORE) {
				if(i % 2 == 0) {
					m.setIncomingAggregateScores(homeTeamAgg, awayTeamAgg);
					m.setIncomingAwayAggregateScores(homeAwayAgg, awayAwayAgg);
				}
				else {
					m.setIncomingAggregateScores(awayTeamAgg, homeTeamAgg);
					m.setIncomingAwayAggregateScores(awayAwayAgg, homeAwayAgg);
				}
				if(i == matches.length - 1) {
					// if last match in series, we can't have a draw
					sport.setDrawsAllowed(false);
				}
				else {
					sport.setDrawsAllowed(true);
				}
			}
			else if(seriesType == BEST_OF_X) {
				sport.setDrawsAllowed(false);
			}
			m.scorinate();
			if(seriesType == AGGREGATE_SCORE) {
				if(i % 2 == 0) {
					homeTeamAgg += m.getHomeScore();
					awayTeamAgg += m.getAwayScore();
					awayAwayAgg += m.getAwayScore();
				}
				else {
					awayTeamAgg += m.getHomeScore();
					homeTeamAgg += m.getAwayScore();
					homeAwayAgg += m.getAwayScore();
				}
			}
			else if(seriesType == BEST_OF_X) {
				if(m.winningTeam() == homeTeam) {
					homeWins++;
				}
				else if(m.winningTeam() == awayTeam) {
					awayWins++;
				}
			}
			if(seriesType == AGGREGATE_SCORE) {
				NSFS.out.append(m.scoreString() + System.getProperty("line.separator"));
			}
			else if(seriesType == BEST_OF_X) {
				if(homeWins == awayWins) {
					NSFS.out.append(m.scoreString() + " [series tied " + homeWins + "-" + awayWins + "]" + System.getProperty("line.separator"));
				}
				else {
					NSFS.out.append(m.scoreString() + " [" + winningTeam() + " leads series " + leadingTeamWins() + "-" + trailingTeamWins() + "]" + System.getProperty("line.separator"));
				}
			}
		}
		NSFS.out.append(System.getProperty("line.separator"));
	}
	
	private int leadingTeamWins() {
		if(homeWins > awayWins)
			return homeWins;
		else
			return awayWins;
	}
	
	private int trailingTeamWins() {
		if(homeWins < awayWins)
			return homeWins;
		else
			return awayWins;
	}
	
	public Team winningTeam() {
		if(seriesType == AGGREGATE_SCORE) {
			if(homeTeamAgg > awayTeamAgg) {
				return homeTeam;
			}
			else if(awayTeamAgg > homeTeamAgg) {
				return awayTeam;
			}
			else {
				// if these are equal, check for away goals
				if(awayGoalsRule) {
					if(homeAwayAgg > awayAwayAgg) {
						return homeTeam;
					}
					else if(awayAwayAgg > homeAwayAgg) {
						return awayTeam;
					}
				}
				// otherwise, the final match must've gone to a shootout
				return matches[matches.length - 1].winningTeam();
			}
		}
		else if(seriesType == BEST_OF_X) {
			if(homeWins > awayWins) {
				return homeTeam;
			}
			else if(awayWins > homeWins) {
				return awayTeam;
			}
			else {
				return null;
			}
		}
		return null;
	}
	
	private boolean seriesOver(int matchesPlayed) {
		if(seriesType == AGGREGATE_SCORE) {
			return false;
		}
		else if(seriesType == BEST_OF_X) {
			int matchesRemaining = matches.length - matchesPlayed;
			if(Math.abs(homeWins - awayWins) > matchesRemaining) {
				return true;
			}
			return false;
		}
		return false;
	}

}
