package com.chessyoup.model;

import java.util.ArrayList;
import java.util.List;

import com.chessyoup.model.GameTree.Node;
import com.chessyoup.model.pgn.PGNOptions;
import com.chessyoup.model.pgn.PgnTokenReceiver;

public class Game {
    boolean pendingDrawOffer;
    public GameTree tree;
    public TimeControl timeController;
    public boolean rated;
    private boolean gamePaused;
    /** If true, add new moves as mainline moves. */
    private AddMoveBehavior addMoveBehavior;

    PgnTokenReceiver gameTextListener;

    public Game(PgnTokenReceiver gameTextListener,
                int timeControl, int movesPerSession, int timeIncrement) {
        this.gameTextListener = gameTextListener;
        tree = new GameTree(gameTextListener);
        timeController = new TimeControl();
        timeController.setTimeControl(timeControl, movesPerSession, timeIncrement);
        gamePaused = false;
        newGame();
    }
    
    
    
    public PgnTokenReceiver getGameTextListener() {
        return gameTextListener;
    }



    public void setGameTextListener(PgnTokenReceiver gameTextListener) {
        this.gameTextListener = gameTextListener;
    }



    /** De-serialize from byte array. */
    public final void fromByteArray(byte[] data) {
        tree.fromByteArray(data);
        updateTimeControl(true);
    }

    public final void setGamePaused(boolean gamePaused) {
        if (gamePaused != this.gamePaused) {
            this.gamePaused = gamePaused;
            updateTimeControl(false);
        }
    }

    /** Controls behavior when a new move is added to the game.*/
    public static enum AddMoveBehavior {
        /** Add the new move first in the list of variations. */
        ADD_FIRST,
        /** Add the new move last in the list of variations. */
        ADD_LAST,
        /** Remove all variations not matching the new move. */
        REPLACE
    };

    /** Set whether new moves are entered as mainline moves or variations. */
    public final void setAddFirst(AddMoveBehavior amb) {
        addMoveBehavior = amb;
    }

    /** Sets start position and discards the whole game tree. */
    public final void setPos(PositionImpl pos) {
        tree.setStartPos(new PositionImpl(pos));
        updateTimeControl(false);
    }

    public final boolean readPGN(String pgn, PGNOptions options) throws ChessParseError {
        boolean ret = tree.readPGN(pgn, options);
        if (ret)
            updateTimeControl(false);
        return ret;
    }
        
    public boolean isRated() {
		return rated;
	}

	public void setRated(boolean rated) {
		this.rated = rated;
	}

	public final PositionImpl currPos() {
        return tree.currentPos;
    }

    public final PositionImpl prevPos() {
        MoveImpl m = tree.currentNode.move;
        if (m != null) {
            tree.goBack();
            PositionImpl ret = new PositionImpl(currPos());
            tree.goForward(-1);
            return ret;
        } else {
            return currPos();
        }
    }

    public final MoveImpl getNextMove() {
        if (canRedoMove()) {
            tree.goForward(-1);
            MoveImpl ret = tree.currentNode.move;
            tree.goBack();
            return ret;
        } else {
            return null;
        }
    }

    /**
     * Update the game state according to move/command string from a player.
     * @param str The move or command to process.
     * @return True if str was understood, false otherwise.
     */
    public final boolean processString(String str) {
    	    	
        if (getGameState() != GameState.ALIVE)
            return false;
        if (str.startsWith("draw ")) {
            String drawCmd = str.substring(str.indexOf(" ") + 1);
            handleDrawCmd(drawCmd);
            return true;
        } else if (str.equals("resign")) {
            addToGameTree(new MoveImpl(0, 0, 0), "resign");
            return true;
        }else if(str.startsWith("abort")){
        	addToGameTree(new MoveImpl(0, 0, 0), "abort");
            return true;
    	}    	        

        MoveImpl m = TextIO.UCIstringToMove(str);
        
        if (m != null)
            if (!TextIO.isValid(currPos(), m)){            	
                m = null;
            }
        if (m == null)
            m = TextIO.stringToMove(currPos(), str);
        if (m == null)
            return false;

        addToGameTree(m, pendingDrawOffer ? "draw offer" : "");
        return true;
    }

