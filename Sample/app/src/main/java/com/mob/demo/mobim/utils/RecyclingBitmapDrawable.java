package com.mob.demo.mobim.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

public class RecyclingBitmapDrawable extends BitmapDrawable {
	static final String TAG = "CountingBitmapDrawable";

	private int cacheRefCount = 0;
	private int displayRefCount = 0;

	private boolean hasBeenDisplayed;

	public RecyclingBitmapDrawable(Resources res, Bitmap bitmap) {
		super(res, bitmap);
	}

	/**
	 * Notify the drawable that the displayed state has changed. Internally a
	 * count is kept so that the drawable knows when it is no longer being
	 * displayed.
	 *
	 * @param isDisplayed - Whether the drawable is being displayed or not
	 */
	public void setIsDisplayed(boolean isDisplayed) {
		// BEGIN_INCLUDE(set_is_displayed)
		synchronized (this) {
			if (isDisplayed) {
				displayRefCount++;
				hasBeenDisplayed = true;
			} else {
				displayRefCount--;
			}
		}

		// Check to see if recycle() can be called
		checkState();
		// END_INCLUDE(set_is_displayed)
	}

	/**
	 * Notify the drawable that the cache state has changed. Internally a count
	 * is kept so that the drawable knows when it is no longer being cached.
	 *
	 * @param isCached - Whether the drawable is being cached or not
	 */
	public void setIsCached(boolean isCached) {
		// BEGIN_INCLUDE(set_is_cached)
		synchronized (this) {
			if (isCached) {
				cacheRefCount++;
			} else {
				cacheRefCount--;
			}
		}

		// Check to see if recycle() can be called
		checkState();
		// END_INCLUDE(set_is_cached)
	}

	private synchronized void checkState() {
		// BEGIN_INCLUDE(check_state)
		// If the drawable cache and display ref counts = 0, and this drawable
		// has been displayed, then recycle
		if (cacheRefCount <= 0 && displayRefCount <= 0 && hasBeenDisplayed
				&& hasValidBitmap()) {
//			if (BuildConfig.DEBUG) {
//				Log.d(TAG, "No longer being used or cached so recycling. "
//						+ toString());
//			}

			getBitmap().recycle();
		}
		// END_INCLUDE(check_state)
	}

	private synchronized boolean hasValidBitmap() {
		Bitmap bitmap = getBitmap();
		return bitmap != null && !bitmap.isRecycled();
	}

}
