package am.project.x;

/**
 * 异常捕获器
 * Created by Alex on 2016/5/11.
 */
public class ProjectXExceptionHandler implements Thread.UncaughtExceptionHandler {
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    public ProjectXExceptionHandler() {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        handleException(thread, ex);
        mDefaultHandler.uncaughtException(thread, ex);
    }

    /**
     * 处理异常信息
     *
     * @param thread 错误线程
     * @param ex     错误
     */
    @SuppressWarnings("unused")
    private void handleException(Thread thread, Throwable ex) {
        // 上报日志
        if (ex != null) {
            String message = ex.getMessage();
        }
    }
}
