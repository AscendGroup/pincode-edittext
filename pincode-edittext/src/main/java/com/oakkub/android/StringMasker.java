package com.oakkub.android;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by metas on 5/1/2017 AD.
 */

public class StringMasker implements Parcelable {

	private char mMask;
	private boolean mShouldShowMask;

	public StringMasker(char mask) {
		mMask = mask;
	}

	protected StringMasker(Parcel in) {
		mMask = (char) in.readInt();
		mShouldShowMask = in.readByte() != 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt((int) mMask);
		dest.writeByte((byte) (mShouldShowMask ? 1 : 0));
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<StringMasker> CREATOR = new Creator<StringMasker>() {
		@Override
		public StringMasker createFromParcel(Parcel in) {
			return new StringMasker(in);
		}

		@Override
		public StringMasker[] newArray(int size) {
			return new StringMasker[size];
		}
	};

	public boolean isShowMask() {
		return mShouldShowMask;
	}

	public void setShouldShowMask(boolean shouldShowMask) {
		mShouldShowMask = shouldShowMask;
	}

	public CharSequence getMaskText(@NonNull CharSequence source) {
		if (!mShouldShowMask) {
			return source;
		}

		int size = source.length();
		StringBuilder builder = new StringBuilder(size);
		for (int i = 0; i < size; i++) {
			builder.append(mMask);
		}
		return builder;
	}

	public void setMask(char mask) {
		mMask = mask;
	}

	public char getMask() {
		return mMask;
	}
}
