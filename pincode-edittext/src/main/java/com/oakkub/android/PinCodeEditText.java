package com.oakkub.android;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputFilter;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.oakkub.android.utils.BitFlagUtils;
import com.oakkub.android.utils.DimenUtils;

import java.util.Arrays;

/**
 * Created by metas on 3/31/2017 AD.
 */

public class PinCodeEditText extends AppCompatEditText {

	private static final String TAG = PinCodeEditText.class.getSimpleName();

	/**
	 * android: namespace
	 */
	private static final String XML_NAMESPACE_ANDROID = "http://schemas.android.com/apk/res/android";

	/**
	 * Keys for save state variables
	 */
	private static final String KEY_SUPER_STATE = "SUPER_STATE";
	private static final String KEY_CHARACTER_NUMBER = "CHARACTER_NUMBER";
	private static final String KEY_GAP_WIDTH = "GAP_WIDTH";
	private static final String KEY_SHOULD_HIGHLIGHT_NEXT_PIN = "SHOULD_HIGHLIGHT_NEXT_PIN";
	private static final String KEY_SHOULD_SHOW_AS_PASSWORD = "SHOULD_SHOW_AS_PASSWORD";
	private static final String KEY_STRING_MASKER = "STRING_MASKER";

	/**
	 * Default colors
	 */
	private static final int DEFAULT_HIGHLIGHT_COLOR_BACKGROUND = 0xFFD5D5D5;
	private static final int DEFAULT_COLOR_BACKGROUND_PIN = 0xFFF5F5F5;
	private static final int DEFAULT_HIGHLIGHT_COLOR_BORDER = 0xFFC5C5C5;
	private static final int DEFAULT_COLOR_BORDER = 0xFFD5D5D5;

	private static final int DEFAULT_LINE_STROKE = 1;
	private static final int DEFAULT_GAP_WIDTH = 24;
	private static final int DEFAULT_MAX_LENGTH = 4;

	private int[][] mPinCodeColorStates = new int[][]{
			new int[]{android.R.attr.state_selected}, // State for the highlighted pin
			new int[]{0} // Default state
	};

	private int mHighlightColorBackground = DEFAULT_HIGHLIGHT_COLOR_BACKGROUND;
	private int mColorBackgroundPin = DEFAULT_COLOR_BACKGROUND_PIN;
	private int mHighlightColorBorder = DEFAULT_HIGHLIGHT_COLOR_BORDER;
	private int mColorBorder = DEFAULT_COLOR_BORDER;

	private int mCharacterNumber;
	private int mGapWidth;
	private boolean mHighlightNextPin;
	private boolean mShowAsPassword;

	private Paint mBackgroundPaint;
	private Paint mBorderPaint;

	private ColorStateList mBackgroundColorState;
	private ColorStateList mBorderColorState;

	View.OnClickListener mOnClickListener;
	TextView.OnEditorActionListener mOnEditorActionListener;
	StringMasker mStringMasker;

	public PinCodeEditText(Context context) {
		super(context);
		init(null, 0);
	}

