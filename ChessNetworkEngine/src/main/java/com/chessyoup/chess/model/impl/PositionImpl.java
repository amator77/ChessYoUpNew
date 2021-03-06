package com.chessyoup.chess.model.impl;

import com.chessyoup.chess.model.Color;
import com.chessyoup.chess.model.Factory;
import com.chessyoup.chess.model.Piece;
import com.chessyoup.chess.model.Position;
import com.chessyoup.chess.model.Result;
import com.chessyoup.chess.model.Square;

import java.awt.Point;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class PositionImpl implements Position {
    private int[] squares;

    public boolean whiteMove;

    /** Bit definitions for the castleMask bit mask. */
    public static final int A1_CASTLE = 0; /** White long castle. */
    public static final int H1_CASTLE = 1; /** White short castle. */
    public static final int A8_CASTLE = 2; /** Black long castle. */
    public static final int H8_CASTLE = 3; /** Black short castle. */

    private int castleMask;

    private int epSquare;

    /** Number of half-moves since last 50-move reset. */
    public int halfMoveClock;

    /** Game move number, starting from 1. */
    public int fullMoveCounter;

    private long hashKey;           // Cached Zobrist hash key
    private int wKingSq, bKingSq;   // Cached king positions

    /** Initialize board to empty position. */
    public PositionImpl() {
        squares = new int[64];
        for (int i = 0; i < 64; i++)
            squares[i] = PieceImpl.EMPTY;
        whiteMove = true;
        castleMask = 0;
        epSquare = -1;
        halfMoveClock = 0;
        fullMoveCounter = 1;
        hashKey = computeZobristHash();
        wKingSq = bKingSq = -1;
    }

    public PositionImpl(PositionImpl other) {
        squares = new int[64];
        System.arraycopy(other.squares, 0, squares, 0, 64);
        whiteMove = other.whiteMove;
        castleMask = other.castleMask;
        epSquare = other.epSquare;
        halfMoveClock = other.halfMoveClock;
        fullMoveCounter = other.fullMoveCounter;
        hashKey = other.hashKey;
        wKingSq = other.wKingSq;
        bKingSq = other.bKingSq;
    }

    @Override
    public boolean equals(Object o) {
        if ((o == null) || (o.getClass() != this.getClass()))
            return false;
        PositionImpl other = (PositionImpl)o;
        if (!drawRuleEquals(other))
            return false;
        if (halfMoveClock != other.halfMoveClock)
            return false;
        if (fullMoveCounter != other.fullMoveCounter)
            return false;
        if (hashKey != other.hashKey)
            return false;
        return true;
    }
    @Override
    public int hashCode() {
        return (int)hashKey;
    }

    /**
     * Return Zobrish hash value for the current position.
     * Everything except the move counters are included in the hash value.
     */
    public final long zobristHash() {
        return hashKey;
    }

    /**
     * Decide if two positions are equal in the sense of the draw by repetition rule.
     * @return True if positions are equal, false otherwise.
     */
    final public boolean drawRuleEquals(PositionImpl other) {
        for (int i = 0; i < 64; i++) {
            if (squares[i] != other.squares[i])
                return false;
        }
        if (whiteMove != other.whiteMove)
            return false;
        if (castleMask != other.castleMask)
            return false;
        if (epSquare != other.epSquare)
            return false;
        return true;
    }

    public final void setWhiteMove(boolean whiteMove) {
        if (whiteMove != this.whiteMove) {
            hashKey ^= whiteHashKey;
            this.whiteMove = whiteMove;
        }
    }
    /** Return index in squares[] vector corresponding to (x,y). */
    public final static int getSquare(int x, int y) {
        return y * 8 + x;
    }
    /** Return x position (file) corresponding to a square. */
    public final static int getX(int square) {
        return square & 7;
    }
    /** Return y position (rank) corresponding to a square. */
    public final static int getY(int square) {
        return square >> 3;
    }
    /** Return true if (x,y) is a dark square. */
    public final static boolean darkSquare(int x, int y) {
        return (x & 1) == (y & 1);
    }


    /** Return piece occupying a square. */
    public final int getPiece(int square) {
        return squares[square];
    }
    /** Set a square to a piece value. */
    public final void setPiece(int square, int piece) {
        // Update hash key
        int oldPiece = squares[square];
        hashKey ^= psHashKeys[oldPiece][square];
        hashKey ^= psHashKeys[piece][square];

        // Update board
        squares[square] = piece;

        // Update king position
        if (piece == PieceImpl.WKING) {
            wKingSq = square;
        } else if (piece == PieceImpl.BKING) {
            bKingSq = square;
        }
    }

    /** Return true if white long castling right has not been lost. */
    public final boolean a1Castle() {
        return (castleMask & (1 << A1_CASTLE)) != 0;
    }
    /** Return true if white short castling right has not been lost. */
    public final boolean h1Castle() {
        return (castleMask & (1 << H1_CASTLE)) != 0;
    }
    /** Return true if black long castling right has not been lost. */
    public final boolean a8Castle() {
        return (castleMask & (1 << A8_CASTLE)) != 0;
    }
    /** Return true if black short castling right has not been lost. */
    public final boolean h8Castle() {
        return (castleMask & (1 << H8_CASTLE)) != 0;
    }
    /** Bitmask describing castling rights. */
    public final int getCastleMask() {
        return castleMask;
    }
    public final void setCastleMask(int castleMask) {
        hashKey ^= castleHashKeys[this.castleMask];
        hashKey ^= castleHashKeys[castleMask];
        this.castleMask = castleMask;
    }

    /** En passant square, or -1 if no ep possible. */
    public final int getEpSquare() {
        return epSquare;
    }
    public final void setEpSquare(int epSquare) {
        if (this.epSquare != epSquare) {
            hashKey ^= epHashKeys[(this.epSquare >= 0) ? getX(this.epSquare) + 1 : 0];
            hashKey ^= epHashKeys[(epSquare >= 0) ? getX(epSquare) + 1 : 0];
            this.epSquare = epSquare;
        }
    }


    public final int getKingSq(boolean whiteMove) {
        return whiteMove ? wKingSq : bKingSq;
    }

    /**
     * Count number of pieces of a certain type.
     */
    public final int nPieces(int pType) {
        int ret = 0;
        for (int sq = 0; sq < 64; sq++) {
            if (squares[sq] == pType)
                ret++;
        }
        return ret;
    }

    /** Apply a move to the current position. */
    public final void makeMove(MoveImpl move, UndoInfo ui) {
        ui.capturedPiece = squares[move.to];
        ui.castleMask = castleMask;
        ui.epSquare = epSquare;
        ui.halfMoveClock = halfMoveClock;
        boolean wtm = whiteMove;

        int p = squares[move.from];
        int capP = squares[move.to];

        boolean nullMove = (move.from == 0) && (move.to == 0);

        if (nullMove || (capP != PieceImpl.EMPTY) || (p == (wtm ? PieceImpl.WPAWN : PieceImpl.BPAWN))) {
            halfMoveClock = 0;
        } else {
            halfMoveClock++;
        }
        if (!wtm) {
            fullMoveCounter++;
        }

        // Handle castling
        int king = wtm ? PieceImpl.WKING : PieceImpl.BKING;
        int k0 = move.from;
        if (p == king) {
            if (move.to == k0 + 2) { // O-O
                setPiece(k0 + 1, squares[k0 + 3]);
                setPiece(k0 + 3, PieceImpl.EMPTY);
            } else if (move.to == k0 - 2) { // O-O-O
                setPiece(k0 - 1, squares[k0 - 4]);
                setPiece(k0 - 4, PieceImpl.EMPTY);
            }
            if (wtm) {
                setCastleMask(castleMask & ~(1 << PositionImpl.A1_CASTLE));
                setCastleMask(castleMask & ~(1 << PositionImpl.H1_CASTLE));
            } else {
                setCastleMask(castleMask & ~(1 << PositionImpl.A8_CASTLE));
                setCastleMask(castleMask & ~(1 << PositionImpl.H8_CASTLE));
            }
        }
        if (!nullMove) {
            int rook = wtm ? PieceImpl.WROOK : PieceImpl.BROOK;
            if (p == rook) {
                removeCastleRights(move.from);
            }
            int oRook = wtm ? PieceImpl.BROOK : PieceImpl.WROOK;
            if (capP == oRook) {
                removeCastleRights(move.to);
            }
        }

        // Handle en passant and epSquare
        int prevEpSquare = epSquare;
        setEpSquare(-1);
        if (p == PieceImpl.WPAWN) {
            if (move.to - move.from == 2 * 8) {
                int x = PositionImpl.getX(move.to);
                if (    ((x > 0) && (squares[move.to - 1] == PieceImpl.BPAWN)) ||
                        ((x < 7) && (squares[move.to + 1] == PieceImpl.BPAWN))) {
                    setEpSquare(move.from + 8);
                }
            } else if (move.to == prevEpSquare) {
                setPiece(move.to - 8, PieceImpl.EMPTY);
            }
        } else if (p == PieceImpl.BPAWN) {
            if (move.to - move.from == -2 * 8) {
                int x = PositionImpl.getX(move.to);
                if (    ((x > 0) && (squares[move.to - 1] == PieceImpl.WPAWN)) ||
                        ((x < 7) && (squares[move.to + 1] == PieceImpl.WPAWN))) {
                    setEpSquare(move.from - 8);
                }
            } else if (move.to == prevEpSquare) {
                setPiece(move.to + 8, PieceImpl.EMPTY);
            }
        }

        // Perform move
        setPiece(move.from, PieceImpl.EMPTY);
        // Handle promotion
        if (move.promoteTo != PieceImpl.EMPTY) {
            setPiece(move.to, move.promoteTo);
        } else {
            setPiece(move.to, p);
        }
        setWhiteMove(!wtm);
    }

    public final void unMakeMove(MoveImpl move, UndoInfo ui) {
        setWhiteMove(!whiteMove);
        int p = squares[move.to];
        setPiece(move.from, p);
        setPiece(move.to, ui.capturedPiece);
        setCastleMask(ui.castleMask);
        setEpSquare(ui.epSquare);
        halfMoveClock = ui.halfMoveClock;
        boolean wtm = whiteMove;
        if (move.promoteTo != PieceImpl.EMPTY) {
            p = wtm ? PieceImpl.WPAWN : PieceImpl.BPAWN;
            setPiece(move.from, p);
        }
        if (!wtm) {
            fullMoveCounter--;
        }

        // Handle castling
        int king = wtm ? PieceImpl.WKING : PieceImpl.BKING;
        int k0 = move.from;
        if (p == king) {
            if (move.to == k0 + 2) { // O-O
                setPiece(k0 + 3, squares[k0 + 1]);
                setPiece(k0 + 1, PieceImpl.EMPTY);
            } else if (move.to == k0 - 2) { // O-O-O
                setPiece(k0 - 4, squares[k0 - 1]);
                setPiece(k0 - 1, PieceImpl.EMPTY);
            }
        }

        // Handle en passant
        if (move.to == epSquare) {
            if (p == PieceImpl.WPAWN) {
                setPiece(move.to - 8, PieceImpl.BPAWN);
            } else if (p == PieceImpl.BPAWN) {
                setPiece(move.to + 8, PieceImpl.WPAWN);
            }
        }
    }

    private final void removeCastleRights(int square) {
        if (square == PositionImpl.getSquare(0, 0)) {
            setCastleMask(castleMask & ~(1 << PositionImpl.A1_CASTLE));
        } else if (square == PositionImpl.getSquare(7, 0)) {
            setCastleMask(castleMask & ~(1 << PositionImpl.H1_CASTLE));
        } else if (square == PositionImpl.getSquare(0, 7)) {
            setCastleMask(castleMask & ~(1 << PositionImpl.A8_CASTLE));
        } else if (square == PositionImpl.getSquare(7, 7)) {
            setCastleMask(castleMask & ~(1 << PositionImpl.H8_CASTLE));
        }
    }

    /* ------------- Hashing code ------------------ */

    private static long[][] psHashKeys;    // [piece][square]
    private static long whiteHashKey;
    private static long[] castleHashKeys;  // [castleMask]
    private static long[] epHashKeys;      // [epFile + 1] (epFile==-1 for no ep)

    static {
        psHashKeys = new long[PieceImpl.nPieceTypes][64];
        castleHashKeys = new long[16];
        epHashKeys = new long[9];
        int rndNo = 0;
        for (int p = 0; p < PieceImpl.nPieceTypes; p++) {
            for (int sq = 0; sq < 64; sq++) {
                psHashKeys[p][sq] = getRandomHashVal(rndNo++);
            }
        }
        whiteHashKey = getRandomHashVal(rndNo++);
        for (int cm = 0; cm < castleHashKeys.length; cm++)
            castleHashKeys[cm] = getRandomHashVal(rndNo++);
        for (int f = 0; f < epHashKeys.length; f++)
            epHashKeys[f] = getRandomHashVal(rndNo++);
    }

    /**
     * Compute the Zobrist hash value non-incrementally. Only useful for test programs.
     */
    final long computeZobristHash() {
        long hash = 0;
        for (int sq = 0; sq < 64; sq++) {
            int p = squares[sq];
            hash ^= psHashKeys[p][sq];
        }
        if (whiteMove)
            hash ^= whiteHashKey;
        hash ^= castleHashKeys[castleMask];
        hash ^= epHashKeys[(epSquare >= 0) ? getX(epSquare) + 1 : 0];
        return hash;
    }

    private final static long getRandomHashVal(int rndNo) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] input = new byte[4];
            for (int i = 0; i < 4; i++)
                input[i] = (byte)((rndNo >> (i * 8)) & 0xff);
            byte[] digest = md.digest(input);
            long ret = 0;
            for (int i = 0; i < 8; i++) {
                ret ^= ((long)digest[i]) << (i * 8);
            }
            return ret;
        } catch (NoSuchAlgorithmException ex) {
            throw new UnsupportedOperationException("SHA-1 not available");
        }
    }

    public Result computeResult() {

        ArrayList<MoveImpl> moves = new MoveGen().legalMoves(this);

        if (moves.size() == 0) {
            if (MoveGen.inCheck(this)) {
                return new Result( this.whiteMove ? Result.VALUE.BLACK_WIN : Result.VALUE.WHITE_WIN, Result.REASON.MATE);
            } else {
                return new Result( Result.VALUE.DRAW, Result.REASON.STALEMATE);
            }
        }

        if (insufficientMaterial()) {
            return new Result( Result.VALUE.DRAW, Result.REASON.INSUFFICIENT_MATERIAL);
        }

        if( halfMoveClock >= 100 ){
            return new Result( Result.VALUE.DRAW, Result.REASON.DRAY_BY_50_RULE);
        }

        return Result.NO_RESULT;
    }

    private boolean insufficientMaterial() {
        if (this.nPieces(PieceImpl.WQUEEN) > 0) return false;
        if (this.nPieces(PieceImpl.WROOK)  > 0) return false;
        if (this.nPieces(PieceImpl.WPAWN)  > 0) return false;
        if (this.nPieces(PieceImpl.BQUEEN) > 0) return false;
        if (this.nPieces(PieceImpl.BROOK)  > 0) return false;
        if (this.nPieces(PieceImpl.BPAWN)  > 0) return false;
        int wb = this.nPieces(PieceImpl.WBISHOP);
        int wn = this.nPieces(PieceImpl.WKNIGHT);
        int bb = this.nPieces(PieceImpl.BBISHOP);
        int bn = this.nPieces(PieceImpl.BKNIGHT);
        if (wb + wn + bb + bn <= 1) {
            return true;    // King + bishop/knight vs king is draw
        }
        if (wn + bn == 0) {
            // Only bishops. If they are all on the same color, the position is a draw.
            boolean bSquare = false;
            boolean wSquare = false;
            for (int x = 0; x < 8; x++) {
                for (int y = 0; y < 8; y++) {
                    int p = this.getPiece(PositionImpl.getSquare(x, y));
                    if ((p == PieceImpl.BBISHOP) || (p == PieceImpl.WBISHOP)) {
                        if (PositionImpl.darkSquare(x, y)) {
                            bSquare = true;
                        } else {
                            wSquare = true;
                        }
                    }
                }
            }
            if (!bSquare || !wSquare) {
                return true;
            }
        }
        return false;
    }

    /** Useful for debugging. */
    public final String toString() {
        return TextIO.asciiBoard(this);
    }

    @Override
    public Piece getPieceAt(Square square) {
       return getPieceAt(square.getFile(),square.getRank());
    }

    @Override
    public Piece getPieceAt(char file, int rank) {
        int index = Util.getSquareRealIndex(file,rank-1);
        return Util.convertPiece(getPiece( index ));
    }

    @Override
    public Color getActiveColor() {
        return this.whiteMove ? Color.WHITE : Color.BLACK;
    }


    @Override
    public Square getEnpassantSquare() {
        return Util.getSquare(epSquare);
    }

    @Override
    public boolean isWhiteKingSideCastleAvailable() {
        return !h1Castle();
    }

    @Override
    public boolean isWhiteQueenSideCastleCastleAvailable() {
        return !a1Castle();
    }

    @Override
    public boolean isBlackKingSideCastleAvailable() {
        return !h8Castle();
    }

    @Override
    public boolean isBlackQueenSideCastleAvailable() {
        return !a8Castle();
    }

    @Override
    public int getHalfMoveClock() {
        return this.halfMoveClock;
    }

    @Override
    public int getFullMoveNumber() {
        return this.fullMoveCounter;
    }
}
