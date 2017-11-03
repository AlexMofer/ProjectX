/*
 * Copyright (C) 2015 AlexMofer
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

package am.util.printer;

/**
 * ESC-POS Commands
 * Created by Alex on 2017/11/1.
 */
public class PrintCommands {

    private static final byte DLE = 16;
    private static final byte ESC = 27;
    private static final byte FS = 28;
    private static final byte GS = 29;

    /**
     * Moves the print position to the next tab position.
     * HT
     *
     * @return command
     */
    public static byte[] horizontalTab() {
        return new byte[]{9};
    }

    /**
     * Prints the data in the print buffer and feeds one line based on the current line spacing.
     * LF
     *
     * @return command
     */
    public static byte[] printLineFeed() {
        return new byte[]{10};
    }

    /**
     * When automatic line feed is enabled, this command functions the same as LF;
     * when automatic line feed is disabled, this command is ignored.
     * CR
     *
     * @return command
     */
    public static byte[] printCarriageReturn() {
        return new byte[]{13};
    }

    /**
     * Prints the data in the print buffer and returns to standard mode.
     * FF
     *
     * @return command
     */
    public static byte[] printReturnStandardMode() {
        return new byte[]{12};
    }

    /**
     * In page mode, delete all the print data in the current printable area.
     * CAN
     *
     * @return command
     */
    public static byte[] cancelPrintData() {
        return new byte[]{24};
    }

    /**
     * Transmits the selected printer status specified by n in real-time,
     * according to the following parameters:
     * n=1 : Transmit printer status
     * n=2 : Transmit off-line status
     * n=3 : Transmit error status
     * n=4 : Transmit paper roll sensor status
     * DLE EOT n
     *
     * @param n 1≤n≤4
     * @return command
     */
    public static byte[] realTimeStatusTransmission(int n) {
        return new byte[]{DLE, 4, (byte) n};
    }

    /**
     * Responds to a request from the host computer.
     * n specifies the requests as follows:
     * n=1 : Recover from an error and restart printing from the line where the error occurred
     * n=2 : Recover from an error aft clearing the receive and print buffers
     * DLE ENQ n
     *
     * @param n 1≤n≤2
     * @return command
     */
    public static byte[] realTimeRequestToPrinter(int n) {
        return new byte[]{DLE, 5, (byte) n};
    }

    /**
     * Outputs the pulse specified by t to connector pin m as follows:
     * m=0 : Drawer kick-out connector pin 2.
     * m=1 : Drawer kick-out connector pin 5.
     * The pulse ON time is [ t x 100 ms] and the OFF time is [ t x 100 ms].
     * DLE DC4 n m t
     *
     * @param n n=1
     * @param m m=0,1
     * @param t 1≤t≤8
     * @return command
     */
    public static byte[] generatePulseAtRealTime(int n, int m, int t) {
        return new byte[]{DLE, 20, (byte) n, (byte) m, (byte) t};
    }

    /**
     * In page mode, prints all buffered data in the printable area collectively.
     * ESC FF
     *
     * @return command
     */
    public static byte[] printData() {
        return new byte[]{ESC, 12};
    }

    /**
     * Sets the character spacing for the right side of the character to
     * [n x horizontal or vertical motion units].
     * ESC SP n
     *
     * @param n 0≤n≤255 default 0
     * @return command
     */
    public static byte[] setRightSideCharacterSpacing(int n) {
        return new byte[]{ESC, 32, (byte) n};
    }

    /**
     * Selects print mode(s) using n as follows:
     * n=0 : Character font A (12x24)
     * Emphasized mode not selected.
     * Double-height mode not selected.
     * Double-width mode not selected.
     * Underline mode not selected.
     * n=1 : Character font B (9x24)
     * n=8 : Emphasized mode selected.
     * n=16 : Double-height mode selected.
     * n=32 : Double-width mode selected.
     * n=128 : Underline mode selected.
     * ESC ! n
     *
     * @param n 0≤n≤255 default 0
     * @return command
     */
    public static byte[] selectPrintMode(int n) {
        return new byte[]{ESC, 33, (byte) n};
    }

