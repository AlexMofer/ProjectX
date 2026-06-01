package io.github.alexmofer.android.support.widget.maxsize;

/**
 * 最大尺寸可控制的
 * Created by Alex on 2026/1/23.
 */
public interface MaxSizeControllable {

    /**
     * 获取最大高度
     *
     * @return 最大高度
     */
    int getMaximumHeight();

    /**
     * 设置最大高度
     *
     * @param maxHeight 最大高度
     */
    void setMaximumHeight(int maxHeight);

    /**
     * 获取最大宽度
     *
     * @return 最大宽度
     */
    int getMaximumWidth();

    /**
     * 设置最大宽度
     *
     * @param maxWidth 最大宽度
     */
    void setMaximumWidth(int maxWidth);
}
