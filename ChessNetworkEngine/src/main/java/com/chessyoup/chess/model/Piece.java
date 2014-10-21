package com.chessyoup.chess.model;

public interface Piece {

	public enum Type {
		KING, QUEEN, ROOK, KNIGHT, BISHOP, PAWN , NONE
	}

	public Color getColor();

	public Type getType();
}
