package by.epamtc.lyskovkirill.taskmultithread.util.concurrency;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class MatrixThread extends Thread {
    private final int[][] matrix;
    private final int[] results;

    ReentrantLock locker;
    Semaphore semaphore;
    CyclicBarrier barrier;

    public MatrixThread(String name, int[][] matrix, int[] results, ReentrantLock locker, Semaphore semaphore, CyclicBarrier barrier) {
        super(name);
        this.matrix = matrix;
        this.results = results;

        this.locker = locker;
        this.semaphore = semaphore;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        Logger logger = LogManager.getLogger();
        try {
            semaphore.acquire();

            logger.info("Поток " + getName() + " начал работу с матрицей");
            int nameNumber = Integer.parseInt(getName());
            for (int i = 0; i < matrix.length; i++) {
                synchronized ((Object) matrix[i][i]) {
                    matrix[i][i] = nameNumber;
                }
                int r = new Random().nextInt(matrix.length);
                synchronized ((Object) matrix[i][r]) {
                    matrix[i][r] = nameNumber;
                }
            }
            logger.info("Поток " + getName() + " закончил работу с матрицей");

            logger.info("Поток " + getName() + " делает вычисления");
            int sum = 0;
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix.length; j++) {
                    if (i == nameNumber % matrix.length || j == nameNumber % matrix.length)
                        sum += matrix[i][j];
                }
            }
            locker.lock();
            results[nameNumber % matrix.length] = sum;
            locker.unlock();

            logger.info("Поток " + getName() + " закончил, его индекс = " + nameNumber % matrix.length);
            barrier.await(10, TimeUnit.SECONDS);

        } catch (InterruptedException | BrokenBarrierException | TimeoutException e) {
            barrier.reset();
            logger.error(e);
        } finally {
            semaphore.release();
        }
    }
}
