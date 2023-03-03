package main.java.net.chess.ai;

import main.java.net.chess.engine.board.Board;
import main.java.net.chess.engine.board.Move;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Minimax implements AI{
    @Override
    public Move execute(Board board, int depth) {
        return null;
    }

    public class MiniMax {
        private static final int MAX_DEPTH = 3; // maximum depth of game tree to explore


        // minimax algo with alpha beta pruning
        public static int minimax(final Board board
                , final int depth
                , int alpha
                , int beta
                , final boolean maximizingPlayer) {

            if (depth == MAX_DEPTH || board.isGameOver()) {
                //return board.evaluate(); // evaluate board position using heuristic function
            }
            if (maximizingPlayer) {
                int maxEval = Integer.MIN_VALUE;
                for (Move move : board.getAllLegalMoves()) {
                    final Board newBoard = move.execute();
                    int eval = minimax(newBoard, depth + 1, alpha, beta, false);
                    maxEval = Math.max(maxEval, eval);
                    alpha = Math.max(alpha, eval);
                    if (beta <= alpha) {
                        break;
                    }
                }
                return maxEval;
            } else {
                int minEval = Integer.MAX_VALUE;
                for (Move move : board.getAllLegalMoves()) {
                    final Board newBoard = move.execute();
                    int eval = minimax(newBoard, depth + 1, alpha, beta, true);
                    minEval = Math.min(minEval, eval);
                    beta = Math.min(beta, eval);
                    if (beta <= alpha) {
                        break;
                    }
                }
                return minEval;
            }
        }

        public static int parallelMinimax(Board board, int numThreads) {
            int bestEval = Integer.MIN_VALUE;
            List<Thread> threads = new ArrayList<>();
            List<Move> moves = board.getAllLegalMoves().stream().toList();
            List<Integer> evaluations = new ArrayList<>(Collections.nCopies(moves.size(), 0));

            for (int i = 0; i < moves.size(); i++) {
                int finalI = i;
                Thread thread = new Thread(() -> {
                    final Board newBoard = moves.get(finalI).execute();
                    int eval = minimax(newBoard, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
                    evaluations.set(finalI, eval);
                });
                threads.add(thread);
                thread.start();
            }

            try {
                for (Thread thread : threads) {
                    thread.join();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (int eval : evaluations) {
                bestEval = Math.max(bestEval, eval);
            }
            return bestEval;
        }
    }

}
