package com.chessyoup.chess.model.impl;

import com.chessyoup.chess.model.exception.ChessParseError;
import com.chessyoup.chess.model.impl.pgn.PGNOptions;

import java.util.ArrayList;
import java.util.List;

public class TextIO {
    static public final String startPosFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    /** Localized version of "P N B R Q K". */
    private static String[] pieceNames = null;

    /** Set localized piece names. */
    public static final void setPieceNames(String pieceNames) {
        String[] pn = pieceNames.split(" ");
        if (pn.length == 6)
            TextIO.pieceNames = pn;
    }

    /** Parse a FEN string and return a chess Position object. */
    public static final PositionImpl readFEN(String fen) throws ChessParseError {
        PositionImpl pos = new PositionImpl();
        String[] words = fen.split(" ");
        if (words.length < 2) {
            throw new ChessParseError("Chess Parse Error");
        }
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].trim();
        }

        // Piece placement
        int row = 7;
        int col = 0;
        for (int i = 0; i < words[0].length(); i++) {
            char c = words[0].charAt(i);
            switch (c) {
                case '1': col += 1; break;
                case '2': col += 2; break;
                case '3': col += 3; break;
                case '4': col += 4; break;
                case '5': col += 5; break;
                case '6': col += 6; break;
                case '7': col += 7; break;
                case '8': col += 8; break;
                case '/': row--; col = 0; break;
                case 'P': safeSetPiece(pos, col, row, PieceImpl.WPAWN);   col++; break;
                case 'N': safeSetPiece(pos, col, row, PieceImpl.WKNIGHT); col++; break;
                case 'B': safeSetPiece(pos, col, row, PieceImpl.WBISHOP); col++; break;
                case 'R': safeSetPiece(pos, col, row, PieceImpl.WROOK);   col++; break;
                case 'Q': safeSetPiece(pos, col, row, PieceImpl.WQUEEN);  col++; break;
                case 'K': safeSetPiece(pos, col, row, PieceImpl.WKING);   col++; break;
                case 'p': safeSetPiece(pos, col, row, PieceImpl.BPAWN);   col++; break;
                case 'n': safeSetPiece(pos, col, row, PieceImpl.BKNIGHT); col++; break;
                case 'b': safeSetPiece(pos, col, row, PieceImpl.BBISHOP); col++; break;
                case 'r': safeSetPiece(pos, col, row, PieceImpl.BROOK);   col++; break;
                case 'q': safeSetPiece(pos, col, row, PieceImpl.BQUEEN);  col++; break;
                case 'k': safeSetPiece(pos, col, row, PieceImpl.BKING);   col++; break;
                default: throw new ChessParseError("err_invalid_piece", pos);
            }
        }

        if (words[1].length() > 0) {
            boolean wtm;
            switch (words[1].charAt(0)) {
            case 'w': wtm = true; break;
            case 'b': wtm = false; break;
            default: throw new ChessParseError("err_invalid_side", pos);
            }
            pos.setWhiteMove(wtm);
        } else {
            throw new ChessParseError("err_invalid_side", pos);
        }

        // Castling rights
        int castleMask = 0;
        if (words.length > 2) {
            for (int i = 0; i < words[2].length(); i++) {
                char c = words[2].charAt(i);
                switch (c) {
                    case 'K':
                        castleMask |= (1 << PositionImpl.H1_CASTLE);
                        break;
                    case 'Q':
                        castleMask |= (1 << PositionImpl.A1_CASTLE);
                        break;
                    case 'k':
                        castleMask |= (1 << PositionImpl.H8_CASTLE);
                        break;
                    case 'q':
                        castleMask |= (1 << PositionImpl.A8_CASTLE);
                        break;
                    case '-':
                        break;
                    default:
                        throw new ChessParseError("err_invalid_castling_flags", pos);
                }
            }
        }
        pos.setCastleMask(castleMask);
        removeBogusCastleFlags(pos);

        if (words.length > 3) {
            // En passant target square
            String epString = words[3];
            if (!epString.equals("-")) {
                if (epString.length() < 2) {
                    throw new ChessParseError("err_invalid_en_passant_square", pos);
                }
                pos.setEpSquare(getSquare(epString));
            }
        }

        try {
            if (words.length > 4) {
                pos.halfMoveClock = Integer.parseInt(words[4]);
            }
            if (words.length > 5) {
                pos.fullMoveCounter = Integer.parseInt(words[5]);
            }
        } catch (NumberFormatException nfe) {
            // Ignore errors here, since the fields are optional
        }

        // Each side must have exactly one king
        int[] nPieces = new int[PieceImpl.nPieceTypes];
        for (int i = 0; i < PieceImpl.nPieceTypes; i++)
            nPieces[i] = 0;
        for (int x = 0; x < 8; x++)
            for (int y = 0; y < 8; y++)
                nPieces[pos.getPiece(PositionImpl.getSquare(x, y))]++;
        if (nPieces[PieceImpl.WKING] != 1)
            throw new ChessParseError("err_white_num_kings", pos);
        if (nPieces[PieceImpl.BKING] != 1)
            throw new ChessParseError("err_black_num_kings", pos);

        // White must not have too many pieces
        int maxWPawns = 8;
        maxWPawns -= Math.max(0, nPieces[PieceImpl.WKNIGHT] - 2);
        maxWPawns -= Math.max(0, nPieces[PieceImpl.WBISHOP] - 2);
        maxWPawns -= Math.max(0, nPieces[PieceImpl.WROOK  ] - 2);
        maxWPawns -= Math.max(0, nPieces[PieceImpl.WQUEEN ] - 1);
        if (nPieces[PieceImpl.WPAWN] > maxWPawns)
            throw new ChessParseError("err_too_many_white_pieces", pos);

        // Black must not have too many pieces
        int maxBPawns = 8;
        maxBPawns -= Math.max(0, nPieces[PieceImpl.BKNIGHT] - 2);
        maxBPawns -= Math.max(0, nPieces[PieceImpl.BBISHOP] - 2);
        maxBPawns -= Math.max(0, nPieces[PieceImpl.BROOK  ] - 2);
        maxBPawns -= Math.max(0, nPieces[PieceImpl.BQUEEN ] - 1);
        if (nPieces[PieceImpl.BPAWN] > maxBPawns)
            throw new ChessParseError("err_too_many_black_pieces", pos);

        // Make sure king can not be captured
        PositionImpl pos2 = new PositionImpl(pos);
        pos2.setWhiteMove(!pos.whiteMove);
        if (MoveGen.inCheck(pos2)) {
            throw new ChessParseError("err_king_capture_possible", pos);
        }

        fixupEPSquare(pos);

        return pos;
    }

    public static final void removeBogusCastleFlags(PositionImpl pos) {
        int castleMask = pos.getCastleMask();
        int validCastle = 0;
        if (pos.getPiece(4) == PieceImpl.WKING) {
            if (pos.getPiece(0) == PieceImpl.WROOK) validCastle |= (1 << PositionImpl.A1_CASTLE);
            if (pos.getPiece(7) == PieceImpl.WROOK) validCastle |= (1 << PositionImpl.H1_CASTLE);
        }
        if (pos.getPiece(60) == PieceImpl.BKING) {
            if (pos.getPiece(56) == PieceImpl.BROOK) validCastle |= (1 << PositionImpl.A8_CASTLE);
            if (pos.getPiece(63) == PieceImpl.BROOK) validCastle |= (1 << PositionImpl.H8_CASTLE);
        }
        castleMask &= validCastle;
        pos.setCastleMask(castleMask);
    }

    /** Remove pseudo-legal EP square if it is not legal, ie would leave king in check. */
    public static final void fixupEPSquare(PositionImpl pos) {
        int epSquare = pos.getEpSquare();
        if (epSquare >= 0) {
            ArrayList<MoveImpl> moves = MoveGen.instance.legalMoves(pos);
            boolean epValid = false;
            for (MoveImpl m : moves) {
                if (m.to == epSquare) {
                    if (pos.getPiece(m.from) == (pos.whiteMove ? PieceImpl.WPAWN : PieceImpl.BPAWN)) {
                        epValid = true;
                        break;
                    }
                }
            }
            if (!epValid)
                pos.setEpSquare(-1);
        }
    }

    private static final void safeSetPiece(PositionImpl pos, int col, int row, int p) throws ChessParseError {
        if (row < 0) throw new ChessParseError("err_too_many_rows");
        if (col > 7) throw new ChessParseError("err_too_many_columns");
        if ((p == PieceImpl.WPAWN) || (p == PieceImpl.BPAWN)) {
            if ((row == 0) || (row == 7))
                throw new ChessParseError("err_pawn_on_first_last_rank");
        }
        pos.setPiece(PositionImpl.getSquare(col, row), p);
    }

    /** Return a FEN string corresponding to a chess Position object. */
    public static final String toFEN(PositionImpl pos) {
        StringBuilder ret = new StringBuilder();
        // Piece placement
        for (int r = 7; r >=0; r--) {
            int numEmpty = 0;
            for (int c = 0; c < 8; c++) {
                int p = pos.getPiece(PositionImpl.getSquare(c, r));
                if (p == PieceImpl.EMPTY) {
                    numEmpty++;
                } else {
                    if (numEmpty > 0) {
                        ret.append(numEmpty);
                        numEmpty = 0;
                    }
                    switch (p) {
                        case PieceImpl.WKING:   ret.append('K'); break;
                        case PieceImpl.WQUEEN:  ret.append('Q'); break;
                        case PieceImpl.WROOK:   ret.append('R'); break;
                        case PieceImpl.WBISHOP: ret.append('B'); break;
                        case PieceImpl.WKNIGHT: ret.append('N'); break;
                        case PieceImpl.WPAWN:   ret.append('P'); break;
                        case PieceImpl.BKING:   ret.append('k'); break;
                        case PieceImpl.BQUEEN:  ret.append('q'); break;
                        case PieceImpl.BROOK:   ret.append('r'); break;
                        case PieceImpl.BBISHOP: ret.append('b'); break;
                        case PieceImpl.BKNIGHT: ret.append('n'); break;
                        case PieceImpl.BPAWN:   ret.append('p'); break;
                        default: throw new RuntimeException();
                    }
                }
            }
            if (numEmpty > 0) {
                ret.append(numEmpty);
            }
            if (r > 0) {
                ret.append('/');
            }
        }
        ret.append(pos.whiteMove ? " w " : " b ");

        // Castling rights
        boolean anyCastle = false;
        if (pos.h1Castle()) {
            ret.append('K');
            anyCastle = true;
        }
        if (pos.a1Castle()) {
            ret.append('Q');
            anyCastle = true;
        }
        if (pos.h8Castle()) {
            ret.append('k');
            anyCastle = true;
        }
        if (pos.a8Castle()) {
            ret.append('q');
            anyCastle = true;
        }
        if (!anyCastle) {
            ret.append('-');
        }

        // En passant target square
        {
            ret.append(' ');
            if (pos.getEpSquare() >= 0) {
                int x = PositionImpl.getX(pos.getEpSquare());
                int y = PositionImpl.getY(pos.getEpSquare());
                ret.append((char)(x + 'a'));
                ret.append((char)(y + '1'));
            } else {
                ret.append('-');
            }
        }

        // Move counters
        ret.append(' ');
        ret.append(pos.halfMoveClock);
        ret.append(' ');
        ret.append(pos.fullMoveCounter);

        return ret.toString();
    }

    /**
     * Convert a chess move to human readable form.
     * @param pos       The chess position.
     * @param move      The executed move.
     * @param longForm  If true, use long notation, eg Ng1-f3.
     *                  Otherwise, use short notation, eg Nf3.
     * @param localized If true, use localized piece names.
     */
    public static final String moveToString(PositionImpl pos, MoveImpl move, boolean longForm,
                                            boolean localized) {
        return moveToString(pos, move, longForm, localized, null);
    }
    public static final String moveToString(PositionImpl pos, MoveImpl move, boolean longForm,
                                            boolean localized, List<MoveImpl> moves) {
        if ((move == null) || move.equals(new MoveImpl(0, 0, 0)))
            return "--";
        StringBuilder ret = new StringBuilder();
        int wKingOrigPos = PositionImpl.getSquare(4, 0);
        int bKingOrigPos = PositionImpl.getSquare(4, 7);
        if (move.from == wKingOrigPos && pos.getPiece(wKingOrigPos) == PieceImpl.WKING) {
            // Check white castle
            if (move.to == PositionImpl.getSquare(6, 0)) {
                    ret.append("O-O");
            } else if (move.to == PositionImpl.getSquare(2, 0)) {
                ret.append("O-O-O");
            }
        } else if (move.from == bKingOrigPos && pos.getPiece(bKingOrigPos) == PieceImpl.BKING) {
            // Check black castle
            if (move.to == PositionImpl.getSquare(6, 7)) {
                ret.append("O-O");
            } else if (move.to == PositionImpl.getSquare(2, 7)) {
                ret.append("O-O-O");
            }
        }
        if (ret.length() == 0) {
            if (pieceNames == null)
                localized = false;
            int p = pos.getPiece(move.from);
            if (localized)
                ret.append(pieceToCharLocalized(p));
            else
                ret.append(pieceToChar(p));
            int x1 = PositionImpl.getX(move.from);
            int y1 = PositionImpl.getY(move.from);
            int x2 = PositionImpl.getX(move.to);
            int y2 = PositionImpl.getY(move.to);
            if (longForm) {
                ret.append((char)(x1 + 'a'));
                ret.append((char) (y1 + '1'));
                ret.append(isCapture(pos, move) ? 'x' : '-');
            } else {
                if (p == (pos.whiteMove ? PieceImpl.WPAWN : PieceImpl.BPAWN)) {
                    if (isCapture(pos, move)) {
                        ret.append((char) (x1 + 'a'));
                    }
                } else {
                    int numSameTarget = 0;
                    int numSameFile = 0;
                    int numSameRow = 0;
                    if (moves == null)
                        moves = MoveGen.instance.legalMoves(pos);
                    int mSize = moves.size();
                    for (int mi = 0; mi < mSize; mi++) {
                        MoveImpl m = moves.get(mi);
                        if ((pos.getPiece(m.from) == p) && (m.to == move.to)) {
                            numSameTarget++;
                            if (PositionImpl.getX(m.from) == x1)
                                numSameFile++;
                            if (PositionImpl.getY(m.from) == y1)
                                numSameRow++;
                        }
                    }
                    if (numSameTarget < 2) {
                        // No file/row info needed
                    } else if (numSameFile < 2) {
                        ret.append((char) (x1 + 'a'));   // Only file info needed
                    } else if (numSameRow < 2) {
                        ret.append((char) (y1 + '1'));   // Only row info needed
                    } else {
                        ret.append((char) (x1 + 'a'));   // File and row info needed
                        ret.append((char) (y1 + '1'));
                    }
                }
                if (isCapture(pos, move)) {
                    ret.append('x');
                }
            }
            ret.append((char) (x2 + 'a'));
            ret.append((char) (y2 + '1'));
            if (move.promoteTo != PieceImpl.EMPTY) {
                if (localized)
                    ret.append(pieceToCharLocalized(move.promoteTo));
                else
                    ret.append(pieceToChar(move.promoteTo));
            }
        }
        UndoInfo ui = new UndoInfo();
        pos.makeMove(move, ui);
        boolean givesCheck = MoveGen.inCheck(pos);
        if (givesCheck) {
            ArrayList<MoveImpl> nextMoves = MoveGen.instance.legalMoves(pos);
            if (nextMoves.size() == 0) {
                ret.append('#');
            } else {
                ret.append('+');
            }
        }
        pos.unMakeMove(move, ui);

        return ret.toString();
    }

    private static final boolean isCapture(PositionImpl pos, MoveImpl move) {
        if (pos.getPiece(move.to) == PieceImpl.EMPTY) {
            int p = pos.getPiece(move.from);
            if ((p == (pos.whiteMove ? PieceImpl.WPAWN : PieceImpl.BPAWN)) && (move.to == pos.getEpSquare())) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    /**
     * Decide if move is valid in position pos.
     * @param pos   Position for which to test move.
     * @param move  The move to check for validity.
     * @return True if move is valid in position pos, false otherwise.
     */
    public static final boolean isValid(PositionImpl pos, MoveImpl move) {
        if (move == null)
            return false;
        ArrayList<MoveImpl> moves = new MoveGen().legalMoves(pos);
        for (int i = 0; i < moves.size(); i++)
            if (move.equals(moves.get(i)))
                return true;
        return false;
    }

    private final static class MoveInfo {
        int piece;                  // -1 for unspecified
        int fromX, fromY, toX, toY; // -1 for unspecified
        int promPiece;              // -1 for unspecified
        MoveInfo() { piece = fromX = fromY = toX = toY = promPiece = -1; }
    }

    /**
     * Convert a chess move string to a Move object.
     * The string may specify any combination of piece/source/target/promotion
     * information as long as it matches exactly one valid move.
     */
    public static final MoveImpl stringToMove(PositionImpl pos, String strMove) {
        return stringToMove(pos, strMove, null);
    }
    public static final MoveImpl stringToMove(PositionImpl pos, String strMove,
                                          ArrayList<MoveImpl> moves) {
        if (strMove.equals("--"))
            return new MoveImpl(0, 0, 0);

        strMove = strMove.replaceAll("=", "");
        strMove = strMove.replaceAll("\\+", "");
        strMove = strMove.replaceAll("#", "");
        boolean wtm = pos.whiteMove;

        MoveInfo info = new MoveInfo();
        boolean capture = false;
        if (strMove.equals("O-O") || strMove.equals("0-0") || strMove.equals("o-o")) {
            info.piece = wtm ? PieceImpl.WKING : PieceImpl.BKING;
            info.fromX = 4;
            info.toX = 6;
            info.fromY = info.toY = wtm ? 0 : 7;
            info.promPiece= PieceImpl.EMPTY;
        } else if (strMove.equals("O-O-O") || strMove.equals("0-0-0") || strMove.equals("o-o-o")) {
            info.piece = wtm ? PieceImpl.WKING : PieceImpl.BKING;
            info.fromX = 4;
            info.toX = 2;
            info.fromY = info.toY = wtm ? 0 : 7;
            info.promPiece= PieceImpl.EMPTY;
        } else {
            boolean atToSq = false;
            for (int i = 0; i < strMove.length(); i++) {
                char c = strMove.charAt(i);
                if (i == 0) {
                    int piece = charToPiece(wtm, c);
                    if (piece >= 0) {
                        info.piece = piece;
                        continue;
                    }
                }
                int tmpX = c - 'a';
                if ((tmpX >= 0) && (tmpX < 8)) {
                    if (atToSq || (info.fromX >= 0))
                        info.toX = tmpX;
                    else
                        info.fromX = tmpX;
                }
                int tmpY = c - '1';
                if ((tmpY >= 0) && (tmpY < 8)) {
                    if (atToSq || (info.fromY >= 0))
                        info.toY = tmpY;
                    else
                        info.fromY = tmpY;
                }
                if ((c == 'x') || (c == '-')) {
                    atToSq = true;
                    if (c == 'x')
                        capture = true;
                }
                if (i == strMove.length() - 1) {
                    int promPiece = charToPiece(wtm, c);
                    if (promPiece >= 0) {
                        info.promPiece = promPiece;
                    }
                }
            }
            if ((info.fromX >= 0) && (info.toX < 0)) {
                info.toX = info.fromX;
                info.fromX = -1;
            }
            if ((info.fromY >= 0) && (info.toY < 0)) {
                info.toY = info.fromY;
                info.fromY = -1;
            }
            if (info.piece < 0) {
                boolean haveAll = (info.fromX >= 0) && (info.fromY >= 0) &&
                                  (info.toX >= 0) && (info.toY >= 0);
                if (!haveAll)
                    info.piece = wtm ? PieceImpl.WPAWN : PieceImpl.BPAWN;
            }
            if (info.promPiece < 0)
                info.promPiece = PieceImpl.EMPTY;
        }

        if (moves == null)
            moves = MoveGen.instance.legalMoves(pos);

        ArrayList<MoveImpl> matches = new ArrayList<MoveImpl>(2);
        for (int i = 0; i < moves.size(); i++) {
            MoveImpl m = moves.get(i);
            int p = pos.getPiece(m.from);
            boolean match = true;
            if ((info.piece >= 0) && (info.piece != p))
                match = false;
            if ((info.fromX >= 0) && (info.fromX != PositionImpl.getX(m.from)))
                match = false;
            if ((info.fromY >= 0) && (info.fromY != PositionImpl.getY(m.from)))
                match = false;
            if ((info.toX >= 0) && (info.toX != PositionImpl.getX(m.to)))
                match = false;
            if ((info.toY >= 0) && (info.toY != PositionImpl.getY(m.to)))
                match = false;
            if ((info.promPiece >= 0) && (info.promPiece != m.promoteTo))
                match = false;
            if (match) {
                matches.add(m);
            }
        }
        int nMatches = matches.size();
        if (nMatches == 0)
            return null;
        else if (nMatches == 1)
            return matches.get(0);
        if (!capture)
            return null;
        MoveImpl move = null;
        for (int i = 0; i < matches.size(); i++) {
            MoveImpl m = matches.get(i);
            int capt = pos.getPiece(m.to);
            if (capt != PieceImpl.EMPTY) {
                if (move == null)
                    move = m;
                else
                    return null;
            }
        }
        return move;
    }

    /** Convert a move object to UCI string format. */
    public static final String moveToUCIString(MoveImpl m) {
        String ret = squareToString(m.from);
        ret += squareToString(m.to);
        switch (m.promoteTo) {
            case PieceImpl.WQUEEN:
            case PieceImpl.BQUEEN:
                ret += "q";
                break;
            case PieceImpl.WROOK:
            case PieceImpl.BROOK:
                ret += "r";
                break;
            case PieceImpl.WBISHOP:
            case PieceImpl.BBISHOP:
                ret += "b";
                break;
            case PieceImpl.WKNIGHT:
            case PieceImpl.BKNIGHT:
                ret += "n";
                break;
            default:
                break;
        }
        return ret;
    }

    /**
     * Convert a string in UCI move format to a Move object.
     * @return A move object, or null if move has invalid syntax
     */
    public static final MoveImpl UCIstringToMove(String move) {
        MoveImpl m = null;
        if ((move.length() < 4) || (move.length() > 5))
            return m;
        int fromSq = TextIO.getSquare(move.substring(0, 2));
        int toSq   = TextIO.getSquare(move.substring(2, 4));
        if ((fromSq < 0) || (toSq < 0)) {
            return m;
        }
        char prom = ' ';
        boolean white = true;
        if (move.length() == 5) {
            prom = move.charAt(4);
            if (PositionImpl.getY(toSq) == 7) {
                white = true;
            } else if (PositionImpl.getY(toSq) == 0) {
                white = false;
            } else {
                return m;
            }
        }
        int promoteTo;
        switch (prom) {
            case ' ':
                promoteTo = PieceImpl.EMPTY;
                break;
            case 'q':
                promoteTo = white ? PieceImpl.WQUEEN : PieceImpl.BQUEEN;
                break;
            case 'r':
                promoteTo = white ? PieceImpl.WROOK : PieceImpl.BROOK;
                break;
            case 'b':
                promoteTo = white ? PieceImpl.WBISHOP : PieceImpl.BBISHOP;
                break;
            case 'n':
                promoteTo = white ? PieceImpl.WKNIGHT : PieceImpl.BKNIGHT;
                break;
            default:
                return m;
        }
        m = new MoveImpl(fromSq, toSq, promoteTo);
        return m;
    }

    /**
     * Convert a string, such as "e4" to a square number.
     * @return The square number, or -1 if not a legal square.
     */
    public static final int getSquare(String s) {
        int x = s.charAt(0) - 'a';
        int y = s.charAt(1) - '1';
        if ((x < 0) || (x > 7) || (y < 0) || (y > 7))
            return -1;
        return PositionImpl.getSquare(x, y);
    }

    /**
     * Convert a square number to a string, such as "e4".
     */
    public static final String squareToString(int square) {
        StringBuilder ret = new StringBuilder();
        int x = PositionImpl.getX(square);
        int y = PositionImpl.getY(square);
        ret.append((char) (x + 'a'));
        ret.append((char) (y + '1'));
        return ret.toString();
    }

    /**
     * Create an ascii representation of a position.
     */
    public static final String asciiBoard(PositionImpl pos) {
        StringBuilder ret = new StringBuilder(400);
        String nl = String.format("%n");
        ret.append("    +----+----+----+----+----+----+----+----+"); ret.append(nl);
        for (int y = 7; y >= 0; y--) {
            ret.append("    |");
            for (int x = 0; x < 8; x++) {
                ret.append(' ');
                int p = pos.getPiece(PositionImpl.getSquare(x, y));
                if (p == PieceImpl.EMPTY) {
                    boolean dark = PositionImpl.darkSquare(x, y);
                    ret.append(dark ? ".. |" : "   |");
                } else {
                    ret.append(PieceImpl.isWhite(p) ? 'W' : 'B');
                    String pieceName = pieceToChar(p);
                    if (pieceName.length() == 0)
                        pieceName = "P";
                    ret.append(pieceName);
                    ret.append(" |");
                }
            }
            ret.append(nl);
            ret.append("    +----+----+----+----+----+----+----+----+");
            ret.append(nl);
        }
        return ret.toString();
    }

    /** Convert a piece and a square to a string, such as Nf3. */
    public final static String pieceAndSquareToString(int currentPieceType, int p, int sq) {
        StringBuilder ret = new StringBuilder();
        if (currentPieceType == PGNOptions.PT_FIGURINE) {
            ret.append(PieceImpl.toUniCode(p));
        } else {
            boolean localized = (currentPieceType != PGNOptions.PT_ENGLISH);
            if ((p == PieceImpl.WPAWN) || (p == PieceImpl.BPAWN))
                ret.append(localized ? pieceNames[0] : "P");
            else
                ret.append(localized ? pieceToCharLocalized(p) : pieceToChar(p));
        }
        ret.append(squareToString(sq));
        return ret.toString();
    }

    private final static String pieceToChar(int p) {
        switch (p) {
            case PieceImpl.WQUEEN:  case PieceImpl.BQUEEN:  return "Q";
            case PieceImpl.WROOK:   case PieceImpl.BROOK:   return "R";
            case PieceImpl.WBISHOP: case PieceImpl.BBISHOP: return "B";
            case PieceImpl.WKNIGHT: case PieceImpl.BKNIGHT: return "N";
            case PieceImpl.WKING:   case PieceImpl.BKING:   return "K";
        }
        return "";
    }

    public final static String pieceToCharLocalized(int p) {
        switch (p) {
            case PieceImpl.WQUEEN:  case PieceImpl.BQUEEN:  return pieceNames[4];
            case PieceImpl.WROOK:   case PieceImpl.BROOK:   return pieceNames[3];
            case PieceImpl.WBISHOP: case PieceImpl.BBISHOP: return pieceNames[2];
            case PieceImpl.WKNIGHT: case PieceImpl.BKNIGHT: return pieceNames[1];
            case PieceImpl.WKING:   case PieceImpl.BKING:   return pieceNames[5];
        }
        return "";
    }

    private final static int charToPiece(boolean white, char c) {
        switch (c) {
        case 'Q': case 'q': return white ? PieceImpl.WQUEEN  : PieceImpl.BQUEEN;
        case 'R': case 'r': return white ? PieceImpl.WROOK   : PieceImpl.BROOK;
        case 'B':           return white ? PieceImpl.WBISHOP : PieceImpl.BBISHOP;
        case 'N': case 'n': return white ? PieceImpl.WKNIGHT : PieceImpl.BKNIGHT;
        case 'K': case 'k': return white ? PieceImpl.WKING   : PieceImpl.BKING;
        case 'P': case 'p': return white ? PieceImpl.WPAWN   : PieceImpl.BPAWN;
        }
        return -1;
    }

    /** Add an = sign to a promotion move, as required by the PGN standard. */
    public final static String pgnPromotion(String str) {
        int idx = str.length() - 1;
        while (idx > 0) {
            char c = str.charAt(idx);
            if ((c != '#') && (c != '+'))
                break;
            idx--;
        }
        if ((idx > 0) && (charToPiece(true, str.charAt(idx)) != -1))
            idx--;
        return str.substring(0, idx + 1) + '=' + str.substring(idx + 1, str.length());
    }        
}
