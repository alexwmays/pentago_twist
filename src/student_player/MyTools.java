package student_player;

import pentago_twist.PentagoBoardState.Piece;

import java.util.List;
import java.util.Random;

 public class MyTools {
    //https://github.com/WenboDu1228/Pentago-Swap-Game/blob/main/Zobrist.java
        public static long random() {
            Random r = new Random();
            return r.nextLong();
        }
        static long[][][] zobrist = new long[6][6][2];
        static {
            for(int i = 0; i < 6; i ++) {
                for(int j = 0; j < 6; j++) {
                    //WHITE
                    zobrist[i][j][0]=random();
                    //BLACK
                    zobrist[i][j][1]=random();
                }
            }
        }

        public static long getHash(Piece[][] board) {
            long key = 0;
            for(int i=0; i<6; i++) {
                for(int j =0; j<6; j++) {
                    if(board[i][j] == Piece.BLACK) {
                        key^=zobrist[i][j][1];
                    }
                    else if(board[i][j] == Piece.WHITE) {
                        key^=zobrist[i][j][0];
                    }
                }
            }
            return key;
        }
}