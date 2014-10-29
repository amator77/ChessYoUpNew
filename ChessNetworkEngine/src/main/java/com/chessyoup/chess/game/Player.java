package com.chessyoup.chess.game;

import com.chessyoup.chess.game.Game.OFFER;
import com.chessyoup.chess.model.Move;

public interface Player {
	
	public interface PlayerListener {

        /**
         * Called when this player produce an valid move for current chessboard.
         * @param move
         */
		public void onMove(Player source,String gameId , Move move , long moveTime);

        /**
         * Called when this player make an offer to opponent
         * @param offer
         */
		public void onOffer(Player source,OFFER offer,String gameId);

        /**
         * Called when this player resign on this chessboard.
         */
		public void onResign(Player source,String gameId);

        /**
         * Called when this player leave ( exit )  this chessboard.
         * The chessboard will finish with this player lousing the chessboard.
         */
        public void onExit(Player source,String gameId);

        /**
         * Called when this player is out of time ( flag ).
         * The chessboard will finish with this player lousing the chessboard.
         */
        public void onFlag(Player source,String gameId);

        /**
         * Called when this player is sending an chat message to opponent.
         */
        public void onChat(Player source,String gameId,String message);

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

    /**
     * Notify this player that is his turn to move on t his game.
     * The player will produce new move on the game , and will generate an onMove event.
     */
    public void yourTurn(Game game);

	public void addListener(PlayerListener listener);
	
	public void removeListener(PlayerListener listener);
}
