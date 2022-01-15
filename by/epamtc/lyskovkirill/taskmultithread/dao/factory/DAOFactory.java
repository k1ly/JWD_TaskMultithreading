package by.epamtc.lyskovkirill.taskmultithread.dao.factory;

import by.epamtc.lyskovkirill.taskmultithread.dao.MatrixDAO;
import by.epamtc.lyskovkirill.taskmultithread.dao.impl.TxtMatrixDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class DAOFactory {
    private static final Logger logger = LogManager.getLogger();
    private static final ReentrantLock lock = new ReentrantLock();

    private static DAOFactory instance;

    private MatrixDAO matrixDAO = new TxtMatrixDAO();

    public static DAOFactory getInstance() {
        if (instance == null) {
            try {
                if (lock.tryLock(10, TimeUnit.SECONDS)) {
                    if (instance == null)
                        instance = new DAOFactory();
                    else {
                        logger.warn("DAOFactory instance is been already initializing by another thread");
                    }
                } else {
                    logger.error("Timeout exceeded");
                    throw new RuntimeException("Timeout exceeded");
                }
            } catch (InterruptedException exception) {
                logger.error(exception);
                throw new RuntimeException(exception.getMessage(), exception);
            } finally {
                if (lock.isHeldByCurrentThread())
                    lock.unlock();
            }
        }
        return instance;
    }

    public MatrixDAO getMatrixDAO() {
        return matrixDAO;
    }

    public synchronized void setMatrixDAO(MatrixDAO matrixDAO) {
        this.matrixDAO = matrixDAO;
    }
}
