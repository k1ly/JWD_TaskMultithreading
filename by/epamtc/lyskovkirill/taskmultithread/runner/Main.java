package by.epamtc.lyskovkirill.taskmultithread.runner;

import by.epamtc.lyskovkirill.taskmultithread.util.concurrency.MatrixThread;

public class Main {

    public static void main(String[] args) {
        int N = MatrixThread.getN();
        int Y = MatrixThread.getY();

        for (int i = 0; i < N * Y; i++) {
            new MatrixThread(String.valueOf(i + 1)).start();
        }
    }
}
