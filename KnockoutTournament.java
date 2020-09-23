package com.ten93.nsfs.knockout;

import java.io.Serializable;
import java.util.Vector;

import com.ten93.nsfs.NSFS;
import com.ten93.nsfs.Team;
import com.ten93.nsfs.sports.Sport;

public class KnockoutTournament implements Serializable {

	private Vector<Round> rounds;
	//private Vector<Team> teams;
	private Sport sport;
	private String name;
	private int lastCompletedRound = -1;
	//private int seriesType;
	//private int seriesLength;
	//private boolean randomizeTeams;
	//private boolean useAwayGoals;
	//private boolean neutralGround;
	private static final long serialVersionUID = 301;
	
	public KnockoutTournament(Sport sp) {
		this(sp, Series.AGGREGATE_SCORE);
	}
	
	public KnockoutTournament(Sport sp, int type) {
		//seriesType = type;
		rounds = new Vector<Round>();
		rounds.add(new Round(this, "First Round", sp));
		//teams = new Vector<Team>();
		sport = sp;
		sport.setDrawsAllowed(false);
		setRandomizeTeams(false);
	}
	
	/*
	public void addTeam(Team t) {
		teams.add(t);
	}
	*/
	
	/*
	private void buildRounds() {
		int teamIndex = 0;
		for(Round r : rounds) {
			r.reset();
			for(int i = 0; i < r.numTeams(); i++, teamIndex++) {
				r.addTeam(teams.get(teamIndex));
			}
		}

		int numRounds = 0;
		rounds.clear();
		while(Math.pow(2, numRounds) <= teams.size()) {
			numRounds++;
			rounds.add(new Round(sport));
		}
		Round firstRound = rounds.get(0);
		for(Team t : teams) {
			firstRound.addTeam(t);
		}

		for(int i = 0; i < Math.pow(2, numRounds); i++) {
			firstRound.addTeam(teams.get(i));
		}
	}
	*/
	
	public void scorinate(int startRound, int numRounds) {
		NSFS.out = new StringBuffer(); // clear out old results
		int endRound = startRound + numRounds;
		if(numRounds < 1) {
			endRound = rounds.size();
		}
		if(endRound > rounds.size()) {
			endRound = rounds.size();
		}
		Vector<Team> advancingTeams = null;
		/*
		if(startRound == 0)
			buildRounds();
		*/
		for(int i = startRound; i < endRound; i++) {
			Round r = rounds.get(i);
			advancingTeams = r.scorinate();
			if(i < rounds.size() - 1 && advancingTeams != null && advancingTeams.size() > 0) {
				for(Team t : advancingTeams) {
					Round next = rounds.get(i+1);
					next.addTeam(t);
				}
			}
			setLastCompletedRound(i);
		}
	}
	
	public Sport getSport() {
		return sport;
	}
	
	public void setSport(Sport sp) {
		sport = sp;
		for(Round r : rounds) {
			r.setSport(sp);
		}
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String n) {
		name = n;
	}
	
	/*
	public Vector<Team> getTeams() {
		return teams;
	}
	
	public void removeTeam(Team t) {
		teams.remove(t);
	}
	*/
	
	public Vector<Round> getRounds() {
		return rounds;
	}
	
	/*
	public int unassignedTeams() {
		int u = teams.size();
		for(Round r : rounds) {
			u -= r.numTeams();
		}
		return u;
	}
	*/
	
	public void addRound(Round r) {
		rounds.add(r);
	}
	
	public void removeRound(Round r) {
		rounds.remove(r);
	}
	
	public int totalTeamsInRound(Round r) {
		int roundIndex = rounds.indexOf(r);
		if(roundIndex == 0) {
			return r.numTeams();
		}
		else {
			return r.numTeams(totalAdvancingTeamsFromRound(rounds.get(roundIndex - 1)));
		}
	}
	
	public int totalAdvancingTeamsFromRound(Round r) {
		int roundIndex = rounds.indexOf(r);
		if(roundIndex == 0) {
			return r.numAdvancingTeams();
		}
		else {
			return r.numAdvancingTeams(totalAdvancingTeamsFromRound(rounds.get(roundIndex - 1)));
		}
	}

	public void setLastCompletedRound(int lastCompletedRound) {
		this.lastCompletedRound = lastCompletedRound;
	}

	public int getLastCompletedRound() {
		return lastCompletedRound;
	}
	
	/*
	public int getSeriesType() {
		return seriesType;
	}
	*/
	
	public void setSeriesType(int type) {
		//seriesType = type;
		for(Round r : rounds) {
			r.setSeriesType(type);
		}
	}
	
	public void setSeriesLength(int sl) {
		//seriesLength = sl;
		for(Round r : rounds) {
			r.setSeriesLength(sl);
		}
	}

	public void setRandomizeTeams(boolean randomizeTeams) {
		//this.randomizeTeams = randomizeTeams;
		for(Round r : rounds) {
			r.setRandomizeTeams(randomizeTeams);
		}
	}

	/*
	public boolean randomizeTeams() {
		return randomizeTeams;
	}
	*/
	
	/*
	public boolean useAwayGoals() {
		return useAwayGoals;
	}
	*/
	
	public void setUseAwayGoals(boolean uag) {
		//useAwayGoals = uag;
		for(Round r : rounds) {
			r.setUseAwayGoals(uag);
		}
	}

	public void setNeutralGround(boolean neutralGround) {
		//this.neutralGround = neutralGround;
		for(Round r : rounds) {
			r.setNeutralGround(neutralGround);
		}
	}

	/*
	public boolean isNeutralGround() {
		return neutralGround;
	}
	*/
	
	/*
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
	*/
}
