package by.epamtc.lyskovkirill.taskmultithread.runner;

import by.epamtc.lyskovkirill.taskmultithread.bean.Matrix;
import by.epamtc.lyskovkirill.taskmultithread.dao.MatrixDAO;
import by.epamtc.lyskovkirill.taskmultithread.dao.exception.DAOException;
import by.epamtc.lyskovkirill.taskmultithread.dao.factory.DAOFactory;
import by.epamtc.lyskovkirill.taskmultithread.util.concurrency.MatrixThread;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Ini;

import java.util.Arrays;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    private final static String MATRIX_INI_SECTION = "Matrix";
    private final static String NUMBER_N_INI_OPTION = "iNumberN";
    private final static String NUMBER_Y_INI_OPTION = "iNumberY";

    public static void main(String[] args) {
        Logger logger = LogManager.getLogger();
        DAOFactory daoFactory = DAOFactory.getInstance();
        MatrixDAO matrixDAO = daoFactory.getMatrixDAO();
        try {
            Ini matrixIni = matrixDAO.readInitializationData();
            int N = Integer.parseInt(matrixIni.get(MATRIX_INI_SECTION, NUMBER_N_INI_OPTION));
            int Y = Integer.parseInt(matrixIni.get(MATRIX_INI_SECTION, NUMBER_Y_INI_OPTION));

            int[][] matrix = new int[N][N];
            int[] results = new int[N];
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    matrix[i][j] = 0;
                }
                results[i] = 0;
            }

            ReentrantLock locker = new ReentrantLock();
            Semaphore semaphore = new Semaphore(matrix.length);
            CyclicBarrier barrier = new CyclicBarrier(matrix.length, () -> {
                logger.info("Идет запись матрицы в файл...");
                try {
                    matrixDAO.writeMatrix(new Matrix(matrix, results));
                    Arrays.fill(results, 0);
                } catch (DAOException e) {
                    logger.error(e.getMessage());
                }
            });

            for (int i = 0; i < N * Y; i++) {
                new MatrixThread(String.valueOf(i + 1), matrix, results, locker, semaphore, barrier).start();
            }
        } catch (DAOException e) {
            logger.error(e);
        }
    }
}
