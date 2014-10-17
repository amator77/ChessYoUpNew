package com.chessyoup.chess.game;

import com.chessyoup.chess.game.ui.ChessGameUI;
import com.chessyoup.chess.model.Chessboard;
import com.chessyoup.chess.model.Color;

public interface Game {

	public enum STATE {
		NOT_READY, READY, IN_PROGRESS
	}

	public enum OFFER {
		DRAW, ABORT, REMATCH
	}

	public String getId();

	public Chessboard getChessboard();

	public void setPlayer(Player player, Color color);

	public Player getWhitePlayer();

	public Player getBlackPlayer();
	
	public OFFER getLastOffer();
	
	public Clock getClock();

	public ChessGameUI getGameUI();

	public Service getGameService();

	public STATE getState();
}
