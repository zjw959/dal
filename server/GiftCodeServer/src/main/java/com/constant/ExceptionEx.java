package com.constant;

public class ExceptionEx {
    // 错误堆栈的内容
    public static String e2s(Exception e) {
        StringBuilder sb = new StringBuilder();
        sb.append(e);
        Throwable throwable = e.getCause();
        if (throwable != null) {
            String message = throwable.getMessage();
            if (message != null) {
                sb.append(" : ");
                sb.append(message);
            }
        }
        sb.append("\n");
        for (StackTraceElement ste : e.getStackTrace()) {
            sb.append("at ");
            sb.append(ste);
            sb.append("\n");
        }
        sb.append("threadName:" + Thread.currentThread().getName());
        sb.append("\n");
        return sb.toString();
    }

    // 错误堆栈的内容
    public static String t2s(Throwable t) {
        StringBuilder sb = new StringBuilder();
        sb.append(t);
        sb.append("\n");
        for (StackTraceElement ste : t.getStackTrace()) {
            sb.append("at ");
            sb.append(ste);
            sb.append("\n");
        }
        return sb.toString();
    }


    /**
     * 打印当前线程堆栈信息
     *
     * @return
     */
    public static String currentThreadTracesWithOutMineLine() {
        return currentThreadTraces(2, false);
    }

    /**
     * 打印当前线程堆栈信息
     *
     * @return
     */
    public static String currentThreadTraces(int len) {
        return currentThreadTraces(len, false);
    }

    /**
     * 打印当前线程堆栈信息
     *
     * @return
     */
    public static String currentThreadTracesSingleOne(int len) {
        return currentThreadTraces(len, true);
    }

    /**
     * 打印当前线程堆栈信息
     *
     * @return
     */
    public static String currentThreadTraces() {
        return currentThreadTraces(1);
    }

    /**
     * 打印当前线程堆栈信息
     * 
     * @param len
     * @param isOne
     * @return
     */
    public static String currentThreadTraces(int len, boolean isOne) {
        Thread thread = Thread.currentThread();

        StringBuilder sb = new StringBuilder();

        sb.append("thread id: " + thread.getId() + ", name: " + thread.getName() + ", "
                + thread.getState()).append("\n");
        StackTraceElement[] stackTraces = thread.getStackTrace();
        int totalLen = stackTraces.length;
        int beginLen = 2 + len;
        if (totalLen < 3 + len) {
            return "";
        }

        if (isOne) {
            totalLen = beginLen + 1;
        }

        for (int i = beginLen; i < totalLen; i++) {
            sb.append("at ");
            sb.append(stackTraces[i]);
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * 打印当前线程堆栈信息
     * 
     * @param len
     * @param isOne
     * @return
     */
    public static String currentThreadTraces(StackTraceElement[] stackTraces, 
            boolean isOne) {
        StringBuilder sb = new StringBuilder();
        int totalLen = stackTraces.length;
        int len = 0;
        int beginLen = 1 + len;
        if (totalLen < 3 + len) {
            return "";
        }

        if (isOne) {
            totalLen = beginLen + 1;
        }

        for (int i = beginLen; i < totalLen; i++) {
            sb.append("at ");
            sb.append(stackTraces[i]);
            sb.append("\n");
        }
        return sb.toString();
    }
}
