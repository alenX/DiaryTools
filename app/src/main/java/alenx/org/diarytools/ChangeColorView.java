package alenx.org.diarytools;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by wangss on 2015/7/22.
 */
public class ChangeColorView extends View {
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mPaint;

    private int mColor = 0xffffff;//源颜色
    private float mAlpha = 0f;
    private Bitmap mIconBitmap;
    private Rect mIconRect;

    private String mText = "aa";

    private int mTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10,
            getResources().getDisplayMetrics());


    private Paint mTextPaint;
    private Rect mTextBound = new Rect();

    public ChangeColorView(Context context) {
        super(context);
    }

    public ChangeColorView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ChangeColorView);

        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.ChangeColorView_icon_a:
                    BitmapDrawable drawable = (BitmapDrawable) a.getDrawable(attr);
                    mIconBitmap = drawable.getBitmap();
                    break;
                case R.styleable.ChangeColorView_color_a:
                    mColor = a.getColor(attr, 0xffffff);
                    break;
                case R.styleable.ChangeColorView_text_a:
                    mText = a.getString(attr);
                    break;
                case R.styleable.ChangeColorView_text_size_a:
                    mTextSize = (int) a.getDimension(attr,
                            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                    break;
            }
        }
        a.recycle();

        mTextPaint = new Paint();
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.getTextBounds(mText, 0, mText.length(), mTextBound);

    }

    public ChangeColorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int bitmapWidth = Math.min(getMeasuredWidth() - getPaddingLeft() -
                getPaddingRight(), getMeasuredHeight() - getPaddingTop() - getPaddingBottom() - mTextBound.height());
        int left = getMeasuredWidth() / 2 - bitmapWidth / 2;
        int top = (getMeasuredHeight() - mTextBound.height()) / 2 - bitmapWidth / 2;

        mIconRect = new Rect(left, top, left + bitmapWidth, top + bitmapWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int alpha = (int) Math.ceil(255 * mAlpha);
        canvas.drawBitmap(mIconBitmap, null, mIconRect, null);
        setupTargetBitmap(alpha);
        drawSourceText(canvas, alpha);
        drawTargetText(canvas, alpha);
        canvas.drawBitmap(mBitmap, 0, 0, null);
    }

    private void drawTargetText(Canvas canvas, int alpha) {

        mTextPaint.setColor(mColor);
        mTextPaint.setAlpha(alpha);
        canvas.drawText(mText, mIconRect.left + mIconRect.width() / 2 - mTextBound.width() / 2,
                mIconRect.bottom + mTextBound.height(), mTextPaint);
    }

    private void drawSourceText(Canvas canvas, int alpha) {

        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(0x000000);
        mTextPaint.setAlpha(255 - alpha);
        canvas.drawText(mText, mIconRect.left + mIconRect.width() / 2 - mTextBound.width() / 2,
                mIconRect.bottom + mTextBound.height(), mTextPaint);
    }

    private void setupTargetBitmap(int alpha) {

        mBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setAlpha(255 - alpha);

        mCanvas.drawRect(mIconRect, mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mPaint.setAlpha(255);
        mCanvas.drawBitmap(mIconBitmap, null, mIconRect, mPaint);
    }

    public void setIconAlpha(float alpha) {
        this.mAlpha = alpha;
        invalidateView();
    }

    private void invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }
}
