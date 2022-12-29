package student_player;

import boardgame.Move;
import pentago_twist.PentagoMove;
import pentago_twist.PentagoPlayer;
import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoBoardState.Piece;

import java.io.NotActiveException;
import java.lang.reflect.Array;
import java.util.*;

public class StudentPlayer extends PentagoPlayer {
    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    private int MAX_DEPTH = 3;
    private Piece COLOR;
    private Piece OPP_COLOR;
    private PentagoMove bestMove;
    private double bestValue;
    public StudentPlayer() {
        super("260771993");
    }
    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(PentagoBoardState boardState) {
        COLOR = checkColor(boardState);
        if (COLOR == Piece.BLACK) {
            OPP_COLOR = Piece.WHITE;
        } else
            OPP_COLOR = Piece.BLACK;
        bestValue = Integer.MIN_VALUE+10;

        long stopTime = System.currentTimeMillis() + 1500;
        abPrune(boardState, Integer.MIN_VALUE+100, Integer.MAX_VALUE-100, MAX_DEPTH, 1, stopTime);
        System.out.println("Returning node with " + bestValue + " score");
        return bestMove;
    }

    //Pieces taken from: https://en.wikipedia.org/wiki/Alpha%E2%80%93beta_pruning
    public double abPrune(PentagoBoardState boardState, double a, double b, int depth, int color, long stopTime) {
        if (depth == 0 || boardState.gameOver() || System.currentTimeMillis() >= stopTime) {
            return score(boardState);
        }
        ArrayList<PentagoMove> moves = getMoves(boardState, boardState.getTurnNumber() < 2);
        Collections.shuffle(moves);
        if(color == 1) {
            double value= Integer.MIN_VALUE+100;
            for (PentagoMove move : moves) {
                PentagoBoardState clone = (PentagoBoardState) boardState.clone();
                clone.processMove(move);
                value = Math.max(value, abPrune(clone, a, b, depth - 1, -1, stopTime));
                if(depth == MAX_DEPTH) {System.out.println(value);}
                if (value > bestValue && depth == MAX_DEPTH) {
                    bestValue = value;
                    bestMove = moves.get(moves.indexOf(move));
                }
                a = Math.max(a, value);
                if (a >= b) {
                    break;
                }
            }
            return value;
        }
        else {
            double value= Integer.MAX_VALUE-100;
            for (PentagoMove move : moves) {
                PentagoBoardState clone = (PentagoBoardState) boardState.clone();
                clone.processMove(move);
                value = Math.min(value, abPrune(clone, a, b, depth - 1, 1, stopTime));
                b = Math.min(b, value);
                if (b <= a) {
                    break;
                }
            }
            return value;
        }
    }


