/*
    DroidFish - An Android chess program.
    Copyright (C) 2012  Peter Österlund, peterosterlund2@gmail.com

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.chessyoup.game.view;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.Toast;

import com.chessyoup.model.MoveImpl;
import com.chessyoup.model.MoveGen;
import com.chessyoup.model.Pair;
import com.chessyoup.model.PieceImpl;
import com.chessyoup.model.PositionImpl;
import com.chessyoup.model.TextIO;
import com.chessyoup.model.pgn.PGNOptions;

/** Chess board widget suitable for play mode. */
public class ChessBoardPlayView extends ChessBoardView {
    private PGNOptions pgnOptions = null;
    boolean oneTouchMoves;
    
    public ChessBoardPlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        oneTouchMoves = false;
    }

    public void setPgnOptions(PGNOptions pgnOptions) {
        this.pgnOptions = pgnOptions;
    }

    @Override
    protected int getXCrd(int x) { return x0 + sqSize * (flipped ? 7 - x : x); }
    @Override
    protected int getYCrd(int y) { return y0 + sqSize * (flipped ? y : 7 - y); }
    @Override
    protected int getXSq(int xCrd) { int t = (xCrd - x0) / sqSize; return flipped ? 7 - t : t; }
    @Override
    protected int getYSq(int yCrd) { int t = (yCrd - y0) / sqSize; return flipped ? t : 7 - t; }

    @Override
    protected int getWidth(int sqSize) { return sqSize * 8; }
    @Override
    protected int getHeight(int sqSize) { return sqSize * 8; }
    @Override
    protected int getSqSizeW(int width) { return (width) / 8; }
    @Override
    protected int getSqSizeH(int height) { return (height) / 8; }
    @Override
    protected int getMaxHeightPercentage() { return 75; }
    @Override
    protected int getMaxWidthPercentage() { return 65; }

    @Override
    protected void computeOrigin(int width, int height) {
        x0 = (width - sqSize * 8) / 2;
        Configuration config = getResources().getConfiguration();
        boolean landScape = (config.orientation == Configuration.ORIENTATION_LANDSCAPE);
        y0 = landScape ? 0 : (height - sqSize * 8) / 2;
    }
    @Override
    protected int getXFromSq(int sq) { return PositionImpl.getX(sq); }
    @Override
    protected int getYFromSq(int sq) { return PositionImpl.getY(sq); }

    @Override
    protected int minValidY() { return 0; }
    @Override
    protected int maxValidX() { return 7; }
    @Override
    protected int getSquare(int x, int y) { return PositionImpl.getSquare(x, y); }

    @Override
    protected void drawExtraSquares(Canvas canvas) {
    }

    private final boolean myColor(int piece) {
        return (piece != PieceImpl.EMPTY) && (PieceImpl.isWhite(piece) == pos.whiteMove);
    }

    public MoveImpl mousePressed(int sq) {
        if (sq < 0)
            return null;
        cursorVisible = false;
        if ((selectedSquare != -1) && !userSelectedSquare)
            setSelection(-1); // Remove selection of opponents last moving piece

        if (!oneTouchMoves) {
            int p = pos.getPiece(sq);
            if (selectedSquare != -1) {
                if (sq == selectedSquare) {
                    if (toggleSelection)
                        setSelection(-1);
                    return null;
                }
                if (!myColor(p)) {
                    MoveImpl m = new MoveImpl(selectedSquare, sq, PieceImpl.EMPTY);
                    setSelection(sq);
                    userSelectedSquare = false;
                    return m;
                } else
                    setSelection(sq);
            } else {
                if (myColor(p))
                    setSelection(sq);
            }
        } else {
            int prevSq = userSelectedSquare ? selectedSquare : -1;
            if (prevSq == sq) {
                if (toggleSelection)
                    setSelection(-1);
                return null;
            }
            ArrayList<MoveImpl> moves = new MoveGen().legalMoves(pos);
            MoveImpl matchingMove = null;
            if (prevSq >= 0)
                matchingMove = matchingMove(prevSq, sq, moves).first;
            boolean anyMatch = false;
            if  (matchingMove == null) {
                Pair<MoveImpl, Boolean> match = matchingMove(-1, sq, moves);
                matchingMove = match.first;
                anyMatch = match.second;
            }
            if (matchingMove != null) {
                setSelection(matchingMove.to);
                userSelectedSquare = false;
                return matchingMove;
            }
            if (!anyMatch && (sq >= 0)) {
                int p = pos.getPiece(sq);
                if (myColor(p)) {
                    String msg = "Unmovable piece!";
                    int pieceType = (pgnOptions == null) ? PGNOptions.PT_LOCAL
                                                         : pgnOptions.view.pieceType;
                    msg += ": " + TextIO.pieceAndSquareToString(pieceType, p, sq);
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                }
            }
            setSelection(anyMatch ? sq : -1);
        }
        return null;
    }

    /**
     * Determine if there is a unique legal move corresponding to one or two selected squares.
     * @param sq1   First square, or -1.
     * @param sq2   Second square.
     * @param moves List of legal moves.
     * @return      Matching move if unique.
     *              Boolean indicating if there was at least one match.
     */
    private final Pair<MoveImpl, Boolean> matchingMove(int sq1, int sq2, ArrayList<MoveImpl> moves) {
        MoveImpl matchingMove = null;
        boolean anyMatch = false;
        for (MoveImpl m : moves) {
            boolean match;
            if (sq1 == -1)
                match = (m.from == sq2) || (m.to == sq2);
            else
                match = (m.from == sq1) && (m.to == sq2) ||
                        (m.from == sq2) && (m.to == sq1);
            if (match) {
                if (matchingMove == null) {
                    matchingMove = m;
                    anyMatch = true;
                } else {
                    if ((matchingMove.from == m.from) &&
                        (matchingMove.to == m.to)) {
                        matchingMove.promoteTo = PieceImpl.EMPTY;
                    } else {
                        matchingMove = null;
                        break;
                    }
                }
            }
        }
        return new Pair<MoveImpl, Boolean>(matchingMove, anyMatch);
    }
}