	public PinCodeEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs, 0);
	}

	public PinCodeEditText(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(attrs, defStyleAttr);
	}

	private void init(AttributeSet attrs, int defStyleAttr) {
		initDefault(attrs, defStyleAttr);
	}

	private void initDefault(AttributeSet attrs, int defStyleAttr) {
		mGapWidth = DimenUtils.dpToPx(getContext(), DEFAULT_GAP_WIDTH);
		mStringMasker = new StringMasker('\u2022'); // Dot
		mCharacterNumber = DEFAULT_MAX_LENGTH;
		mShowAsPassword = true;
		mHighlightNextPin = true;

		if (attrs != null) {
			initAttrs(attrs, defStyleAttr);
			mCharacterNumber = attrs.getAttributeIntValue(XML_NAMESPACE_ANDROID, "maxLength", DEFAULT_MAX_LENGTH);
		}

		initPaints();
		disableCopyAndPaste();
		setBackgroundColor(Color.TRANSPARENT);

		// Remove cursor
		setCursorVisible(false);

		initClickListener();
		initOnEditorActionListener();
		initColorStates();

		setMaxLength(mCharacterNumber);
		setDefaultInputType();
	}

	private void initAttrs(AttributeSet attrs, int defStyleAttr) {
		TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PinCodeEditText, defStyleAttr, 0);
		try {
			mHighlightColorBackground = a.getColor(R.styleable.PinCodeEditText_pinNextPinBackgroundColor, DEFAULT_HIGHLIGHT_COLOR_BACKGROUND);
			mColorBackgroundPin = a.getColor(R.styleable.PinCodeEditText_pinBackgroundColor, DEFAULT_COLOR_BACKGROUND_PIN);
			mHighlightColorBorder = a.getColor(R.styleable.PinCodeEditText_pinNextPinBorderColor, DEFAULT_HIGHLIGHT_COLOR_BORDER);
			mColorBorder = a.getColor(R.styleable.PinCodeEditText_pinBorderColor, DEFAULT_COLOR_BORDER);
			mHighlightNextPin = a.getBoolean(R.styleable.PinCodeEditText_pinShowHighlightNextPin, true);
			mShowAsPassword = a.getBoolean(R.styleable.PinCodeEditText_pinShowAsPassword, true);
			mGapWidth = a.getDimensionPixelSize(R.styleable.PinCodeEditText_pinGapWidth, DimenUtils.dpToPx(getContext(), DEFAULT_GAP_WIDTH));

			String maskCharacter = a.getString(R.styleable.PinCodeEditText_pinMaskCharacter);
			setMaskCharacterFromStyleable(maskCharacter);
		} finally {
			a.recycle();
		}

		mStringMasker.setShouldShowMask(mShowAsPassword);
	}

	private void setMaskCharacterFromStyleable(String maskCharacter) {
		if (maskCharacter == null) return;

		if (maskCharacter.length() > 1) {
			throw new IllegalArgumentException("PinCodeEditText pinMaskCharacter can only have 1 character");
		}
		mStringMasker.setMask(maskCharacter.charAt(0));
	}

	private void initPaints() {
		int strokeWidth = DimenUtils.dpToPx(getContext(), DEFAULT_LINE_STROKE);

		mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mBackgroundPaint.setStyle(Paint.Style.FILL);

		mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mBorderPaint.setStrokeWidth(strokeWidth);
		mBorderPaint.setStyle(Paint.Style.STROKE);
	}

	private void initClickListener() {
		// When tapped, move cursor to end of text
		super.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setSelection(getText().length());
				if (mOnClickListener != null) {
					mOnClickListener.onClick(v);
				}
			}
		});
	}

	@SuppressWarnings("SimplifiableIfStatement")
	private void initOnEditorActionListener() {
		super.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// For some reasons the soft keyboard does not response when tap :(
				if (mOnEditorActionListener != null) {
					return mOnEditorActionListener.onEditorAction(v, actionId, event);
				} else {
					return false;
				}
			}
		});
	}

	private void initColorStates() {
		int[] pinCodeColors = new int[]{
				mHighlightColorBackground,
				mColorBackgroundPin,
		};

		int[] pinCodeBorderColors = new int[]{
				mHighlightColorBorder,
				mColorBorder,
		};

		mBackgroundColorState = new ColorStateList(mPinCodeColorStates, pinCodeColors);
		mBorderColorState = new ColorStateList(mPinCodeColorStates, pinCodeBorderColors);
	}

	private void setDefaultInputType() {
		// Default input type of EditText: InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE
		// InputType.TYPE_TEXT_FLAG_MULTI_LINE is not allow for this PinCodeEditText

		unsetInputTypeFlag(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
	}

	private void setMaxLength(int maxLength) {
		InputFilter[] editTextFilters = getFilters();
		int length = editTextFilters.length;
		InputFilter[] customFilters = Arrays.copyOf(editTextFilters, length + 1);
		customFilters[length] = new InputFilter.LengthFilter(maxLength);
		setFilters(customFilters);
	}

	private void unsetInputTypeFlag(int... flagsToBeUnset) {
		for (int unsetFlag : flagsToBeUnset) {
			int inputTypeWithoutMultiline = BitFlagUtils.unsetFlag(getInputType(), unsetFlag);
			setInputType(inputTypeWithoutMultiline);
		}
	}

	@Override
	public Parcelable onSaveInstanceState() {
		// I prefer to save state this way :)

		Bundle args = new Bundle(6);
		Parcelable superState = super.onSaveInstanceState();

		args.putParcelable(KEY_SUPER_STATE, superState);
		args.putInt(KEY_CHARACTER_NUMBER, mCharacterNumber);
		args.putInt(KEY_GAP_WIDTH, mGapWidth);
		args.putBoolean(KEY_SHOULD_HIGHLIGHT_NEXT_PIN, mHighlightNextPin);
		args.putBoolean(KEY_SHOULD_SHOW_AS_PASSWORD, mShowAsPassword);
		args.putParcelable(KEY_STRING_MASKER, mStringMasker);

		return args;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		Bundle args = (Bundle) state;
		Parcelable superState = args.getParcelable(KEY_SUPER_STATE);
		super.onRestoreInstanceState(superState);

		mCharacterNumber = args.getInt(KEY_CHARACTER_NUMBER);
		mGapWidth = args.getInt(KEY_GAP_WIDTH);
		mHighlightNextPin = args.getBoolean(KEY_SHOULD_HIGHLIGHT_NEXT_PIN);
		mShowAsPassword = args.getBoolean(KEY_SHOULD_SHOW_AS_PASSWORD);
		mStringMasker = args.getParcelable(KEY_STRING_MASKER);
	}

	@Override
	public void setOnClickListener(OnClickListener onClickListener) {
		mOnClickListener = onClickListener;
	}

	@Override
	public void setOnEditorActionListener(OnEditorActionListener onEditorActionListener) {
		mOnEditorActionListener = onEditorActionListener;
	}

	// Force EditText to not show copy and paste popup
	@Override
	public boolean isSuggestionsEnabled() {
		return false;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		getPaint().setColor(getCurrentTextColor());

		int paddingStart = ViewCompat.getPaddingStart(this);
		int paddingEnd = ViewCompat.getPaddingEnd(this);
		int width = getMeasuredWidth() - paddingStart - paddingEnd;

		float pinCodeSize = calculatePinCodeSize(width, mCharacterNumber, mGapWidth);
		String text = mStringMasker.getMaskText(getText()).toString();
		drawLineForEachPin(canvas, text, mCharacterNumber, pinCodeSize, mGapWidth);
	}

	private float calculatePinCodeSize(int width, int characterNumber, float gapWidth) {
		// Get total space that will be using
		float totalSpaceWidth = gapWidth * (characterNumber - 1);

		// Remove total space from width
		float widthWithoutSpace = width - totalSpaceWidth;

		// Total character size for each character
		return widthWithoutSpace / characterNumber;
	}

	@SuppressWarnings("UnnecessaryLocalVariable")
	private void drawLineForEachPin(Canvas canvas,
									String text,
									float characterNumber,
									float pinCodeSize,
									float gapWidth) {
		int startX = ViewCompat.getPaddingStart(this);
		int top = getPaddingTop();
		int bottom = getMeasuredHeight() - getPaddingBottom();
		int textLength = text.length();

		for (int i = 0; i < characterNumber; i++) {
			float endX = startX + pinCodeSize;

			boolean shouldSetSelectedPin = i == textLength && isFocused();
			updatePaintColor(shouldSetSelectedPin, mHighlightNextPin);

			canvas.drawRoundRect(new RectF(startX, top, endX, bottom), 0, 0, mBackgroundPaint);
			canvas.drawRoundRect(new RectF(startX, top, endX, bottom), 0, 0, mBorderPaint);

			if (i < textLength) {
				int start = i;
				int end = i + 1;
				float middleX = startX + (pinCodeSize / 2f);
				float textMiddleX = getTextMiddleX(text, start, end, getPaint());
				float x = middleX + textMiddleX;

				float characterHeightMiddleY = getTextMiddleY(text, start, end, getPaint());
				float bottomMiddle = bottom / 2f;
				float topMiddle = top / 2f;
				float y = bottomMiddle + topMiddle + characterHeightMiddleY;

				canvas.drawText(text, start, end, x, y, getPaint());
			}

			startX += pinCodeSize + gapWidth;
		}
	}

	private void updatePaintColor(boolean shouldSetSelectedColor, boolean highlightNextPin) {
		if (shouldSetSelectedColor && highlightNextPin) {
			mBackgroundPaint.setColor(getColorForState(mBackgroundColorState, android.R.attr.state_selected));
			mBorderPaint.setColor(getColorForState(mBorderColorState, android.R.attr.state_selected));
		} else {
			mBackgroundPaint.setColor(getColorForState(mBackgroundColorState, 0));
			mBorderPaint.setColor(getColorForState(mBorderColorState, 0));
		}
	}

	private int getColorForState(ColorStateList colorStateList, int... states) {
		return colorStateList.getColorForState(states, Color.GRAY);
	}

	private float getTextMiddleX(String text, int start, int end, Paint paint) {
		Rect bounds = getTextBounds(text, start, end, paint);
		return (bounds.width() / 2f) - bounds.right;
	}

	private float getTextMiddleY(String text, int start, int end, Paint paint) {
		Rect bounds = getTextBounds(text, start, end, paint);
		return (bounds.height() / 2f) - bounds.bottom;
	}

	private Rect getTextBounds(String text, int start, int end, Paint paint) {
		Rect bounds = new Rect();
		paint.getTextBounds(text, start, end, bounds);
		return bounds;
	}

	public void setShowAsPassword(boolean shouldShowAsPassword) {
		if (mStringMasker.isShowMask() == shouldShowAsPassword) return;

		mStringMasker.setShouldShowMask(shouldShowAsPassword);
		setSelection(length());
		invalidate();
	}

	public boolean isShowAsPassword() {
		return mStringMasker.isShowMask();
	}

	public int getMaxLength() {
		return mCharacterNumber;
	}

	public void setGapWidth(int gapWidth) {
		if (mGapWidth == gapWidth) return;

		mGapWidth = gapWidth;
		requestLayout();
	}

	public float getGapWidth() {
		return mGapWidth;
	}

	public char getMaskCharacter() {
		return mStringMasker.getMask();
	}

	public void setMaskCharacter(char maskCharacter) {
		if (mStringMasker.getMask() == maskCharacter) return;

		mStringMasker.setMask(maskCharacter);
		invalidate();
	}

	private void disableCopyAndPaste() {
		setLongClickable(false);
	}

}
