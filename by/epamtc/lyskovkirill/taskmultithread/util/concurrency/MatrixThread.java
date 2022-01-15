package by.epamtc.lyskovkirill.taskmultithread.util.concurrency;

import by.epamtc.lyskovkirill.taskmultithread.bean.Matrix;
import by.epamtc.lyskovkirill.taskmultithread.dao.MatrixDAO;
import by.epamtc.lyskovkirill.taskmultithread.dao.exception.DAOException;
import by.epamtc.lyskovkirill.taskmultithread.dao.factory.DAOFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Ini;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MatrixThread extends Thread {
    private final static String MATRIX_INI_SECTION = "Matrix";
    private final static String NUMBER_N_INI_OPTION = "iNumberN";
    private final static String NUMBER_Y_INI_OPTION = "iNumberY";

    private final static Logger logger = LogManager.getLogger();

    private static int N;
    private static int Y;

    private static int[][] matrix;
    private static int[] results;

    private static Lock locker;
    private static Semaphore semaphore;
    private static CyclicBarrier barrier;

    static {
        DAOFactory daoFactory = DAOFactory.getInstance();
        MatrixDAO matrixDAO = daoFactory.getMatrixDAO();
        try {
            Ini matrixIni = matrixDAO.readInitializationData();
            N = Integer.parseInt(matrixIni.get(MATRIX_INI_SECTION, NUMBER_N_INI_OPTION));
            Y = Integer.parseInt(matrixIni.get(MATRIX_INI_SECTION, NUMBER_Y_INI_OPTION));

            matrix = new int[N][N];
            results = new int[N];
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    matrix[i][j] = 0;
                }
                results[i] = 0;
            }

            locker = new ReentrantLock();
            semaphore = new Semaphore(N);
            barrier = new CyclicBarrier(N, () -> {
                logger.info("Идет запись матрицы в файл...");
                try {
                    matrixDAO.writeMatrix(new Matrix(matrix, results));
                    Arrays.fill(results, 0);
                } catch (DAOException e) {
                    logger.error(e.getMessage());
                }
            });

        } catch (DAOException e) {
            logger.error(e);
        }
    }

    public MatrixThread(String name) {
        super(name);
    }

    public static int getN() {
        return N;
    }

    public static int getY() {
        return Y;
    }

    @Override
    public void run() {
        try {
            semaphore.acquire();

            logger.info("Поток " + getName() + " начал работу с матрицей");
            int nameNumber = Integer.parseInt(getName());
            for (int i = 0; i < N; i++) {
                synchronized ((Object) matrix[i][i]) {
                    matrix[i][i] = nameNumber;
                }
                int r = new Random().nextInt(N);
                synchronized ((Object) matrix[i][r]) {
                    matrix[i][r] = nameNumber;
                }
            }
            logger.info("Поток " + getName() + " закончил работу с матрицей");

            logger.info("Поток " + getName() + " делает вычисления");
            int sum = 0;
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if (i == nameNumber % N || j == nameNumber % N)
                        sum += matrix[i][j];
                }
            }
            locker.lock();
            results[nameNumber % N] = sum;
            locker.unlock();

            logger.info("Поток " + getName() + " закончил, его индекс = " + nameNumber % N);
            barrier.await(10, TimeUnit.SECONDS);

        } catch (InterruptedException | BrokenBarrierException | TimeoutException e) {
            barrier.reset();
            logger.error(e);
        } finally {
            semaphore.release();
        }
    }
}
