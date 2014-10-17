package com.chessyoup.chess.game.impl;

import com.chessyoup.chess.game.Game;
import com.chessyoup.chess.game.Clock;
import com.chessyoup.chess.game.Service;
import com.chessyoup.chess.game.Player;
import com.chessyoup.chess.game.ui.ChessGameUI;
import com.chessyoup.chess.model.Chessboard;
import com.chessyoup.chess.model.Color;


public class ChessGameImpl implements Game {

	public String gameId;

	public Chessboard game;

	public Player whitePlayer;

	public Player blackPlayer;

	public Clock clock;

	public OFFER lastOffer;

	public STATE state;

	public ChessGameImpl(String gameId, Chessboard game) {
		this.gameId = gameId;
		this.game = game;
		this.whitePlayer = null;
		this.blackPlayer = null;
		this.lastOffer = null;
		this.state = STATE.NOT_READY;
	}

	@Override
	public String getId() {
		return this.gameId;
	}

    @Override
    public Chessboard getChessboard() {
        return this.getChessboard();
    }


    @Override
	public void setPlayer(Player player, Color color) {
		switch (color) {
		case WHITE:
			this.whitePlayer = player;
			break;
		case BLACK:
			this.blackPlayer = player;
			break;
		default:
			break;
		}
	}

	@Override
	public Player getWhitePlayer() {
		return this.whitePlayer;
	}

	@Override
	public Player getBlackPlayer() {
		return this.blackPlayer;
	}

	@Override
	public OFFER getLastOffer() {
		return null;
	}

	@Override
	public Clock getClock() {
		return this.clock;
	}

	@Override
	public Service getGameService() {
		return null;
	}

	@Override
	public STATE getState() {
		return this.state;
	}

	@Override
	public ChessGameUI getGameUI() {
		// TODO Auto-generated method stub
		return null;
	}
}
