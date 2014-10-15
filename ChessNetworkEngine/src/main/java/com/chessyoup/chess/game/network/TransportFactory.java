package com.chessyoup.chess.game.network;

import com.chessyoup.chess.game.network.impl.InMemoryTransport;

public class TransportFactory {
	
	private static Transport defaultTransport = new InMemoryTransport();
	
	public static Transport getDefautlTransport(){
		return defaultTransport;
	}
}
