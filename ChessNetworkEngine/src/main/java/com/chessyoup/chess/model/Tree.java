package com.chessyoup.chess.model;

import java.util.List;

/**
 * Created by leo on 15.10.2014.
 */
public interface Tree {

    /**
     * Get the root of this game tree
     * @return
     */
    public TreeNode getRoot();

    /**
     * Get all the tree
     * @return
     */
    public List<TreeNode> getTree();

    /**
     * Get main line
     * @return
     */
    public List<Position> getMainLine();

    /**
     * Get current selected node in the tree.
     * @return
     */
    public TreeNode getSelectedNode();

    /**
     * Set the current selected node in the game tree
     */
    public void setSelectedNode(TreeNode node);

    /**
     * Add new node using current selected node as parent.
     * The new node will become the new current selection.
     * @param node
     */
    public void appendNode(TreeNode node);

    /**
     * Insert new node in the tree.
     * The new node will become the new current selection.
     * @param parent - the parent for the new node
     * @param node - the node
     */
    public void insertNode(TreeNode parent,TreeNode node);
}
