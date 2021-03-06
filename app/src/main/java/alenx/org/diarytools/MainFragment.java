package alenx.org.diarytools;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangss on 2015/7/21.
 */
public class MainFragment extends FragmentActivity implements View.OnClickListener {

    private FragmentPagerAdapter mPageAdapter;
    private ViewPager mViewPager;

    private List<Fragment> mList;
    private List<ChangeColorView> mTabIndicator;

    /*private LinearLayout mTabExpress;
    private LinearLayout mTabTranslate;*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);/*屏蔽Title*/
        setContentView(R.layout.activity_main);


        initViews();
//        initClickEvent();
        setSelectPage(0);

    }

    private void initViews() {
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);

        /*mTabExpress = (LinearLayout) findViewById(R.id.id_tab_bottom_express);
        mTabTranslate = (LinearLayout) findViewById(R.id.id_tab_bottom_dictionary);*/

       /* mExpressImgBtn = (View) findViewById(R.id.id_tab_bottom_express);
        mTranslateImgBtn = (ImageButton) findViewById(R.id.id_tab_bottom_dictionary);
        mExpressImgBtn.setOnClickListener(this);
        mTranslateImgBtn.setOnClickListener(this);*/

        mList = new ArrayList<>();
        mTabIndicator = new ArrayList<>();
        Fragment expressFrg = new ExpressFragement();
        Fragment translateFrg = new TranslateFragment();
        mList.add(translateFrg);
        mList.add(expressFrg);

        ChangeColorView dictionary = (ChangeColorView) findViewById(R.id.id_tab_bottom_dictionary);
        ChangeColorView express = (ChangeColorView) findViewById(R.id.id_tab_bottom_express);
        dictionary.setOnClickListener(this);
        express.setOnClickListener(this);
        mTabIndicator.add(dictionary);
        mTabIndicator.add(express);


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

                if (positionOffset > 0) {
                    ChangeColorView left = mTabIndicator.get(position);
                    ChangeColorView right = mTabIndicator.get(position + 1);
                    Log.v("Test", positionOffset + "," + position);
                    left.setIconAlpha((1 - positionOffset) / 2.0f + 0.5f);
                    right.setIconAlpha((positionOffset + 1f) / 2.0f);
                }
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
                mTabIndicator.get(0).setIconAlpha(1.0f);
//                mTranslateImgBtn.setImageResource(R.drawable.dictionary_press);
                break;
            case 1:
                mTabIndicator.get(1).setIconAlpha(1.0f);
//                mExpressImgBtn.setImageResource(R.drawable.express_press);
                break;
            default:
                break;

        }
    }

    private void resetImgs() {
        /*mTranslateImgBtn.setImageResource(R.drawable.dictionary_normal);
        mExpressImgBtn.setImageResource(R.drawable.express_normal);*/
        mTabIndicator.get(0).setIconAlpha(0.2f);
        mTabIndicator.get(1).setIconAlpha(0.2f);
    }


    @Override
    public void onClick(View view) {
        resetImgs();
        switch (view.getId()) {
            case R.id.id_tab_bottom_dictionary:
                setSelectPage(0);
                break;
            case R.id.id_tab_bottom_express:
                setSelectPage(1);
                break;
            default:
                break;
        }
    }
}
