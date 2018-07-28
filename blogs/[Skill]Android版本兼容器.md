# Android版本兼容器

随着Android版本一代代发布，碎片化的问题越来越严重，不过好在趋势上市面上的版本已经开始比较集中了。但我们终究还是要面对版本兼容问题。我们不能因为要用高版本方法而提高最低版本限制，高版本里炫酷的效果及高效的方法只会导致你的最低版本显示越来越高，而官方的解决方案（Support-v4）无疑是给了我们新的启示。

## 示例
首先我们抽取一个官方的版本兼容器的一部分看看：
```java
/**
 * Helper for accessing features in {@link TextView} introduced after API level
 * 4 in a backwards compatible fashion.
 */
public final class TextViewCompat {

    // Hide constructor
    private TextViewCompat() {}

    interface TextViewCompatImpl {
        ...
        int getMaxLines(TextView textView);
        int getMinLines(TextView textView);
        void setTextAppearance(@NonNull TextView textView, @StyleRes int resId);
        Drawable[] getCompoundDrawablesRelative(@NonNull TextView textView);
    }

    static class BaseTextViewCompatImpl implements TextViewCompatImpl {
        ...
        @Override
        public int getMaxLines(TextView textView) {
            return TextViewCompatGingerbread.getMaxLines(textView);
        }

        @Override
        public int getMinLines(TextView textView) {
            return TextViewCompatGingerbread.getMinLines(textView);
        }

        @Override
        public void setTextAppearance(TextView textView, @StyleRes int resId) {
            TextViewCompatGingerbread.setTextAppearance(textView, resId);
        }

        @Override
        public Drawable[] getCompoundDrawablesRelative(@NonNull TextView textView) {
            return TextViewCompatGingerbread.getCompoundDrawablesRelative(textView);
        }
    }

    static class JbTextViewCompatImpl extends BaseTextViewCompatImpl {
        @Override
        public int getMaxLines(TextView textView) {
            return TextViewCompatJb.getMaxLines(textView);
        }

        @Override
        public int getMinLines(TextView textView) {
            return TextViewCompatJb.getMinLines(textView);
        }
    }

    static class JbMr1TextViewCompatImpl extends JbTextViewCompatImpl {
        ...
        @Override
        public Drawable[] getCompoundDrawablesRelative(@NonNull TextView textView) {
            return TextViewCompatJbMr1.getCompoundDrawablesRelative(textView);
        }
    }

    static class JbMr2TextViewCompatImpl extends JbMr1TextViewCompatImpl {
        ...
        @Override
        public void setCompoundDrawablesRelativeWithIntrinsicBounds(@NonNull TextView textView,
                @DrawableRes int start, @DrawableRes int top, @DrawableRes int end,
                @DrawableRes int bottom) {
            TextViewCompatJbMr2.setCompoundDrawablesRelativeWithIntrinsicBounds(textView,
                    start, top, end, bottom);
        }
    }

    static class Api23TextViewCompatImpl extends JbMr2TextViewCompatImpl {
        @Override
        public void setTextAppearance(@NonNull TextView textView, @StyleRes int resId) {
            TextViewCompatApi23.setTextAppearance(textView, resId);
        }
    }

    static final TextViewCompatImpl IMPL;

    static {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            IMPL = new Api23TextViewCompatImpl();
        } else if (version >= 18) {
            IMPL = new JbMr2TextViewCompatImpl();
        } else if (version >= 17) {
            IMPL = new JbMr1TextViewCompatImpl();
        } else if (version >= 16) {
            IMPL = new JbTextViewCompatImpl();
        } else {
            IMPL = new BaseTextViewCompatImpl();
        }
    }
    
    ...
    public static int getMaxLines(@NonNull TextView textView) {
        return IMPL.getMaxLines(textView);
    }

    public static int getMinLines(@NonNull TextView textView) {
        return IMPL.getMinLines(textView);
    }

    public static void setTextAppearance(@NonNull TextView textView, @StyleRes int resId) {
        IMPL.setTextAppearance(textView, resId);
    }

    public static Drawable[] getCompoundDrawablesRelative(@NonNull TextView textView) {
        return textView.getCompoundDrawables();
    }
}
```

