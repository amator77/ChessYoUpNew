package com.chessyoup.tests;

import com.chessyoup.chess.game.Player;
import com.chessyoup.chess.game.TimeCtrl;
import com.chessyoup.chess.game.impl.PlayerImpl;
import com.chessyoup.chess.game.impl.RandomPlayer;
import com.chessyoup.chess.game.network.OnlineGame;
import com.chessyoup.chess.game.network.OnlineGameConfig;
import com.chessyoup.chess.game.network.exceptions.NetworkException;
import com.chessyoup.chess.game.network.impl.SocketConnection;
import com.chessyoup.service.ServiceFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by leo on 30.10.2014.
 */
public class OnlineLocalTest {

    public static void main(String[] args) throws NetworkException, IOException {

        SocketConnection connection = new SocketConnection("127.0.0.1",8989);
        connection.open();
        PlayerImpl local = new RandomPlayer("1");
        PlayerImpl remote = new RandomPlayer("2");
        connection.setRemotePlayer(remote);
        OnlineGameConfig config = new OnlineGameConfig();
        config.setConnection(connection).setGameId("1").setOwner(local).setLocalPlayer(local).setTimeCtrl(new TimeCtrl(1000*60*1,0));
        OnlineGame game = new OnlineGame(config);

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = "";

        while( true ){
            line  = reader.readLine();

            if( line.equals("exit")){
                break;
            }


        }

        connection.close();
    }
}
