package io.github.alexmofer.android.support.lifecycle;

import android.os.Bundle;
import android.util.ArrayMap;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.UUID;

/**
 * Fragment 回调传递 ViewModel
 * 借助 Activity 传递 Fragment 的回调
 * Created by Alex on 2025/7/15.
 */
public class FragmentCallbackTransferViewModel extends ViewModel {

    private static final String KEY_ID = "fragment_callback_id";
    private final ArrayMap<String, Object> mCallbacks = new ArrayMap<>();

    /**
     * 存入回调
     *
     * @param args     Fragment 参数
     * @param activity FragmentActivity
     * @param callback 回调
     */
    public static void putCallback(Bundle args, FragmentActivity activity, Object callback) {
        final FragmentCallbackTransferViewModel viewModel =
                new ViewModelProvider(activity).get(FragmentCallbackTransferViewModel.class);
        final String id = UUID.randomUUID().toString();
        viewModel.mCallbacks.put(id, callback);
        args.putString(KEY_ID, id);
    }

    /**
     * 获取回调
     *
     * @param fragment Fragment
     * @return 回调，注意类型转换问题
     */
    @Nullable
    public static <T> T getCallback(Fragment fragment) {
        final Bundle args = fragment.getArguments();
        if (args == null || !args.containsKey(KEY_ID)) {
            return null;
        }
        final FragmentCallbackTransferViewModel viewModel =
                new ViewModelProvider(fragment.requireActivity())
                        .get(FragmentCallbackTransferViewModel.class);
        //noinspection unchecked
        return (T) viewModel.mCallbacks.get(args.getString(KEY_ID));
    }

    /**
     * 移除回调
     *
     * @param fragment Fragment
     */
    public static void removeCallback(Fragment fragment) {
        final Bundle args = fragment.getArguments();
        if (args == null || !args.containsKey(KEY_ID)) {
            return;
        }
        final FragmentCallbackTransferViewModel viewModel =
                new ViewModelProvider(fragment.requireActivity())
                        .get(FragmentCallbackTransferViewModel.class);
        viewModel.mCallbacks.remove(args.getString(KEY_ID));
    }
}
