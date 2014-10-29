package com.chessyoup.tests;

import com.chessyoup.chess.game.TimeCtrl;
import com.chessyoup.chess.game.impl.GameImpl;
import com.chessyoup.chess.game.impl.RandomPlayer;
import com.chessyoup.chess.model.Chessboard;
import com.chessyoup.chess.model.Color;
import com.chessyoup.chess.model.Result;
import com.chessyoup.chess.model.impl.ChessboardImpl;
import com.chessyoup.chess.model.impl.PositionImpl;
import com.chessyoup.chess.model.impl.TextIO;

public class Test {

    public static void main(String[] args) {

        RandomPlayer whitePlayer = new RandomPlayer("white");
        RandomPlayer blackPlayer = new RandomPlayer("black");
        final GameImpl game = new GameImpl("1",new ChessboardImpl());

        game.getChessboard().addChessboardListener(new Chessboard.ChessboardListener() {
            @Override
            public void onChange(Chessboard source) {
                PositionImpl impl = (PositionImpl)source.getPosition();
                System.out.println("On change event");
                System.out.println(TextIO.asciiBoard(impl));
//                System.out.println(source.getMovesTree().toString());
                System.out.println("to move: " + impl.getActiveColor() + " ,move nr :" + impl.getFullMoveNumber());
                System.out.println("White time: "+source.getClockTime(Color.WHITE));
                System.out.println("Black time: "+source.getClockTime(Color.BLACK));
            }

            @Override
            public void onResult(Chessboard source) {
                System.out.println("game over , result : "+game.getResult());
            }
        });

        game.setup(whitePlayer, blackPlayer, new TimeCtrl(1000 * 10 * 1, 0));

        while( game.getResult() == Result.NO_RESULT){
            switch (game.getChessboard().getPosition().getActiveColor()){
                case WHITE:whitePlayer.yourTurn(game);
                case BLACK:blackPlayer.yourTurn(game);
            }
        }
    }
}
