package com.prasilabs.machinelearningtictactoe.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.Toast;

import com.prasilabs.machinelearningtictactoe.R;
import com.prasilabs.machinelearningtictactoe.logic.LearningEngine;
import com.prasilabs.machinelearningtictactoe.logic.TicTacToeBoard;
import com.prasilabs.machinelearningtictactoe.pojos.XO;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int WINNING_LENGTH = 3;
    private static final int BOARD_SIZE = 3;

    private FontAweSomeTextView[][] gridPage = new FontAweSomeTextView[BOARD_SIZE][BOARD_SIZE];

    private ProgressDialog progressBar;
    private LearningEngine learningEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        GridLayout gridLayout = (GridLayout) findViewById(R.id.grid_layout);
        gridLayout.setRowCount(BOARD_SIZE);
        gridLayout.setColumnCount(BOARD_SIZE);
        gridLayout.setBackgroundColor(ActivityCompat.getColor(this, android.R.color.white));

        learningEngine  = new LearningEngine(BOARD_SIZE, WINNING_LENGTH);

        for(int i=0;i<BOARD_SIZE; i++) {
            for(int j=0;j<BOARD_SIZE;j++) {

                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view =  inflater.inflate(R.layout.item_board, gridLayout, false);

                FontAweSomeTextView fontAweSomeTextView = (FontAweSomeTextView) view.findViewById(R.id.text_view);
                TextData textData = new TextData(i,j, null);
                fontAweSomeTextView.setTag(textData);
                fontAweSomeTextView.setOnClickListener(this);

                gridLayout.addView(view);
                gridPage[i][j] = fontAweSomeTextView;
            }
        }

        progressBar = new ProgressDialog(this);
        progressBar.setCanceledOnTouchOutside(false);
        resetData();
    }

    void resetData() {
        for(int i = 0; i< BOARD_SIZE; i++) {
            for(int j = 0; j< BOARD_SIZE; j++) {
                FontAweSomeTextView fontAweSomeTextView = gridPage[i][j];
                fontAweSomeTextView.setText("");
                TextData textData1 = new TextData(i, j, null);
                fontAweSomeTextView.setTag(textData1);
            }
        }
        learningEngine.resetToHead();
    }

    @Override
    public void onClick(View view) {
        if(view instanceof FontAweSomeTextView) {
            TextData textData = (TextData) view.getTag();
            if(textData.xo == null) {
                ((FontAweSomeTextView) view).setText(R.string.fa_circle_o);
                ((FontAweSomeTextView) view).setTextColor(ActivityCompat.getColor(this,R.color.green));
                textData.xo = XO.O;
                view.setTag(textData);
                final int xPos = textData.x;
                final int oPos = textData.o;

                progressBar.show();
                new AsyncTask<Void,Void,Void>() {

                    Integer nextXPos = null;
                    Integer nextOPos = null;
                    XO winner = null;

                    @Override
                    protected Void doInBackground(Void... voids) {

                        learningEngine.playUser(xPos, oPos);
                        if(learningEngine.checkWon()) {
                            winner = XO.O;
                        } else {
                            if(learningEngine.checkDraw()) {
                                winner = null;
                            } else {
                                learningEngine.playSystem();
                                TicTacToeBoard board = learningEngine.getCurrentBoard();
                                if(board != null) {
                                    nextXPos = board.getmCurrentXPos();
                                    nextOPos = board.getmCurrentOPos();

                                    if(learningEngine.checkWon()) {
                                        winner = XO.X;
                                    }
                                }
                            }
                        }


                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);

                        progressBar.dismiss();
                        if(nextXPos != null && nextOPos != null) {
                            FontAweSomeTextView fontAweSomeTextView = gridPage[nextXPos][nextOPos];
                            fontAweSomeTextView.setText(R.string.fa_times);
                            fontAweSomeTextView.setTextColor(ActivityCompat.getColor(GameActivity.this, R.color.red));
                            TextData textData1 = new TextData(nextXPos, nextOPos, XO.X);
                            fontAweSomeTextView.setTag(textData1);

                            if(winner != null) {
                                showWinner(winner);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        resetData();
                                    }
                                },2000);
                            }

                        } else {
                            showWinner(winner);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    resetData();
                                }
                            },2000);
                        }
                    }
                }.execute();
            }
        }
    }

    void showWinner(XO winner) {
        if(winner != null) {
            Toast.makeText(this, winner.toString() + " wins", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Draw ", Toast.LENGTH_LONG).show();
        }
    }

    private class TextData {
        int x;
        int o;
        XO xo;

        public TextData(int x, int o, XO xo) {
            this.x = x;
            this.o = o;
            this.xo = xo;
        }
    }
}
