package net.shiftstudios.widget;

import net.shiftstudios.squaredtext.R;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.widget.TextView;

public class AdvancedTextView extends TextView {

	private boolean stroke = false;
	private float strokeWidth = 0.0f;
	private int strokeColor;

	public AdvancedTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		initView(context, attrs);
	}

	public AdvancedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);

		initView(context, attrs);
	}

	public AdvancedTextView(Context context) {
		super(context);
	}

	private void initView(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.StTextView);
		float f = a.getFloat(R.styleable.StTextView_textStrokeWidth, 0.0f);
		if (f != 0) {
			stroke = true;
		} else {
			stroke = false;
		}
		strokeWidth = a.getFloat(R.styleable.StTextView_textStrokeWidth, 0.0f);
		strokeColor = a.getColor(R.styleable.StTextView_textStrokeColor,
				0xffffffff);
	}

	public void setStrokeWidth(float f) {
		if (f != 0) {
			stroke = true;
		} else {
			stroke = false;
		}
		strokeWidth = f;
	}

	public void setStrokeColor(int i) {
		strokeColor = i;
	}

	@Override
	protected void onDraw(Canvas canvas) {

		if (stroke) {
			ColorStateList states = getTextColors();
			getPaint().setStyle(Style.STROKE);
			getPaint().setStrokeWidth(strokeWidth);
			setTextColor(strokeColor);
			super.onDraw(canvas);
			getPaint().setStyle(Style.FILL);
			setTextColor(states);
		}

		super.onDraw(canvas);
	}
}