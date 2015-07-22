package alenx.org.diarytools;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangss on 2015/7/21.
 */
public class MainFragment extends FragmentActivity implements View.OnClickListener {

    private FragmentPagerAdapter mPageAdapter;
    private ViewPager mViewPager;

    private List<Fragment> mList;

    private LinearLayout mTabExpress;
    private LinearLayout mTabTranslate;

    private ImageButton mExpressImgBtn;
    private ImageButton mTranslateImgBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);/*屏蔽Title*/
        setContentView(R.layout.activity_main);


        initViews();
        initClickEvent();
        setSelectPage(0);

    }

    private void initViews() {
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);

        mTabExpress = (LinearLayout) findViewById(R.id.id_tab_bottom_express);
        mTabTranslate = (LinearLayout) findViewById(R.id.id_tab_bottom_dictionary);

        mExpressImgBtn = (ImageButton) findViewById(R.id.btn_tab_bottom_express);
        mTranslateImgBtn = (ImageButton) findViewById(R.id.btn_tab_bottom_dictionary);

        mList = new ArrayList<>();
        Fragment expressFrg = new ExpressFragement();
        Fragment translateFrg = new TranslateFragment();
        mList.add(translateFrg);
        mList.add(expressFrg);

        mPageAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mList.get(position);
            }

            @Override
            public int getCount() {
                return mList.size();
            }
        };

        mViewPager.setAdapter(mPageAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int currentItem = mViewPager.getCurrentItem();
                setSelectPage(currentItem);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setSelectPage(int i) {
        setTab(i);
        mViewPager.setCurrentItem(i);
    }

    private void setTab(int i) {
        resetImgs();
        switch (i) {
            case 0:
                mTranslateImgBtn.setImageResource(R.drawable.dictionary_press);
                break;
            case 1:
                mExpressImgBtn.setImageResource(R.drawable.express_press);
                break;
            default:
                break;

        }
    }

    private void resetImgs() {
        mTranslateImgBtn.setImageResource(R.drawable.dictionary_normal);
        mExpressImgBtn.setImageResource(R.drawable.express_normal);
    }

    private void initClickEvent() {
        mExpressImgBtn.setOnClickListener(this);
        mTranslateImgBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        resetImgs();
        switch (view.getId()) {
            case R.id.btn_tab_bottom_dictionary:
                setSelectPage(0);
                break;
            case R.id.btn_tab_bottom_express:
                setSelectPage(1);
                break;
            default:
                break;
        }
    }
}
