package am.util.opentype;

import java.io.File;


/**
 * 字体工具
 * Created by Alex on 2018/9/6.
 */
public class OpenTypeUtils {

    private OpenTypeUtils() {
        //no instance
    }

    public static void test() {
        final File dir = new File("/system/fonts");
        if (dir.exists() && dir.isDirectory()) {
            // list
            final File[] fonts = dir.listFiles();
            if (fonts != null) {
                for (File font : fonts) {
                    test(font);
                }
            }
        }
    }

    private static void test(File font) {
        if (font == null)
            return;
        OpenTypeReader reader = null;
        try {
            reader = new FileOpenTypeReader(font);
            final OpenTypeParser parser = new OpenTypeParser();
            parser.parse(reader);
        } catch (Exception e) {
            // ignore
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    // ignore
                }
            }
        }
    }
}
