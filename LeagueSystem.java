package com.ten93.nsfs.league;

import java.io.Serializable;
import java.util.Vector;

import com.ten93.nsfs.NSFS;
import com.ten93.nsfs.sports.Sport;
import com.ten93.nsfs.comparison.HeadToHeadTiebreaker;
import com.ten93.nsfs.comparison.PointsTiebreaker;
import com.ten93.nsfs.comparison.Tiebreaker;
import com.ten93.nsfs.comparison.WinsTiebreaker;

public class LeagueSystem implements Serializable {
	private Vector<League> leagues;
	private String name;
	private Sport sport;
	private Vector<Tiebreaker> tiebreakers;
	private static final long serialVersionUID = 300;
	
	public LeagueSystem(String lsName, Sport sp) {
		name = lsName;
		sport = sp;
		leagues = new Vector<League>();
		tiebreakers = new Vector<Tiebreaker>();
		
		// temporary
		tiebreakers.add(PointsTiebreaker.getInstance());
		tiebreakers.add(HeadToHeadTiebreaker.getInstance());
		tiebreakers.add(WinsTiebreaker.getInstance());
		
		for(Tiebreaker t : tiebreakers) {
			t.setSport(sport);
		}
	}
	
	public void setSport(Sport sp) {
		sport = sp;
	}
	
	public Sport getSport() {
		return sport;
	}
	
	public void addTiebreaker(Tiebreaker tb) {
		tb.setSport(sport);
		tiebreakers.add(tb);
	}
	
	public void removeTiebreaker(Tiebreaker tb) {
		tiebreakers.remove(tb);
	}
	
	public void shiftTiebreakerDown(Tiebreaker tb) {
		int index = tiebreakers.indexOf(tb);
		if(index == tiebreakers.size() - 1) {
			return;
		}
		tiebreakers.remove(tb);
		tiebreakers.add(index + 1, tb);
	}
	
	public void shiftTiebreakerUp(Tiebreaker tb) {
		int index = tiebreakers.indexOf(tb);
		if(index == 0) {
			return;
		}
		tiebreakers.remove(tb);
		tiebreakers.add(index - 1, tb);
	}
	
	public void addLeague(League lg) {
		leagues.add(lg);
	}
	
	public void removeLeague(League lg) {
		leagues.remove(lg);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String n) {
		name = n;
	}
	
	public Vector<League> getLeagues() {
		return leagues;
	}
	
	public void schedule(boolean neutral) {
		for(League lg : leagues) {
			lg.schedule(sport, neutral);
		}
	}
	
	public void schedule() {
		for(League lg : leagues) {
			lg.schedule(sport, false);
		}
	}
	
	public void scorinate(int startDay, int numDays) {
		NSFS.out = new StringBuffer(); // clear out old results
		
		for(Tiebreaker t : tiebreakers) {
			t.setSport(sport);
		}
		
		for(League lg : leagues) {
			lg.scorinate(startDay, numDays);
		}
	}
	
	public void printStandings() {
		for(League lg : leagues) {
			lg.printStandings(tiebreakers);
		}
	}
	
	public void clearSchedules() {
		for(League lg : leagues) {
			lg.clearSchedule();
		}
	}
	
	public int getLastCompletedMatchday() {
		return leagues.get(0).getLastCompletedMatchday();
	}
	
	public Vector<Tiebreaker> getTiebreakers() {
		return tiebreakers;
	}
	
	public Vector<Tiebreaker> unusedTiebreakers() {
		Vector<Tiebreaker> all = NSFS.availableTiebreakers;
		Vector<Tiebreaker> unused = new Vector<Tiebreaker>();
		for(Tiebreaker t : all) {
			if(!tiebreakers.contains(t)) {
				unused.add(t);
			}
		}
		return unused;
	}
}