    /**
     * Sets the distance from the beginning of the line to the position at which subsequent
     * characters are to be printed.
     * The distance from the beginning of the line to the print position is
     * [(nL + nH x 256) x (vertical or horizontal motion unit)] inches.
     * ESC $ nL nH
     *
     * @param nL 0≤nL≤255
     * @param nH 0≤nH≤255
     * @return command
     */
    public static byte[] setAbsolutePrintPosition(int nL, int nH) {
        return new byte[]{ESC, 36, (byte) nL, (byte) nH};
    }

    /**
     * Cancels the user-defined character set.
     * ESC % n
     *
     * @return command
     */
    public static byte[] cancelUserDefinedCharacterSet() {
        return new byte[]{ESC, 37, 0};
    }

    /**
     * Selects the user-defined character set.
     * ESC % n
     *
     * @return command
     */
    public static byte[] selectUserDefinedCharacterSet() {
        return new byte[]{ESC, 37, 1};
    }

    /**
     * Defines user-defined characters
     * y specifies the number of bytes in the vertical direction, it always 3.
     * c1 specifies the beginning character code for the definition, and c2 specifies the final code.
     * X specifies the number of dots in the horizontal direction.
     * ESC & y c1 c2 [x1 d1…d(y x x1)]..[ xk d1..d(y x xk)]
     *
     * @param c1   32≤c1≤c2≤126
     * @param c2   32≤c1≤c2≤126
     * @param dots 0 ≤ x ≤ 12 Font A (when font A (12 x 24) is selected)
     *             0 ≤ x ≤ 9 Font B (when font B (9 x 17) is selected)
     *             0 ≤ d1 ... d(y x xk) ≤ 255
     * @return command
     */
    public static byte[] defineUserDefinedCharacters(int c1, int c2, byte[] dots) {
        byte[] part = new byte[]{ESC, 38, 3, (byte) c1, (byte) c2};
        byte[] destination = new byte[part.length + dots.length];
        System.arraycopy(part, 0, destination, 0, part.length);
        System.arraycopy(dots, 0, destination, part.length, dots.length);
        return destination;
    }

    /**
     * Selects a bit-image mode using m for the number of dots specified by nL and nH, as follows:
     * m=0
     * Mode:8-dot single-density
     * Vertical NO. of Dots:8
     * Vertical Dot Density: 60 DPI
     * Horizontal Dot Density: 90 DPI
     * Number of (Data(K)):nL + nH x 256
     * m=1
     * Mode:8-dot double-density
     * Vertical NO. of Dots:8
     * Vertical Dot Density: 60 DPI
     * Horizontal Dot Density: 180 DPI
     * Number of (Data(K)):nL + nH x 256
     * m=32
     * Mode:24-dot single-density
     * Vertical NO. of Dots:24
     * Vertical Dot Density: 180 DPI
     * Horizontal Dot Density: 90 DPI
     * Number of (Data(K)):(nL + nH x 256) x 3
     * m=33
     * Mode:24-dot single-density
     * Vertical NO. of Dots:24
     * Vertical Dot Density: 180 DPI
     * Horizontal Dot Density: 180 DPI
     * Number of (Data(K)):(nL + nH x 256) x 3
     * ESC * m nL nH [d1...dk]
     *
     * @param m     m = 0, 1, 32, 33
     * @param nL    0≤nL ≤255
     * @param nH    0≤nH ≤3
     * @param image 0≤d≤255
     * @return command
     */
    public static byte[] selectBitImageMode(int m, int nL, int nH, byte[] image) {
        byte[] part = new byte[]{ESC, 42, (byte) m, (byte) nL, (byte) nH};
        byte[] destination = new byte[part.length + image.length];
        System.arraycopy(part, 0, destination, 0, part.length);
        System.arraycopy(image, 0, destination, part.length, image.length);
        return destination;
    }

    /**
     * Turns underline mode off
     * ESC - n
     *
     * @return command
     */
    public static byte[] turnUnderlineModeOff() {
        return new byte[]{ESC, 45, 0};
    }

    /**
     * Turns underline mode on or off, based on the following values of n.
     * n=0, 48 : Turns off underline mode
     * n=1, 49 : Turns on underline mode (1-dot thick)
     * n=2, 50 : Turns on underline mode (2-dots thick)
     * ESC - n
     *
     * @param n 0≤n ≤2, 48≤n ≤50
     * @return command
     */
    public static byte[] turnUnderlineMode(int n) {
        return new byte[]{ESC, 45, 1};
    }

