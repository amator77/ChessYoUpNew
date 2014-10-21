package com.chessyoup.chess.model;

public interface Move {
	
	public Square getSource();
	
	public Square getDestination();

    public Piece getPromotionPiece();
}
