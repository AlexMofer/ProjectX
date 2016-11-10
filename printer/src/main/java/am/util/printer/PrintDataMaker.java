package am.util.printer;

import java.util.List;

/**
 * Print
 * Created by Alex on 2016/11/10.
 */

public interface PrintDataMaker {
    List<byte[]> getPrintData(int type);
}
