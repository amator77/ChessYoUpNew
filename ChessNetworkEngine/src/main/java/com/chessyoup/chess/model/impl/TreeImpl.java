package com.chessyoup.chess.model.impl;

import com.chessyoup.chess.model.Node;
import com.chessyoup.chess.model.Position;
import com.chessyoup.chess.model.Tree;
import com.chessyoup.chess.model.exception.ChessParseError;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leo on 17.10.2014.
 */
public class TreeImpl implements Tree {

    private Node root;
    private Node selectedNode;

    public TreeImpl(Node root) {
        this.root = root;
        this.selectedNode = root;
    }

    @Override
    public Node getRoot() {
        return this.root;
    }

    @Override
    public List<Node> getTree() {
        ArrayList<Node> list = new ArrayList<Node>();
        this.collectNodes(this.root, list);

        //TODO cache this

        return list;
    }

    @Override
    public Node getSelectedNode() {
        return this.selectedNode;
    }

    @Override
    public void setSelectedNode(Node node) {
        this.selectedNode = node;
    }

    @Override
    public void appendNode(Node node) {
        this.selectedNode.getChilds().add(node);
        this.selectedNode = node;
    }

    @Override
    public void insertNode(Node parent, Node node) {
        parent.getChilds().add(node);
        this.selectedNode = node;
    }

    public String print(){
        try {
            StringBuffer sb = new StringBuffer();
            List<Node> nodes = getTree();
            PositionImpl pos = TextIO.readFEN(TextIO.startPosFEN);

            int index = 1;
            int count = 1;
            for( Node node : nodes){
                if( node.getMove() != null ) {

                    MoveImpl move = Util.convertMove(node.getMove());
                    pos.makeMove(move, new UndoInfo());
                    String moveStr = TextIO.moveToUCIString(move);

                    if( index++ % 2 != 0){
                        sb.append( count++).append(".");
                    }

                    sb.append(moveStr);
                    sb.append(" ");
                }
            }

            return sb.toString();

        } catch (ChessParseError chessParseError) {
            chessParseError.printStackTrace();
            return "";
        }
    }

    private void collectNodes(Node node, ArrayList<Node> list) {
        list.add(node);

        if (node.getChilds().size() > 0) {

            for (Node n : node.getChilds()) {
                this.collectNodes(n, list);
            }
        }
    }

    @Override
    public String toString() {
        return "TreeImpl{" +
                "root=" + root +
                ", selectedNode=" + selectedNode +
                ", tree=" + print() +
                '}';
    }
}
