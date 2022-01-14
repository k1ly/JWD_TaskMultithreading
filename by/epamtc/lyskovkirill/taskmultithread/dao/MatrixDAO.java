package by.epamtc.lyskovkirill.taskmultithread.dao;

import by.epamtc.lyskovkirill.taskmultithread.bean.Matrix;
import by.epamtc.lyskovkirill.taskmultithread.dao.exception.DAOException;
import org.ini4j.Ini;

public interface MatrixDAO {

    Ini readInitializationData() throws DAOException;
    boolean writeMatrix(Matrix matrix) throws DAOException;
}
