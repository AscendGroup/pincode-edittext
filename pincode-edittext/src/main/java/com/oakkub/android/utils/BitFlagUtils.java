package com.oakkub.android.utils;

/**
 * Created by metas on 5/3/2017 AD.
 */

public class BitFlagUtils {

	public static boolean isFlagSet(int flags, int value) {
		return (flags & value) == value;
	}

	public static int setFlag(int flags, int value) {
		return flags | value;
	}

	public static int unsetFlag(int flags, int value) {
		return flags & ~value;
	}

}
