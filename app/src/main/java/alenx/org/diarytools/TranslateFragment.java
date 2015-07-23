package alenx.org.diarytools;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import alenx.org.diarytools.CustomViews.CustomProgressDialog;

/**
 * Created by wangss on 2015/7/21.
 * 翻译Fragement
 */
public class TranslateFragment extends Fragment {

    public static final String LOG_TYPE = "TranslateFragment---";
    private Button mTranslateBtn;
    private EditText mTranslateTextView;
    private TextView mResultTextView;


    HttpClient client;

    boolean isFromChinese = true;//是否是从中文翻译为英文

    private View mTranslateView ;


    private void getTranslation(String source) {
        new AsyncTask<String, Void, String>() {
            CustomProgressDialog pd;
            @Override
            protected String doInBackground(String... strings) {

                Pattern pattern = Pattern.compile("^[\\u0391-\\uFFE5]+$");
                Matcher matcher = pattern.matcher(strings[0]);
                isFromChinese= matcher.matches();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                StringBuilder sb = new StringBuilder("http://fanyi.youdao.com/openapi.do?keyfrom=wangss&key=795137117&type=data&doctype=xml&version=1.1&q=");
                sb.append(strings[0]);

                HttpGet get = new HttpGet(sb.toString());
                String value = "";
                try {
                    HttpResponse response = client.execute(get);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        value = parseXMLByPull(EntityUtils.toString(response.getEntity()),isFromChinese);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return value;
            }


            @Override
            protected void onPostExecute(String s) {
                if (pd!=null){
                    pd.hide();
                }
                mResultTextView.setText(s);
                super.onPostExecute(s);
            }
            @Override
            protected void onPreExecute() {
                pd =  CustomProgressDialog.createDialog(getActivity());
                pd.setTitle("翻译提示");
                pd.setMessage("拼命加载中...");
                pd.setCancelable(true);
                pd.show();
                super.onPreExecute();
            }
        }.execute(source);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mTranslateView= inflater.inflate(R.layout.dictinonary_tab_activity,container,false);
        mTranslateBtn = (Button)mTranslateView.findViewById(R.id.translateBtn);
        mTranslateTextView = (EditText)mTranslateView.findViewById(R.id.sourceEditText);
        mResultTextView = (TextView)mTranslateView.findViewById(R.id.resultEditText);
        mTranslateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(LOG_TYPE, "点击生效，开始解析");
                getTranslation(mTranslateTextView.getText().toString().trim());
            }
        });
        client = new DefaultHttpClient();
        return mTranslateView;
    }
    private String parseXMLByPull(String xmlSource,Boolean isFromChinese) {
        String result = "\n";

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlSource));

            int eventType = xmlPullParser.getEventType();
            ArrayList<String> exs = new ArrayList<>();
            if (!isFromChinese){
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
                                result = phonetic + us_phonetic + uk_phonetic+"翻译:\n ";
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
            }else{
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
                            }   else if ("ex".equals(nodeName)) {
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
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;

    }

}
