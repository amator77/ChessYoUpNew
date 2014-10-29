package com.chessyoup.chess;

import com.chessyoup.chess.game.Player;
import com.chessyoup.chess.game.Rating;
import com.chessyoup.chess.game.TimeCtrl;

import org.goochjs.glicko2.RatingCalculator;
import org.goochjs.glicko2.RatingPeriodResults;

/**
 * Created by leo on 29.10.2014.
 */
public class Util {

    public static long DEFAULT_RATING_DEVIATION = 150;
    public static long DEFAULT_RATING_VOLATILITY = 0;

    public static RatingCalculator ratingSystem = new RatingCalculator(0.06, 0.5);

    public static final void updateRatingsOnResult(Player winner, Player louser, Rating.TYPE type) {
        org.goochjs.glicko2.Rating winnerRating = convertRating(winner.getId(), winner.getRating(type));
        org.goochjs.glicko2.Rating louserRating = convertRating(louser.getId(), louser.getRating(type));
        RatingPeriodResults results = new RatingPeriodResults();
        results.addResult(winnerRating, louserRating);
        ratingSystem.updateRatings(results);
        winner.getRating(type).setValue(winnerRating.getRating());
        winner.getRating(type).setRatingDeviation(winnerRating.getRatingDeviation());
        winner.getRating(type).setVolatility(winnerRating.getVolatility());
        louser.getRating(type).setValue(louserRating.getRating());
        louser.getRating(type).setRatingDeviation(louserRating.getRatingDeviation());
        louser.getRating(type).setVolatility(louserRating.getVolatility());
    }

    public static final void updateRatingsOnDraw(Player whitePlayer, Player blackPlayer, Rating.TYPE type) {
        org.goochjs.glicko2.Rating whiteRating = convertRating(whitePlayer.getId(), whitePlayer.getRating(type));
        org.goochjs.glicko2.Rating blackRating = convertRating(blackPlayer.getId(), blackPlayer.getRating(type));
        RatingPeriodResults results = new RatingPeriodResults();
        results.addDraw(whiteRating, blackRating);
        ratingSystem.updateRatings(results);
        whitePlayer.getRating(type).setValue(whiteRating.getRating());
        whitePlayer.getRating(type).setRatingDeviation(whiteRating.getRatingDeviation());
        whitePlayer.getRating(type).setVolatility(whiteRating.getVolatility());
        blackPlayer.getRating(type).setValue(blackRating.getRating());
        blackPlayer.getRating(type).setRatingDeviation(blackRating.getRatingDeviation());
        blackPlayer.getRating(type).setVolatility(blackRating.getVolatility());
    }

    private static org.goochjs.glicko2.Rating convertRating(String playerId, Rating rating) {
        org.goochjs.glicko2.Rating glickoRating = new org.goochjs.glicko2.Rating(playerId, ratingSystem);
        glickoRating.setRating(rating.getValue());
        glickoRating.setRatingDeviation(rating.getRatingDeviation());
        glickoRating.setVolatility(rating.getVolatility());
        return glickoRating;
    }

    public static Rating.TYPE getGameType(TimeCtrl timeCtrl) {
        long blitz = 1000 * 60 * 3;
        long standard = 1000 * 60 * 10;

        if( timeCtrl.getTime() < blitz ){
            return Rating.TYPE.BULLET;
        }
        else if( timeCtrl.getTime() >= blitz &&  timeCtrl.getTime() < standard ){
            return Rating.TYPE.BLITZ;
        }
        else{
            return Rating.TYPE.STANDARD;
        }
    }
}
