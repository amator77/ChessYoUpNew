package com.chessyoup.chess.game.network.messages;

import java.util.Map;

import com.chessyoup.chess.game.network.messages.impl.MessageConvertorImpl;
import com.chessyoup.chess.game.network.messages.impl.MessageImpl;

public class MessageFactory {
	
	private static MessageConvertorImpl defaultConverter = new MessageConvertorImpl();
	
	public static IMessage createMessage(IMessage.COMMAND command,Map<String,String> payload){
		return new MessageImpl(command,payload);
	}
	
	public static MessageConverter getDefaultConverter(){
		return defaultConverter;
	}
}
