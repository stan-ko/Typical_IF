package typical_if.android.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.vk.sdk.api.model.VKApiVideo;

import typical_if.android.R;

public class WebViewActivity extends Activity {
    private WebView webView;

    private WebChromeClient.CustomViewCallback customViewCallback;

    private WebChromeClient mWebChromeClient;
    private myWebViewClient mWebViewClient;

    VKApiVideo video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        video = (VKApiVideo)getIntent().getExtras().get("VIDEO_OBJECT");
        setContentView(R.layout.fragment_web_view);
        playVideo(video.player);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        webView.loadUrl("about:blank");
    }

    public WebViewActivity (){}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
      //  getMenuInflater().inflate(R.menu.menu_web_view, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
       // int id = item.getItemId();

        //noinspection SimplifiableIfStatement
      //  if (id == R.id.action_settings) {
        //    return true;
       // }

        return super.onOptionsItemSelected(item);
    }

    public void playVideo(String url) {

        webView = (WebView) findViewById(R.id.webView);

        mWebViewClient = new myWebViewClient();
        webView.setWebViewClient(mWebViewClient);

        mWebChromeClient = new WebChromeClient();
        webView.setWebChromeClient(mWebChromeClient);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSaveFormData(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.clearCache(true);
        CookieSyncManager.createInstance(this);
        final CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();

        webView.loadUrl(url);
    //    webView.setBackgroundColor(Color.TRANSPARENT);
//        webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
//        for (int i =0 ; i<video.photo.size();i++ ) {
//            if (i==video.photo.size()){
//
//            }
//        }
//        webView.setBackground();
//    }
    }


    class myWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return true;
        }
    }



}
