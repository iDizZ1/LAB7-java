import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class MultiThreadingBig {
    public static void main(String[] args) {
        int[][] matrix = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9},
                {10, 11, 12}
        };

        int numThreads = 2;
        int part = matrix.length / numThreads;


        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        List<Thread> threads = new ArrayList<>();
        List<MatrixMaxTask> t = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            int start = i * part;
            int end = (i == numThreads - 1) ? matrix.length : start + part;

            MatrixMaxTask task = new MatrixMaxTask(matrix, start, end);
            t.add(task);
            threads.add(task);
        }


        for (Thread thread : threads) {
            thread.start();
        }

        int globalMax = Integer.MIN_VALUE;
        for (MatrixMaxTask task : t) {
            try {
                task.join();
                globalMax = Math.max(globalMax, task.getMax());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        executorService.shutdown();

        System.out.println("Максимальный элемент в матрице: " + globalMax);
    }
}

class MatrixMaxTask extends Thread {
    private final int[][] matrix;
    private final int start;
    private final int end;
    private int Max = Integer.MIN_VALUE;

    public MatrixMaxTask(int[][] matrix, int start, int end) {
        this.matrix = matrix;
        this.start = start;
        this.end = end;
    }

    @Override
    public void run() {
        for (int i = start; i < end; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                Max = Math.max(Max, matrix[i][j]);
            }
        }
    }

    public int getMax() {
        return Max;
    }
}
