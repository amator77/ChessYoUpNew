package com.chessyoup.model;

import java.util.ArrayList;


public class MoveGen {
    static MoveGen instance;
    static {
        instance = new MoveGen();
    }

    /** Generate and return a list of legal moves. */
    public final ArrayList<MoveImpl> legalMoves(PositionImpl pos) {
        ArrayList<MoveImpl> moveList = pseudoLegalMoves(pos);
        moveList = MoveGen.removeIllegal(pos, moveList);
        return moveList;
    }

    /**
     * Generate and return a list of pseudo-legal moves.
     * Pseudo-legal means that the moves don't necessarily defend from check threats.
     */
    public final ArrayList<MoveImpl> pseudoLegalMoves(PositionImpl pos) {
        ArrayList<MoveImpl> moveList = getMoveListObj();
        final boolean wtm = pos.whiteMove;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                int sq = PositionImpl.getSquare(x, y);
                int p = pos.getPiece(sq);
                if ((p == PieceImpl.EMPTY) || (PieceImpl.isWhite(p) != wtm)) {
                    continue;
                }
                if ((p == PieceImpl.WROOK) || (p == PieceImpl.BROOK) || (p == PieceImpl.WQUEEN) || (p == PieceImpl.BQUEEN)) {
                    if (addDirection(moveList, pos, sq, 7-x,  1)) return moveList;
                    if (addDirection(moveList, pos, sq, 7-y,  8)) return moveList;
                    if (addDirection(moveList, pos, sq,   x, -1)) return moveList;
                    if (addDirection(moveList, pos, sq,   y, -8)) return moveList;
                }
                if ((p == PieceImpl.WBISHOP) || (p == PieceImpl.BBISHOP) || (p == PieceImpl.WQUEEN) || (p == PieceImpl.BQUEEN)) {
                    if (addDirection(moveList, pos, sq, Math.min(7-x, 7-y),  9)) return moveList;
                    if (addDirection(moveList, pos, sq, Math.min(  x, 7-y),  7)) return moveList;
                    if (addDirection(moveList, pos, sq, Math.min(  x,   y), -9)) return moveList;
                    if (addDirection(moveList, pos, sq, Math.min(7-x,   y), -7)) return moveList;
                }
                if ((p == PieceImpl.WKNIGHT) || (p == PieceImpl.BKNIGHT)) {
                    if (x < 6 && y < 7 && addDirection(moveList, pos, sq, 1,  10)) return moveList;
                    if (x < 7 && y < 6 && addDirection(moveList, pos, sq, 1,  17)) return moveList;
                    if (x > 0 && y < 6 && addDirection(moveList, pos, sq, 1,  15)) return moveList;
                    if (x > 1 && y < 7 && addDirection(moveList, pos, sq, 1,   6)) return moveList;
                    if (x > 1 && y > 0 && addDirection(moveList, pos, sq, 1, -10)) return moveList;
                    if (x > 0 && y > 1 && addDirection(moveList, pos, sq, 1, -17)) return moveList;
                    if (x < 7 && y > 1 && addDirection(moveList, pos, sq, 1, -15)) return moveList;
                    if (x < 6 && y > 0 && addDirection(moveList, pos, sq, 1,  -6)) return moveList;
                }
                if ((p == PieceImpl.WKING) || (p == PieceImpl.BKING)) {
                    if (x < 7          && addDirection(moveList, pos, sq, 1,  1)) return moveList;
                    if (x < 7 && y < 7 && addDirection(moveList, pos, sq, 1,  9)) return moveList;
                    if (         y < 7 && addDirection(moveList, pos, sq, 1,  8)) return moveList;
                    if (x > 0 && y < 7 && addDirection(moveList, pos, sq, 1,  7)) return moveList;
                    if (x > 0          && addDirection(moveList, pos, sq, 1, -1)) return moveList;
                    if (x > 0 && y > 0 && addDirection(moveList, pos, sq, 1, -9)) return moveList;
                    if (         y > 0 && addDirection(moveList, pos, sq, 1, -8)) return moveList;
                    if (x < 7 && y > 0 && addDirection(moveList, pos, sq, 1, -7)) return moveList;

                    int k0 = wtm ? PositionImpl.getSquare(4, 0) : PositionImpl.getSquare(4, 7);
                    if (PositionImpl.getSquare(x, y) == k0) {
                        int aCastle = wtm ? PositionImpl.A1_CASTLE : PositionImpl.A8_CASTLE;
                        int hCastle = wtm ? PositionImpl.H1_CASTLE : PositionImpl.H8_CASTLE;
                        int rook = wtm ? PieceImpl.WROOK : PieceImpl.BROOK;
                        if (((pos.getCastleMask() & (1 << hCastle)) != 0) &&
                                (pos.getPiece(k0 + 1) == PieceImpl.EMPTY) &&
                                (pos.getPiece(k0 + 2) == PieceImpl.EMPTY) &&
                                (pos.getPiece(k0 + 3) == rook) &&
                                !sqAttacked(pos, k0) &&
                                !sqAttacked(pos, k0 + 1)) {
                            moveList.add(getMoveObj(k0, k0 + 2, PieceImpl.EMPTY));
                        }
                        if (((pos.getCastleMask() & (1 << aCastle)) != 0) &&
                                (pos.getPiece(k0 - 1) == PieceImpl.EMPTY) &&
                                (pos.getPiece(k0 - 2) == PieceImpl.EMPTY) &&
                                (pos.getPiece(k0 - 3) == PieceImpl.EMPTY) &&
                                (pos.getPiece(k0 - 4) == rook) &&
                                !sqAttacked(pos, k0) &&
                                !sqAttacked(pos, k0 - 1)) {
                            moveList.add(getMoveObj(k0, k0 - 2, PieceImpl.EMPTY));
                        }
                    }
                }
                if ((p == PieceImpl.WPAWN) || (p == PieceImpl.BPAWN)) {
                    int yDir = wtm ? 8 : -8;
                    if (pos.getPiece(sq + yDir) == PieceImpl.EMPTY) { // non-capture
                        addPawnMoves(moveList, sq, sq + yDir);
                        if ((y == (wtm ? 1 : 6)) &&
                                (pos.getPiece(sq + 2 * yDir) == PieceImpl.EMPTY)) { // double step
                            addPawnMoves(moveList, sq, sq + yDir * 2);
                        }
                    }
                    if (x > 0) { // Capture to the left
                        int toSq = sq + yDir - 1;
                        int cap = pos.getPiece(toSq);
                        if (cap != PieceImpl.EMPTY) {
                            if (PieceImpl.isWhite(cap) != wtm) {
                                if (cap == (wtm ? PieceImpl.BKING : PieceImpl.WKING)) {
                                    returnMoveList(moveList);
                                    moveList = getMoveListObj();
                                    moveList.add(getMoveObj(sq, toSq, PieceImpl.EMPTY));
                                    return moveList;
                                } else {
                                    addPawnMoves(moveList, sq, toSq);
                                }
                            }
                        } else if (toSq == pos.getEpSquare()) {
                            addPawnMoves(moveList, sq, toSq);
                        }
                    }
                    if (x < 7) { // Capture to the right
                        int toSq = sq + yDir + 1;
                        int cap = pos.getPiece(toSq);
                        if (cap != PieceImpl.EMPTY) {
                            if (PieceImpl.isWhite(cap) != wtm) {
                                if (cap == (wtm ? PieceImpl.BKING : PieceImpl.WKING)) {
                                    returnMoveList(moveList);
                                    moveList = getMoveListObj();
                                    moveList.add(getMoveObj(sq, toSq, PieceImpl.EMPTY));
                                    return moveList;
                                } else {
                                    addPawnMoves(moveList, sq, toSq);
                                }
                            }
                        } else if (toSq == pos.getEpSquare()) {
                            addPawnMoves(moveList, sq, toSq);
                        }
                    }
                }
            }
        }
        return moveList;
    }

    /**
     * Return true if the side to move is in check.
     */
    public static final boolean inCheck(PositionImpl pos) {
        int kingSq = pos.getKingSq(pos.whiteMove);
        if (kingSq < 0)
            return false;
        return sqAttacked(pos, kingSq);
    }

    /**
     * Return true if a square is attacked by the opposite side.
     */
    public static final boolean sqAttacked(PositionImpl pos, int sq) {
        int x = PositionImpl.getX(sq);
        int y = PositionImpl.getY(sq);
        boolean isWhiteMove = pos.whiteMove;

        final int oQueen= isWhiteMove ? PieceImpl.BQUEEN: PieceImpl.WQUEEN;
        final int oRook = isWhiteMove ? PieceImpl.BROOK : PieceImpl.WROOK;
        final int oBish = isWhiteMove ? PieceImpl.BBISHOP : PieceImpl.WBISHOP;
        final int oKnight = isWhiteMove ? PieceImpl.BKNIGHT : PieceImpl.WKNIGHT;

        int p;
        if (y > 0) {
            p = checkDirection(pos, sq,   y, -8); if ((p == oQueen) || (p == oRook)) return true;
            p = checkDirection(pos, sq, Math.min(  x,   y), -9); if ((p == oQueen) || (p == oBish)) return true;
            p = checkDirection(pos, sq, Math.min(7-x,   y), -7); if ((p == oQueen) || (p == oBish)) return true;
            if (x > 1         ) { p = checkDirection(pos, sq, 1, -10); if (p == oKnight) return true; }
            if (x > 0 && y > 1) { p = checkDirection(pos, sq, 1, -17); if (p == oKnight) return true; }
            if (x < 7 && y > 1) { p = checkDirection(pos, sq, 1, -15); if (p == oKnight) return true; }
            if (x < 6         ) { p = checkDirection(pos, sq, 1,  -6); if (p == oKnight) return true; }

            if (!isWhiteMove) {
                if (x < 7 && y > 1) { p = checkDirection(pos, sq, 1, -7); if (p == PieceImpl.WPAWN) return true; }
                if (x > 0 && y > 1) { p = checkDirection(pos, sq, 1, -9); if (p == PieceImpl.WPAWN) return true; }
            }
        }
        if (y < 7) {
            p = checkDirection(pos, sq, 7-y,  8); if ((p == oQueen) || (p == oRook)) return true;
            p = checkDirection(pos, sq, Math.min(7-x, 7-y),  9); if ((p == oQueen) || (p == oBish)) return true;
            p = checkDirection(pos, sq, Math.min(  x, 7-y),  7); if ((p == oQueen) || (p == oBish)) return true;
            if (x < 6         ) { p = checkDirection(pos, sq, 1,  10); if (p == oKnight) return true; }
            if (x < 7 && y < 6) { p = checkDirection(pos, sq, 1,  17); if (p == oKnight) return true; }
            if (x > 0 && y < 6) { p = checkDirection(pos, sq, 1,  15); if (p == oKnight) return true; }
            if (x > 1         ) { p = checkDirection(pos, sq, 1,   6); if (p == oKnight) return true; }
            if (isWhiteMove) {
                if (x < 7 && y < 6) { p = checkDirection(pos, sq, 1, 9); if (p == PieceImpl.BPAWN) return true; }
                if (x > 0 && y < 6) { p = checkDirection(pos, sq, 1, 7); if (p == PieceImpl.BPAWN) return true; }
            }
        }
        p = checkDirection(pos, sq, 7-x,  1); if ((p == oQueen) || (p == oRook)) return true;
        p = checkDirection(pos, sq,   x, -1); if ((p == oQueen) || (p == oRook)) return true;

        int oKingSq = pos.getKingSq(!isWhiteMove);
        if (oKingSq >= 0) {
            int ox = PositionImpl.getX(oKingSq);
            int oy = PositionImpl.getY(oKingSq);
            if ((Math.abs(x - ox) <= 1) && (Math.abs(y - oy) <= 1))
                return true;
        }

        return false;
    }

    /**
     * Remove all illegal moves from moveList.
     * "moveList" is assumed to be a list of pseudo-legal moves.
     * This function removes the moves that don't defend from check threats.
     */
    public static final ArrayList<MoveImpl> removeIllegal(PositionImpl pos, ArrayList<MoveImpl> moveList) {
        ArrayList<MoveImpl> ret = new ArrayList<MoveImpl>();
        UndoInfo ui = new UndoInfo();
        int mlSize = moveList.size();
        for (int mi = 0; mi < mlSize; mi++) {
            MoveImpl m = moveList.get(mi);
            pos.makeMove(m, ui);
            pos.setWhiteMove(!pos.whiteMove);
            if (!inCheck(pos))
                ret.add(m);
            pos.setWhiteMove(!pos.whiteMove);
            pos.unMakeMove(m, ui);
        }
        return ret;
    }

    /**
     * Add all moves from square sq0 in direction delta.
     * @param maxSteps Max steps until reaching a border. Set to 1 for non-sliding pieces.
     * @ return True if the enemy king could be captured, false otherwise.
     */
    private final boolean addDirection(ArrayList<MoveImpl> moveList, PositionImpl pos, int sq0, int maxSteps, int delta) {
        int sq = sq0;
        boolean wtm = pos.whiteMove;
        final int oKing = (wtm ? PieceImpl.BKING : PieceImpl.WKING);
        while (maxSteps > 0) {
            sq += delta;
            int p = pos.getPiece(sq);
            if (p == PieceImpl.EMPTY) {
                moveList.add(getMoveObj(sq0, sq, PieceImpl.EMPTY));
            } else {
                if (PieceImpl.isWhite(p) != wtm) {
                    if (p == oKing) {
                        returnMoveList(moveList);
                        moveList = getMoveListObj(); // Ugly! this only works because we get back the same object
                        moveList.add(getMoveObj(sq0, sq, PieceImpl.EMPTY));
                        return true;
                    } else {
                        moveList.add(getMoveObj(sq0, sq, PieceImpl.EMPTY));
                    }
                }
                break;
            }
            maxSteps--;
        }
        return false;
    }

    /**
     * Generate all possible pawn moves from (x0,y0) to (x1,y1), taking pawn promotions into account.
     */
    private final void addPawnMoves(ArrayList<MoveImpl> moveList, int sq0, int sq1) {
            if (sq1 >= 56) { // White promotion
            moveList.add(getMoveObj(sq0, sq1, PieceImpl.WQUEEN));
            moveList.add(getMoveObj(sq0, sq1, PieceImpl.WKNIGHT));
            moveList.add(getMoveObj(sq0, sq1, PieceImpl.WROOK));
            moveList.add(getMoveObj(sq0, sq1, PieceImpl.WBISHOP));
        } else if (sq1 < 8) { // Black promotion
            moveList.add(getMoveObj(sq0, sq1, PieceImpl.BQUEEN));
            moveList.add(getMoveObj(sq0, sq1, PieceImpl.BKNIGHT));
            moveList.add(getMoveObj(sq0, sq1, PieceImpl.BROOK));
            moveList.add(getMoveObj(sq0, sq1, PieceImpl.BBISHOP));
        } else { // No promotion
            moveList.add(getMoveObj(sq0, sq1, PieceImpl.EMPTY));
        }
    }

    /**
     * Check if there is an attacking piece in a given direction starting from sq.
     * The direction is given by delta.
     * @param maxSteps Max steps until reaching a border. Set to 1 for non-sliding pieces.
     * @return The first piece in the given direction, or EMPTY if there is no piece
     *         in that direction.
     */
    private static final int checkDirection(PositionImpl pos, int sq, int maxSteps, int delta) {
        while (maxSteps > 0) {
            sq += delta;
            int p = pos.getPiece(sq);
            if (p != PieceImpl.EMPTY)
                return p;
            maxSteps--;
        }
        return PieceImpl.EMPTY;
    }

    // Code to handle the Move cache.

    private MoveImpl[] moveCache = new MoveImpl[2048];
    private int movesInCache = 0;
    private Object[] moveListCache = new Object[200];
    private int moveListsInCache = 0;

    private final MoveImpl getMoveObj(int from, int to, int promoteTo) {
        if (movesInCache > 0) {
            MoveImpl m = moveCache[--movesInCache];
            m.from = from;
            m.to = to;
            m.promoteTo = promoteTo;
            return m;
        }
        return new MoveImpl(from, to, promoteTo);
    }

    @SuppressWarnings("unchecked")
    private final ArrayList<MoveImpl> getMoveListObj() {
        if (moveListsInCache > 0) {
            return (ArrayList<MoveImpl>)moveListCache[--moveListsInCache];
        }
        return new ArrayList<MoveImpl>(60);
    }

    /** Return all move objects in moveList to the move cache. */
    public final void returnMoveList(ArrayList<MoveImpl> moveList) {
        if (movesInCache + moveList.size() <= moveCache.length) {
            int mlSize = moveList.size();
            for (int mi = 0; mi < mlSize; mi++) {
                moveCache[movesInCache++] = moveList.get(mi);
            }
        }
        moveList.clear();
        if (moveListsInCache < moveListCache.length) {
            moveListCache[moveListsInCache++] = moveList;
        }
    }

    public final void returnMove(MoveImpl m) {
        if (movesInCache < moveCache.length) {
            moveCache[movesInCache++] = m;
        }
    }
}
