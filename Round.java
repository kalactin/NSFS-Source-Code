package com.ten93.nsfs.knockout;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import com.ten93.nsfs.NSFS;
import com.ten93.nsfs.Team;
import com.ten93.nsfs.comparison.PointsTiebreaker;
import com.ten93.nsfs.comparison.Tiebreaker;
import com.ten93.nsfs.sports.Sport;

public class Round implements Serializable {
	
	private KnockoutTournament tournament;
	private Vector<Team> teams;
	//private Vector<Match> matches;
	private Vector<Series> series;
	private Sport sport;
	private String name;
	//private int numTeams;
	private boolean neutralGround;
	private boolean useAwayGoals;
	private boolean randomizeTeams;
	private int seriesLength = 2;
	private int seriesType = Series.AGGREGATE_SCORE;
	private static final long serialVersionUID = 301;
	
	public Round(KnockoutTournament t, String n, Sport sp) {
		tournament = t;
		teams = new Vector<Team>();
		//matches = new Vector<Match>();
		series = new Vector<Series>();
		sport = sp;
		name = n;
	}
	
	public void addTeam(Team t) {
		teams.add(t);
	}
	
	public void removeTeam(Team t) {
		teams.remove(t);
	}
	
	public void buildMatches() {
		if(randomizeTeams) {
			randomizeTeams();
		}
		Iterator<Team> it = teams.iterator();
		Team firstTeam = null;
		while(it.hasNext()) {
			if(firstTeam == null) {
				firstTeam = it.next();
			}
			else {
				//matches.add(new Match(firstTeam, it.next(), sport, true));
				Team nextTeam = it.next();
				series.add(new Series(sport, firstTeam, nextTeam, seriesLength, neutralGround, seriesType, useAwayGoals));
				firstTeam = null;
			}
		}
	}
	
	public Vector<Team> scorinate() {
		Vector<Team> advancingTeams = new Vector<Team>();
		buildMatches();
		NSFS.out.append(System.getProperty("line.separator")); // temporary
		NSFS.out.append(name + System.getProperty("line.separator"));
		for(Series s : series) {
			s.scorinate();
			advancingTeams.add(s.winningTeam());
			//NSFS.out.append(m.scoreString() + System.getProperty("line.separator")); // temporary
		}
		if(teams.size() % 2 == 1) {
			advancingTeams.add(teams.lastElement());
		}
		return advancingTeams;
	}
	
	public boolean isEmpty() {
		return (teams.size() == 0);
	}
	
	public int numTeams(int inherited) {
		return teams.size() + inherited;
	}
	
	public int numTeams() {
		return numTeams(0);
	}
	
	public int numAdvancingTeams(int inherited) {
		int totalTeams = teams.size() + inherited;
		if(totalTeams == 1)
			return 0;
		if(totalTeams % 2 == 0)
			return totalTeams / 2;
		else
			return (totalTeams + 1) / 2;
	}
	
	public int numAdvancingTeams() {
		return numAdvancingTeams(0);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public Vector<Team> getTeams() {
		return teams;
	}
	
	/*
	public void setNumTeams(int nt) {
		numTeams = nt;
	}
	*/
	
	public void reset() {
		teams.clear();
		//matches.clear();
		series.clear();
	}
	
	private void randomizeTeams() {
		//System.out.println("Randomizing teams...");
		Vector<Team> newTeams = new Vector<Team>();
		double[] dbl = new double[teams.size()];
		Random rand = new Random();
		for(int i = 0; i < dbl.length; i++) {
			dbl[i] = rand.nextDouble();
			//System.out.println(dbl[i]);
		}
		Team[] sortedTeams = sort(dbl, teams);
		for(int i = 0; i < sortedTeams.length; i++) {
			newTeams.add(sortedTeams[i]);
		}
		teams = newTeams;
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
	
	private Team[] sort(double[] dbl, Vector<Team> tms) {
		Team[] sortedTeams = tms.toArray(new Team[0]);
		for(int i = 1; i < dbl.length; i++) {
			double d = dbl[i];
			Team t = sortedTeams[i];
			int j = i - 1;
			boolean done = false;
			while(!done) {
				done = false;
				if(dbl[j] < d) {
					dbl[j+1] = dbl[j];
					sortedTeams[j+1] = sortedTeams[j];
					j--;
					if(j < 0) {
						done = true;
					}
					//break;
				}
				else if(dbl[j] > d) {
					done = true;
					break;
				}
				else {
					done = true;
				}
			}
			dbl[j+1] = d;
			sortedTeams[j+1] = t;
		}
		return sortedTeams;
	}
	
	public void setSport(Sport sp) {
		sport = sp;
	}
	
	public void setNeutralGround(boolean neutral) {
		neutralGround = neutral;
	}
	
	public void setUseAwayGoals(boolean uag) {
		useAwayGoals = uag;
	}
	
	public void setRandomizeTeams(boolean rt) {
		randomizeTeams = rt;
	}
	
	public boolean teamsRandomized() {
		return randomizeTeams;
	}
	
	public void setSeriesLength(int sl) {
		seriesLength = sl;
	}
	
	public int getSeriesLength() {
		return seriesLength;
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
	
	public void setSeriesType(int type) {
		seriesType = type;
	}
	
	public int getSeriesType() {
		return seriesType;
	}
	
	public boolean awayGoalsUsed() {
		return useAwayGoals;
	}
	
	public boolean neutralGround() {
		return neutralGround;
	}

}
