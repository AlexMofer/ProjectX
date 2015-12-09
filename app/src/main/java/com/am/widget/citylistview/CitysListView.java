package com.am.widget.citylistview;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.am.widget.citylistview.CitysUtils.City;
import com.am.widget.citylistview.CitysUtils.CityDto;

/**
 * 城市选择列表
 * 
 * @author Mofer
 * 
 */
public class CitysListView extends ListView {

	public static final int DEFAULT_TEXTSIZE = 32;// sp
	public static final int DEFAULT_ITEXTSIZE = 12;// sp
	public static final int DEFAULT_DWH = 64;
	public static final int DEFAULT_RADIUS = 10;
	public static final int DEFAULT_NCOLOR = 0x00000000;
	public static final int DEFAULT_PCOLOR = 0x40000000;
	public static final int DEFAULT_BCOLOR = 0x80000000;
	public static final String PENTAGRAM_01 = "\u2606";
	public static final String PENTAGRAM_02 = "\u2605";
	private static final int[] ATTRS = new int[] { android.R.attr.textSize,
			android.R.attr.textColor };
	private static final int[] NORMAL = new int[] {};
	private static final int[] PRESSED = new int[] { android.R.attr.state_pressed };
	private Drawable mNoticeDrawable;
	private int mNoticeTextColor;
	private float mNoticeTextSize;
	private final TextPaint mTextPaint;
	private boolean drawNotice = false;
	private boolean includepad = true;
	private int mNoticeTextHeight;
	private int mNoticeDesc;
	private char mNoticeText;
	private Drawable mScrollBarDrawable;
	private int[] mScrollBarDrawableState = NORMAL;
	private int mScrollBarWidth = 64;
	private float mScrollBarTextHeight;
	private int mScrollBarDesc;
	private float mScrollBarItemHeight;
	private float mScrollBarItemTextSize;
	private int mScrollBarItemTextColor;
	public static char[] INITIALTABLE = { '0', 'A', 'B', 'C', 'D', 'E', 'F',
			'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'W',
			'X', 'Y', 'Z', };
	private float startX;
	private boolean interceptTouchEvent = false;
	private SectionIndexer mSectionIndexer;

	public CitysListView(Context context) {
		this(context, null);
	}

	public CitysListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	@SuppressLint("NewApi")
	public CitysListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		final float density = getResources().getDisplayMetrics().density;
		mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setTextAlign(Align.CENTER);
		if (Build.VERSION.SDK_INT > 4) {
			mTextPaint.density = density;
		}
		final TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
		int textSize = a.getDimensionPixelSize(0,
				(int) (DEFAULT_TEXTSIZE * density));
		int color;
		ColorStateList colors = a.getColorStateList(1);
		if (colors != null) {
			color = colors.getDefaultColor();
		} else {
			color = a.getColor(1, Color.WHITE);
		}
		a.recycle();
		if (mNoticeTextSize == 0) {
			setNoticeTextSize(textSize);
		}
		if (mNoticeTextColor == 0) {
			setNoticeTextColor(color);
		}
		if (mNoticeDrawable == null) {
			mNoticeDrawable = getDefaultNoticeDrawable(DEFAULT_BCOLOR);
		}
		if (mScrollBarDrawable == null) {
			mScrollBarDrawable = getDefaultScrollBarDrawable(DEFAULT_PCOLOR);
		}
		if (mScrollBarItemTextSize == 0) {
			setScrollBarItemTextSize(DEFAULT_ITEXTSIZE * density);
		}
		if (mScrollBarItemTextColor == 0) {
			setScrollBarItemTextColor(Color.BLACK);
		}

		setVerticalScrollBarEnabled(true);
		setFastScrollEnabled(false);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mTextPaint.setTextSize(mNoticeTextSize);
		FontMetricsInt metrics = mTextPaint.getFontMetricsInt();
		if (includepad) {
			mNoticeTextHeight = metrics.bottom - metrics.top;
			mNoticeDesc = mNoticeTextHeight + metrics.top;
		} else {
			mNoticeTextHeight = metrics.descent - metrics.ascent;
			mNoticeDesc = mNoticeTextHeight + metrics.ascent;
		}

