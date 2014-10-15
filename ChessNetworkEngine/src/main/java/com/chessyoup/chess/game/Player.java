package com.chessyoup.chess.game;

import com.chessyoup.chess.game.Game.OFFER;
import com.chessyoup.chess.model.Model;
import com.chessyoup.chess.model.Move;

public interface Player {
	
	public interface PlayerListener {
		
		public void onMove(Model game, Move move);
		
		public void onOffer(Model game,OFFER offer);
		
		public void onResign(Model game);
	}
	
	public String getPlayerId();
	
	public Rating getRating(Rating.TYPE type);
	
	public void updateRating(Rating.TYPE type, double newRating);
	
	public void addPlayerListener(PlayerListener listener);
	
	public void removePlayerListener(PlayerListener listener);
}
