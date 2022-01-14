package by.epamtc.lyskovkirill.taskmultithread.bean;

public final class Matrix {
    private final int[][] matrix;
    private final int[] results;

    public Matrix(int[][] matrix, int[] calculationsResults) {
        this.matrix = matrix;
        this.results = calculationsResults;
    }

    public int[][] getMatrix() {
        return matrix;
    }

    public int[] getResults() {
        return results;
    }
}
