package main.java.net.chess.ai;

import main.java.net.chess.engine.board.Board;
import main.java.net.chess.engine.board.Move;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Minimax implements AI, Evaluator{

    private final int MAX_DEPTH;

    public Minimax (final int depth) {
        this.MAX_DEPTH = depth;
    }

    @Override
    public Move execute(Board board) {
        return null;
    }

    // minimax algo with alpha beta pruning -> currently copied, need to program myself
    public int minimax(final Board board
            , final int depth
            , int alpha
            , int beta
            , final boolean maximizingPlayer) {
        return 0;
    }

    @Override
    public String toString() {
        return "MiniMax";
    }

    // grr
    public int parallelMinimax(Board board, int numThreads) {
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

    @Override
    public int evaluate(Board board, int depth) {
        return 0;
    }
}


