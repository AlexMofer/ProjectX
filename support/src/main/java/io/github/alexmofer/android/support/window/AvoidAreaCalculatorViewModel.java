/*
 * Copyright (C) 2026 AlexMofer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.alexmofer.android.support.window;

import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import java.lang.ref.WeakReference;

import io.github.alexmofer.android.support.function.FunctionRObjectPObjectObject;

/**
 * 避让区域计算器 ViewModel
 * 1. 为避免 FragmentActivity 与 DialogFragment 构建多个计算器。
 * 2. 降低代码入侵，比如：在 FragmentActivity 的任意 Fragment 中都可以随意获取唯一的实例，无需传值。
 * Created by Alex on 2026/1/14.
 */
public final class AvoidAreaCalculatorViewModel extends ViewModel {

    private final AvoidAreaCalculator mCalculator = new AvoidAreaCalculator();
    private final MutableLiveData<View> mView = new MutableLiveData<>();
    private final Observer<View> mObserver;
    private ViewController mViewController;

    public AvoidAreaCalculatorViewModel() {
        mObserver = view -> {
            if (view != null) {
                mCalculator.setView(view);
            }
        };
        mView.observeForever(mObserver);
    }

    @NonNull
    public static <T> AvoidAreaCalculatorViewModel getInstance(@NonNull ViewModelStoreOwner owner,
                                                               @NonNull T controller,
                                                               @NonNull FunctionRObjectPObjectObject<ViewController, T, MutableLiveData<View>> viewControllerConstructor) {
        final AvoidAreaCalculatorViewModel calculator =
                new ViewModelProvider(owner).get(AvoidAreaCalculatorViewModel.class);
        // 使用 ViewController 来避免多次获取实例时重复设置 OnApplyWindowInsetsListener
        if (calculator.mViewController != null
                && calculator.mViewController.isController(controller)) {
            return calculator;
        }
        calculator.mViewController =
                viewControllerConstructor.execute(controller, calculator.mView);
        return calculator;
    }

    private static class FragmentActivityViewController
            extends WeakReferenceViewController<FragmentActivity> {

        public FragmentActivityViewController(@NonNull FragmentActivity activity,
                                              @NonNull MutableLiveData<View> view) {
            super(activity);
            final Lifecycle lifecycle = activity.getLifecycle();
            lifecycle.addObserver(new DefaultLifecycleObserver() {
                @Override
                public void onCreate(@NonNull LifecycleOwner owner) {
                    view.setValue(activity.getWindow().getDecorView());
                }

                @Override
                public void onDestroy(@NonNull LifecycleOwner owner) {
                    view.setValue(null);
                    owner.getLifecycle().removeObserver(this);
                }
            });
            if (lifecycle.getCurrentState().isAtLeast(Lifecycle.State.CREATED)) {
                view.setValue(activity.getWindow().getDecorView());
            }
        }
    }

    /**
     * 通过 FragmentActivity 获取实例
     *
     * @param activity FragmentActivity
     * @return 实例
     */
    @NonNull
    public static AvoidAreaCalculatorViewModel getInstance(@NonNull FragmentActivity activity) {
        return getInstance(activity, activity, FragmentActivityViewController::new);
    }

    private static class DialogFragmentViewController
            extends WeakReferenceViewController<DialogFragment> {

        public DialogFragmentViewController(@NonNull DialogFragment fragment,
                                            @NonNull MutableLiveData<View> view) {
            super(fragment);
            final Observer<LifecycleOwner> observer = owner -> {
                if (owner == null) {
                    view.setValue(null);
                } else {
                    final Window window = fragment.requireDialog().getWindow();
                    if (window != null) {
                        view.setValue(window.getDecorView());
                    }
                }
            };
            fragment.getViewLifecycleOwnerLiveData().observe(fragment, observer);
        }
    }

    /**
     * 通过 DialogFragment 获取实例
     *
     * @param fragment DialogFragment
     * @return 实例
     */
    @NonNull
    public static AvoidAreaCalculatorViewModel getInstance(@NonNull DialogFragment fragment) {
        return getInstance(fragment, fragment, DialogFragmentViewController::new);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mView.removeObserver(mObserver);
    }

    /**
     * 获取计算器
     *
     * @return 计算器
     */
    @NonNull
    public AvoidAreaCalculator getCalculator() {
        return mCalculator;
    }

    public interface ViewController {
        boolean isController(@NonNull Object controller);
    }

    public static class WeakReferenceViewController<T> implements ViewController {

        private final WeakReference<T> mReference;

        public WeakReferenceViewController(@NonNull T controller) {
            mReference = new WeakReference<>(controller);
        }

        @Override
        public boolean isController(@NonNull Object controller) {
            return mReference.get() == controller;
        }
    }
}
