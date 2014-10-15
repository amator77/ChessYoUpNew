package com.chessyoup.chess.game.network.messages;

public interface MessageConverter {
	
	public byte[] encode(IMessage message);
	
	public IMessage decode(byte[] data);
	
}