    /**
     * Selects approximately 4.23 mm {1/6"} spacing.
     * ESC 2
     *
     * @return command
     */
    public static byte[] selectDefaultLineSpacing() {
        return new byte[]{ESC, 50};
    }

    /**
     * Sets the line spacing to [n x (vertical or horizontal motion unit)] inches.
     * ESC 3 n
     *
     * @param n 0≤n≤255
     * @return command
     */
    public static byte[] setLineSpacing(int n) {
        return new byte[]{ESC, 51, (byte) n};
    }

    /**
     * Selects device to which host computer sends data, using n as follows:
     * n=0 : Printer disabled
     * n=1 : Printer enabled
     * ESC = n
     *
     * @param n 1≤n≤255 default 1
     * @return command
     */
    public static byte[] setPeripheralDevice(int n) {
        return new byte[]{ESC, 61, (byte) n};
    }

    /**
     * Cancels user-defined characters.
     * ESC ? n
     *
     * @param n 32 ≤n ≤126
     * @return command
     */
    public static byte[] cancelUserDefinedCharacters(int n) {
        return new byte[]{ESC, 63, (byte) n};
    }

    /**
     * Clears the data in the print buffer and resets the printer mode to the mode
     * that was in effect when the power was turned on.
     * ESC @
     *
     * @return command
     */
    public static byte[] initializePrinter() {
        return new byte[]{ESC, 64};
    }

    /**
     * Set is horizontal tab positions.
     * n specifies the column number for setting a horizontal tab position from the beginning of the line.
     * k indicates the total number of horizontal tab positions to be set.
     * ESC D [n1...nk] NUL
     *
     * @param nk 1≤n ≤255
     *           0≤k ≤32
     * @return command
     */
    public static byte[] setHorizontalTabPositions(byte[] nk) {
        byte[] part = new byte[]{ESC, 68};
        byte[] destination = new byte[part.length + nk.length + 1];
        System.arraycopy(part, 0, destination, 0, part.length);
        System.arraycopy(nk, 0, destination, part.length, nk.length);
        destination[part.length + nk.length] = 0;
        return destination;
    }

    /**
     * Turns emphasized mode on or off.
     * When the LSB of n is 0, emphasized mode is turned off.
     * When the LSB of n is 1, emphasized mode is turned on.
     * ESC E n
     *
     * @param n 0≤n≤255 default 0
     * @return command
     */
    public static byte[] turnEmphasizedMode(int n) {
        return new byte[]{ESC, 69, (byte) n};
    }

    /**
     * Turns double-strike mode on or off.
     * When the LSB of n is 0, double-strike mode is turned off.
     * When the LSB of n is1, double-strike mode is turned on.
     * ESC G n
     *
     * @param n 0≤n≤255 default 0
     * @return command
     */
    public static byte[] turnDoubleStrikeMode(int n) {
        return new byte[]{ESC, 71, (byte) n};
    }

    /**
     * Prints the data in the print buffer and feeds the paper [n x vertical or horizontal motion unit].
     * ESC J n
     *
     * @param n 0≤n ≤255
     * @return command
     */
    public static byte[] printFeedPaper(int n) {
        return new byte[]{ESC, 74, (byte) n};
    }

    /**
     * Switches from standard mode to page mode.
     * ESC L
     *
     * @return command
     */
    public static byte[] selectPageMode() {
        return new byte[]{ESC, 76};
    }

    /**
     * Selects character fonts
     * n=0, 48 : Character font A (12 X 24 ) Selected
     * n=1, 49 : Character font B (9 X 24 ) Selected
     * ESC M n
     *
     * @param n n= 0, 1 , 48, 49
     * @return command
     */
    public static byte[] selectCharacterFont(int n) {
        return new byte[]{ESC, 77, (byte) n};
    }

    /**
     * Selects an international character set n from the following:
     * n=0 : U. S. A
     * n=1 : France
     * n=2 : Germany
     * n=3 : U. K.
     * n=4 : Denmark I
     * n=5 : Sweden
     * n=6 : Italy
     * n=7 : Spain I
     * n=8 : Japan
     * n=9 : Norway
     * n=10 : Denmark II
     * n=11 : Spain II
     * n=12 : Latin America
     *
     * @param n 0≤n≤13 default 0
     * @return command
     */
    public static byte[] selectAnInternationalCharacterSet(int n) {
        return new byte[]{ESC, 82, (byte) n};
    }

