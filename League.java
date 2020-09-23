package com.ten93.nsfs.league;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import com.ten93.nsfs.NSFS;
import com.ten93.nsfs.sports.Sport;
import com.ten93.nsfs.comparison.Tiebreaker;

public class League implements Serializable {
	private Vector<Division> divisions;
	private String name;
	private int divisionMatches;
	private int nonDivisionMatches;
	private LeagueSchedule schedule;
	private LeagueSystem system;
	private static final long serialVersionUID = 300;
	
	public League(String lgName, LeagueSystem sys) {
		name = lgName;
		divisions = new Vector<Division>();
		setSystem(sys);
		divisionMatches = 2;
		nonDivisionMatches = 1;
	}
	
	public void addDivision(Division div) {
		divisions.add(div);
	}
	
	public void removeDivision(Division div) {
		divisions.remove(div);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String n) {
		name = n;
	}
	
	/**
	 * 
	 * @return the number of matches each team plays against each other team in its division
	 */
	public int divisionMatches() {
		return divisionMatches;
	}
	
	public void setDivisionMatches(int dm) {
		divisionMatches = dm;
	}
	
	public int numMatchdays() {
		return schedule.numMatchdays();
	}
	
	public int numMatchesPerTeam() {
		if(divisionSize() == -1)
			return -1;
		return (divisionSize() - 1) * divisionMatches + divisionSize() * (divisions.size() - 1) * nonDivisionMatches;
	}
	
	/**
	 * 
	 * @return the number of matches each team plays against each team <b>not</b> in its division
	 */
	public int nonDivisionMatches() {
		return nonDivisionMatches;
	}
	
	public void setNonDivisionMatches(int ndm) {
		nonDivisionMatches = ndm;
	}
	
	/**
	 * 
	 * @return true if at least one division exists, all divisions have at least two teams, and all divisions have an equal number of teams; false otherwise
	 */
	public boolean isValid() {
		if(numDivisions() == 0) {
			return false;
		}
		for(Division d : divisions) {
			if(d.size() != divisions.get(0).size()) {
				return false;
			}
			if(d.size() < 2) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 
	 * @return -1 if all divisions are not the same size; otherwise division size
	 */
	public int divisionSize() {
		if(!isValid()) {
			return -1;
		}
		return divisions.get(0).size();
	}
	
	public int numDivisions() {
		return divisions.size();
	}
	
	public void schedule(Sport sp, boolean neutral) {
		for(Division d : divisions) {
			d.initialize();
		}
		schedule = new LeagueSchedule(this, sp, neutral);
		schedule.generate();
	}
	
	public Division getDivision(int index) {
		return divisions.get(index);
	}
	
	public Vector<Division> getDivisions() {
		return divisions;
	}
	
	public void scorinate(int startDay, int numDays) {
		schedule.scorinate(startDay, numDays);
	}
	
	public void printStandings(List<Tiebreaker> tiebreakers) {
		for(Division d : divisions) {
			d.printStandings(tiebreakers);
			NSFS.out.append(System.getProperty("line.separator"));
			NSFS.out.append(System.getProperty("line.separator"));
		}
	}
	
	public String toString() {
		return name;
	}
	
	public void clearSchedule() {
		schedule = null;
	}
	
	public int getLastCompletedMatchday() {
		return schedule.getLastCompletedMatchday();
	}
	
	public LeagueSchedule getSchedule() {
		return schedule;
	}

	public void setSystem(LeagueSystem system) {
		this.system = system;
	}

	public LeagueSystem getSystem() {
		return system;
	}
}
