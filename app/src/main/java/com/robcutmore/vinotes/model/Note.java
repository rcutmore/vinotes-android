package com.robcutmore.vinotes.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.robcutmore.vinotes.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;


/**
 * Represents a wine tasting note.
 */
public class Note implements Parcelable {

    private Long id;
    private Wine wine;
    private Date tasted;
    private ArrayList<Trait> colorTraits = new ArrayList<>();
    private ArrayList<Trait> noseTraits = new ArrayList<>();
    private ArrayList<Trait> tasteTraits = new ArrayList<>();
    private ArrayList<Trait> finishTraits = new ArrayList<>();
    private Integer rating;

    /**
     * Constructor.
     *
     * @param wine  wine of note
     */
    public Note(final Wine wine) {
        this.wine = wine;
        this.id = null;
        this.tasted = null;
        this.rating = null;
    }

    /**
     * Constructor.
     *
     * @param id  ID of note
     * @param wine  wine of note
     */
    public Note(final long id, final Wine wine) {
        this(wine);
        this.id = id;
    }

    /**
     * Constructor.
     *
     * @param id  ID of note
     * @param wine  wine of note
     * @param tasted  tasting date of note
     * @param rating  rating of wine
     */
    public Note(final long id, final Wine wine, final Date tasted, final Integer rating) {
        this(id, wine);
        this.tasted = tasted;
        this.rating = rating;
    }

    /**
     * Gets ID of tasting note.
     *
     * @return ID of note (null if not set)
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Sets ID of tasting note.
     * ID can only be set once so subsequent attempts to set ID will default to first ID set.
     *
     * @param id  ID to set for note
     */
    public void setId(final long id) {
        // Only allow id to be set once.
        this.id = this.id == null ? id : this.id;
    }

    /**
     * Gets wine of tasting note.
     *
     * @return wine
     */
    public Wine getWine() {
        return this.wine;
    }

    /**
     * Gets date of tasting note.
     *
     * @return tasting date
     */
    public Date getTasted() {
        return this.tasted;
    }

    /**
     * Sets tasting date.
     *
     * @param tasted  tasting date to set
     */
    public void setTasted(final Date tasted) {
        this.tasted = tasted;
    }

    /**
     * Adds given trait to list of color traits.
     *
     * @param colorTrait  trait to add
     */
    public void addColorTrait(final Trait colorTrait) {
        this.colorTraits.add(colorTrait);
    }

    /**
     * Gets list of color traits of wine.
     *
     * @return list of color traits
     */
    public ArrayList<Trait> getColorTraits() {
        return this.colorTraits;
    }

    /**
     * Sets color traits of wine.
     *
     * @param colorTraits  list of color traits to set
     */
    public void setColorTraits(final ArrayList<Trait> colorTraits) {
        this.colorTraits = colorTraits;
    }

    /**
     * Adds given trait to list of nose traits.
     *
     * @param noseTrait  trait to add
     */
    public void addNoseTrait(final Trait noseTrait) {
        this.noseTraits.add(noseTrait);
    }

    /**
     * Gets list of nose traits of wine.
     *
     * @return list of nose traits
     */
    public ArrayList<Trait> getNoseTraits() {
        return this.noseTraits;
    }

    /**
     * Sets nose traits of wine.
     *
     * @param noseTraits  list of note traits to set
     */
    public void setNoseTraits(final ArrayList<Trait> noseTraits) {
        this.noseTraits = noseTraits;
    }

    /**
     * Adds given trait to list of taste traits.
     *
     * @param tasteTrait  trait to add
     */
    public void addTasteTrait(final Trait tasteTrait) {
        this.tasteTraits.add(tasteTrait);
    }

    /**
     * Gets list of taste traits of wine.
     *
     * @return list of taste traits
     */
    public ArrayList<Trait> getTasteTraits() {
        return this.tasteTraits;
    }

    /**
     * Sets taste traits of wine.
     *
     * @param tasteTraits  list of taste traits to set
     */
    public void setTasteTraits(final ArrayList<Trait> tasteTraits) {
        this.tasteTraits = tasteTraits;
    }

    /**
     * Adds given trait to list of finish traits.
     *
     * @param finishTrait  trait to add
     */
    public void addFinishTrait(final Trait finishTrait) {
        this.finishTraits.add(finishTrait);
    }

    /**
     * Gets list of finish traits of wine.
     *
     * @return list of finish traits
     */
    public ArrayList<Trait> getFinishTraits() {
        return this.finishTraits;
    }

    /**
     * Sets finish traits of wine.
     *
     * @param finishTraits  list of finish traits to set
     */
    public void setFinishTraits(final ArrayList<Trait> finishTraits) {
        this.finishTraits = finishTraits;
    }

    /**
     * Gets rating of wine.
     *
     * @return rating of wine
     */
    public Integer getRating() {
        return this.rating;
    }

    /**
     * Sets rating of wine.
     * Rating set to min (1) or max (5) if input is outside this range.
     *
     * @param rating  rating to set
     */
    public void setRating(final int rating) {
        final int minRating = 1;
        final int maxRating = 5;

        if (rating < minRating) {
            this.rating = minRating;
        } else if (rating > maxRating) {
            this.rating = maxRating;
        } else {
            this.rating = rating;
        }
    }

    /**
     * Gets string representation of tasting note.
     *
     * @return string representation of note
     */
    @Override
    public String toString() {
        String tastedDate = DateUtils.convertDateToString(this.tasted);
        return String.format("%s\n%d stars on %s", this.wine.toString(), this.rating, tastedDate);
    }

    // Parcelable methods

    /**
     * Parcelable constructor.
     *
     * @param in  parcel containing note data
     */
    private Note(final Parcel in) {
        this.id = in.readLong();
        this.wine = in.readParcelable(Wine.class.getClassLoader());
        this.tasted = DateUtils.convertTimestampToDate(in.readLong());
        this.colorTraits = in.readArrayList(Trait.class.getClassLoader());
        this.noseTraits = in.readArrayList(Trait.class.getClassLoader());
        this.tasteTraits = in.readArrayList(Trait.class.getClassLoader());
        this.finishTraits = in.readArrayList(Trait.class.getClassLoader());
        this.rating = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Writes note data to parcel.
     *
     * @param out  parcel to write note data to
     * @param flags  additional flags about how object should be written
     */
    @Override
    public void writeToParcel(final Parcel out, final int flags) {
        out.writeLong(this.id);
        out.writeParcelable(this.wine, flags);
        out.writeLong(DateUtils.convertDateToTimestamp(this.tasted));
        out.writeList(this.colorTraits);
        out.writeList(this.noseTraits);
        out.writeList(this.tasteTraits);
        out.writeList(this.finishTraits);
        out.writeInt(this.rating);
    }

    public static final Parcelable.Creator CREATOR = new Creator() {
        /**
         * Creates note using parcel.
         *
         * @param source  parcel containing note data
         * @return note
         */
        @Override
        public Note createFromParcel(Parcel source) {
            return new Note(source);
        }

        /**
         * Creates array of notes.
         *
         * @param size  size of array
         * @return array of notes
         */
        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

}
