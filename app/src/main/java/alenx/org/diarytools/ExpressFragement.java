package alenx.org.diarytools;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by wangss on 2015/7/21.
 * 快递Fragment
 */
public class ExpressFragement extends Fragment {
    private static String[] expressInfo={"顺丰快递","申通快递","圆通快递","韵达快递","山东海红快递"};//shunfeng、shentong、yuantong、yunda、haihong
    private HashMap<String,String> hashMap = new HashMap<>();
    StringBuilder sb= new StringBuilder("");

    private View mExpressView;
    private Spinner mExpressSpinner;

    private TextView mExpressTextView;
    private EditText mExpressEditText;
    private Button mQueryBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mExpressView= inflater.inflate(R.layout.express_tab_activity,container,false);
        mExpressSpinner = (Spinner) mExpressView.findViewById(R.id.expressSpinner);
        mExpressSpinner.setAdapter(new ArrayAdapter<CharSequence>(getActivity(), android.R.layout.simple_spinner_dropdown_item, expressInfo));
        hashMap.put(expressInfo[0],"shunfeng");
        hashMap.put(expressInfo[1],"shentong");
        hashMap.put(expressInfo[2],"yuantong");
        hashMap.put(expressInfo[3],"yunda");
        hashMap.put(expressInfo[4], "haihong");

        mExpressTextView = (TextView)mExpressView.findViewById(R.id.resultTextView);
        mExpressEditText = (EditText)mExpressView.findViewById(R.id.expressNo);
        mQueryBtn = (Button)mExpressView.findViewById(R.id.queryBtn);
        sb = new StringBuilder("http://www.kuaidiapi.cn/rest/" +
                "?uid=33720&key=c89b47c4e3014bd59a814164f1fd60a8&order="+mExpressEditText.getText().toString()+"&id="+hashMap.get(mExpressSpinner.getSelectedItem().toString()));
        mQueryBtn.setOnClickListener(new ExpressOnClick());

        return mExpressView;
    }


    private class ExpressOnClick implements View.OnClickListener{

        ProgressDialog pd;
        @Override
        public void onClick(View view) {
            new AsyncTask<String,Void,String>(){
                @Override
                protected String doInBackground(String... strings) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String str = "http://www.kuaidiapi.cn/rest/?uid=33720&key=c89b47c4e3014bd59a814164f1fd60a8&order=EJ289957855JP&id=ems&show=json";
                    return parseFromJson(str);
                }

                @Override
                protected void onPostExecute(String s) {
                    pd.hide();
                    super.onPostExecute(s);
                    mExpressTextView.setText(s);

                }

                @Override
                protected void onPreExecute() {
                    pd = ProgressDialog.show(getActivity(),"提示","正在查询中...");
                    pd.setCancelable(false);
                    super.onPreExecute();
                }
            }.execute(sb.toString());
        }
    }

    public String parseFromJson(String resource){
        String result = "\n";
        try {
            HttpGet get = new HttpGet(resource);
            HttpClient client = new DefaultHttpClient( );
            HttpResponse response = client.execute(get);
            if (response.getStatusLine().getStatusCode() == 200) {
//                JSONObject object = new JSONObject(EntityUtils.toString(response.getEntity()));
                JSONObject object = new JSONObject("{\"id\":\"ems\",\"name\":\"EMS快递\",\"order\":\"EJ289957855JP\",\"message\":\"\",\"errcode\":\"0000\",\"status\":4,\"data\":[{\"time\":\"2015-04-23 21:06:00\",\"content\":\"收寄\"},{\"time\":\"2015-04-25 09:37:00\",\"content\":\"离开处理中心,发往中国 北京\"},{\"time\":\"2015-04-27 19:15:55\",\"content\":\"到达处理中心,来自JPKIXH\"},{\"time\":\"2015-04-28 07:28:37\",\"content\":\"离开处理中心,发往留存（待验）\"},{\"time\":\"2015-04-28 08:30:34\",\"content\":\"到达处理中心,来自留存（待验）\"},{\"time\":\"2015-04-28 13:16:15\",\"content\":\"海关放行\"},{\"time\":\"2015-04-28 13:16:15\",\"content\":\"海关放行\"},{\"time\":\"2015-04-28 13:16:25\",\"content\":\"离开处理中心,发往北京邮政速递中关村区域分公司世纪城营投部\"},{\"time\":\"2015-04-28 15:32:39\",\"content\":\"到达处理中心,来自北京邮件处理中心（航空）\"},{\"time\":\"2015-04-28 15:43:53\",\"content\":\"安排投递\"},{\"time\":\"2015-04-28 16:42:15\",\"content\":\"投递并签收\"}]}");

                result +=  "快递名称: [ " + object.get("name") + " ]" + "\n";
                result +=  "快递单号: [ " + object.get("order") + " ]" + "\n";
                JSONArray jsonArray = object.getJSONArray("data");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    result +="时间: [ " +jsonObject.getString("time")+ " ]" + "\n";
                    result +="内容: [ " +jsonObject.getString("content")+ " ]" + "\n";
                }
            }




        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
