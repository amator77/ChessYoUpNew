package com.chessyoup.tests;

import com.chessyoup.chess.game.Game;
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

        final RandomPlayer whitePlayer = new RandomPlayer("white");
        final RandomPlayer blackPlayer = new RandomPlayer("black");
        final GameImpl game = new GameImpl("1", new ChessboardImpl());

        game.addGameListener(new Game.GameListener() {

            boolean running = true;

            @Override
            public void onStateChange(Game source, Game.STATE oldState, Game.STATE newState) {

                switch (newState) {
                    case READY: {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (running) {
                                    switch (game.getChessboard().getPosition().getActiveColor()) {
                                        case WHITE:
                                            whitePlayer.yourTurn(game);
                                        case BLACK:
                                            blackPlayer.yourTurn(game);
                                    }
                                }
                            }
                        }).start();
                    }
                    break;
                    case IN_PROGRESS: {

                    }
                    break;
                    case FINISHED: {
                        running = false;
                    }
                }
            }
        });

        game.setup(whitePlayer, blackPlayer, new TimeCtrl(1000 * 10 * 1, 0));
    }
}
