package am.widget.floatingactionmode.impl;

import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Xiang Zhicheng on 2018/11/14.
 */
final class Size implements Parcelable {
    public int width;
    public int height;

    public static final Parcelable.Creator<Point> CREATOR = new Parcelable.Creator<Point>() {
        /**
         * Return a new point from the data in the specified parcel.
         */
        public Point createFromParcel(Parcel in) {
            Point r = new Point();
            r.readFromParcel(in);
            return r;
        }

        /**
         * Return an array of rectangles of the specified size.
         */
        public Point[] newArray(int size) {
            return new Point[size];
        }
    };

    public Size(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Size(Size src) {
        this.width = src.width;
        this.height = src.height;
    }

    /**
     * Set the point's width and height coordinates
     */
    public void set(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Negate the point's coordinates
     */
    public final void negate() {
        width = -width;
        height = -height;
    }

    /**
     * Offset the point's coordinates by dx, dy
     */
    public final void offset(int dx, int dy) {
        width += dx;
        height += dy;
    }

    /**
     * Returns true if the point's coordinates equal (width,height)
     */
    public final boolean equals(int width, int height) {
        return this.width == width && this.height == height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Size size = (Size) o;

        if (width != size.width) return false;
        return height == size.height;
    }

    @Override
    public int hashCode() {
        int result = width;
        result = 31 * result + height;
        return result;
    }

    @Override
    public String toString() {
        return "Size(" + width + ", " + height + ")";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(width);
        out.writeInt(height);
    }

    public Size() {
    }

    public void readFromParcel(Parcel in) {
        width = in.readInt();
        height = in.readInt();
    }
}
