package me.marko.server

import android.os.Parcel
import android.os.Parcelable

class PersonInfo() : Parcelable {

    var name: String? = null
    var age: Int = 0

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        age = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(age)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PersonInfo> {
        override fun createFromParcel(parcel: Parcel): PersonInfo {
            return PersonInfo(parcel)
        }

        override fun newArray(size: Int): Array<PersonInfo?> {
            return arrayOfNulls(size)
        }
    }
}