    public double score(PentagoBoardState boardState){
        double score = 0;
        Piece[][] board = boardState.getBoard();
        //horizontal score
        for (int i = 0; i < board.length; i++) {
            boolean maxDead = false;
            boolean minDead = false;
            if (dead(board[i], OPP_COLOR)) {
                maxDead = true;
            }
            if (dead(board[i], COLOR)) {
                minDead = true;
            }
            int yourCount = 0;
            int oppCount = 0;
            int midCount = 0;
            for (int j = 0; j < board[i].length; j++) {
                if ((i == 1 && j == 1 || i == 1 && j == 4 || i == 4 && j == 1 || i == 4 && j == 4) && board[i][j] == COLOR) {
                    midCount++;
                }
            }
            yourCount = countInARow(board[i], COLOR, maxDead);
            oppCount = countInARow(board[i], OPP_COLOR, minDead);
            if (oppCount > 2) {
                score = score - Math.pow(oppCount, 7);
            } else if (yourCount > 2) {
                score = score + Math.pow(yourCount, 7);
            }
            score = score + 0.1 * midCount;
            if (oppCount == 5 || oppCount == 6) {
                score = score - 1000000000;
            }
            if (yourCount == 5 || yourCount == 6) {
                score = score + 1000000000;
            }
        }
        //vertical score
        for (int i = 0; i < board.length; i++) {
            boolean maxDead = false;
            boolean minDead = false;
            Piece[] column = new Piece[6];
            for (int t = 0; t < board.length; t++) {
                column[t] = board[t][i];
            }
            if (dead(column, OPP_COLOR)) {
                maxDead = true;
            }
            if (dead(column, COLOR)) {
                minDead = true;
            }
            int yourCount = countInARow(column, COLOR, maxDead);
            int oppCount = countInARow(column, OPP_COLOR, minDead);
            if (oppCount > 2) {
                score = score - Math.pow(oppCount, 7);
            } else if (yourCount > 2) {
                score = score + Math.pow(yourCount, 7);
            }
            if (oppCount == 5 || oppCount == 6) {
                score = score - 1000000000;
            }
            if (yourCount == 5 || yourCount == 6) {
                score = score + 1000000000;
            }
        }
        //diagonal score
        Piece[][] diagonals = pullDiagonals(boardState);
        for (int i = 0; i < diagonals.length; i++) {
            boolean maxDead = false;
            boolean minDead = false;
            if (dead(diagonals[i], OPP_COLOR)) {
                maxDead = true;
            }
            if (dead(diagonals[i], COLOR)) {
                minDead = true;
            }
            int yourCount = countInARow(diagonals[i], COLOR, maxDead);
            int oppCount = countInARow(diagonals[i], OPP_COLOR, minDead);

            if (oppCount > 2) {
                score = score - Math.pow(oppCount, 7);
            } else if (yourCount > 2) {
                score = score + Math.pow(yourCount, 7);
            }
            if (oppCount == 5 || oppCount == 6) {
                score = score - 1000000000;
            }
            if (yourCount == 5 || yourCount == 6) {
                score = score + 1000000000;
            }
        }
        return score;
    }
    public boolean dead(Piece[] array, Piece enemyColor) {
        boolean dead = false;
        for(int i = 0; i < array.length; i++) {
            if(array[i] == enemyColor && !(i == 0 || i == 5)) {
                dead = true;
            }
            if(array[i] == enemyColor && array.length < 6) {
                dead = true;
            }
        }
        return dead;
    }
    public Piece[][] pullDiagonals(PentagoBoardState boardState) {
        Piece[][] board = boardState.getBoard();
        //diagonal1
        Piece[] diagonal1 = new Piece[5];
        for(int i = 0; i < 5; i++) {
          diagonal1[i] = board[i+1][i];
        }
        //diagonal2
        Piece[] diagonal2 = new Piece[6];
        for(int i = 0; i < 6; i++) {
            diagonal2[i] = board[i][i];
        }
        //diagonal3
        Piece[] diagonal3 = new Piece[5];
        for(int i = 0; i < 5; i++) {
            diagonal3[i] = board[i][i+1];
        }
        //diagonal4
        Piece[] diagonal4 = new Piece[5];
        for(int i = 0; i < 5; i++) {
            diagonal4[i] = board[4-i][i];
        }
        //diagonal5
        Piece[] diagonal5 = new Piece[6];
        for(int i = 0; i < 6; i++) {
            diagonal5[i] = board[5-i][i];
        }
        //diagonal6
        Piece[] diagonal6 = new Piece[5];
        for(int i = 0; i < 5; i++) {
           diagonal6[i] = board[5-i][i+1];
        }

        Piece[][] diagonals = {diagonal1, diagonal2, diagonal3, diagonal4, diagonal5, diagonal6};
        return diagonals;
    }
    public static int countInARow(Piece[] array, Piece Color, boolean isDead) {
        if(isDead) {return 0;}
        int best=0;
        int count=0;
        for(int i = 0; i < array.length; i++) {
            if(array[i]==Color) {
                count++;
                if(count > best) {
                    best = count;
                }
            }
            else {
                count = 0;
            }
        }
        return best;
    }
    public ArrayList<PentagoMove> getMoves(PentagoBoardState boardState, boolean prune) {
        ArrayList<PentagoMove> moves = boardState.getAllLegalMoves();
        if(prune == false) {
            return moves;
        }
        else{
            ArrayList<PentagoMove> prunedMoves = new ArrayList<>();
            for(int i = 0; i < moves.size(); i++) {
                if(prune && (moves.get(i).getASwap() != 0 || moves.get(i).getBSwap() != 0)) {
                    continue;
                }
                prunedMoves.add(moves.get(i));
            }
            return prunedMoves;
        }
    }
    public Piece checkColor(PentagoBoardState state) {
        Piece[][] board = state.getBoard();
        int numWhite = 0;
        int numBlack = 0;
        for(int i = 0; i < board.length; i ++) {
            for(int j = 0; j < board[0].length; j++) {
                if(board[i][j] == Piece.WHITE) {numWhite++;}
                if(board[i][j] == Piece.BLACK) {numBlack++;}
            }
        }
        if(numWhite > numBlack) {return Piece.BLACK;}
        else {return Piece.WHITE;}
    }
}