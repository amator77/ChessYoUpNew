package com.chessyoup.chess.game;

import com.chessyoup.chess.game.Game.OFFER;
import com.chessyoup.chess.model.Move;

public interface Player {
	
	public interface PlayerListener {
		
		public void onMove(Move move);
		
		public void onOffer(OFFER offer);
		
		public void onResign();
	}
	
	public String getPlayerId();
	
	public Rating getRating(Rating.TYPE type);
	
	public void updateRating(Rating.TYPE type, double newRating);
	
	public void addPlayerListener(PlayerListener listener);
	
	public void removePlayerListener(PlayerListener listener);
}
