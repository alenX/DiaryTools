package alenx.org.diarytools.CustomViews;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import alenx.org.diarytools.R;

/**
 * Created by wangss on 2015/7/23.
 */
public class CustomProgressDialog extends Dialog {

    private Context mContext;
    private static CustomProgressDialog mDialog = null;


    public CustomProgressDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    public static CustomProgressDialog createDialog(Context context) {
        mDialog = new CustomProgressDialog(context);
        mDialog.setContentView(R.layout.customprogressdialog);
        mDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        return mDialog;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (mDialog == null) {
            return;
        }
        ImageView mImageView = (ImageView) mDialog.findViewById(R.id.loadingImageView);
        AnimationDrawable mAnimation = (AnimationDrawable) mImageView.getBackground();
        mAnimation.start();
    }

    public CustomProgressDialog setMessage(String message){
        TextView mTextView = (TextView)mDialog.findViewById(R.id.loadingTextView);
        if (mTextView!=null){
            mTextView.setText(message);
        }
        return mDialog;
    }

    public void setMsgTitle(CharSequence title) {
        super.setTitle(title);
        if (mDialog!=null){
            mDialog.setTitle(title);
        }
    }

    /*public CustomProgressDialog setTitle(String title){
        if (mDialog!=null){
            mDialog.setTitle(title);
        }
        return mDialog;
    }*/
}
