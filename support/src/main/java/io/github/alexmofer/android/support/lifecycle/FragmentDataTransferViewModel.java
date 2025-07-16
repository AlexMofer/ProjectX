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
 * Fragment 数据传递 ViewModel
 * 借助 Activity 传递 Fragment 的不可序列化的数据，例如：回调
 * Created by Alex on 2025/7/15.
 */
public class FragmentDataTransferViewModel extends ViewModel {

    private static final String KEY_ID = "io.github.alexmofer.android.support.lifecycle.key.ID";
    private final ArrayMap<String, Object> mDataMap = new ArrayMap<>();

    /**
     * 存入数据
     *
     * @param args     Fragment 参数
     * @param activity FragmentActivity
     * @param data     数据
     */
    public static void putData(Bundle args, FragmentActivity activity, Object data) {
        final FragmentDataTransferViewModel viewModel =
                new ViewModelProvider(activity).get(FragmentDataTransferViewModel.class);
        final String id = UUID.randomUUID().toString();
        viewModel.mDataMap.put(id, data);
        args.putString(KEY_ID, id);
    }

    /**
     * 获取数据
     *
     * @param fragment Fragment
     * @return 数据，注意类型转换问题
     */
    @Nullable
    public static <T> T getData(Fragment fragment) {
        final Bundle args = fragment.getArguments();
        if (args == null || !args.containsKey(KEY_ID)) {
            return null;
        }
        final FragmentDataTransferViewModel viewModel =
                new ViewModelProvider(fragment.requireActivity())
                        .get(FragmentDataTransferViewModel.class);
        //noinspection unchecked
        return (T) viewModel.mDataMap.get(args.getString(KEY_ID));
    }

    /**
     * 移除数据
     *
     * @param fragment Fragment
     */
    public static void removeData(Fragment fragment) {
        final Bundle args = fragment.getArguments();
        if (args == null || !args.containsKey(KEY_ID)) {
            return;
        }
        final FragmentDataTransferViewModel viewModel =
                new ViewModelProvider(fragment.requireActivity())
                        .get(FragmentDataTransferViewModel.class);
        viewModel.mDataMap.remove(args.getString(KEY_ID));
    }
}
