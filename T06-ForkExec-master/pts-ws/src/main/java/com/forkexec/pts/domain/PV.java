package com.forkexec.pts.domain;


/**
 * Points
 * <p>
 * A points server.
 */
public class PV {
	private int points;
	private int tag;

	public PV(int value, int tag){
		this.points = value;
		this.tag = tag;
	}

	public int getPoints(){
		return this.points;
	}

	public int getTag(){
		return this.tag;
	}	

}
