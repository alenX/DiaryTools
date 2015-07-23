package alenx.org.diarytools.CustomViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.Button;

import alenx.org.diarytools.R;

/**
 * Created by wangss on 2015/7/23.
 */
public class QueryButton extends Button {

    private int mResourceId = 0;
    private Bitmap mBitmap;

    public QueryButton(Context context) {
        super(context,null);
    }

    public QueryButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setClickable(true);
        mResourceId = R.drawable.pic;
        mBitmap = BitmapFactory.decodeResource(getResources(),mResourceId);

    }

   /* public void setIcon(int resourceId){
        this.mBitmap = BitmapFactory.decodeResource(getResources(),resourceId);
        invalidate();
    }*/

    @Override
    protected void onDraw(Canvas canvas) {
        int x = (int) ((getMeasuredWidth()-mBitmap.getWidth())/(1.5));
        int y =mBitmap.getWidth()/12;
        canvas.drawBitmap(mBitmap,x,y,null);
        canvas.translate(0,(getMeasuredHeight()/2)-(int)getTextSize());
        super.onDraw(canvas);
    }
}
