package com.robcutmore.vinotes.models;


import android.os.Parcel;
import android.os.Parcelable;


/**
 * Represents a winery.
 */
public class Winery implements Parcelable {

    private Long id;
    private String name;

    /**
     * Constructor.
     *
     * @param name  name of winery
     */
    public Winery(final String name) {
        this.name = name;
        this.id = null;
    }

    /**
     * Constructor.
     *
     * @param id  ID of winery
     * @param name  name of winery
     */
    public Winery(final long id, final String name) {
        this(name);
        this.id = id;
    }

    /**
     * Gets ID of winery.
     *
     * @return ID of winery (null if not set)
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Sets ID of winery.
     * ID can only be set once so subsequent attempts to set ID will default to first ID set.
     *
     * @param id  ID to set for winery
     */
    public void setId(final long id) {
        // Only allow id to be set once.
        this.id = this.id == null ? id : this.id;
    }

    /**
     * Gets name of winery.
     *
     * @return name of winery
     */
    public String getName() {
        return this.name;
    }

    // Object methods

    /**
     * Compares winery with other object to see if they are the same.
     *
     * @param other  other object to compare to
     * @return true if the same otherwise false
     */
    @Override
    public boolean equals(Object other) {
        // Return false if other object isn't a winery object.
        if (!(other instanceof Winery)) {
            return false;
        }

        // Compare winery IDs.
        Winery otherWinery = (Winery) other;
        return this.id.equals(otherWinery.id);
    }

    /**
     * Gets string representation of winery.
     *
     * @return string representation of winery
     */
    @Override
    public String toString() {
        return this.name;
    }

    // Parcelable methods

    /**
     * Parcelable constructor.
     *
     * @param in  Parcel containing winery data
     */
    private Winery(final Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Writes winery data to parcel.
     *
     * @param out  parcel to write winery data to
     * @param flags  additional flags about how object should be written
     */
    @Override
    public void writeToParcel(final Parcel out, final int flags) {
        out.writeLong(this.id);
        out.writeString(this.name);
    }

    public static final Parcelable.Creator CREATOR = new Creator() {
        /**
         * Creates winery using parcel.
         *
         * @param source  parcel containing winery data
         * @return winery
         */
        @Override
        public Winery createFromParcel(Parcel source) {
            return new Winery(source);
        }

        /**
         * Creates array of wineries.
         *
         * @param size  size of array
         * @return array of wineries
         */
        @Override
        public Winery[] newArray(int size) {
            return new Winery[size];
        }
    };

}
