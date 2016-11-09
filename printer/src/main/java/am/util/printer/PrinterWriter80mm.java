package am.util.printer;

import java.io.IOException;

/**
 * 纸宽80mm的打印机
 * Created by Alex on 2016/6/17.
 */
@SuppressWarnings("unused")
public class PrinterWriter80mm extends PrinterWriter {

    public static final int TYPE_80 = 80;// 纸宽80mm

    public PrinterWriter80mm() throws IOException {
    }

    public PrinterWriter80mm(int parting) throws IOException {
        super(parting);
    }

    @Override
    protected int getLineWidth() {
        return 24;
    }

    @Override
    protected int getLineStringWidth(int textSize) {
        switch (textSize) {
            default:
            case 0:
                return 47;
            case 1:
                return 23;
        }
    }

    @Override
    protected int getDrawableMaxWidth() {
        return 500;
    }
}
