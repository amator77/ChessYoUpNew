package com.chessyoup.chess.game;

import com.chessyoup.chess.game.Game.OFFER;
import com.chessyoup.chess.model.Move;

public interface Player {
	
	public interface PlayerListener {

        /**
         * Called when this player produce an valid move for current chessboard.
         * @param move
         */
		public void onMove(Move move,String gameId);

        /**
         * Called when this player make an offer to opponent
         * @param offer
         */
		public void onOffer(OFFER offer,String gameId);

        /**
         * Called when this player resign on this chessboard.
         */
		public void onResign(String gameId);

        /**
         * Called when this player leave ( exit )  this chessboard.
         * The chessboard will finish with this player lousing the chessboard.
         */
        public void onExit(String gameId);

        /**
         * Called when this player is out of time ( flag ).
         * The chessboard will finish with this player lousing the chessboard.
         */
        public void onFlag(String gameId);

        /**
         * Called when this player is sending an chat message to opponent.
         */
        public void onChat(String gameId,String message);

	}

    /**
     * Get the player unique ID
     * @return
     */
	public String getId();

    /**
     * Get player rating for specific type.
     * @param type
     * @return
     */
	public Rating getRating(Rating.TYPE type);

    /**
     * Update player rating
     * @param type
     * @param newRating
     */
	public void updateRating(Rating.TYPE type, double newRating);
	
	public void addListener(PlayerListener listener);
	
	public void removeListener(PlayerListener listener);
}
