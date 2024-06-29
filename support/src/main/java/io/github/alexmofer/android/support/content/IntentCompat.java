package io.github.alexmofer.android.support.content;

import android.content.Intent;
import android.os.Build;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Intent兼容器
 * Created by Alex on 2023/2/1.
 */
public class IntentCompat {

    private IntentCompat() {
        //no instance
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name  The name of the desired item.
     * @param clazz The type of the object expected.
     * @return the value of an item previously added with putExtra(),
     * or null if no Parcelable value was found.
     * @see Intent#putExtra(String, Parcelable)
     */
    @Nullable
    public static <T> T getParcelableExtra(@NonNull Intent intent,
                                           @Nullable String name,
                                           @NonNull Class<T> clazz) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return intent.getParcelableExtra(name, clazz);
        } else {
            //noinspection unchecked
            return intent.getParcelableExtra(name);
        }
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name  The name of the desired item.
     * @param clazz The type of the items inside the array. This is only verified when unparceling.
     * @return the value of an item previously added with putExtra(),
     * or null if no Parcelable[] value was found.
     * @see Intent#putExtra(String, Parcelable[])
     */
    @Nullable
    public static <T> T[] getParcelableArrayExtra(@NonNull Intent intent,
                                                  @Nullable String name,
                                                  @NonNull Class<T> clazz) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return intent.getParcelableArrayExtra(name, clazz);
        } else {
            //noinspection unchecked
            return (T[]) intent.getParcelableArrayExtra(name);
        }
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name  The name of the desired item.
     * @param clazz The type of the items inside the array list. This is only verified when
     *              unparceling.
     * @return the value of an item previously added with
     * putParcelableArrayListExtra(), or null if no
     * ArrayList<Parcelable> value was found.
     * @see Intent#putParcelableArrayListExtra(String, ArrayList)
     */
    @Nullable
    public static <T> ArrayList<T> getParcelableArrayListExtra(@NonNull Intent intent,
                                                               @Nullable String name,
                                                               @NonNull Class<? extends T> clazz) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return intent.getParcelableArrayListExtra(name, clazz);
        } else {
            //noinspection unchecked
            return (ArrayList<T>) intent.getParcelableArrayListExtra(name);
        }
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name  The name of the desired item.
     * @param clazz The type of the object expected.
     * @return the value of an item previously added with putExtra(),
     * or null if no Serializable value was found.
     * @see Intent#putExtra(String, Serializable)
     */
    @Nullable
    public static <T extends Serializable> T getSerializableExtra(@NonNull Intent intent,
                                                                  @Nullable String name,
                                                                  @NonNull Class<T> clazz) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return intent.getSerializableExtra(name, clazz);
        } else {
            //noinspection unchecked
            return (T) intent.getSerializableExtra(name);
        }
    }
}
