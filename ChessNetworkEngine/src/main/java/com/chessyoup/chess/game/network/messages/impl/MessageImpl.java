package com.chessyoup.chess.game.network.messages.impl;

import java.util.HashMap;
import java.util.Map;

import com.chessyoup.chess.game.network.messages.IMessage;

public class MessageImpl implements IMessage {
	
	private COMMAND command;
	
	private Map<String, String> payload;	
	
	public MessageImpl(COMMAND type,Map<String, String> payload){
		this.command = type;
		this.payload = payload;
	}
	
	public MessageImpl(){
		this(COMMAND.UNKNOWN,new HashMap<String, String>());		
	}
	
	public MessageImpl(COMMAND command){
		this(command,new HashMap<String, String>());
	}
	
	@Override
	public COMMAND getCommand() {		
		return this.command;
	}

	@Override
	public Map<String, String> getPayload() {
		return this.payload;
	}

	@Override
	public String toString() {
		return "MessageImpl [command=" + command + ", payload=" + payload + "]";
	}
}
