package com.chessyoup.chess.game.network;

import com.chessyoup.chess.game.Player;
import com.chessyoup.chess.game.network.exceptions.NetworkException;
import com.chessyoup.chess.model.Factory;
import com.chessyoup.chess.model.Move;
import com.chessyoup.chess.model.exception.IllegalMoveException;
import com.chessyoup.chess.model.impl.MoveImpl;
import com.chessyoup.chess.model.impl.Util;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by leo on 29.10.2014.
 */
public class OnlineGameProtocol {

    private static final Logger LOG = Logger.getLogger(OnlineGameProtocol.class.toString());

    public static final byte READY = 1;
    public static final byte MOVE = 2;
    public static final byte RESIGN = 3;
    public static final byte LEFT = 4;
    public static final byte FLAG = 5;
    public static final byte OFFER = 6;
    public static final byte CHAT = 7;

    public static final byte OFFER_DRAW = 8;
    public static final byte OFFER_ABORT = 9;
    public static final byte OFFER_REMATCH = 10;

    public static final byte SEPARATOR = 0;

    public static void handleGameData(OnlineGame game, byte[] data) {

        switch (data[0]) {
            case READY: {
                LOG.log(Level.FINE, " handleGameData :: ready");
                game.gameReady();
            }
            break;
            case MOVE: {
                MoveTime moveTime = decodeMoveTime(data);
                LOG.log(Level.FINE, " handleGameData :: move : " + moveTime.move.toUCIString());

                try {
                    game.getChessboard().doMove(moveTime.move,moveTime.time);
                } catch (IllegalMoveException e) {
                    //TODO do something
                    e.printStackTrace();
                }
            }
            break;
            case RESIGN: {
                LOG.log(Level.FINE, " handleGameData :: resign");
            }
            break;
            case LEFT: {
                LOG.log(Level.FINE, " handleGameData :: left");
            }
            break;
            case FLAG: {
                LOG.log(Level.FINE, " handleGameData :: flag");
            }
            break;
            case OFFER: {

                switch (data[1]) {
                    case OFFER_DRAW: {
                        LOG.log(Level.FINE, " handleGameData :: offer draw");
                    }
                    break;
                    case OFFER_ABORT: {
                        LOG.log(Level.FINE, " handleGameData :: offer abort");
                    }
                    break;
                    case OFFER_REMATCH: {
                        LOG.log(Level.FINE, " handleGameData :: offer rematch");
                    }
                    break;
                }
            }
            break;
            case CHAT: {

            }
            break;
        }
    }

    public static void sendReady(OnlineGame game) throws NetworkException {
        sendData(game, getData(READY));
    }

    public static void sendMove(OnlineGame game, Move move, int moveTime) throws NetworkException {
        sendData(game, getData(MOVE, encodeMove(new MoveTime(move,moveTime))));
    }

    public static void sendResign(OnlineGame game) throws NetworkException {
        sendData(game, getData(RESIGN));
    }

    public static void sendLeft(OnlineGame game) throws NetworkException {
        sendData(game, getData(LEFT));
    }

    public static void sendFlag(OnlineGame game) throws NetworkException {
        sendData(game, getData(FLAG));
    }

    public static void sendDrawOffer(OnlineGame game) throws NetworkException {
        sendOffer(game, OFFER_DRAW);
    }

    public static void sendAbortOffer(OnlineGame game) throws NetworkException {
        sendOffer(game, OFFER_ABORT);
    }

    public static void sendRematchOffer(OnlineGame game) throws NetworkException {
        sendOffer(game, OFFER_REMATCH);
    }

    private static void sendOffer(OnlineGame game, byte offerType) throws NetworkException {
        byte[] payload = new byte[1];
        payload[0] = offerType;
        sendData(game, getData(OFFER, payload));
    }

    private static void sendData(OnlineGame game, byte[] data) throws NetworkException {
        LOG.log(Level.FINE, "Send " + data.length + " bytes to :" + game.getConfig().remotePlayer.getId());

        game.getConfig().connection.sendMessage(game.getConfig().remotePlayer, data);
    }

    private static byte[] getData(byte command) {
        return getData(command, null);
    }

    private static byte[] getData(byte command, byte[] payload) {
        byte[] data = new byte[1 + payload.length];
        data[0] = command;

        if (payload != null) {
            System.arraycopy(payload, 0, data, 1, payload.length);
        }

        return data;
    }

    private static final byte[] intToByteArray(int value) {
        return new byte[]{
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) value};
    }

    private static int byteArrayToInt(byte[] b) {
        return b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    private static byte[] encodeMove(MoveTime moveTime) {
        byte[] timeBytes = intToByteArray(moveTime.time);
        byte[] uciBytes = moveTime.move.toUCIString().getBytes();
        byte[] payload = new byte[4 + uciBytes.length];
        System.arraycopy(timeBytes, 0, payload, 0, 4);
        System.arraycopy(uciBytes, 0, payload, 4, uciBytes.length);
        return payload;
    }

    private static MoveTime decodeMoveTime(byte[] data) {
        MoveTime moveTime = new MoveTime(null,0);
        byte[] time = new byte[4];
        System.arraycopy(data, 1 , time, 0, 4);
        moveTime.time = byteArrayToInt(time);
        byte[] move = new byte[data.length - 5];
        System.arraycopy(data, 5 , move, 0, move.length);
        moveTime.move = Factory.getFactory().uciStringToMove(new String(move));
        return moveTime;
    }


    private static class MoveTime {
        Move move;
        int time;

        MoveTime(Move move, int time){
            this.move = move;
            this.time = time;
        }

        @Override
        public String toString() {
            return "MoveTime{" +
                    "move=" + move +
                    ", time=" + time +
                    '}';
        }
    }

    public static void main(String[] args) {
        MoveTime moveTime = new MoveTime(Util.UCIstringToMove("e2e4"),3345);
        byte[] payload = encodeMove(moveTime);
        System.out.println("payload length "+payload.length);
        byte[] data = new byte[payload.length+1];
        data[0] = MOVE;
        System.arraycopy(payload,0,data,1,payload.length);
        MoveTime decoded = decodeMoveTime(data);
        System.out.println(decoded);
    }
}
