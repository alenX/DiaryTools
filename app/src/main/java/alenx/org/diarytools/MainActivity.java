package alenx.org.diarytools;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends Activity {

    private static String[] expressInfo = {"顺丰快递", "申通快递", "圆通快递", "韵达快递", "山东海红快递"};//shunfeng、shentong、yuantong、yunda、haihong
    StringBuilder sb = new StringBuilder("");
    HttpClient client;
    boolean isFromChinese = true;//是否是从中文翻译为英文
    private ViewPager mViewPage;
    private PagerAdapter mAdapter;
    private List<View> mViews;
    private LayoutInflater mInflater;
    private LinearLayout mTabDictionaryLayout;
    private LinearLayout mTabExpressLayout;
    private Spinner mExpressSpinner;
    private Button mQueryBtn;
    private EditText mExpressEditText;
    private TextView mExpressTextView;
    private HashMap<String, String> hashMap = new HashMap<>();
    private EditText mSourceEditText;
    private TextView mResultEditText;
    private Button mTranslateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        mInflater = LayoutInflater.from(this);

        initView();
        mViewPage = (ViewPager) findViewById(R.id.id_viewpager);

        mViewPage.setAdapter(mAdapter);

        mViewPage.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });


    }


    private void initView() {
        mTabDictionaryLayout = (LinearLayout) findViewById(R.id.id_tab_bottom_dictionary);
        mTabExpressLayout = (LinearLayout) findViewById(R.id.id_tab_bottom_express);

        mViews = new ArrayList<>();

        View first = mInflater.inflate(R.layout.dictinonary_tab_activity, null);
        View second = mInflater.inflate(R.layout.express_tab_activity, null);

        mViews.add(first);
        mViews.add(second);

        initDictionary(first);

        mAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return mViews.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object o) {
                return view == o;//TODO
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(mViews.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view = mViews.get(position);
                container.addView(view);
                return view;
            }
        };

        mExpressSpinner = (Spinner) second.findViewById(R.id.expressSpinner);
        mExpressSpinner.setAdapter(new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_dropdown_item, expressInfo));


        hashMap.put(expressInfo[0], "shunfeng");
        hashMap.put(expressInfo[1], "shentong");
        hashMap.put(expressInfo[2], "yuantong");
        hashMap.put(expressInfo[3], "yunda");
        hashMap.put(expressInfo[4], "haihong");


        mExpressTextView = (TextView) second.findViewById(R.id.resultTextView);
        mExpressEditText = (EditText) second.findViewById(R.id.expressNo);
        mQueryBtn = (Button) second.findViewById(R.id.queryBtn);
        sb = new StringBuilder("http://www.kuaidiapi.cn/rest/" +
                "?uid=33720&key=c89b47c4e3014bd59a814164f1fd60a8&order=" + mExpressEditText.getText().toString() + "&id=" + hashMap.get(mExpressSpinner.getSelectedItem().toString()));


        mQueryBtn.setOnClickListener(new ExpressOnClick());

    }

    public String parseFromJson(String resource) {
        String result = "\n";

        try {
            HttpGet get = new HttpGet(resource);
            HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(get);
            if (response.getStatusLine().getStatusCode() == 200) {

//                JSONObject object = new JSONObject(EntityUtils.toString(response.getEntity()));
                JSONObject object = new JSONObject("{\"id\":\"ems\",\"name\":\"EMS快递\",\"order\":\"EJ289957855JP\",\"message\":\"\",\"errcode\":\"0000\",\"status\":4,\"data\":[{\"time\":\"2015-04-23 21:06:00\",\"content\":\"收寄\"},{\"time\":\"2015-04-25 09:37:00\",\"content\":\"离开处理中心,发往中国 北京\"},{\"time\":\"2015-04-27 19:15:55\",\"content\":\"到达处理中心,来自JPKIXH\"},{\"time\":\"2015-04-28 07:28:37\",\"content\":\"离开处理中心,发往留存（待验）\"},{\"time\":\"2015-04-28 08:30:34\",\"content\":\"到达处理中心,来自留存（待验）\"},{\"time\":\"2015-04-28 13:16:15\",\"content\":\"海关放行\"},{\"time\":\"2015-04-28 13:16:15\",\"content\":\"海关放行\"},{\"time\":\"2015-04-28 13:16:25\",\"content\":\"离开处理中心,发往北京邮政速递中关村区域分公司世纪城营投部\"},{\"time\":\"2015-04-28 15:32:39\",\"content\":\"到达处理中心,来自北京邮件处理中心（航空）\"},{\"time\":\"2015-04-28 15:43:53\",\"content\":\"安排投递\"},{\"time\":\"2015-04-28 16:42:15\",\"content\":\"投递并签收\"}]}");

                result += "快递名称: [ " + object.get("name") + " ]" + "\n";
                result += "快递单号: [ " + object.get("order") + " ]" + "\n";
                JSONArray jsonArray = object.getJSONArray("data");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    result += "时间: [ " + jsonObject.getString("time") + " ]" + "\n";
                    result += "内容: [ " + jsonObject.getString("content") + " ]" + "\n";
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

    public void initDictionary(View view) {
        client = new DefaultHttpClient();
        mSourceEditText = (EditText) view.findViewById(R.id.sourceEditText);
        mResultEditText = (TextView) view.findViewById(R.id.resultEditText);
        view.findViewById(R.id.translateBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getTranslation(mSourceEditText.getText().toString().trim());
            }
        });
    }

    private void getTranslation(String source) {


        new AsyncTask<String, Void, String>() {
            ProgressDialog pd;

            @Override
            protected String doInBackground(String... strings) {

                Pattern pattern = Pattern.compile("^[\\u0391-\\uFFE5]+$");
                Matcher matcher = pattern.matcher(strings[0]);
                isFromChinese = matcher.matches();

                StringBuilder sb = new StringBuilder("http://fanyi.youdao.com/openapi.do?keyfrom=wangss&key=795137117&type=data&doctype=xml&version=1.1&q=");
                sb.append(strings[0]);

                HttpGet get = new HttpGet(sb.toString());
                String value = "";
                try {
                    HttpResponse response = client.execute(get);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        value = parseXMLByPull(EntityUtils.toString(response.getEntity()), isFromChinese);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return value;
            }


            @Override
            protected void onPostExecute(String s) {
                pd.hide();
                mResultEditText.setText(s);
                super.onPostExecute(s);

            }

            @Override
            protected void onPreExecute() {
                pd = ProgressDialog.show(MainActivity.this, "提示", "正在查询中...");
                pd.setCancelable(true);
                super.onPreExecute();
            }
        }.execute(source);
    }

    public String parseXMLByPull(String xmlSource, Boolean isFromChinese) {
        String result = "\n";

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlSource));

            int eventType = xmlPullParser.getEventType();
            ArrayList<String> exs = new ArrayList<>();
            if (!isFromChinese) {
                String phonetic = "";//音标
                String us_phonetic = "";//美式音标
                String uk_phonetic = "";//英式音标
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    String nodeName = xmlPullParser.getName();
                    switch (eventType) {
                        case XmlPullParser.START_TAG:

                            if ("phonetic".equals(nodeName)) {
                                phonetic = "音标: [ " + xmlPullParser.nextText() + " ]" + "\n";
                            } else if ("us-phonetic".equals(nodeName)) {
                                us_phonetic = "美式音标: [ " + xmlPullParser.nextText() + " ]" + "\n";
                            } else if ("uk-phonetic".equals(nodeName)) {
                                uk_phonetic = "英式音标: [ " + xmlPullParser.nextText() + " ]" + "\n";
                            } else if ("ex".equals(nodeName)) {
                                exs.add(xmlPullParser.nextText() + "\n");
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            if ("basic".equals(nodeName)) {
                                result = phonetic + us_phonetic + uk_phonetic + "翻译:\n ";
                                for (String ex : exs) {
                                    result += ex;
                                }
                                return result;
                            }
                            break;
                        default:
                            break;


                    }
                    eventType = xmlPullParser.next();
                }
            } else {
                String paragraph = "";
                String phonetic = "";
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    String nodeName = xmlPullParser.getName();
                    switch (eventType) {
                        case XmlPullParser.START_TAG:
                            if ("phonetic".equals(nodeName)) {
                                phonetic = "拼音: [ " + xmlPullParser.nextText() + " ]" + "\n";
                            } else if ("us-paragraph".equals(nodeName)) {
                                paragraph = "翻译: [ " + xmlPullParser.nextText() + " ]" + "\n";
                            } else if ("ex".equals(nodeName)) {
                                exs.add(xmlPullParser.nextText() + "\n");
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            if ("basic".equals(nodeName)) {
                                result = phonetic + paragraph + "翻译:\n";
                                for (String ex : exs) {
                                    result += ex;
                                }
                                return result;
                            }
                            break;
                        default:
                            break;


                    }
                    eventType = xmlPullParser.next();
                }
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return result;

    }

    private class ExpressOnClick implements View.OnClickListener {

        ProgressDialog pd;

        @Override
        public void onClick(View view) {

            new AsyncTask<String, Void, String>() {

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
                    pd = ProgressDialog.show(MainActivity.this, "提示", "正在查询中...");
                    pd.setCancelable(false);
                    super.onPreExecute();
                }
            }.execute(sb.toString());

        }
    }
}
