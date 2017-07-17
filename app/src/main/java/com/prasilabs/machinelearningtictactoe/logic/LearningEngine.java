package com.prasilabs.machinelearningtictactoe.logic;

import android.util.Log;

import com.prasilabs.machinelearningtictactoe.pojos.XO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by prasi on 15/7/17.
 */

public class LearningEngine {
    private static final String TAG = LearningEngine.class.getSimpleName();

    private List<TicTacToeBoard> mNodes;
    private List<Edge> mConnections;

    private TicTacToeBoard headBoard;
    private TicTacToeBoard currentBoard;
    private final int mBoardSize;
    private final int mWinningLength;

    public LearningEngine(int boardSize, int winningLength) {
        mNodes = new ArrayList<>();
        mConnections = new ArrayList<>();
        mBoardSize = boardSize;
        mWinningLength = winningLength;
        headBoard = new TicTacToeBoard(winningLength);
    }

    public void playUser(int xPos, int yPos) {
        if(currentBoard == null) {
            currentBoard = headBoard;
            mNodes.add(currentBoard);
        }
        XO[][] nextBoard = getNextPosBoard(currentBoard.getmBoard(), xPos, yPos, XO.O);

        TicTacToeBoard nextTicTacToeBoard = getTicTacToeBoardForBoard(nextBoard);

        if(nextTicTacToeBoard == null) {
            nextTicTacToeBoard = new TicTacToeBoard(nextBoard, xPos, yPos, XO.O);
            mNodes.add(nextTicTacToeBoard);
        }

        Edge edge = getEdgeForBoard(currentBoard, nextTicTacToeBoard);
        if(edge == null) {
            edge = new Edge(currentBoard, nextTicTacToeBoard);
            mConnections.add(edge);
        }

        currentBoard = nextTicTacToeBoard;
    }

    public void playSystem() {

        Edge nextPossibleEdge = null;

        List<Edge> edgeList = getChildEdges(currentBoard);

        Collections.sort(edgeList, new Comparator<Edge>() {
            @Override
            public int compare(Edge edge, Edge t1) {
                if(edge.getCost() < t1.getCost()) {
                    return -1;
                } else if(edge.getCost() > t1.getCost()) {
                    return 1;
                }
                return 0;
            }
        });

        if(!edgeList.isEmpty()) {
            nextPossibleEdge = edgeList.get(0);
        }

        if(nextPossibleEdge != null && nextPossibleEdge.getCost() <= 0) {
            Log.e(TAG, "cost of next edge is : " + nextPossibleEdge.getCost());
            currentBoard = nextPossibleEdge.getChild();
            return;
        } else {
            for(int i = 0; i<mBoardSize; i++) {
                for(int j=0; j<mBoardSize; j++) {
                    if(currentBoard.getmBoard()[i][j] == null) {
                        Log.d(TAG, "trying new : " + i + ":" + j);
                        XO[][] nextBoard = getNextPosBoard(currentBoard.getmBoard(), i,j, XO.X);

                        TicTacToeBoard nextTicTacToeBoard = getTicTacToeBoardForBoard(nextBoard);
                        if(nextTicTacToeBoard != null) {
                            List<Edge> parentEdges = getParentEdges(nextTicTacToeBoard);
                            if(!parentEdges.isEmpty()) {
                                for(Edge edge : parentEdges) {
                                    if(edge.getParent() != currentBoard) {
                                        Edge newEdge = new Edge(currentBoard, nextTicTacToeBoard);
                                        mConnections.add(newEdge);
                                        currentBoard = nextTicTacToeBoard;
                                        return;
                                    }
                                }
                            }
                        } else {
                            TicTacToeBoard ticTacToeBoard = new TicTacToeBoard(nextBoard, i, j, XO.X);
                            Edge edge = new Edge(currentBoard, ticTacToeBoard);
                            mNodes.add(ticTacToeBoard);
                            mConnections.add(edge);
                            currentBoard = ticTacToeBoard;
                            return;
                        }
                    }
                }
            }
        }

        if(nextPossibleEdge != null) {
            currentBoard = nextPossibleEdge.getChild();
        } else {
            Log.w(TAG, "dont know what to do");
        }
    }

