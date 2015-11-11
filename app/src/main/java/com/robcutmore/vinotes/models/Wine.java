package com.robcutmore.vinotes.models;


import android.os.Parcel;
import android.os.Parcelable;


/**
 * Represents a wine.
 */
public class Wine implements Parcelable {

    private Long id;
    private Winery winery;
    private String name;
    private int vintage;

    /**
     * Constructor.
     *
     * @param winery  winery of wine
     * @param name  name of wine
     * @param vintage  vintage of wine
     */
    public Wine(final Winery winery, final String name, final int vintage) {
        this.winery = winery;
        this.name = name;
        this.vintage = vintage;
        this.id = null;
    }

    /**
     * Constructor.
     *
     * @param id  ID of wine
     * @param winery  winery of wine
     * @param name  name of wine
     * @param vintage  vintage of wine
     */
    public Wine(final long id, final Winery winery, final String name, final int vintage) {
        this(winery, name, vintage);
        this.id = id;
    }

    /**
     * Gets ID of wine.
     *
     * @return ID of wine (null if not set)
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Sets ID of wine.
     * ID can only be set once so subsequent attempts to set ID will default to first ID set.
     *
     * @param id  ID to set for wine
     */
    public void setId(final long id) {
        // Only allow id to be set once.
        this.id = this.id == null ? id : this.id;
    }

    /**
     * Gets winery of wine.
     *
     * @return winery of wine
     */
    public Winery getWinery() {
        return this.winery;
    }

    /**
     * Gets name of wine.
     *
     * @return name of wine
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets vintage of wine.
     *
     * @return vintage of wine
     */
    public int getVintage() {
        return this.vintage;
    }

    // Object methods

    /**
     * Compares wine with other object to see if they are the same.
     *
     * @param other  other object to compare to
     * @return true if the same otherwise false
     */
    @Override
    public boolean equals(Object other) {
        // Return false if other object isn't a wine object.
        if (!(other instanceof Wine)) {
            return false;
        }

        // Compare wine IDs.
        Wine otherWine = (Wine) other;
        return this.id.equals(otherWine.id);
    }

    /**
     * Gets string representation of wine.
     *
     * @return string representation of wine
     */
    @Override
    public String toString() {
        return String.format("%s %s (%d)", this.winery.toString(), this.name, this.vintage);
    }

    // Parcelable methods

    /**
     * Parcelable constructor.
     *
     * @param in  parcel containing wine data
     */
    private Wine(final Parcel in) {
        this.id = in.readLong();
        this.winery = in.readParcelable(Winery.class.getClassLoader());
        this.name = in.readString();
        this.vintage = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Writes wine data to parcel.
     *
     * @param out  parcel to write wine data to
     * @param flags  additional flags about how object should be written
     */
    @Override
    public void writeToParcel(final Parcel out, final int flags) {
        out.writeLong(this.id);
        out.writeParcelable(this.winery, flags);
        out.writeString(this.name);
        out.writeInt(this.vintage);
    }

    public static final Parcelable.Creator CREATOR = new Creator() {
        /**
         * Creates wine using parcel.
         *
         * @param source  parcel containing wine data
         * @return wine
         */
        @Override
        public Wine createFromParcel(final Parcel source) {
            return new Wine(source);
        }

        /**
         * Creates array of wines.
         *
         * @param size  size of array
         * @return array of wines
         */
        @Override
        public Wine[] newArray(final int size) {
            return new Wine[size];
        }
    };

}
