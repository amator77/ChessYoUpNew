package com.chessyoup.chess.game.ui;

import com.chessyoup.chess.game.Game;
import com.chessyoup.chess.game.Player;

public interface UI {
	
	public interface ChessGameUIListener{
		
	}
	
	public Player getPlayerById(String playerId);
	
	public Game getChessGame();
	
	public void addChessGameUIListener(ChessGameUIListener listener);
	
	public void removeChessGameUIListener(ChessGameUIListener listener);	
}
