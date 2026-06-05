package io.github.alexmofer.android.support.widget;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import io.github.alexmofer.android.support.R;

/**
 * 可点击的
 * Created by Alex on 2026/1/22.
 */
public final class ClickableSpanClicker extends ClickableSpan {

    private final View.OnClickListener mListener;

    public ClickableSpanClicker(@NonNull View.OnClickListener listener) {
        mListener = listener;
    }

    /**
     * 新建链接文案
     *
     * @param context    Context
     * @param message    有待充填区域的主文案
     * @param underline  是否需要下划线
     * @param link       链接文案
     * @param listener   链接点击回调
     * @param formatArgs 待充填的参数
     * @return 文案
     */
    @NonNull
    public static SpannableString newLink(@NonNull Context context,
                                          @StringRes int message, boolean underline,
                                          @StringRes int link,
                                          @NonNull View.OnClickListener listener,
                                          @NonNull Object... formatArgs) {
        final String l = context.getString(link);
        final String msg;
        final int count = formatArgs.length;
        if (count == 0) {
            msg = context.getString(message, l);
        } else {
            final Object[] args = new Object[count + 1];
            args[0] = l;
            System.arraycopy(formatArgs, 0, args, 1, count);
            msg = context.getString(message, args);
        }
        final SpannableString spannableMessage = new SpannableString(msg);
        final int index = msg.indexOf(l);
        if (index >= 0) {
            spannableMessage.setSpan(new ClickableSpanClicker(listener),
                    index, index + l.length(),
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (underline) {
                spannableMessage.setSpan(new UnderlineSpan(),
                        index, index + l.length(),
                        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannableMessage;
    }

    /**
     * 新建链接文案
     *
     * @param context    Context
     * @param message    有待充填区域的主文案
     * @param link       链接文案
     * @param listener   链接点击回调
     * @param formatArgs 待充填的参数
     * @return 文案
     */
    @NonNull
    public static SpannableString newLink(@NonNull Context context,
                                          @StringRes int message,
                                          @StringRes int link,
                                          @NonNull View.OnClickListener listener,
                                          @NonNull Object... formatArgs) {
        return newLink(context, message, false, link, listener, formatArgs);
    }

    /**
     * 新建双链接文案
     *
     * @param context    Context
     * @param message    有两处待充填区域的主文案
     * @param underline  是否需要下划线
     * @param link1      链接1文案
     * @param link2      链接2文案
     * @param listener1  链接1点击回调
     * @param listener2  链接2点击回调
     * @param formatArgs 待充填的参数
     * @return 文案
     */
    @NonNull
    public static SpannableString newDoubleLinks(@NonNull Context context,
                                                 @StringRes int message, boolean underline,
                                                 @StringRes int link1, @StringRes int link2,
                                                 @NonNull View.OnClickListener listener1,
                                                 @NonNull View.OnClickListener listener2,
                                                 @NonNull Object... formatArgs) {
        final String l1 = context.getString(link1);
        final String l2 = context.getString(link2);
        final String msg;
        final int count = formatArgs.length;
        if (count == 0) {
            msg = context.getString(message, l1, l2);
        } else {
            final Object[] args = new Object[count + 2];
            args[0] = l1;
            args[1] = l2;
            System.arraycopy(formatArgs, 0, args, 2, count);
            msg = context.getString(message, args);
        }
        final SpannableString spannableMessage = new SpannableString(msg);
        final int indexLink1 = msg.indexOf(l1);
        if (indexLink1 >= 0) {
            spannableMessage.setSpan(new ClickableSpanClicker(listener1),
                    indexLink1, indexLink1 + l1.length(),
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (underline) {
                spannableMessage.setSpan(new UnderlineSpan(),
                        indexLink1, indexLink1 + l1.length(),
                        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        final int indexLink2 = msg.indexOf(l2);
        if (indexLink2 >= 0) {
            spannableMessage.setSpan(new ClickableSpanClicker(listener2),
                    indexLink2, indexLink2 + l2.length(),
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (underline) {
                spannableMessage.setSpan(new UnderlineSpan(),
                        indexLink2, indexLink2 + l2.length(),
                        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannableMessage;
    }

    /**
     * 新建双链接文案
     *
     * @param context    Context
     * @param message    有两处待充填区域的主文案
     * @param link1      链接1文案
     * @param link2      链接2文案
     * @param listener1  链接1点击回调
     * @param listener2  链接2点击回调
     * @param formatArgs 待充填的参数
     * @return 文案
     */
    @NonNull
    public static SpannableString newDoubleLinks(@NonNull Context context,
                                                 @StringRes int message,
                                                 @StringRes int link1, @StringRes int link2,
                                                 @NonNull View.OnClickListener listener1,
                                                 @NonNull View.OnClickListener listener2,
                                                 @NonNull Object... formatArgs) {
        return newDoubleLinks(context, message, false,
                link1, link2, listener1, listener2, formatArgs);
    }

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        ds.setColor(ds.linkColor);
    }

    @Override
    public void onClick(@NonNull View widget) {
        widget.setTag(R.id.support_v_tag_clickable_span, this);
        mListener.onClick(widget);
    }
}