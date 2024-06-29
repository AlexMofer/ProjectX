package io.github.alexmofer.android.support.graphics;

import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * PointD holds two float coordinates
 * Created by Alex on 2022/11/26.
 */
public class PointD implements Parcelable {
    public static final Creator<PointD> CREATOR = new Creator<PointD>() {
        /**
         * Return a new point from the data in the specified parcel.
         */
        @Override
        public PointD createFromParcel(Parcel in) {
            PointD r = new PointD();
            r.readFromParcel(in);
            return r;
        }

        /**
         * Return an array of rectangles of the specified size.
         */
        @Override
        public PointD[] newArray(int size) {
            return new PointD[size];
        }
    };
    public double x;
    public double y;

    public PointD() {
    }

    public PointD(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public PointD(@NonNull PointF p) {
        this.x = p.x;
        this.y = p.y;
    }

    /**
     * Create a new PointF initialized with the values in the specified
     * PointD (which is left unmodified).
     *
     * @param p The point whose values are copied into the new
     *          point.
     */
    public PointD(@NonNull PointD p) {
        this.x = p.x;
        this.y = p.y;
    }

    /**
     * Returns the euclidian distance from (0,0) to (x,y)
     */
    public static double length(double x, double y) {
        return Math.hypot(x, y);
    }

    /**
     * Set the point's x and y coordinates
     */
    public final void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Set the point's x and y coordinates to the coordinates of p
     */
    public final void set(@NonNull PointD p) {
        this.x = p.x;
        this.y = p.y;
    }

    public final void negate() {
        x = -x;
        y = -y;
    }

    public final void offset(double dx, double dy) {
        x += dx;
        y += dy;
    }

    /**
     * Returns true if the point's coordinates equal (x,y)
     */
    public final boolean equals(double x, double y) {
        return this.x == x && this.y == y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PointD p = (PointD) o;

        if (Double.compare(p.x, x) != 0) return false;
        return Double.compare(p.y, y) == 0;
    }

    @Override
    public int hashCode() {
        int result = Double.valueOf(x).hashCode();
        result = 31 * result + Double.valueOf(y).hashCode();
        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return "PointD(" + x + ", " + y + ")";
    }

    /**
     * Return the euclidian distance from (0,0) to the point
     */
    public final double length() {
        return length(x, y);
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
        out.writeDouble(x);
        out.writeDouble(y);
    }

    /**
     * Set the point's coordinates from the data stored in the specified
     * parcel. To write a point to a parcel, call writeToParcel().
     *
     * @param in The parcel to read the point's coordinates from
     */
    public void readFromParcel(@NonNull Parcel in) {
        x = in.readDouble();
        y = in.readDouble();
    }
}