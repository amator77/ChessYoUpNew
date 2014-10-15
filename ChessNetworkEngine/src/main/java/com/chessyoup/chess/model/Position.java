package com.chessyoup.chess.model;

public interface Position {

    /**
     * Get the piece for this square.
     *
     * @param square
     * @return - the piece , or null for no piece
     */
    public Piece getPieceAt(Square square);

    /**
     * Get side to move.
     *
     * @return
     */
    public Color getActiveColor();

    /**
     * Get the enpassant square if any
     *
     * @return - the square or null
     */
    public Square getEnpassantSquare();

    /**
     * @return
     */
    public boolean isWhiteKingSideCastleAvailable();

    /**
     * @return
     */
    public boolean isWhiteQueenSideCastleCastleAvailable();

    /**
     * @return
     */
    public boolean isBlackKingSideCastleAvailable();

    /**
     * @return
     */
    public boolean isBlackQueenSideCastleAvailable();

    /**
     * Halfmove clock: This is the number of halfmoves since the last capture or pawn advance.
     * This is used to determine if a draw can be claimed under the fifty-move rule.
     * @return
     */
    public int getHalfMoveClock();

    /**
     * Fullmove number: The number of the full move. It starts at 1, and is incremented after Black's move.
     * @return
     */
    public int getFullMoveNumber();
}