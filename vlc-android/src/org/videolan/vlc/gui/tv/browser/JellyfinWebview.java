package org.videolan.vlc.gui.tv.browser;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.videolan.medialibrary.interfaces.media.AbstractMediaWrapper;
import org.videolan.medialibrary.media.MediaWrapper;
import org.videolan.vlc.R;
import org.videolan.vlc.media.MediaUtils;
import org.videolan.vlc.media.MediaWrapperList;
import org.videolan.vlc.util.Util;

import java.util.ArrayList;

public class JellyfinWebview extends BaseTvActivity {

    private WebView mWebView;
    String indexpage = "file:///android_asset/www/index.html";

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                String url = msg.obj.toString();
                MediaUtils.INSTANCE.openMediaNoUi(Uri.parse(url));
            }else if(msg.what == 2){
                ArrayList<AbstractMediaWrapper> list = getList(msg.obj.toString());
                MediaUtils.INSTANCE.openList(getBaseContext(),list,0);
            }
        }
    };

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browser_jellyfinweb);
        mWebView = findViewById(R.id.jellyfinWebview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setBlockNetworkImage(false);
        mWebView.getSettings().setBlockNetworkLoads(false);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.getSettings().setGeolocationEnabled(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setDefaultTextEncodingName("UTF-8");
        //mWebView.getSettings().setUserAgentString(mWebView.getSettings().getUserAgentString() + " TV");

        mWebView.addJavascriptInterface(this,"ToVlcPlay");

        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        mWebView.loadUrl(indexpage);

    }

    @JavascriptInterface
    public void toPlay(String url){
        Message msg = new Message();
        msg.what = 1;
        msg.obj = url;
        handler.sendMessage(msg);
    }

    @JavascriptInterface
    public void toPlayList(String items){
        Message msg = new Message();
        msg.what = 2;
        msg.obj = items;
        handler.sendMessage(msg);
    }

    @JavascriptInterface
    public void appExit(){

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void refresh() {}

    public ArrayList<AbstractMediaWrapper>  getList(String items){
        ArrayList<AbstractMediaWrapper> list = new ArrayList<AbstractMediaWrapper>();

        JSONArray jsa = null;
        try {
            jsa = new JSONArray(items);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for(int i=0;i<jsa.length();i++){
            try {

                JSONObject jso = jsa.getJSONObject(i);
                String title = jso.getString("title");
                String url = jso.getString("url");
                MediaWrapper mediaWrapper =  new MediaWrapper(Uri.parse(url));
                mediaWrapper.setTitle(title);
                list.add(mediaWrapper);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list ;
    }
}
