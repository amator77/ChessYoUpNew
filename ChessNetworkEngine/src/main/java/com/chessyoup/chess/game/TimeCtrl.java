package com.chessyoup.chess.game;

public class TimeCtrl {

    private long time;

    private int movesPerGame;

    private long increment;

    public TimeCtrl(long time , int movesPerGame , long increment){
        this.time = time;
        this.movesPerGame = movesPerGame;
        this.increment = increment;
    }

    public TimeCtrl(long time , long increment){
        this(time,0,increment);
    }

    public TimeCtrl(long time){
        this(time,0,0);
    }

    /**
     * Game time in milliseconds
     * @return
     */
	public long getTime(){
        return this.time;
    }

    /**
     * Nr of moves to be made in time.
     * @return
     */
	public int getMovesPerGame(){
        return this.movesPerGame;
    }

    /**
     * Increment in milliseconds.
     * @return
     */
    public long getIncrement(){
        return this.increment;
    }
}
