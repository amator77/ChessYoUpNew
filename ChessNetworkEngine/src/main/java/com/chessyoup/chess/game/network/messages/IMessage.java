package com.chessyoup.chess.game.network.messages;

import java.util.Map;

public interface IMessage {

	public enum COMMAND {
		UNKNOWN, CONNECT, DISCONNECT, READY, EXIT, MOVE, RESIGN, FLAG, OFFER, CHAT
	}

	public COMMAND getCommand();

	public Map<String, String> getPayload();
}