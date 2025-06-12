package io.github.alexmofer.projectx.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.view.WindowCompat;

/**
 * 半模态转场对话框
 * Created by Alex on 2025/6/12.
 */
public class SheetDialogFragment extends AppCompatDialogFragment {

    @NonNull
    @Override
    public AppCompatDialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new SheetDialog(requireContext(), getTheme());
    }

    private static class SheetDialog extends AppCompatDialog {

        public SheetDialog(@NonNull Context context, int theme) {
            super(context, theme);
        }

        @Override
        public void setContentView(@NonNull View view) {
            super.setContentView(view);
            final Window window = getWindow();
            if (window != null) {
                onWindowContentViewChanged(window);
            }
        }

        @Override
        public void setContentView(int layoutResID) {
            super.setContentView(layoutResID);
            final Window window = getWindow();
            if (window != null) {
                onWindowContentViewChanged(window);
            }
        }

        @Override
        public void setContentView(@NonNull View view, ViewGroup.LayoutParams params) {
            super.setContentView(view, params);
            final Window window = getWindow();
            if (window != null) {
                onWindowContentViewChanged(window);
            }
        }

        private void onWindowContentViewChanged(@NonNull Window window) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowCompat.setDecorFitsSystemWindows(window, false);
            window.setStatusBarColor(0);

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                window.setNavigationBarContrastEnforced(false);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                final WindowManager.LayoutParams lp = window.getAttributes();
                lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;
                window.setAttributes(lp);
            }

        }
    }
}
