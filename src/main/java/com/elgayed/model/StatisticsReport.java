package com.elgayed.model;

import java.io.Serializable;

/**
 * Holds information about:
 * <ul>
 * <li>Least wordy speaker</li>
 * <li>The speaker with most speeches in 2013</li>
 * <li>The speaker with most speeches about internal security</li>
 * </ul>
 */
public class StatisticsReport implements Serializable{
	
	private static final long serialVersionUID = 4876172865021316130L;
	
	private String mostSpeeches;
	private String mostSecurity;
	private String leastWordy;
	
	public StatisticsReport(String mostSpeeches, String mostSecurity, String leastWordy) {
		super();
		this.mostSpeeches = mostSpeeches;
		this.mostSecurity = mostSecurity;
		this.leastWordy = leastWordy;
	}
	
	/**
	 * @return The name of the speaker with most speeches in 2013
	 */
	public String getMostSpeeches() {
		return mostSpeeches;
	}
	
	/**
	 * @return The name of the speaker with most speeches about internal security
	 */
	public String getMostSecurity() {
		return mostSecurity;
	}
	
	/**
	 * @return The name of the speaker with least words in their speeches
	 */
	public String getLeastWordy() {
		return leastWordy;
	}

	@Override
	public String toString() {
		return "StatisticsReport [mostSpeeches=" + mostSpeeches + ", mostSecurity=" + mostSecurity + ", leastWordy="
				+ leastWordy + "]";
	}
}