    private final void addToGameTree(MoveImpl m, String playerAction) {
        if (m.equals(new MoveImpl(0, 0, 0))) { // Don't create more than one game-ending move at a node
            List<MoveImpl> varMoves = tree.variations();
            for (int i = varMoves.size() - 1; i >= 0; i--) {
                if (varMoves.get(i).equals(m)) {
                    tree.deleteVariation(i);
                }
            }
        }

        boolean movePresent = false;
        int varNo;
        {
            ArrayList<MoveImpl> varMoves = tree.variations();
            int nVars = varMoves.size();
            if (addMoveBehavior == AddMoveBehavior.REPLACE) {
                boolean modified = false;
                for (int i = nVars-1; i >= 0; i--) {
                    if (!m.equals(varMoves.get(i))) {
                        tree.deleteVariation(i);
                        modified = true;
                    }
                }
                if (modified) {
                    varMoves = tree.variations();
                    nVars = varMoves.size();
                }
            }
            for (varNo = 0; varNo < nVars; varNo++) {
                if (varMoves.get(varNo).equals(m)) {
                    movePresent = true;
                    break;
                }
            }
        }
        if (!movePresent) {
            String moveStr = TextIO.moveToUCIString(m);
            varNo = tree.addMove(moveStr, playerAction, 0, "", "");
        }
        int newPos = 0;
        if (addMoveBehavior == AddMoveBehavior.ADD_LAST)
            newPos = varNo;
        tree.reorderVariation(varNo, newPos);
        tree.goForward(newPos);
        int remaining = timeController.moveMade(System.currentTimeMillis(), !gamePaused);
        tree.setRemainingTime(remaining);
        updateTimeControl(true);
        pendingDrawOffer = false;
    }

    private final void updateTimeControl(boolean discardElapsed) {
        PositionImpl currPos = currPos();
        int move = currPos.fullMoveCounter;
        boolean wtm = currPos.whiteMove;
        if (discardElapsed || (move != timeController.currentMove) || (wtm != timeController.whiteToMove)) {
            int initialTime = timeController.getInitialTime();
            int whiteBaseTime = tree.getRemainingTime(true, initialTime);
            int blackBaseTime = tree.getRemainingTime(false, initialTime);
            timeController.setCurrentMove(move, wtm, whiteBaseTime, blackBaseTime);
        }
        long now = System.currentTimeMillis();
        boolean stopTimer = gamePaused || (getGameState() != GameState.ALIVE);
        if (!stopTimer) {
            try {
                if (TextIO.readFEN(TextIO.startPosFEN).equals(currPos))
                    stopTimer = true;
            } catch (ChessParseError e) {
            }
        }
        if (stopTimer) {
            timeController.stopTimer(now);
        } else {
            timeController.startTimer(now);
        }
    }

    public final String getDrawInfo(boolean localized) {
        return tree.getGameStateInfo(localized);
    }

    /**
     * Get the last played move, or null if no moves played yet.
     */
    public final MoveImpl getLastMove() {
        return tree.currentNode.move;
    }

    /** Return true if there is a move to redo. */
    public final boolean canRedoMove() {
        int nVar = tree.variations().size();
        return nVar > 0;
    }

    /** Get number of variations in current game position. */
    public final int numVariations() {
        if (tree.currentNode == tree.rootNode)
            return 1;
        tree.goBack();
        int nChildren = tree.variations().size();
        tree.goForward(-1);
        return nChildren;
    }

    /** Get current variation in current position. */
    public final int currVariation() {
        if (tree.currentNode == tree.rootNode)
            return 0;
        tree.goBack();
        int defChild = tree.currentNode.defaultChild;
        tree.goForward(-1);
        return defChild;
    }

    /** Go to a new variation in the game tree. */
    public final void changeVariation(int delta) {
        if (tree.currentNode == tree.rootNode)
            return;
        tree.goBack();
        int defChild = tree.currentNode.defaultChild;
        int nChildren = tree.variations().size();
        int newChild = defChild + delta;
        newChild = Math.max(newChild, 0);
        newChild = Math.min(newChild, nChildren - 1);
        tree.goForward(newChild);
        pendingDrawOffer = false;
        updateTimeControl(true);
    }

    /** Move current variation up/down in the game tree. */
    public final void moveVariation(int delta) {
        if (tree.currentNode == tree.rootNode)
            return;
        tree.goBack();
        int varNo = tree.currentNode.defaultChild;
        int nChildren = tree.variations().size();
        int newPos = varNo + delta;
        newPos = Math.max(newPos, 0);
        newPos = Math.min(newPos, nChildren - 1);
        tree.reorderVariation(varNo, newPos);
        tree.goForward(newPos);
        pendingDrawOffer = false;
        updateTimeControl(true);
    }

    /** Delete whole game sub-tree rooted at current position. */
    public final void removeSubTree() {
        if (getLastMove() != null) {
            tree.goBack();
            int defChild = tree.currentNode.defaultChild;
            tree.deleteVariation(defChild);
        } else {
            while (canRedoMove())
                tree.deleteVariation(0);
        }
        pendingDrawOffer = false;
        updateTimeControl(true);
    }

    public static enum GameState {
        ALIVE,
        WHITE_MATE,         // White mates
        BLACK_MATE,         // Black mates
        WHITE_STALEMATE,    // White is stalemated
        BLACK_STALEMATE,    // Black is stalemated
        DRAW_REP,           // Draw by 3-fold repetition
        DRAW_50,            // Draw by 50 move rule
        DRAW_NO_MATE,       // Draw by impossibility of check mate
        DRAW_AGREE,         // Draw by agreement
        RESIGN_WHITE,       // White resigns
        RESIGN_BLACK,       // Black resigns
        ABORTED				//Game aborted by agreement
    }

