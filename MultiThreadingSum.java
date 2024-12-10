import java.util.ArrayList;
import java.util.List;

public class MultiThreadingSum {
    public static void main(String[] args) {
        int[] arr = new int[100];
        for (int i = 0; i < arr.length; ++i) {
            arr[i] = i + 1;
        }

        int numThreads = 4;
        int part = arr.length / numThreads;


        List<ArraySumTask> threads = new ArrayList<>();


        for (int i = 0; i < numThreads; i++) {
            int start = i * part;
            int end = (i == numThreads - 1) ? arr.length : start + part;

            ArraySumTask t = new ArraySumTask(arr, start, end);
            threads.add(t);
            t.start();
        }


        int Sum = 0;
        for (ArraySumTask t : threads) {
            try {
                t.join();
                Sum += t.getSum();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Сумма массива: " + Sum);
    }
}

class ArraySumTask extends Thread {
    private final int[] link;
    private final int start;
    private final int end;
    private int sum;

    public ArraySumTask(int[] arr, int start, int end) {
        this.link = arr;
        this.start = start;
        this.end = end;
    }

    @Override
    public void run() {
        sum = 0;
        for (int i = start; i < end; i++) {
            sum += link[i];
        }
    }

    public int getSum() {
        return sum;
    }
}

