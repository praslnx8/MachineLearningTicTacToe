package com.prasilabs.machinelearningtictactoe.logic;

import com.prasilabs.machinelearningtictactoe.pojos.XO;

/**
 * Created by prasi on 15/7/17.
 */

public class TicTacToeBoard {
    private XO[][] mBoard;
    private int mCurrentXPos;
    private int mCurrentOPos;
    private XO player;
    private boolean isWin;

    public TicTacToeBoard(int boardSize) {
        this.mBoard = new XO[boardSize][boardSize];
    }

    public TicTacToeBoard(XO[][] mBoard, int mCurrentXPos, int mCurrentOPos, XO player) {
        this.mBoard = mBoard;
        this.mCurrentXPos = mCurrentXPos;
        this.mCurrentOPos = mCurrentOPos;
        this.player = player;
    }

    public XO[][] getmBoard() {
        return mBoard;
    }

    public void setmBoard(XO[][] mBoard) {
        this.mBoard = mBoard;
    }

    public int getmCurrentXPos() {
        return mCurrentXPos;
    }

    public void setmCurrentXPos(int mCurrentXPos) {
        this.mCurrentXPos = mCurrentXPos;
    }

    public int getmCurrentOPos() {
        return mCurrentOPos;
    }

    public void setmCurrentOPos(int mCurrentOPos) {
        this.mCurrentOPos = mCurrentOPos;
    }

    public XO getPlayer() {
        return player;
    }

    public void setPlayer(XO player) {
        this.player = player;
    }

    public boolean isWin() {
        return isWin;
    }

    public void setWin(boolean win) {
        isWin = win;
    }
}
