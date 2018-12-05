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
@SuppressWarnings({"WeakerAccess", "unused"})
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
     * Turns emphasized mode off.
     * ESC E n
     *
     * @return command
     */
    public static byte[] turnOffEmphasizedMode() {
        return new byte[]{ESC, 69, 0};
    }

    /**
     * Turns emphasized mode on.
     * ESC E n
     *
     * @return command
     */
    public static byte[] turnOnEmphasizedMode() {
        return new byte[]{ESC, 69, 1};
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
     *            default xL = xH = yL = yH = 0, dxL = 0, dxH = 2, dyL =126, dyH = 6
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

    /**
     * Aligns all the data in one line to the specified position
     * n selects the justification as follows:
     * n=0,48 : Left justification
     * n=1,49 : Centering
     * n=2,50 : Right justification
     * ESC a n
     *
     * @param n 0≤n≤2,48≤n≤50 default 0
     * @return command
     */
    public static byte[] selectJustification(int n) {
        return new byte[]{ESC, 97, (byte) n};
    }

    /**
     * Selects the paper sensor(s) to output paper end signals.
     * Each bit of n is used as follows:
     * n=0 : Paper roll near-end sensor disabled / Paper roll end sensor disabled
     * n=1,2 : Paper roll near-end sensor enabled
     * n=4,8 : Paper roll end sensor enabled
     * ESC c 3 n
     *
     * @param n 0≤n≤255 default 15
     * @return command
     */
    public static byte[] selectPaperSensorToOutputPaperEndSignals(int n) {
        return new byte[]{ESC, 99, 51, (byte) n};
    }

    /**
     * Selects the paper sensor(s) used to stop printing when a paper-end is detected.
     * using n as follows:
     * n=0 : Paper roll near end sensor disabled
     * n=1,2 : Paper roll near end sensor enabled
     * ESC c 4 n
     *
     * @param n 0≤n≤255 default 0
     * @return command
     */
    public static byte[] selectPaperSensorToStopPrinting(int n) {
        return new byte[]{ESC, 99, 52, (byte) n};
    }

    /**
     * Disables the panel buttons.
     * ESC c 5 n
     *
     * @return command
     */
    public static byte[] disablePanelButtons() {
        return new byte[]{ESC, 99, 53, 0};
    }

    /**
     * Enables the panel buttons.
     * ESC c 5 n
     *
     * @return command
     */
    public static byte[] enablePanelButtons() {
        return new byte[]{ESC, 99, 53, 1};
    }

    /**
     * Prints the data in the print buffer and feeds n lines.
     * ESC d n
     *
     * @param n 0≤n ≤255
     * @return command
     */
    public static byte[] printFeedNLines(int n) {
        return new byte[]{ESC, 100, (byte) n};
    }

    /**
     * When this command is received, paper is cut (only when the auto cutter is loaded).
     * ESC i
     *
     * @return command
     */
    public static byte[] executePaperFullCut() {
        return new byte[]{ESC, 105};
    }

    /**
     * When this command is received, paper is cut (only when the auto cutter is loaded).
     * ESC m
     *
     * @return command
     */
    public static byte[] executePaperPartialCut() {
        return new byte[]{ESC, 109};
    }

    /**
     * Outputs the pulse specified by t1 and t2 to connector pin m as follows:
     * m=0,48 : Drawer kick-out connector pin2.
     * m=1,49 : Drawer kick-out connector pin5.
     * ESC p m t1 t2
     *
     * @param m  m = 0, 1, 48, 49
     * @param t1 0≤t1≤255
     * @param t2 0≤t2≤255
     * @return command
     */
    public static byte[] generatePulse(int m, int t1, int t2) {
        return new byte[]{ESC, 112, (byte) m, (byte) t1, (byte) t2};
    }

    /**
     * Selects a page n from the character code table.
     * n=0 : PC437 [U.S.A., Standard Europe]
     * n=1 : Katakana
     * n=2 : PC850 [Multilingual]
     * n=3 : PC860 [Portuguese]
     * n=4 : PC863 [Canadian-French]
     * n=5 : PC865 [Nordic]
     * n=17 : PC866 [Cyrillic #2]
     * n=255 : Space page
     * ESC t n
     *
     * @param n 0≤n≤5, 16≤n≤26, n=255 default 0
     * @return command
     */
    public static byte[] selectCharacterCodeTable(int n) {
        return new byte[]{ESC, 116, (byte) n};
    }

    /**
     * Turns upside-down printing mode off.
     * ESC { n
     *
     * @return command
     */
    public static byte[] turnsOffUpsideDownPrintingMode() {
        return new byte[]{ESC, 123, 0};
    }

    /**
     * Turns upside-down printing mode on.
     * ESC { n
     *
     * @return command
     */
    public static byte[] turnsOnUpsideDownPrintingMode() {
        return new byte[]{ESC, 123, 1};
    }

    /**
     * Prints a NV bit image n using the mode specified by m.
     * m=0,48 : Normal Mode Vertical=180dpi Horizontal=180dpi
     * m=1,49 : Double-width Mode Vertical=180dpi Horizontal=90dpi
     * m=2,50 : Double-height Mode Vertical=90dpi Horizontal=180dpi
     * m=3,51 : Quadruple Mode Vertical=90dpi Horizontal=90dpi
     * FS p n m
     *
     * @param n 1≤n≤255
     * @param m 0≤m≤3, 48≤m≤51
     * @return command
     */
    public static byte[] printNVBitImage(int n, int m) {
        return new byte[]{FS, 112, (byte) n, (byte) m};
    }

    /**
     * Define the NV bit image specified by n.
     * FS q n [xL xH yL yH d1...dk] 1...[xL xH yL yH d1...dk]n
     *
     * @param n     1≤n≤255
     * @param image 0≤xL≤255
     *              0≤xH≤3 (when 1≤(xL + xH x 256)≤1023)
     *              0≤yL≤255
     *              0≤yL≤1 (when 1≤(yL + yH x 256)≤288)
     *              0≤d≤255
     *              k = (xL + xH x 256) x (yL + yH x 256) x 8
     *              Total defined data area = 2M bits (256K bytes)
     * @return command
     */
    public static byte[] defineNVBitImage(int n, byte[] image) {
        byte[] part = new byte[]{FS, 113, (byte) n};
        byte[] destination = new byte[part.length + image.length];
        System.arraycopy(part, 0, destination, 0, part.length);
        System.arraycopy(image, 0, destination, part.length, image.length);
        return destination;
    }

    /**
     * Selects the character height using bits 0 to 3 and selects the character width using bits
     * 4 to 7, as follows:
     * 0-3bit:
     * n=0 : height = 1(normal)
     * n=1 : height = 2(double-height)
     * n=2 : height = 3
     * n=3 : height = 4
     * n=4 : height = 5
     * n=5 : height = 6
     * n=6 : height = 7
     * n=7 : height = 8
     * 4-7bit:
     * n=0 : height = 1(normal)
     * n=1 : height = 2(double-width)
     * n=2 : height = 3
     * n=3 : height = 4
     * n=4 : height = 5
     * n=5 : height = 6
     * n=6 : height = 7
     * n=7 : height = 8
     * GS ! n
     *
     * @param n 0≤n≤255(1≤vertical number of times≤8, 1≤horizontal number of times≤8) default 0
     * @return command
     */
    public static byte[] selectCharacterSize(int n) {
        return new byte[]{GS, 33, (byte) n};
    }

    /**
     * Sets the absolute vertical print starting position for buffer character data in page mode.
     * This command sets the absolute print position to [ (nL + nH x 256) x (vertical or horizontal
     * motion unit)] inches.
     * If the [ (nL + nH x 256) x (vertical or horizontal motion unit)] exceeds the specified
     * printing area, this command is ignored.
     * The horizontal starting buffer position does not move.
     * The reference starting position is that specified by ESC T.
     * This command operates as follows, depending on the starting position of the printing area
     * specified by ESC T:
     * 1. When the starting position is set to the upper left or lower right, this command sets the
     * absolute position in the vertical direction.
     * 2. When the starting position is set to the upper right or lower left, this command sets the
     * absolute position in the horizontal direction.
     * The horizontal and vertical motion units are specified by GS P.
     * The GS P command can change the horizontal and vertical motion unit.
     * However, the value cannot be less than the minimum horizontal movement amount, and it must be
     * in even units of the minimum horizontal movement amount.
     * GS $ nL nH
     *
     * @param nL 0≤nL≤255
     * @param nH 0≤nH≤255
     * @return command
     */
    public static byte[] setAbsoluteVerticalPrintPositionInPageMade(int nL, int nH) {
        return new byte[]{GS, 36, (byte) nL, (byte) nH};
    }

    /**
     * Defines a downloaded bit image with the number of dots specified by x and y.
     * x indicates the number of dots in the horizontal direction.
     * y indicates he number of dots in the vertical direction.
     * GS * x y d1...d (x x y x 8)
     *
     * @param x     1≤n≤255
     * @param y     1≤n≤255
     * @param image x x y≤1536
     *              0≤d≤255
     * @return command
     */
    public static byte[] defineDownloadedBitImage(int x, int y, byte[] image) {
        byte[] part = new byte[]{GS, 42, (byte) x, (byte) y};
        byte[] destination = new byte[part.length + image.length];
        System.arraycopy(part, 0, destination, 0, part.length);
        System.arraycopy(image, 0, destination, part.length, image.length);
        return destination;
    }

    /**
     * Prints a downloaded bit image using the mode specified by m.
     * m selects a mode from the table below:
     * m=0,48 : Normal Mode Vertical=180dpi Horizontal=180dpi
     * m=1,49 : Double-width Mode Vertical=180dpi Horizontal=90dpi
     * m=2,50 : Double-height Mode Vertical=90dpi Horizontal=180dpi
     * m=3,51 : Quadruple Mode Vertical=90dpi Horizontal=90dpi
     * GS / m
     *
     * @param m 0≤m≤3, 48≤m≤51
     * @return command
     */
    public static byte[] printDownloadedBitImage(int m) {
        return new byte[]{GS, 47, (byte) m};
    }

    /**
     * Starts or ends macro definition.
     * GS :
     *
     * @return command
     */
    public static byte[] startOrEndMacroDefinition() {
        return new byte[]{GS, 58};
    }

    /**
     * Turns off white/black reverse printing mode.
     * GS B n
     *
     * @return command
     */
    public static byte[] turnOffWhiteBlackReversePrintingMode() {
        return new byte[]{GS, 66, 0};
    }

    /**
     * Turns on white/black reverse printing mode.
     * GS B n
     *
     * @return command
     */
    public static byte[] turnOnWhiteBlackReversePrintingMode() {
        return new byte[]{GS, 66, 1};
    }

    /**
     * Selects the printing position of HRI characters when printing a bar code.
     * n selects the printing position as follows:
     * m=0,48 : Not printed
     * m=1,49 : Above the bar code
     * m=2,50 : Below the bar code
     * m=3,51 : Both above and below the bar code
     * GS H n
     *
     * @param n 0≤m≤3, 48≤m≤51 default 0
     * @return command
     */
    public static byte[] selectPrintingPositionOfHRICharacters(int n) {
        return new byte[]{GS, 72, (byte) n};
    }

    /**
     * Sets the left margin using nL and nH.
     * The left margin is set to [(nL + nH x 256) x (horizontal motion unit)] inches.
     * GS L nL nH
     *
     * @param nL 0≤nL≤255 default 0
     * @param nH 0≤nH≤255 default 0
     * @return command
     */
    public static byte[] setLeftMargin(int nL, int nH) {
        return new byte[]{GS, 76, (byte) nL, (byte) nH};
    }

    /**
     * Sets the horizontal and vertical motion units to 1/x inch and 1/y inch, respectively.
     * When x and u are set to 0, the default setting of each value is used. (x = 180, y = 360)
     * GS P x y
     *
     * @param x 0≤x≤255 default 180
     * @param y 0≤y≤255 default 360
     * @return command
     */
    public static byte[] setHorizontalAndVerticalMotionUnits(int x, int y) {
        return new byte[]{GS, 80, (byte) x, (byte) y};
    }

    /**
     * Selects a mode for cutting paper and executes paper cutting. The value of m selects the mode
     * as follows:
     * m=1,49 : Partial cut(one point center uncut)
     * m=66 : Feeds paper(cutting position + [n x(vertical motion unit)]) ,
     * and cuts the paper partially(one point center uncut)
     * GS V m
     * GS V m n
     *
     * @param m m=1,49,66
     * @param n 0≤n≤255
     * @return command
     */
    public static byte[] selectCutModeAndCutPaper(int m, int n) {
        if (m == 66) {
            return new byte[]{GS, 86, 66, (byte) n};
        } else {
            return new byte[]{GS, 86, (byte) m};
        }
    }

    /**
     * Sets the printing area width to the area specified by nL and nH.
     * The printing area width is set to [( nL + nH x 256) x horizontal motion
     * unit]].
     * GS W nL nH
     *
     * @param nL 0≤nL≤255 default 0
     * @param nH 0≤nH≤255 default 2
     * @return command
     */
    public static byte[] setPrintingAreaWidth(int nL, int nH) {
        return new byte[]{GS, 87, (byte) nL, (byte) nH};
    }

    /**
     * Sets the relative vertical print starting position from the current position in page mode.
     * This command sets the distance from the current position to [( nL + nH x 256) vertical or
     * horizontal motion unit] inches.
     * GS \ nL nH
     *
     * @param nL 0≤nL≤255
     * @param nH 0≤nH≤255
     * @return command
     */
    public static byte[] setRelativeVerticalPrintPositionInPageMode(int nL, int nH) {
        return new byte[]{GS, 92, (byte) nL, (byte) nH};
    }

    /**
     * Executes a macro.
     * r specifies the number of times to execute the macro.
     * t specifies the waiting time for executing the macro.
     * m specifies macro executing mode.
     * When the LSB of m = 0:
     * The macro executes r times continuously at the interval specified by t.
     * When the LSB of m = 1:
     * After waiting for the period specified by t, the PAPER OUT LED indicators blink and the
     * printer waits for the FEED button to be pressed. After the button is pressed, the printer
     * executes the macro once. The printer repeats the operation r times.
     * GS ^ r t m
     *
     * @param r 0≤r≤255
     * @param t 0≤t≤255
     * @param m m=0,1
     * @return command
     */
    public static byte[] executeMacro(int r, int t, int m) {
        return new byte[]{GS, 94, (byte) r, (byte) t, (byte) m};
    }

    /**
     * Enables or disables ASB and specifies the status items to include, using n as follows:
     * bit=0,n=0 : Drawer kick-out connector pin 3 status disabled.
     * bit=0,n=1 : Drawer kick-out connector pin 3 status enabled.
     * bit=1,n=0 : On-line/off-line status disabled.
     * bit=1,n=2 : On-line/off-line status enabled,
     * bit=2,n=0 : Error status disabled.
     * bit=2,n=4 : Error status enabled.
     * bit=3,n=0 : Paper roll sensor status disabled.
     * bit=3,n=8 : Paper roll sensor status enabled.
     * GS a n
     *
     * @param n 0≤n≤255 default 0
     * @return command
     */
    public static byte[] setAutomaticStatusBack(int n) {
        return new byte[]{GS, 97, (byte) n};
    }

    /**
     * selects a font for the HRI characters used when printing a bar code.
     * n selects the font from the following table:
     * n=0,48 : Font A (12 x 24)
     * n=1,49 : Font B (9 x 24)
     * GS f n
     *
     * @param n n = 0, 1, 48, 49
     * @return command
     */
    public static byte[] selectFontForHumanReadableInterpretationCharacters(int n) {
        return new byte[]{GS, 102, (byte) n};
    }

    /**
     * Select the height of the bar code.
     * n specifies the number of dots in the vertical direction.
     * GS h n
     *
     * @param n 1≤n≤255 default 162
     * @return command
     */
    public static byte[] selectBarCodeHeight(int n) {
        return new byte[]{GS, 104, (byte) n};
    }

    /**
     * Selects a bar code system and prints the bar code.
     * m selects a bar code system as follows:
     * m=0 : UPC–A 11≤k≤12 48≤d≤57
     * m=1 : UPC–E 11≤k≤12 48≤d≤57
     * m=2 : EAN13 12≤k≤13 48≤d≤57
     * m=3 : EAN8 7≤k≤8 48≤d≤57
     * m=4 : CODE39 1≤k 48≤d≤57,65≤d≤90,32,36,37,43,45,46,47
     * m=5 : ITF 1≤k(even number) 48≤d≤57
     * m=6 : CODABAR 1≤k 48≤d≤57,65≤d≤68,36,43,45,46,47,58
     * m=65 : UPC–A 11≤k≤12 48≤d≤57
     * m=66 : UPC–E 11≤k≤12 48≤d≤57
     * m=67 : EAN13 12≤k≤13 48≤d≤57
     * m=68 : EAN8 7≤k≤8 48≤d≤57
     * m=69 : CODE39 1≤k≤255 48≤d≤57,65≤d≤90,32,36,37,43,45,46,47
     * m=70 : ITF 1≤k≤255(even number) 48≤d≤57
     * m=71 : CODABAR 1≤k≤255 48≤d≤57,65≤d≤68,36,43,45,46,47,58
     * m=72 : CODE93 1≤k≤255 0≤d≤127
     * m=73 : CODE128 2≤k≤255 0≤d≤127
     * GS k m d1…dk NUL
     * GS k m n d1…dn
     *
     * @param m    0≤m≤6
     *             65≤m≤73
     * @param n    n and d depends on the code system used
     * @param data k and d depends on the code system used
     * @return command
     */
    public static byte[] printBarCode(int m, int n, byte[] data) {
        if (m <= 6) {
            byte[] part = new byte[]{GS, 107, (byte) m};
            byte[] destination = new byte[part.length + data.length + 1];
            System.arraycopy(part, 0, destination, 0, part.length);
            System.arraycopy(data, 0, destination, part.length, data.length);
            destination[part.length + data.length] = 0;
            return destination;
        } else {
            byte[] part = new byte[]{GS, 107, (byte) m, (byte) n};
            byte[] destination = new byte[part.length + data.length];
            System.arraycopy(part, 0, destination, 0, part.length);
            System.arraycopy(data, 0, destination, part.length, data.length);
            return destination;
        }
    }

    /**
     * Transmits the status specified by n as follows:
     * n=1,49 : Transmits paper sensor status
     * n=2,50 : Transmits drawer kick-out connector status
     * GS r n
     *
     * @param n n=1, 2, 49, 50
     * @return command
     */
    public static byte[] transmitStatus(int n) {
        return new byte[]{GS, 114, (byte) n};
    }

    /**
     * Selects Raster bit-image mode.
     * The value of m selects the mode, as follows:
     * m=0,48 : Normal Mode Vertical=180dpi Horizontal=180dpi
     * m=1,49 : Double-width Mode Vertical=180dpi Horizontal=90dpi
     * m=2,50 : Double-height Mode Vertical=90dpi Horizontal=180dpi
     * m=3,51 : Quadruple Mode Vertical=90dpi Horizontal=90dpi
     * xL, xH, select the number of data bytes (xL+xHx256) in the horizontal direction for the bit
     * image.
     * yL, yH, select the number of data bytes (xL+xHx256) in the vertical direction for the bit
     * image.
     * GS v 0 m xL xH yL yH d1....dk
     *
     * @param m     0≤m≤3, 48≤m≤51
     * @param xL    0≤xL≤255
     * @param xH    0≤xH≤255
     * @param yL    0≤yL≤255
     * @param yH    0≤yH≤8
     * @param image 0≤d≤255
     *              k=(xL + xH x 256) x (yL + yH x 256) (k≠0)
     * @return command
     */
    public static byte[] printRasterBitImage(int m, int xL, int xH, int yL, int yH, byte[] image) {
        byte[] part = new byte[]{GS, 118, 48, (byte) m, (byte) xL, (byte) xH, (byte) yL, (byte) yH};
        byte[] destination = new byte[part.length + image.length];
        System.arraycopy(part, 0, destination, 0, part.length);
        System.arraycopy(image, 0, destination, part.length, image.length);
        return destination;
    }

    /**
     * Set the horizontal size of the bar code.
     * n specifies the bar code width as follows:
     * n=2 :
     * 0.282mm (Module width for Mult-level Bar code),
     * 0.282mm (Thin element width),
     * 0.706mm (Thick element width)
     * n=3 :
     * 0.423mm (Module width for Mult-level Bar code),
     * 0.423mm (Thin element width),
     * 1.129mm (Thick element width)
     * n=4 :
     * 0.564mm (Module width for Mult-level Bar code),
     * 0.564mm (Thin element width),
     * 1.411mm (Thick element width)
     * n=5 :
     * 0.706mm (Module width for Mult-level Bar code),
     * 0.706mm (Thin element width),
     * 1.834mm (Thick element width)
     * n=6 :
     * 0.847mm (Module width for Mult-level Bar code),
     * 0.847mm (Thin element width),
     * 2.258mm (Thick element width)
     * Multi-level bar codes are as follows:
     * UPC-A, UPC-E, JAN13 (EAN13), JAN8 (EAN8), CODE93, CODE128
     * Binary-level bar codes are as follows: CODE39, ITF, CODABAR
     * GS w n
     *
     * @param n 2≤n≤6
     * @return command
     */
    public static byte[] setBarCodeWidth(int n) {
        return new byte[]{GS, 119, (byte) n};
    }

    /**
     * Selects the model for QR Code.
     * (pL + pH x 256) = 4 (pL=4, pH=0)
     * cn = 49
     * fn = 65
     * n1 Function
     * 49 Selects model 1.
     * 50 Selects model 2.
     * GS ( k pL pH cn fn n1 n2
     *
     * @param n1 n1 = 49,50
     * @param n2 n2 = 0
     * @return command
     */
    public static byte[] selectQRCodeModel(int n1, int n2) {
        return new byte[]{GS, 40, 107, 4, 0, 49, 65, (byte) n1, (byte) n2};
    }

    /**
     * Sets the size of the module for QR Code to n dots.
     * GS ( k pL pH cn fn n
     *
     * @param n (pL + pH x 256) = 3 (pL=3, pH=0)
     *          cn = 49
     *          fn = 67
     * @return command
     */
    public static byte[] setQRCodeSizeOfModule(int n) {
        return new byte[]{GS, 40, 107, 3, 0, 49, 67, (byte) n};
    }

    /**
     * Selects the error correction level for QR code.
     * n     fuction                             Recovery Capacity %(approx.)
     * 48    Selects Error correction level L    7
     * 49    Selects Error correction level M    15
     * 50    Selects Error correction level Q    25
     * 51    Selects Error correction level H    30
     * (pL + pH x 256) = 3 (pL=3, pH=0)
     * cn = 49
     * fn = 69
     * GS ( k pL pH cn fn n
     *
     * @param n 47<n<52
     * @return command
     */
    public static byte[] selectQRCodeErrorCorrectionLevel(int n) {
        return new byte[]{GS, 40, 107, 3, 0, 49, 69, (byte) n};
    }

    /**
     * Store the QR Code symbol data (d1…dk) in the symbol storage area.
     * cn = 49
     * fn = 80
     * m = 48
     * GS ( k pL pH cn fn m d1…dk
     *
     * @param pL   0≤pL<256,
     * @param pH   0≤pH<28
     * @param data 0≤d < 255
     *             k = (pL + pH* 256) - 3
     * @return command
     */
    public static byte[] storeQRCodeDataInTheSymbolStorageArea(int pL, int pH, byte[] data) {
        byte[] part = new byte[]{GS, 40, 107, (byte) pL, (byte) pH, 49, 80, 48};
        byte[] destination = new byte[part.length + data.length];
        System.arraycopy(part, 0, destination, 0, part.length);
        System.arraycopy(data, 0, destination, part.length, data.length);
        return destination;
    }

    /**
     * Encodes and prints the QR Code symbol data in the symbol storage area using the process of
     * <Store the data >.
     * (pL + pH x 256) = 3 (pL=3, pH=0)
     * cn = 49
     * fn = 81
     * m = 48
     * GS ( k pL pH cn fn m
     *
     * @return command
     */
    public static byte[] printQRCodeSymbolDataInTheSymbolStorageArea() {
        return new byte[]{GS, 40, 107, 3, 0, 49, 81, 48};
    }

    /**
     * Sets the number of columns of the data area for PDF417.
     * n = 0 specifies automatic processing.
     * (pL + pH x 256) = 3 (pL=3, pH=0)
     * cn = 48
     * fn = 65
     * GS ( k pL pH cn fn n
     *
     * @param n 0≤n≤30
     * @return command
     */
    public static byte[] setNumberOfColumnsOfTheDataAreaForPDF417(int n) {
        return new byte[]{GS, 40, 107, 3, 0, 48, 65, (byte) n};
    }

    /**
     * Sets the number of rows of data area for PDF417.
     * n = 0 specifies automatic processing.
     * (pL + pH x 256) = 3 (pL=3, pH=0)
     * cn = 48
     * fn = 66
     * GS ( k pL pH cn fn n
     *
     * @param n n=0, 3≤n≤90
     * @return command
     */
    public static byte[] setNumberOfRowsOfDataAreaForPDF417(int n) {
        return new byte[]{GS, 40, 107, 3, 0, 48, 66, (byte) n};
    }

    /**
     * Sets the module width of one PDF417 symbol to n dots.
     * (pL + pH x 256) = 3 (pL=3, pH=0)
     * cn = 48
     * fn = 67
     * GS ( k pL pH cn fn n
     *
     * @param n 2≤n≤8
     * @return command
     */
    public static byte[] setModuleWidthOfOnePDF417SymbolDots(int n) {
        return new byte[]{GS, 40, 107, 3, 0, 48, 67, (byte) n};
    }

    /**
     * Sets the module height to [(module width) × n].
     * (pL + pH x 256) = 3 (pL=3, pH=0)
     * cn = 48
     * fn = 68
     * GS ( k pL pH cn fn n
     *
     * @param n 2≤n≤8
     * @return command
     */
    public static byte[] setPDF417ModuleHeight(int n) {
        return new byte[]{GS, 40, 107, 3, 0, 48, 68, (byte) n};
    }

    /**
     * Sets the error correction level for PDF417 symbols.
     * (pL + pH x 256) = 4 (pL=4, pH=0)
     * cn = 48
     * fn = 69
     * GS ( k pL pH cn fn m n
     *
     * @param m m = 48,49
     * @param n 48≤n≤56 (when m=48 is specified)
     *          1≤n≤40 (when m=49 is specified)
     * @return command
     */
    public static byte[] setErrorCorrectionLevelForPDF417Symbols(int m, int n) {
        return new byte[]{GS, 40, 107, 4, 0, 48, 69, (byte) m, (byte) n};
    }

    /**
     * Stores symbol data (d1...dk) in the PDF417 symbol storage area.
     * Bytes of ((pL + pH × 256) - 3) after m (d1…dk) are processed as symbol data.
     * 4≤(pL + pH x 256) ≤65535 (0≤pL≤255, 0≤pH≤255)
     * cn = 48
     * fn = 80
     * m = 48
     * GS ( k pL pH cn fn m d1…dk
     *
     * @param pL   0≤pL≤255
     * @param pH   0≤pH≤255
     * @param data 0≤d≤255
     *             k = (pL + pH*256) - 3
     * @return command
     */
    public static byte[] storeSymbolDataInThePDF417SymbolStorageArea(int pL, int pH, byte[] data) {
        byte[] part = new byte[]{GS, 40, 107, (byte) pL, (byte) pH, 48, 80, 48};
        byte[] destination = new byte[part.length + data.length];
        System.arraycopy(part, 0, destination, 0, part.length);
        System.arraycopy(data, 0, destination, part.length, data.length);
        return destination;
    }

    /**
     * Prints the PDF417 symbol data in the symbol storage area.
     * (pL + pH* 256) = 4 (pL=4, pH=0)
     * cn = 48
     * fn = 81
     * m = 48
     * GS ( k pL pH cn fn m
     *
     * @return command
     */
    public static byte[] printPDF417SymbolDataInTheSymbolStorageArea() {
        return new byte[]{GS, 40, 107, 4, 0, 48, 81, 48};
    }
}
