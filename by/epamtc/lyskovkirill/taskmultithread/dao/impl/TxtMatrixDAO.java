package by.epamtc.lyskovkirill.taskmultithread.dao.impl;

import by.epamtc.lyskovkirill.taskmultithread.bean.Matrix;
import by.epamtc.lyskovkirill.taskmultithread.dao.MatrixDAO;
import by.epamtc.lyskovkirill.taskmultithread.dao.exception.DAOException;
import org.ini4j.Ini;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TxtMatrixDAO implements MatrixDAO {
    private static final String MATRIX_INI_PATH = "resources/matrix.ini";
    private static final String MATRIX_FILE_PATH = "resources/matrix.txt";

    @Override
    public Ini readInitializationData() throws DAOException {
        Ini ini;
        File file = new File(MATRIX_INI_PATH);
        if (!file.exists()) {
            throw new DAOException("Error: Source file does not exist");
        }
        try {
            ini = new Ini(file);
        } catch (IOException e) {
            throw new DAOException("Error during reading initializing data from file", e);
        }
        return ini;
    }

    @Override
    public boolean writeMatrix(Matrix matrix) throws DAOException {
        boolean isWritten;
        File file = new File(MATRIX_FILE_PATH);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            if (!(isWritten = file.exists())) {
                isWritten = file.createNewFile();
            }
            int[][] m = matrix.getMatrix();
            for (int[] ints : m) {
                for (int j = 0; j < m.length; j++) {
                    writer.append(String.valueOf(ints[j])).append('\t');
                }
                writer.append('\n');
            }
            writer.append("\n");
            int[] results = matrix.getResults();
            writer.append("results -> ");
            for (int i = 0; i < results.length; i++) {
                writer.append(String.valueOf(results[i]));
                if (i < results.length - 1)
                    writer.append(", ");
            }
            writer.append("\n\n\n");
        } catch (IOException e) {
            throw new DAOException("Error during writing matrix to file", e);
        }
        return isWritten;
    }
}
