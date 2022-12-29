package student_player;

import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoBoardState.Piece;

import static pentago_twist.PentagoBoardState.Piece.*;

public class test {
    public static Piece OPP_COLOR = WHITE;
    public static Piece COLOR = BLACK;

    public static void main(String[] args) {
        Piece[][] board = {{WHITE, BLACK, EMPTY, WHITE, WHITE, WHITE},
                           {WHITE, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,},
                           {BLACK, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                           {BLACK, EMPTY, BLACK, EMPTY, EMPTY, EMPTY,},
                           {BLACK, BLACK, EMPTY, EMPTY, EMPTY, EMPTY,},
                           {BLACK, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY}};
        System.out.println(score(board));

    }

    public static double score(Piece[][] board) {
        double score = 0;

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
                score = score - Math.pow(oppCount, 5);
            } else if (yourCount > 2) {
                score = score + Math.pow(yourCount, 5);
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
                score = score - Math.pow(oppCount, 5);
            } else if (yourCount > 2) {
                score = score + Math.pow(yourCount, 5);
            }
            if (oppCount == 5 || oppCount == 6) {
                score = score - 1000000000;
            }
            if (yourCount == 5 || yourCount == 6) {
                score = score + 1000000000;
            }
        }
        //diagonal score
        Piece[][] diagonals = pullDiagonals(board);
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
                score = score - Math.pow(oppCount, 5);
            } else if (yourCount > 2) {
                score = score + Math.pow(yourCount, 5);
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
    public static boolean dead(Piece[] array, Piece enemyColor) {
        boolean dead = false;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == enemyColor && !(i == 0 || i == 5)) {
                dead = true;
            }
            if (array[i] == enemyColor && array.length < 6) {
                dead = true;
            }
        }
        return dead;
    }

    public static Piece[][] pullDiagonals(Piece[][] board) {
        //diagonal1
        Piece[] diagonal1 = new Piece[5];
        for (int i = 0; i < 5; i++) {
            diagonal1[i] = board[i + 1][i];
        }
        //diagonal2
        Piece[] diagonal2 = new Piece[6];
        for (int i = 0; i < 6; i++) {
            diagonal2[i] = board[i][i];
        }
        //diagonal3
        Piece[] diagonal3 = new Piece[5];
        for (int i = 0; i < 5; i++) {
            diagonal3[i] = board[i][i + 1];
        }
        //diagonal4
        Piece[] diagonal4 = new Piece[5];
        for (int i = 0; i < 5; i++) {
            diagonal4[i] = board[4 - i][i];
        }
        //diagonal5
        Piece[] diagonal5 = new Piece[6];
        for (int i = 0; i < 6; i++) {
            diagonal5[i] = board[5 - i][i];
        }
        //diagonal6
        Piece[] diagonal6 = new Piece[5];
        for (int i = 0; i < 5; i++) {
            diagonal6[i] = board[5 - i][i + 1];
        }

        Piece[][] diagonals = {diagonal1, diagonal2, diagonal3, diagonal4, diagonal5, diagonal6};
        return diagonals;
    }
}