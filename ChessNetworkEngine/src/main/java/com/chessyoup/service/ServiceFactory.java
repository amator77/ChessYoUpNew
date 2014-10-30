package com.chessyoup.service;

import com.chessyoup.chess.game.*;
import com.chessyoup.chess.game.impl.ServiceImpl;

/**
 * Created by leo on 28.10.2014.
 */
public class ServiceFactory {

    private static final ServiceImpl service = new ServiceImpl();

    private ServiceFactory(){

    }

    public static Service getService(){
        return ServiceFactory.service;
    }

}
