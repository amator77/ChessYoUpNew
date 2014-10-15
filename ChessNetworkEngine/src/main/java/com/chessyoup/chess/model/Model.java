package com.chessyoup.chess.model;

import java.util.List;

public interface Model {
	
	public interface GameListener {
		
		public void onMove(Model source , Move move);
		
		public void onResult(Model source);
	}
		
	public List<Move> getMoves();

	public Position getCurrentPosition();
			
	public Result getResult();
	
	public void setResult(Result result);
	
	public void applyMove(Move move);

	public void addGameListener(GameListener listener);
	
	public void removeGameListener(GameListener listener);
	
	public void reset();
	
	public void goToStart();
	
	public void goToEnd();
	
	public void goForward();
	
	public void goBack();
	
	public void jumpTo(int moveIndex);
}