    private XO[][] getNextPosBoard(XO[][] board, int xPos, int yPos, XO player) {

        XO[][] newBoard = new XO[mBoardSize][mBoardSize];

        if(board != null) {
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board.length; j++) {
                    if (board[i][j] != null) {
                        newBoard[i][j] = board[i][j];
                    }
                }
            }
        }
        newBoard[xPos][yPos] = player;

        return newBoard;
    }

    public TicTacToeBoard getTicTacToeBoardForBoard(XO[][] board) {
        for(TicTacToeBoard tBoard : mNodes) {
            if(isSameTicTacToe(tBoard, board)) {
                return tBoard;
            }
        }

        return null;
    }

    private boolean isSameTicTacToe(TicTacToeBoard tBoard, XO[][] board) {
        for(int i = 0; i< mBoardSize; i++) {
            for(int j = 0; j< mBoardSize; j++) {
                if(board[i][j] != tBoard.getmBoard()[i][j]) {
                    return false;
                }
            }
        }

        return true;
    }

    private Edge getEdgeForBoard(TicTacToeBoard t1, TicTacToeBoard t2) {
        for(Edge edge : mConnections) {
            if(edge.getParent() == t1 && edge.getChild() == t2) {
                return edge;
            }
        }

        return null;
    }

    private List<Edge> getChildEdges(TicTacToeBoard board) {
        List<Edge> edgeList = new ArrayList<>();

        for(Edge edge : mConnections) {
            if(edge.getParent() == board) {
                edgeList.add(edge);
            }
        }

        return edgeList;
    }

    private List<Edge> getParentEdges(TicTacToeBoard board) {
        List<Edge> edgeList = new ArrayList<>();

        Log.d(TAG, "connection size is : " + mConnections.size());
        for(Edge edge : mConnections) {
            if(edge.getChild() == board) {
                edgeList.add(edge);
            }
        }

        return edgeList;
    }

    public boolean checkWon() {
        if(isWon()) {
            currentBoard.setWin(true);
            if(currentBoard.getPlayer() == XO.X) {
                //happy
                updateCost(-2, currentBoard);
            } else {
                //sad
                updateCost(+2, currentBoard);
            }

            return true;
        } else {
            currentBoard.setWin(false);

            return false;
        }
    }

    private void updateCost(int cost, TicTacToeBoard child) {
        if(child != null) {
            List<Edge> edgeList = getParentEdges(child);

            for (Edge edge : edgeList) {
                edge.setCost(edge.getCost() + cost);
                updateCost(cost, edge.getParent());
            }
        }
    }

    public TicTacToeBoard getCurrentBoard() {
        return currentBoard;
    }

    private boolean isDraw() {
        if(currentBoard != null) {
            for (int i = 0; i < mBoardSize; i++) {
                for (int j = 0; j < mBoardSize; j++) {
                    if(currentBoard.getmBoard()[i][j] == null) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private boolean isWon() {
        if(currentBoard != null && currentBoard.getPlayer() != null) {
            XO[][] mBoard = currentBoard.getmBoard();
            int xPos = currentBoard.getmCurrentXPos();
            int oPos = currentBoard.getmCurrentOPos();
            XO mPlayer = currentBoard.getPlayer();
            int seq = 0;
            //check col
            for (int i = 0; i < mBoardSize; i++) {
                if (mBoard[xPos][i] == mPlayer) {
                    seq++;
                    if (seq == mWinningLength) {
                        return true;
                    }
                } else {
                    seq = 0;
                    if (mBoardSize - i < mWinningLength) {
                        break;
                    }
                }
            }

            seq = 0;
            //check row
            for (int i = 0; i < mBoardSize; i++) {
                if (mBoard[i][oPos] == mPlayer) {
                    seq++;
                    if (seq == mWinningLength) {
                        return true;
                    }
                } else {
                    seq = 0;
                    if (mBoardSize - i < mWinningLength) {
                        break;
                    }
                }
            }

            seq = 0;
            //check diag
            if (xPos == oPos) {
                //we're on a diagonal
                for (int i = 0; i < mBoardSize; i++) {
                    if (mBoard[i][i] == mPlayer) {
                        seq++;
                        if (seq == mWinningLength) {
                            return true;
                        }
                    } else {
                        seq = 0;
                        if (mBoardSize - i < mWinningLength) {
                            break;
                        }
                    }
                }
            }

            seq = 0;
            //check anti diag (thanks rampion)
            if (xPos + oPos == mBoardSize - 1) {
                for (int i = 0; i < mBoardSize; i++) {
                    if (mBoard[i][(mBoardSize - 1) - i] == mPlayer) {
                        seq++;
                        if (seq == mWinningLength) {
                            return true;
                        }
                    } else {
                        seq = 0;
                        if (mBoardSize - i < mWinningLength) {
                            break;
                        }
                    }
                }
            }
        }

        return false;
    }

    public void resetToHead() {
        currentBoard = headBoard;
    }

    public boolean checkDraw() {
        if(isDraw()) {
            updateCost(-1, currentBoard);
            return true;
        }

        return false;
    }
}
