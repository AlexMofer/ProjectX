package io.github.alexmofer.projectx.business.developing;

import androidx.annotation.Nullable;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import io.github.alexmofer.android.support.concurrent.UIThreadExecutor;

/**
 * ListenableFuture 工具
 * Created by Alex on 2025/5/9.
 */
public class ListenableFutureUtils {

    private static ListeningExecutorService sService;
    private static UIThreadExecutor sUIThreadExecutor;

    private ListenableFutureUtils() {
        //no instance
    }

    public static ListeningExecutorService getListeningExecutorService() {
        if (sService == null) {
            sService = MoreExecutors.listeningDecorator(ThreadPoolExecutorManager.getJobThreadPool());
        }
        return sService;
    }

    public static Executor getUIThreadExecutor() {
        if (sUIThreadExecutor == null) {
            sUIThreadExecutor = new UIThreadExecutor();
        }
        return sUIThreadExecutor;
    }

    /**
     * 提交异步任务
     *
     * @param task    任务执行回调
     * @param success 任务成功回调
     * @param failure 任务失败回调
     * @param <T>     返回类型
     * @return 异步任务
     * @noinspection UnusedReturnValue
     */
    public static <T> ListenableFuture<T> submit(Callable<T> task, Consumer<T> success,
                                                 @Nullable Consumer<Throwable> failure) {
        final ListeningExecutorService service = getListeningExecutorService();
        final ListenableFuture<T> future = service.submit(task);
        Futures.addCallback(
                future,
                new FutureCallback<T>() {

                    public void onSuccess(T result) {
                        success.accept(result);
                    }

                    public void onFailure(@androidx.annotation.NonNull Throwable t) {
                        if (failure != null) {
                            failure.accept(t);
                        }
                    }
                },
                getUIThreadExecutor());
        return future;
    }
}
