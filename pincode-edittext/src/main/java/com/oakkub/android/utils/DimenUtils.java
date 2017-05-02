package com.oakkub.android.utils;

import android.content.Context;

/**
 * Created by metas on 3/31/2017 AD.
 */

public class DimenUtils {

	public static int dpToPx(Context context, int dp) {
		return (int) (dp * context.getResources().getDisplayMetrics().density);
	}

	public static int pxToDp(Context context, int px) {
		return (int) (px / context.getResources().getDisplayMetrics().density);
	}

}
