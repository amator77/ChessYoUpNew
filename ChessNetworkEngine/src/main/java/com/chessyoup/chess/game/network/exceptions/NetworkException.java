package com.chessyoup.chess.game.network.exceptions;

import java.io.IOException;

/**
 * Created by leo on 29.10.2014.
 */
public class NetworkException extends Exception {

    public NetworkException(Exception e) {
        super(e);
    }

    public NetworkException(String message,Exception e) {
        super(message,e);
    }
}