    /**
     * Switches from page mode to standard mode.
     * ESC S
     *
     * @return command
     */
    public static byte[] selectStandardMode() {
        return new byte[]{ESC, 83};
    }

    /**
     * Select the print direction and starting position in page mode.
     * n specifies the print direction and starting position as follows:
     * n=0, 48 : Left to right(Print Direction) Upper left(Starting Position)
     * n=1, 49 : Bottom to top(Print Direction) Lower left(Starting Position)
     * n=2, 50 : Right to left(Print Direction) Lower right(Starting Position)
     * n=3, 51 : Top to bottom(Print Direction) Upper right(Starting Position)
     * ESC T n
     *
     * @param n 0≤n ≤3,
     *          48≤n ≤51
     * @return command
     */
    public static byte[] selectPrintDirectionInPageMode(int n) {
        return new byte[]{ESC, 84, (byte) n};
    }

    /**
     * Turns 90˚ clockwise rotation mode on or off.
     * n is used as follows:
     * n=0, 48 : Turns off 90˚ clockwise rotation mode
     * n=1, 49 : Turns on 90˚ clockwise rotation mode
     * ESC V n
     *
     * @param n 0≤n≤1,48≤n≤49 default 0
     * @return command
     */
    public static byte[] turn90ClockwiseRotationMode(int n) {
        return new byte[]{ESC, 86, (byte) n};
    }

    /**
     * The horizontal starting position, vertical starting position, printing area
     * width, and printing area height are defined as x0, y0, dx, dy,
     * respectively.
     * Each setting for the printable area is calculated as follow:
     * x0 = [(xL + xH x 256) x (horizontal motion unit)]
     * y0 = [(yL + yH x 256) x (vertical motion unit)]
     * dx = [(dxL + dxH x 256) x (horizontal motion unit)]
     * dy = [(dyL + dyH x 256) x (vertical motion unit)]
     * ESC W xL xH yL yH dxL dxH dyL dyH
     *
     * @param xL  0≤ xL xH yL yH dxL dxH dyL dyH ≤255 (except dxL=dxH=0 or dyL=dyH=0)
     * @param xH  0≤ xL xH yL yH dxL dxH dyL dyH ≤255 (except dxL=dxH=0 or dyL=dyH=0)
     * @param yL  0≤ xL xH yL yH dxL dxH dyL dyH ≤255 (except dxL=dxH=0 or dyL=dyH=0)
     * @param yH  0≤ xL xH yL yH dxL dxH dyL dyH ≤255 (except dxL=dxH=0 or dyL=dyH=0)
     * @param dxL 0≤ xL xH yL yH dxL dxH dyL dyH ≤255 (except dxL=dxH=0 or dyL=dyH=0)
     * @param dxH 0≤ xL xH yL yH dxL dxH dyL dyH ≤255 (except dxL=dxH=0 or dyL=dyH=0)
     * @param dyL 0≤ xL xH yL yH dxL dxH dyL dyH ≤255 (except dxL=dxH=0 or dyL=dyH=0)
     * @param dyH 0≤ xL xH yL yH dxL dxH dyL dyH ≤255 (except dxL=dxH=0 or dyL=dyH=0)
     * @return command
     */
    public static byte[] setPrintingAreaInPageMode(int xL, int xH, int yL, int yH,
                                                   int dxL, int dxH, int dyL, int dyH) {
        return new byte[]{ESC, 87, (byte) xL, (byte) xH, (byte) yL, (byte) yH,
                (byte) dxL, (byte) dxH, (byte) dyL, (byte) dyH};
    }

    /**
     * Sets the print starting position based on the current position by using the horizontal or vertical motion unit.
     * This command sets the distance from the current position to [(nL+ nH x 256) x (horizontal or vertical unit)].
     * ESC \ nL nH
     *
     * @param nL 0≤nL≤255
     * @param nH 0≤nH≤255
     * @return command
     */
    public static byte[] setRelativePrintPosition(int nL, int nH) {
        return new byte[]{ESC, 92, (byte) nL, (byte) nH};
    }
}