## 分析
首先private私有化构造函数，避免开发者去new一个出来；然后定义接口，其包含配套的需要做版本兼容的方法；接着实现基础的类来实现这些接口，再就是根据版本断层来重写这一基础类，重写这些类以达到高版本使用高级方法，在就是通过静态代码块来根据不同系统版本实例化一个兼容接口，最后用静态方法来调用这一接口达到版本兼容的效果。

官方的兼容器习惯于使用另外的一个类的静态方法来实现具体功能，好处是版本兼容器只管版本兼容不管具体实现，使代码更清晰，但我们可以直接简化到该重写方法内部实现即可。有些低版本上无法实现高版本方法的效果，但有些可以以曲线救国的方式来达到。**所以，使用版本控制器还是要注意低版本上是不做任何处理还是实现了同样的效果。**

官方支持库内提供了很多版本兼容器：ViewCompat、ViewGroupCompat、ViewConfigurationCompat、ViewParentCompat等等，基本使用Compat结尾命名。

## 使用
载入支持库后，我们可以直接使用这些版本兼容器，但是官方也并非面面俱到得提供了所有方法的版本兼容器。但我们可以套用这种模式实现自己的版本兼容器。

下面列出一个WebView的版本兼容器：
```java
public class WebViewCompat {
    private WebViewCompat() {
    }

    /**
     * A callback interface used to provide values asynchronously.
     */
    public interface ValueCallbackCompat {
        /**
         * Invoked when the value is available.
         *
         * @param value The value.
         */
        void onReceiveValue(String value);
    }

    private interface WebViewCompatImpl {
        boolean isSupportEvaluateJavascript();

        void evaluateJavascript(WebView webView, String script, ValueCallbackCompat callback);
    }

    private static class BaseWebViewCompatImpl implements WebViewCompatImpl {
        @Override
        public boolean isSupportEvaluateJavascript() {
            return false;
        }

        @Override
        public void evaluateJavascript(WebView webView, String script, ValueCallbackCompat callback) {
            // do nothing
        }
    }

    @TargetApi(19)
    private static class KitKatWebViewCompatImpl extends BaseWebViewCompatImpl implements ValueCallback<String> {

        private WeakReference<ValueCallbackCompat> callbackCompatWeakReference;

        @Override
        public boolean isSupportEvaluateJavascript() {
            return true;
        }

        @Override
        public void evaluateJavascript(WebView webView, String script, ValueCallbackCompat callback) {
            callbackCompatWeakReference = new WeakReference<>(callback);
            webView.evaluateJavascript(script, this);
        }

        @Override
        public void onReceiveValue(String value) {
            try {
                callbackCompatWeakReference.get().onReceiveValue(value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static final WebViewCompatImpl IMPL;

    static {
        final int version = android.os.Build.VERSION.SDK_INT;
        if (version >= 19) {
            IMPL = new KitKatWebViewCompatImpl();
        } else {
            IMPL = new BaseWebViewCompatImpl();
        }
    }

    public static boolean isSupportEvaluateJavascript() {
        return IMPL.isSupportEvaluateJavascript();
    }

    public static void evaluateJavascript(WebView webView, String script, ValueCallbackCompat callback) {
        IMPL.evaluateJavascript(webView, script, callback);
    }
}
```
值得注意的是我这里定义了另一个回调接口，因为我将版本限制放置在4，而ValueCallback是在API 7才引入，所以在最低版本限制在7以上的，也就可以直接使用ValueCallback，不需要重新定义另一个接口了。@TargetApi(int)用于避免编译器报错，一旦用到android.os.Build.VERSION.SDK_INT，那么版本限制就一定要放在4了，从4开始才有这个参数。

最后再给出一个我用该方式构建的一个控件[***ShapeImageView***](https://github.com/AlexMofer/ProjectX/tree/master/shapeimageview)，高低版本呈现的效果一致，但是使用的是不同的方式。