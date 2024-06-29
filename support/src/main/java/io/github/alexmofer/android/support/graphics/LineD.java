package io.github.alexmofer.android.support.graphics;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * 直线
 * Created by Alex on 2022/11/26.
 */
public class LineD implements Parcelable {
    public static final Creator<LineD> CREATOR = new Creator<LineD>() {
        /**
         * Return a new point from the data in the specified parcel.
         */
        @Override
        public LineD createFromParcel(Parcel in) {
            LineD r = new LineD(0, 0);
            r.readFromParcel(in);
            return r;
        }

        /**
         * Return an array of rectangles of the specified size.
         */
        @Override
        public LineD[] newArray(int size) {
            return new LineD[size];
        }
    };
    public double k;
    public double b;

    public LineD(double k, double b) {
        this.k = k;
        this.b = b;
    }

    /**
     * 点斜式
     *
     * @param k 斜率
     * @param x 直线经过的点的X轴坐标
     * @param y 直线经过的点的Y轴坐标
     */
    public LineD(double k, double x, double y) {
        this.k = k;
        b = y - k * x;
    }

    /**
     * 两点式
     *
     * @param x1 直线经过的点1的X轴坐标
     * @param y1 直线经过的点1的Y轴坐标
     * @param x2 直线经过的点2的X轴坐标
     * @param y2 直线经过的点2的Y轴坐标
     */
    public LineD(double x1, double y1, double x2, double y2) {
        final double v = x1 - x2;
        if (v != 0) {
            this.k = (y1 - y2) / v;
            this.b = (y1 - x1 * k);
        } else {
            k = Double.NaN;
            b = x1;
        }
    }

    /**
     * 直线垂直于X轴
     *
     * @param x X轴值
     */
    public LineD(double x) {
        this.k = Double.NaN;
        b = x;
    }


    /**
     * 判断是否垂直于X轴（斜率不存在）
     *
     * @return 垂直于X轴时返回true
     */
    public boolean isPerpendicularToX() {
        return Double.isNaN(k);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LineD l = (LineD) o;

        if (Double.isNaN(l.k)) {
            if (Double.isNaN(k)) {
                return Double.compare(l.b, b) == 0;
            } else {
                return false;
            }
        }

        if (Double.compare(l.k, k) != 0) return false;
        return Double.compare(l.b, b) == 0;
    }

    @Override
    public int hashCode() {
        int result = Double.valueOf(k).hashCode();
        result = 31 * result + Double.valueOf(b).hashCode();
        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return "LineD(" + k + ", " + b + ")";
    }

    /**
     * Parcelable interface methods
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Write this point to the specified parcel. To restore a point from
     * a parcel, use readFromParcel()
     *
     * @param out The parcel to write the point's coordinates into
     */
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeDouble(k);
        out.writeDouble(b);
    }

    /**
     * Set the point's coordinates from the data stored in the specified
     * parcel. To write a point to a parcel, call writeToParcel().
     *
     * @param in The parcel to read the point's coordinates from
     */
    public void readFromParcel(@NonNull Parcel in) {
        k = in.readDouble();
        b = in.readDouble();
    }
}
