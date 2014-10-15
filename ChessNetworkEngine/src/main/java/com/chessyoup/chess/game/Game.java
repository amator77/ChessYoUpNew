package com.chessyoup.chess.game;

import com.chessyoup.chess.game.ui.ChessGameUI;
import com.chessyoup.chess.model.Color;
import com.chessyoup.chess.model.Model;

public interface Game {

	public enum STATE {
		NOT_READY, READY, IN_PROGRESS
	}

	public enum OFFER {
		DRAW, ABORT, REMATCH
	}

	public String getId();

	public Model getGame();

	public void setPlayer(Player player, Color color);

	public Player getWhitePlayer();

	public Player getBlackPlayer();
	
	public OFFER getLastOffer();
	
	public Clock getClock();

	public ChessGameUI getGameUI();

	public Service getGameService();

	public STATE getState();
}
