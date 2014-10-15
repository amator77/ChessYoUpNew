package com.chessyoup.chess.game;

public interface Rating {
	
	public enum TYPE { BULLET , BLITZ , STANDARD }
	
	public TYPE getType();
	
	public double getRating();
	
	public String getDisplayRating();
	
	public double getRatingDeviation();
	
	public double getVolatility();
}
