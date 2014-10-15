package com.chessyoup.chess.game.network.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.chessyoup.chess.game.network.Transport;
import com.chessyoup.chess.game.network.messages.IMessage;
import com.chessyoup.chess.game.network.messages.MessageConverter;
import com.chessyoup.chess.game.network.messages.MessageFactory;

public class InMemoryTransport implements Transport, Runnable {

	private static final Logger LOG = Logger.getLogger(InMemoryTransport.class
			.getName());

	public List<TransportListener> listeners = new ArrayList<Transport.TransportListener>();

	private Queue<IMessage> messages;

	private MessageConverter converter;

	private static final Object lock = new Object();

	private Random randomLag;

	public InMemoryTransport() {
		this.messages = new PriorityBlockingQueue<IMessage>();
		this.converter = MessageFactory.getDefaultConverter();
		this.randomLag = new Random();
		new Thread(this, "Transport").start();
	}

	@Override
	public void addTransportListener(TransportListener listener) {

		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	@Override
	public void removeTransportListener(TransportListener listener) {

		if (listeners.contains(listener)) {
			listeners.remove(listener);
		}

	}

	@Override
	public void sendMessage(IMessage message) {
		LOG.log(Level.FINE, "sendMessage :: " + message.toString());

		// simulate encoding / decoding of the message
		byte[] data = this.converter.encode(message);
		this.messages.add(this.converter.decode(data));

		synchronized (lock) {
			lock.notify();
		}
	}

	@Override
	public void run() {
		
		while(true){
			
			synchronized (lock) {
				
				try {
					LOG.fine("Waiting for messages.");
					lock.wait();
					
					while( !this.messages.isEmpty() ){
						IMessage newMessage = this.messages.poll();						
						LOG.fine("New incomig message :: "+newMessage.toString());
						
						// simulate some random lag between 1 and 5000 ms
						int lag = this.randomLag.nextInt(5000);																		
						Thread.sleep(lag);
						LOG.finest("lag :: "+ lag );
						
						InMemoryTransport.this.fireNewMessageReceived(newMessage);						
					}
										
				} catch (InterruptedException e) {
					LOG.warning("InterruptedException. Transport layer is closing!");
					break;
				}
			}			
		}		
	}

	private void fireNewMessageReceived(IMessage message) {
		for (TransportListener listener : this.listeners) {
			listener.onMessageReceived(message);
		}
	}
	
	public static void main(String[] args) {
		
		Logger log = LogManager.getLogManager().getLogger("");
		for (Handler h : log.getHandlers()) {
			System.out.println("aici");
		    h.setLevel(Level.FINEST);
		}
		
		new InMemoryTransport();
	}
}
