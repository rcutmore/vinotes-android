package com.robcutmore.vinotes.model;


import android.os.Parcel;
import android.os.Parcelable;


/**
 * Represents characteristics of a wine.
 * Describes a wine's color, nose, taste, and finish.
 */
public class Trait implements Parcelable {

    private Long id;
    private String name;

    /**
     * Constructor.
     *
     * @param name  name of trait
     */
    public Trait(final String name) {
        this.name = name;
        this.id = null;
    }

    /**
     * Constructor.
     *
     * @param id  ID of trait
     * @param name  name of trait
     */
    public Trait(final long id, final String name) {
        this(name);
        this.id = id;
    }

    /**
     * Gets ID of trait.
     *
     * @return ID of trait (null if not set)
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Sets ID of trait.
     * ID can only be set once so subsequent attempts to set ID will default to first ID set.
     *
     * @param id  ID to set for trait
     */
    public void setId(final long id) {
        // Only allow id to be set once.
        this.id = this.id == null ? id : this.id;
    }

    /**
     * Gets name of trait.
     *
     * @return name of trait
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets string representation of trait.
     *
     * @return string representation of trait
     */
    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Compares trait with other object to see if they are the same.
     *
     * @param other  other object to compare to
     * @return true if the same otherwise false
     */
    @Override
    public boolean equals(Object other) {
        // Return false if other object isn't a Trait object.
        if (!(other instanceof Trait)) {
            return false;
        }

        // Compare trait IDs.
        Trait otherTrait = (Trait) other;
        return this.id.equals(otherTrait.id);
    }

    // Parcelable methods

    /**
     * Parcelable constructor.
     *
     * @param in  Parcel containing trait data
     */
    private Trait(final Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Writes trait data to parcel.
     *
     * @param out  parcel containing trait data
     * @param flags  additional flags about how object should be written
     */
    @Override
    public void writeToParcel(final Parcel out, final int flags) {
        out.writeLong(this.id);
        out.writeString(this.name);
    }

    public static final Parcelable.Creator CREATOR = new Creator() {
        /**
         * Creates trait using parcel.
         *
         * @param source  parcel containing trait data
         * @return trait
         */
        @Override
        public Trait createFromParcel(final Parcel source) {
            return new Trait(source);
        }

        /**
         * Creates array of traits.
         *
         * @param size  size of array
         * @return array of traits
         */
        @Override
        public Trait[] newArray(final int size) {
            return new Trait[size];
        }
    };

}
