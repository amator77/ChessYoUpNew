package com.chessyoup.chess.game;

public class Rating {

    public enum TYPE {BULLET, BLITZ, STANDARD}

    private TYPE type;

    private double value;

    private double ratingDeviation;

    private double volatility;

    private static final Double DEFAULT_RATING_DEVIATION = 350d;

    private static final Double DEFAULT_RATING_VALUE = 1500d;

    private static final Double DEFAULT_VOLATILITY = 0d;

    public Rating(TYPE type, double value, double ratingDeviation, double volatility) {
        this.type = type;
        this.value = value;
        this.ratingDeviation = ratingDeviation;
        this.volatility = volatility;
    }

    public Rating(TYPE type, double value) {
        this(type, value, DEFAULT_RATING_DEVIATION, DEFAULT_VOLATILITY);
    }

    public Rating(TYPE type) {
        this(type, DEFAULT_RATING_VALUE, DEFAULT_RATING_DEVIATION, DEFAULT_VOLATILITY);
    }

    public Rating(double value) {
        this(TYPE.BLITZ, value, DEFAULT_RATING_DEVIATION, DEFAULT_VOLATILITY);
    }

    public Rating() {
        this(TYPE.BLITZ, DEFAULT_RATING_VALUE, DEFAULT_RATING_DEVIATION, DEFAULT_VOLATILITY);
    }

    public TYPE getType() {
        return this.type;
    }

    public double getValue() {
        return this.value;
    }

    public String getDisplayRating() {
        return String.valueOf(this.value);
    }

    public double getRatingDeviation() {
        return this.ratingDeviation;
    }

    public double getVolatility() {
        return this.volatility;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setRatingDeviation(double ratingDeviation) {
        this.ratingDeviation = ratingDeviation;
    }

    public void setVolatility(double volatility) {
        this.volatility = volatility;
    }
}
