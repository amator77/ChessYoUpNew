package com.chessyoup.chess.game.network;

import com.chessyoup.chess.game.network.messages.IMessage;


public interface Transport {

	public interface TransportListener {
		
		public void onMessageReceived(IMessage message);						
	}
	
	public void addTransportListener(TransportListener listener);
	
	public void removeTransportListener(TransportListener listener);
			
	public void sendMessage(IMessage message);
}
