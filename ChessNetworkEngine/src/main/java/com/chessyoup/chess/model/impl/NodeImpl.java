package com.chessyoup.chess.model.impl;

import com.chessyoup.chess.model.Move;
import com.chessyoup.chess.model.Node;
import com.chessyoup.chess.model.Position;
import com.chessyoup.chess.model.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leo on 17.10.2014.
 */
public class NodeImpl implements Node {

    private Move move;
    private Node parent;
    private List<Node> childs;
    private boolean main;
    private Result result;
    private long moveTime;
    private String preComment;
    private String postComment;
    private UndoInfo ui;

    public NodeImpl( Node parent, List<Node> childs, Move move, boolean main, Result result, long moveTime) {
        this.parent = parent;
        this.childs = childs;
        this.move = move;
        this.main = main;
        this.result = result;
        this.moveTime = moveTime;
        this.preComment = "";
        this.postComment = "";
        this.result = Result.NO_RESULT;
    }

    public NodeImpl(Node parent, List<Node> childs, Move move, boolean main) {
        this(parent,childs,move,main,null,0);
    }

    public NodeImpl(Node parent,Move move) {
        this(parent,new ArrayList<Node>(),move,true,null,0);
    }

    public NodeImpl(Move move, boolean main) {
        this(null,new ArrayList<Node>(),move,main,null,0);
    }

    public NodeImpl(Move move) {
        this(null,new ArrayList<Node>(),move,true,null,0);
    }

    public NodeImpl() {
        this(null,new ArrayList<Node>(),null,true,null,0);
    }

    public NodeImpl(Node parent, Move move, UndoInfo ui) {
        this(parent,move);
        this.ui = ui;
    }

    public NodeImpl(Node parent, Move move ,long time, UndoInfo ui) {
        this(parent,move);
        this.ui = ui;
        this.moveTime = time;
    }

    @Override
    public Move getMove() {
        return this.move;
    }

    @Override
    public boolean isMain() {
        return this.main;
    }

    @Override
    public Node getParent() {
        return this.parent;
    }

    @Override
    public List<Node> getChilds() {
        return this.childs;
    }

    @Override
    public Result getResult() {
        return this.result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    @Override
    public long moveTime() {
        return this.moveTime;
    }

    @Override
    public String getPreComment() {
        return this.preComment;
    }

    @Override
    public String getPostComment() {
        return this.postComment;
    }

    public UndoInfo getUi() {
        return ui;
    }

    public void setUi(UndoInfo ui) {
        this.ui = ui;
    }

    @Override
    public String toString() {
        return "NodeImpl{" +
                "move=" + move +
                ", parent="+
                ", main=" + main +
                ", result=" + result +
                ", moveTime=" + moveTime +
                ", preComment='" + preComment + '\'' +
                ", postComment='" + postComment + '\'' +
                ", veTime=" + moveTime() +
                '}';
    }
}