		mTextPaint.setTextSize(mScrollBarItemTextSize);
		metrics = mTextPaint.getFontMetricsInt();
		mScrollBarTextHeight = metrics.descent - metrics.ascent;
		mScrollBarDesc = (int) (mScrollBarTextHeight - (-metrics.ascent
				- metrics.descent + (metrics.bottom - metrics.descent)
				* getResources().getDisplayMetrics().density));
		mScrollBarTextHeight += mScrollBarDesc;
		mScrollBarWidth = Math.round(mScrollBarTextHeight);
		mScrollBarItemHeight = getMeasuredHeight() / INITIALTABLE.length;
		startX = getMeasuredWidth() - mScrollBarWidth;
	}

	/**
	 * Use setAdapter(BaseCitysAdapter adapter);
	 */
	@Override
	@Deprecated
	public void setAdapter(ListAdapter adapter) {
		if (adapter instanceof BaseCitysAdapter) {
			setAdapter((BaseCitysAdapter) adapter);
		} else {
			mSectionIndexer = null;
			super.setAdapter(adapter);
		}
	}

	public void setAdapter(BaseCitysAdapter adapter) {
		super.setAdapter(adapter);
		mSectionIndexer = adapter;
	}

	@Override
	@SuppressLint("ClickableViewAccessibility")
	public boolean onTouchEvent(MotionEvent ev) {
		final float x = ev.getX();
		final float y = ev.getY();
		final int action = ev.getAction();
		switch (action) {
		default:
			if (interceptTouchEvent && !isInScrollBar(x, y)) {
				stopIntercept();
				return true;
			}
			break;
		case MotionEvent.ACTION_DOWN:
			if (isInScrollBar(x, y)) {
				interceptTouchEvent = true;
			}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			if (interceptTouchEvent) {
				stopIntercept();
				return true;
			}
			break;
		}
		if (interceptTouchEvent) {
			intercept(y);
			return true;
		}
		return super.onTouchEvent(ev);
	}

	private boolean isInScrollBar(float x, float y) {
		if (x < startX || x > getWidth() || y < 0 || y > getHeight()) {
			return false;
		}
		return true;
	}

	private void stopIntercept() {
		interceptTouchEvent = false;
		drawNotice = false;
		mScrollBarDrawableState = NORMAL;
		invalidate();
	}

	private void intercept(float y) {
		drawNotice = true;
		mScrollBarDrawableState = PRESSED;
		mNoticeText = getCharByY(y);
		notifySectionIndexer();
		invalidate();
	}

	private void notifySectionIndexer() {
		if (mSectionIndexer != null) {
			setSelection(mSectionIndexer.getPositionByTag(mNoticeText));
		}
	}

	private char getCharByY(float y) {
		int position = (int) Math.floor(y / mScrollBarItemHeight);
		if (position > -1 && position < INITIALTABLE.length) {
			return INITIALTABLE[position];
		} else if (position < 0) {
			return INITIALTABLE[0];
		} else {
			return INITIALTABLE[INITIALTABLE.length - 1];
		}
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		drawScollerBar(canvas);
		drawNotice(canvas);

	}

	private void drawScollerBar(Canvas canvas) {
		if (mScrollBarDrawable != null) {
			int right = getWidth();
			mScrollBarDrawable.setBounds(right - mScrollBarWidth, 0, right,
					getHeight());
			mScrollBarDrawable.setState(mScrollBarDrawableState);
			mScrollBarDrawable.draw(canvas);
		}
		canvas.save();
		float dx = getWidth() - mScrollBarWidth * 0.5f;
		float dy = mScrollBarItemHeight * 0.5f;
		canvas.translate(dx, dy);
		mTextPaint.setColor(mScrollBarItemTextColor);
		mTextPaint.setTextSize(mScrollBarItemTextSize);
		for (int i = 0; i < INITIALTABLE.length; i++) {
			canvas.drawText(
					i == 0 ? PENTAGRAM_01 : String.valueOf(INITIALTABLE[i]), 0,
					mScrollBarDesc, mTextPaint);
			canvas.translate(0, mScrollBarItemHeight);
		}
		canvas.restore();

	}

	private void drawNotice(Canvas canvas) {
		if (drawNotice) {
			float centerX = getWidth() * 0.5f;
			float centerY = getHeight() * 0.5f;
			canvas.translate(centerX, centerY);
			canvas.save();
			if (mNoticeDrawable != null) {
				int width = mNoticeDrawable.getIntrinsicWidth();
				int height = mNoticeDrawable.getIntrinsicHeight();
				if (width < mNoticeTextHeight) {
					width = mNoticeTextHeight;
				}
				if (height < mNoticeTextHeight) {
					height = mNoticeTextHeight;
				}
				int left = (int) (-width * 0.5f);
				int top = (int) (-height * 0.5f);
				mNoticeDrawable
						.setBounds(left, top, left + width, top + height);
				mNoticeDrawable.draw(canvas);
			}
			mTextPaint.setColor(mNoticeTextColor);
			mTextPaint.setTextSize(mNoticeTextSize);
			canvas.drawText(
					mNoticeText == '0' ? PENTAGRAM_01 : String
							.valueOf(mNoticeText), 0, mNoticeDesc, mTextPaint);
			canvas.restore();
		}
	}

	private Drawable getDefaultNoticeDrawable(int color) {
		final float density = getResources().getDisplayMetrics().density;
		GradientDrawable drawable = new GradientDrawable();
		drawable.setColor(color);
		drawable.setCornerRadius(DEFAULT_RADIUS * density);
		drawable.setSize((int) (DEFAULT_DWH * density),
				(int) (DEFAULT_DWH * density));
		return drawable;
	}

	private Drawable getDefaultScrollBarDrawable(int color) {
		StateListDrawable sld = new StateListDrawable();
		GradientDrawable normal = new GradientDrawable();
		normal.setColor(DEFAULT_NCOLOR);
		GradientDrawable pressed = new GradientDrawable();
		pressed.setColor(color);
		sld.addState(new int[] { android.R.attr.state_pressed }, pressed);
		sld.addState(new int[] {}, normal);
		return sld;
	}

	public final void setNoticeTextSize(int textSize) {
		if (mNoticeTextSize != textSize) {
			mNoticeTextSize = textSize;
			invalidate();
		}
	}

	public void setNoticeTextColor(int color) {
		if (mNoticeTextColor != color) {
			mNoticeTextColor = color;
			invalidate();
		}
	}

	public void setNoticeBackground(int color) {
		mNoticeDrawable = getDefaultNoticeDrawable(color);
		invalidate();
	}

	public void setNoticeBackground(Drawable drawable) {
		mNoticeDrawable = drawable;
		invalidate();
	}

	public void setScrollBarDrawable(int color) {
		mScrollBarDrawable = getDefaultScrollBarDrawable(color);
		invalidate();
	}

	public void setScrollBarDrawable(Drawable drawable) {
		mScrollBarDrawable = drawable;
		invalidate();
	}

	public void setScrollBarItemTextSize(float textSize) {
		mScrollBarItemTextSize = textSize;
		requestLayout();
	}

	public void setScrollBarItemTextColor(int color) {
		mScrollBarItemTextColor = color;
		invalidate();
	}

	public interface SectionIndexer {
		public int getPositionByTag(char tag);
	}

	/**
	 * 城市Adapter
	 * 
	 * @author Mofer
	 * 
	 */
	public static abstract class BaseCitysAdapter extends BaseAdapter implements
			SectionIndexer {

		private List<CityDto> citys;

		public BaseCitysAdapter() {
			citys = getCitysByPinyin();
		}

		@Override
		public final int getCount() {
			return citys.size();
		}

		@Override
		public final CityDto getItem(int position) {
			return citys.get(position);
		}

		@Override
		public final long getItemId(int position) {
			return position;
		}

		@Override
		public final int getPositionByTag(char tag) {
			int position = 0;
			switch (tag) {
			case '0':
				position = 0;
				break;
			case 'A':
				position = 11;
				break;
			case 'B':
				position = 22;
				break;
			case 'C':
				position = 41;
				break;
			case 'D':
				position = 60;
				break;
			case 'E':
				position = 74;
				break;
			case 'F':
				position = 77;
				break;
			case 'G':
				position = 84;
				break;
			case 'H':
				position = 95;
				break;
			case 'J':
				position = 130;
				break;
			case 'K':
				position = 151;
				break;
			case 'L':
				position = 156;
				break;
			case 'M':
				position = 184;
				break;
			case 'N':
				position = 190;
				break;
			case 'P':
				position = 202;
				break;
			case 'Q':
				position = 210;
				break;
			case 'R':
				position = 223;
				break;
			case 'S':
				position = 225;
				break;
			case 'T':
				position = 254;
				break;
			case 'W':
				position = 270;
				break;
			case 'X':
				position = 284;
				break;
			case 'Y':
				position = 305;
				break;
			case 'Z':
				position = 330;
				break;
			default:
				break;
			}
			return position;
		}

		/**
		 * 获取拼音排序好的城市集合
		 * 
		 * @return
		 */
		private List<CityDto> getCitysByPinyin() {
			List<CityDto> citys = CitysUtils.getCityList();
			// 添加热门城市
			citys.add(0, new CityDto(City.XI_AN));
			citys.add(0, new CityDto(City.CHENG_DOU));
			citys.add(0, new CityDto(City.WU_HAN));
			citys.add(0, new CityDto(City.NAN_JING));
			citys.add(0, new CityDto(City.SHEN_YANG));
			citys.add(0, new CityDto(City.CHONG_QING));
			citys.add(0, new CityDto(City.TIAN_JIN));
			citys.add(0, new CityDto(City.SHEN_ZHEN));
			citys.add(0, new CityDto(City.GUANG_ZHOU));
			citys.add(0, new CityDto(City.SHANG_HAI));
			citys.add(0, new CityDto(City.BEI_JING));
			return citys;
		}

	}
}
