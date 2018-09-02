package am.project.support.font;

import android.text.TextUtils;

import java.util.ArrayList;

/**
 * 字体族
 * Created by Alex on 2018/8/30.
 */
class Family {
    private final String mName;// 名称
    private final ArrayList<Font> mFonts = new ArrayList<>();// 字体

    Family(String name) {
        mName = name;
    }

    boolean isAvailable() {
        return !TextUtils.isEmpty(mName) && mFonts.size() > 0;
    }

    void addFont(Font font) {
        if (font == null)
            return;
        mFonts.add(font);
    }

    String getName() {
        return mName;
    }

    ArrayList<TypefaceItem> convert(int weight) {
        final ArrayList<TypefaceItem> items = new ArrayList<>();
        for (Font font : mFonts) {
            if (weight == -1)
                items.add(font.convert());
            else {
                if (weight == font.getWeight())
                    items.add(font.convert());
            }
        }
        return items.isEmpty() ? null : items;
    }
}
