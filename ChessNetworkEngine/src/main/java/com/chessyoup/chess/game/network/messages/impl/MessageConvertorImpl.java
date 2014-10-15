package com.chessyoup.chess.game.network.messages.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import com.chessyoup.chess.game.network.messages.IMessage;
import com.chessyoup.chess.game.network.messages.IMessage.COMMAND;
import com.chessyoup.chess.game.network.messages.MessageConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Simple message converter -> compressed UTF-8 JSON 
 * @author leo
 *
 */
public class MessageConvertorImpl implements MessageConverter {

	private static final Logger LOG = Logger
			.getLogger(MessageConvertorImpl.class.getName());
	private static final Gson gson = new GsonBuilder().create();
	
	public MessageConvertorImpl(){
		LOG.setLevel(Level.FINEST);
	}
	
	@Override
	public byte[] encode(IMessage message) {
		LOG.fine("encode :: " + message.toString());
		
		String json = gson.toJson(message);
				
		try {
			byte[] data = json.getBytes("UTF-8");
			LOG.finest("encode :: payload size before compress :" + data.length);
			byte[] cData = deflate(data);
			LOG.finest("encode :: payload size after compress :" + cData.length);
			return cData;
		} catch (UnsupportedEncodingException e) {
			LOG.warning("Error encoding using UTF-8");
			return inflate(json.getBytes());
		} 				
	}

	@Override
	public MessageImpl decode(byte[] message) {
		LOG.fine("decode :: message size :" + message.length);
		byte[] decodedData = inflate(message);
		LOG.finest("decode :: payload size after decompress :" + decodedData.length);
		String json;
		
		try {
			json = new String(decodedData,"UTF-8");
			LOG.finest("decode :: json :" + json);
			return gson.fromJson(json, MessageImpl.class);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return gson.fromJson(new String(decodedData), MessageImpl.class);
		}												
	}

	private byte[] deflate(byte[] originalBytes) {

		Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION, false);
		deflater.setInput(originalBytes);
		deflater.finish();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[8192];
		while (!deflater.finished()) {
			int byteCount = deflater.deflate(buf);
			baos.write(buf, 0, byteCount);
		}
		deflater.end();

		return baos.toByteArray();
	}

	private byte[] inflate(byte[] compressedBytes) {

		Inflater inflater = new Inflater();
		inflater.setInput(compressedBytes, 0, compressedBytes.length);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[8192];
		while (!inflater.finished()) {
			
			int byteCount;
			
			try {
				byteCount = inflater.inflate(buf);
			} catch (DataFormatException e) {
				LOG.log(Level.SEVERE,"Invalid message!",e);
				return null;
			}
			
			baos.write(buf, 0, byteCount);
		}

		inflater.end();

		return baos.toByteArray();
	}
	
	public static void main(String[] args) throws IOException {			
		MessageConvertorImpl convertor = new MessageConvertorImpl();
		MessageImpl m = new MessageImpl(COMMAND.MOVE);
		m.getPayload().put("gameId", "1");
		m.getPayload().put("move", "e2e4");
		byte[] data = convertor.encode(m);		
		IMessage decoded = convertor.decode( data );
		System.out.println(decoded);
	}
}
