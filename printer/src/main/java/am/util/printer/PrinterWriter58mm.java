package am.util.printer;

import java.io.IOException;

/**
 * 纸宽58mm的打印机
 * 未验证
 * Created by Alex on 2016/6/17.
 */
@SuppressWarnings("unused")
public class PrinterWriter58mm extends PrinterWriter {
    public PrinterWriter58mm() throws IOException {
    }

    @Override
    protected int getLineWidth() {
        return 17;
    }

    @Override
    protected int getLineStringWidth(int textSize) {
        switch (textSize) {
            default:
            case 0:
                return 34;
            case 1:
                return 16;
        }
    }

    @Override
    protected int getDrawableMaxWidth() {
        return 362;
    }
}
