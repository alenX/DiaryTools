package alenx.org.diarytools;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by wangss on 2015/7/21.
 * 快递Fragment
 */
public class ExpressFragement extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.express_tab_activity,container,false);
    }
}
