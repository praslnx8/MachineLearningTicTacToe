package com.prasilabs.machinelearningtictactoe.logic;

/**
 * Created by prasi on 15/7/17.
 */

public class Edge {
    private TicTacToeBoard parent;
    private TicTacToeBoard child;
    private int cost;

    public Edge(TicTacToeBoard parent, TicTacToeBoard child) {
        this.parent = parent;
        this.child = child;
    }

    public TicTacToeBoard getParent() {
        return parent;
    }

    public void setParent(TicTacToeBoard parent) {
        this.parent = parent;
    }

    public TicTacToeBoard getChild() {
        return child;
    }

    public void setChild(TicTacToeBoard child) {
        this.child = child;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
}
