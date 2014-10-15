package com.chessyoup.chess.game;

import com.chessyoup.chess.model.Color;


public interface Clock {
	
	public interface ClockListener {
		
		public void onStop(Clock source);
		
		public void onTimeUpdate(Clock source);
		
		public void onClockPress(Clock source);
		
		public void onFlag(Clock source,Color color);
	}
	
	public enum STATE { NOT_RUNNING , RUNNING }
	
	public TimeControll getTimeControll();
	
	public STATE getState();
	
	public void press();
	
	public void start();
	
	public void stop();
	
	public Color getRunningColor(); 
	
	public void reset(TimeControll timeControll);
	
	public long getWhiteTime();
	
	public long getBlackTime();
	
	public void addClockListener();
	
	public void removeClockListener();
}
