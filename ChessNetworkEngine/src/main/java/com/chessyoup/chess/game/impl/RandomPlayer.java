package com.chessyoup.chess.game.impl;

import com.chessyoup.chess.game.Game;
import com.chessyoup.chess.model.impl.MoveGen;
import com.chessyoup.chess.model.impl.MoveImpl;
import com.chessyoup.chess.model.impl.PositionImpl;
import com.chessyoup.chess.model.impl.Util;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by leo on 28.10.2014.
 */
public class RandomPlayer extends PlayerImpl {

    Random r = new Random();

    public RandomPlayer(String playerId) {
        super(playerId);
    }

    @Override
    public void yourTurn(Game game) {
        PositionImpl impl = (PositionImpl)game.getChessboard().getPosition();
        ArrayList<MoveImpl> moveList = MoveGen.instance.legalMoves(impl);

        if( moveList.size() > 0) {
            int index = moveList.size()-1;
            MoveImpl move = moveList.get( index > 0 ? r.nextInt(index) : index);
            long moveTime = r.nextInt(100);
            try { Thread.sleep(moveTime);} catch (InterruptedException e) {e.printStackTrace();}
            fireMoveEvent(game.getId(), move, moveTime);
        }
        else{
            System.out.println("No move moves !!!");
        }
    }
}
