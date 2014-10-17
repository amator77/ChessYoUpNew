package com.chessyoup.chess.model;

import java.util.List;

/**
 * Created by leo on 15.10.2014.
 */
public interface Node {

    /**
     * Move generating this game node.
     * @return
     */
    public Move getMove();

    /**
     * Mark if this node is part of the main line of the game.
     * @return
     */
    public boolean isMain();

    /**
     * Node position.
     * @return
     */
    public Position getPosition();

    /**
     * Parent position of this node. This is null for the root position of the game.
     * @return
     */
    public Node getParent();

    /**
     * Next positions from this node.This is null if this node is an leaf.
     * @return
     */
    public List<Node> getChilds();

    /**
     * The node result if any .
     * @return - the result of the node , or NO_RESULT
     */
    public Result getResult();

    /**
     * Move tine for this node in milliseconds.
     * @return
     */
    public long moveTime();

    /**     *
     * @return
     */
    public String getPreComment();

    /**     *
     * @return
     */
    public String getPostComment();
}