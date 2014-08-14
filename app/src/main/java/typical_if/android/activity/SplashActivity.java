package typical_if.android.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCaptchaDialog;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import typical_if.android.Constants;
import typical_if.android.ItemDataSetter;
import typical_if.android.OfflineMode;
import typical_if.android.R;
import typical_if.android.VKHelper;

public class SplashActivity extends Activity implements Animation.AnimationListener {

    Animation animMoveDown;
    Animation animFadeIn;

    TextView textView;
    ImageView imageView;

    Locale locale;
    Configuration config;
    private static String sTokenKey = "VK_ACCESS_TOKEN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        textView = (TextView) findViewById(R.id.splash_title);
        imageView = (ImageView) findViewById(R.id.splash_logo);

        animMoveDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_down);
        animMoveDown.setAnimationListener(this);
        textView.startAnimation(animMoveDown);

        animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        //animFadeIn.setAnimationListener(this);
        imageView.startAnimation(animFadeIn);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent i = new Intent(SplashActivity.this, MainActivity.class);
//                startActivity(i);
//                finish();
//            }
//        }, 5000);

        locale = new Locale("uk");
        Locale.setDefault(locale);

        config = new Configuration();
        config.locale = locale;
        getApplicationContext().getResources().updateConfiguration(config, getApplicationContext().getResources().getDisplayMetrics());

        VKUIHelper.onCreate(this);
        VKSdk.initialize(sdkListener, Constants.APP_ID, VKAccessToken.tokenFromSharedPreferences(this, sTokenKey));

        ItemDataSetter.loadUserId();
    }

    void showAlertNoInternet() {
        Log.d("----------------Internet conection Error", "------------------------");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name)
                .setMessage("No fucking active Internet connection is available. Would you like to")
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        checkIfOnlineAndProceed();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        // TODO: add neutral button if has cached data to work offline with

        builder.create().show();
    }

    void checkIfOnlineAndProceed() {
        if ( OfflineMode.isOnline(getApplicationContext()) ) {
            makeRequests();
        } else {
            showAlertNoInternet();
        }
    }

    private void makeRequests(){
        threadsCounter = new AtomicInteger(4);
        //   --------------------START------------- all Request from internet before start APP----------------------
        VKHelper.doGroupWallRequest(Constants.TF_ID, new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                OfflineMode.saveJSON(response.json, Constants.TF_ID);
                decrementThreadsCounter();
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                decrementThreadsCounter();
            }
        });
        VKHelper.doGroupWallRequest(Constants.TZ_ID, new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                OfflineMode.saveJSON(response.json, Constants.TZ_ID);
                decrementThreadsCounter();
            }
            @Override
            public void onError(VKError error) {
                super.onError(error);
                decrementThreadsCounter();
            }
        });
        VKHelper.doGroupWallRequest(Constants.FB_ID, new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                OfflineMode.saveJSON(response.json, Constants.FB_ID);
                decrementThreadsCounter();
            }
            @Override
            public void onError(VKError error) {
                super.onError(error);
                decrementThreadsCounter();
            }
        });
        VKHelper.doGroupWallRequest(Constants.FN_ID, new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                OfflineMode.saveJSON(response.json, Constants.FN_ID);
                decrementThreadsCounter();
            }
            @Override
            public void onError(VKError error) {
                super.onError(error);
                decrementThreadsCounter();
            }
        });
        //-------------------------END-------- all Request from internet before start APP----------------------
    }

    AtomicInteger threadsCounter;
    void decrementThreadsCounter(){
        if (threadsCounter.decrementAndGet()==0)
            startNextActivity();
    }

    private void startNextActivity() {
        final Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    private final VKSdkListener sdkListener = new VKSdkListener() {
        @Override
        public void onCaptchaError(VKError captchaError) {
            new VKCaptchaDialog(captchaError).show();
        }

        @Override
        public void onTokenExpired(VKAccessToken expiredToken) {
            VKSdk.authorize(Constants.sMyScope);
        }

        @Override
        public void onAccessDenied(final VKError authorizationError) {
            new AlertDialog.Builder(VKUIHelper.getTopActivity())
                    .setMessage(authorizationError.toString())
                    .show();
        }

        @Override
        public void onReceiveNewToken(VKAccessToken newToken) {

        }

        @Override
        public void onAcceptUserToken(VKAccessToken token) {
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    // Animation listener implementation
    @Override
    public void onAnimationStart(Animation animation) {}

    @Override
    public void onAnimationEnd(Animation animation) {
        checkIfOnlineAndProceed();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {}
}
