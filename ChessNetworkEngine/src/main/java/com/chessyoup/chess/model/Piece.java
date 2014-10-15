package com.chessyoup.chess.model;

public interface Piece {
		
	public enum PieceType {
		KING, QUEEN, ROOK, KNIGHT, BISHOP, PAWN
	}

	public Color getColor();

	public PieceType getType();
}