    /**
     * Get the current state (draw, mate, ongoing, etc) of the game.
     */
    public final GameState getGameState() {
        return tree.getGameState();
    }

    /**
     * Check if a draw offer is available.
     * @return True if the current player has the option to accept a draw offer.
     */
    public final boolean haveDrawOffer() {
        return tree.currentNode.playerAction.equals("draw offer");
    }

    public final void undoMove() {
        MoveImpl m = tree.currentNode.move;
        if (m != null) {
            tree.goBack();
            pendingDrawOffer = false;
            updateTimeControl(true);
        }
    }

    public final void redoMove() {
        if (canRedoMove()) {
            tree.goForward(-1);
            pendingDrawOffer = false;
            updateTimeControl(true);
        }
    }

    /** Go to given node in game tree.
     * @return True if current node changed, false otherwise. */
    public final boolean goNode(Node node) {
        if (!tree.goNode(node))
            return false;
        pendingDrawOffer = false;
        updateTimeControl(true);
        return true;
    }

    public final void newGame() {
        tree = new GameTree(gameTextListener);
        timeController.reset();
        pendingDrawOffer = false;
        updateTimeControl(true);
    }


    /**
     * Return the last zeroing position and a list of moves
     * to go from that position to the current position.
     */
    public final Pair<PositionImpl, ArrayList<MoveImpl>> getUCIHistory() {
        Pair<List<Node>, Integer> ml = tree.getMoveList();
        List<Node> moveList = ml.first;
        PositionImpl pos = new PositionImpl(tree.startPos);
        ArrayList<MoveImpl> mList = new ArrayList<MoveImpl>();
        PositionImpl currPos = new PositionImpl(pos);
        UndoInfo ui = new UndoInfo();
        int nMoves = ml.second;
        for (int i = 0; i < nMoves; i++) {
            Node n = moveList.get(i);
            mList.add(n.move);
            currPos.makeMove(n.move, ui);
            if (currPos.halfMoveClock == 0) {
                pos = new PositionImpl(currPos);
                mList.clear();
            }
        }
        return new Pair<PositionImpl, ArrayList<MoveImpl>>(pos, mList);
    }

    private final void handleDrawCmd(String drawCmd) {    	
        PositionImpl pos = tree.currentPos;
        if (drawCmd.startsWith("rep") || drawCmd.startsWith("50")) {
            boolean rep = drawCmd.startsWith("rep");
            MoveImpl m = null;
            String ms = null;
            int firstSpace = drawCmd.indexOf(" ");
            if (firstSpace >= 0) {
                ms = drawCmd.substring(firstSpace + 1);
                if (ms.length() > 0) {
                    m = TextIO.stringToMove(pos, ms);
                }
            }
            boolean valid;
            if (rep) {
                valid = false;
                UndoInfo ui = new UndoInfo();
                int repetitions = 0;
                PositionImpl posToCompare = new PositionImpl(tree.currentPos);
                if (m != null) {
                    posToCompare.makeMove(m, ui);
                    repetitions = 1;
                }
                Pair<List<Node>, Integer> ml = tree.getMoveList();
                List<Node> moveList = ml.first;
                PositionImpl tmpPos = new PositionImpl(tree.startPos);
                if (tmpPos.drawRuleEquals(posToCompare))
                    repetitions++;
                int nMoves = ml.second;
                for (int i = 0; i < nMoves; i++) {
                    Node n = moveList.get(i);
                    tmpPos.makeMove(n.move, ui);
                    TextIO.fixupEPSquare(tmpPos);
                    if (tmpPos.drawRuleEquals(posToCompare))
                        repetitions++;
                }
                if (repetitions >= 3)
                    valid = true;
            } else {
                PositionImpl tmpPos = new PositionImpl(pos);
                if (m != null) {
                    UndoInfo ui = new UndoInfo();
                    tmpPos.makeMove(m, ui);
                }
                valid = tmpPos.halfMoveClock >= 100;
            }
            if (valid) {
                String playerAction = rep ? "draw rep" : "draw 50";
                if (m != null)
                    playerAction += " " + TextIO.moveToString(pos, m, false, false);
                addToGameTree(new MoveImpl(0, 0, 0), playerAction);
            } else {
                pendingDrawOffer = true;
                if (m != null) {
                    processString(ms);
                }
            }
        } else if (drawCmd.startsWith("offer")) {
            pendingDrawOffer = true;
            String ms = drawCmd.substring(drawCmd.indexOf(" ") + 1);
            if (TextIO.stringToMove(pos, ms) != null) {
                processString(ms);
            }
        } else if (drawCmd.equals("accept")) {        	
            if (pendingDrawOffer){
                addToGameTree(new MoveImpl(0, 0, 0), "draw accept");
                pendingDrawOffer = false;
            }
        }
    }
}

    