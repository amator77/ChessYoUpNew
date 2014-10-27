package com.chessyoup.chess.model;

import java.util.List;

/**
 * Created by leo on 15.10.2014.
 */
public interface Tree {

    /**
     * Get the root of this chessboard tree
     * @return
     */
    public Node getRoot();

    /**
     * Get all the tree
     * @return
     */
    public List<Node> getTree();

    /**
     * Get current selected node in the tree.
     * @return
     */
    public Node getSelectedNode();

    /**
     * Set the current selected node in the chessboard tree
     */
    public void setSelectedNode(Node node);

    /**
     * Add new node using current selected node as parent.
     * The new node will become the new current selection.
     * @param node
     */
    public void appendNode(Node node);

    /**
     * Insert new node in the tree.
     * The new node will become the new current selection.
     * @param parent - the parent for the new node
     * @param node - the node
     */
    public void insertNode(Node parent,Node node);
}
