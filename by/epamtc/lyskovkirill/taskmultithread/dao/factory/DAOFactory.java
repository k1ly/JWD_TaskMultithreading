package by.epamtc.lyskovkirill.taskmultithread.dao.factory;

import by.epamtc.lyskovkirill.taskmultithread.dao.MatrixDAO;
import by.epamtc.lyskovkirill.taskmultithread.dao.impl.TxtMatrixDAO;

public class DAOFactory {
    private static DAOFactory instance;


    private MatrixDAO matrixDAO = new TxtMatrixDAO();

    public static synchronized DAOFactory getInstance() {
        if (instance == null)
            instance = new DAOFactory();
        return instance;
    }

    public MatrixDAO getMatrixDAO() {
        return matrixDAO;
    }

    public void setMatrixDAO(MatrixDAO matrixDAO) {
        this.matrixDAO = matrixDAO;
    }
}
