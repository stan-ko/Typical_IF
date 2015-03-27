package typical_if.android.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.github.johnpersano.supertoasts.SuperCardToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCaptchaDialog;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKError;

import org.json.JSONObject;

import java.net.InetAddress;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import typical_if.android.Constants;
import typical_if.android.ItemDataSetter;
import typical_if.android.NotificationService;
import typical_if.android.OfflineMode;
import typical_if.android.R;
import typical_if.android.VKHelper;
import typical_if.android.VKRequestListener;
import typical_if.android.util.BooleanLock;

public class SplashActivity extends Activity {

    ImageView imageView;

    Locale locale;
    int counter = 5;
    static public Configuration config;
    SharedPreferences firstOpenPref = null;

    final int offsetDefault = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        VKUIHelper.onCreate(this);
        VKSdk.initialize(sdkListener, Constants.APP_ID, VKAccessToken.tokenFromSharedPreferences(this, Constants.TIF_VK_API_KEY_TOKEN));

        firstOpenPref = getSharedPreferences("firstRun", MODE_PRIVATE);

        imageView = (ImageView) findViewById(R.id.splash_logo);

        Animation rotationOrange = AnimationUtils.loadAnimation(this, R.anim.rotate_splash_spinner_orange);
        findViewById(R.id.img_splash_spinner_orange).startAnimation(rotationOrange);

        Animation rotationBlue = AnimationUtils.loadAnimation(this, R.anim.rotate_splash_spinner_blue);
        findViewById(R.id.img_splash_spinner_blue).startAnimation(rotationBlue);

        config = new Configuration();
        if (ItemDataSetter.getUserLan() != "") {
            config.locale = ItemDataSetter.loadUserLanguage();
            getApplicationContext().getResources().updateConfiguration(config, getApplicationContext().getResources().getDisplayMetrics());

        } else {
            locale = new Locale("ua");
            Locale.setDefault(locale);

            config = new Configuration();
            config.locale = locale;
            getApplicationContext().getResources().updateConfiguration(config, getApplicationContext().getResources().getDisplayMetrics());

        }

        final SuperCardToast superCardToast = new SuperCardToast(SplashActivity.this);
        superCardToast.setText(getString(R.string.main_creed_1));
        superCardToast.setDuration(SuperToast.Duration.EXTRA_LONG);
        superCardToast.setAnimations(SuperToast.Animations.FADE);
        superCardToast.show();

        findViewById(R.id.card_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                superCardToast.dismiss();

                SuperCardToast.create(
                        SplashActivity.this,
                        getString(R.string.main_creed_2),
                        SuperToast.Duration.EXTRA_LONG,
                        SuperToast.Animations.FADE
                    ).show();

                v.setEnabled(false);
            }
        });

        ItemDataSetter.loadUserId();
        ItemDataSetter.loadUserLanguage();

        checkIfOnlineAndProceed();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Locale myLocale = ItemDataSetter.loadUserLanguage();
        if (myLocale != null) {
            newConfig.locale = myLocale;
            Locale.setDefault(myLocale);
            getApplicationContext().getResources().updateConfiguration(newConfig, getApplicationContext().getResources().getDisplayMetrics());
        }
    }

    void showAlertNoInternet() {
        //Log.d("----------------Internet conection Error", "------------------------");
        counter--;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name)
                .setCancelable(false)
                .setMessage(getString(R.string.no_internet_chooser))
                .setPositiveButton(getString(R.string.retry) + " (" + counter + ")", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (counter < 2) {
                            startActivity(new Intent(Settings.ACTION_SETTINGS));
                            counter = 5;
                        }
                        checkIfOnlineAndProceed();
                        builder.setCancelable(true);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        builder.setCancelable(true);
                    }
                });
        if (!isFirstOpen() & OfflineMode.loadJSON(Constants.TF_ID) != null) {
            builder.setNeutralButton(getString(R.string.offline), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    builder.setCancelable(true);
                    startNextActivity();
                }
            });
        }
        builder.create().show();
    }

    private boolean isFirstOpen() {
        boolean temp = false;
        if (firstOpenPref.getBoolean("firstRun", true)) {
            temp = true;
            firstOpenPref.edit().putBoolean("firstRun", false).commit();
        }
        return temp;
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("vk.com");

            if (ipAddr.equals("")) {
                return false;
            } else {
                return true;
            }

        } catch (Exception e) {
            return false;
        }

    }

    void checkIfOnlineAndProceed() {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
//                        boolean isICA = com.stanko.tools.InternetConnectionHelper.checkHostByConnection("vk.com");
                        if (!isInternetAvailable()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showAlertNoInternet();
                                }
                            });

                        }else {
                            makeRequests();
                        }
                    }
                }
        ).start();
    }


    private final AtomicInteger threadsCounter = new AtomicInteger(4);
    private final BooleanLock isRequestErrorToastShown = new BooleanLock();

    private void makeRequests() {
        //final AtomicInteger requestSessionThreadsCounter = threadsCounter;
        //   --------------------START------------- all Request from internet before start APP----------------------
        VKHelper.doGroupWallRequest(offsetDefault, Constants.TIF_VK_PRELOAD_POSTS_COUNT, Constants.TF_ID, new VKRequestListener() {
            @Override
            public void onSuccess() {
                handleRequestComplete(vkJson, Constants.TF_ID);
            }

//            @Override
//            public void onError() {
//                showErrorToast();
//            }
        });
        VKHelper.doGroupWallRequest(offsetDefault, Constants.TIF_VK_PRELOAD_POSTS_COUNT, Constants.TZ_ID, new VKRequestListener() {
            @Override
            public void onSuccess() {
                handleRequestComplete(vkJson, Constants.TZ_ID);
            }

//            @Override
//            public void onError() {
//                showErrorToast();
//            }
        });
        VKHelper.doGroupWallRequest(offsetDefault, Constants.TIF_VK_PRELOAD_POSTS_COUNT, Constants.FB_ID, new VKRequestListener() {
            @Override
            public void onSuccess() {
                handleRequestComplete(vkJson, Constants.FB_ID);
            }

//            @Override
//            public void onError() {
//                showErrorToast();
//            }
        });
        VKHelper.doGroupWallRequest(offsetDefault, Constants.TIF_VK_PRELOAD_POSTS_COUNT, Constants.FN_ID, new VKRequestListener() {
            @Override
            public void onSuccess() {
                handleRequestComplete(vkJson, Constants.FN_ID);
            }

//            @Override
//            public void onError() {
//                showErrorToast();
//            }
        });
        VKHelper.doGroupWallRequest(offsetDefault, Constants.TIF_VK_PRELOAD_POSTS_COUNT, Constants.ZF_ID, new VKRequestListener() {
            @Override
            public void onSuccess() {
                handleRequestComplete(vkJson, Constants.ZF_ID);
            }

//            @Override
//            public void onError() {
//                showErrorToast();
//            }
        });
        VKHelper.doGroupWallRequest(offsetDefault, Constants.TIF_VK_PRELOAD_POSTS_COUNT, Constants.ST_ID, new VKRequestListener() {
            @Override
            public void onSuccess() {
                handleRequestComplete(vkJson, Constants.ST_ID);
            }

//            @Override
//            public void onError() {
//                showErrorToast();
//            }
        });
        //-------------------------END-------- all Request from internet before start APP----------------------
    }

    void handleRequestComplete(final JSONObject json, final long id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OfflineMode.saveJSON(json, id);
                decrementThreadsCounter(threadsCounter);
            }
        }).start();
    }

    void handleRequestError() {
        decrementThreadsCounter(threadsCounter);
        synchronized (isRequestErrorToastShown) {
            if (!isRequestErrorToastShown.value) {
                isRequestErrorToastShown.value = true;
                OfflineMode.onErrorToast();
            }
        }
    }

    void decrementThreadsCounter(final AtomicInteger requestSessionThreadsCounter) {
        if (threadsCounter != requestSessionThreadsCounter)
            return;
        if (requestSessionThreadsCounter.decrementAndGet() == 0 && !isFinishing())
            startNextActivity();
    }

    private void startNextActivity() {
        final Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);

        try {
            if (OfflineMode.isFirstRun("SplashFirstRun")) {
                startService(new Intent(this, NotificationService.class).setAction(Constants.ACTION_FIRST_RUN));
            } else if (OfflineMode.loadInt(Constants.DATE_OF_NOTIF_SEND) != Calendar.getInstance().get(Calendar.DATE)) {
                startService(new Intent(this, NotificationService.class).setAction(Constants.ACTION_START_FROM_SPLASH_ACTIVITY));
            }
        }
        catch (NumberFormatException nfe) {
            startService(new Intent(this, NotificationService.class).setAction(Constants.ACTION_FIRST_RUN));
        }
        catch (NullPointerException npe){
            startService(new Intent(this, NotificationService.class).setAction(Constants.ACTION_FIRST_RUN));
        }
        finish();
    }


    private final VKSdkListener sdkListener = new VKSdkListener() {
        @Override
        public void onCaptchaError(VKError captchaError) {
            new VKCaptchaDialog(captchaError).show();
        }

        @Override
        public void onTokenExpired(VKAccessToken expiredToken) {
            VKSdk.authorize(Constants.S_MY_SCOPE);
        }

        @Override
        public void onAccessDenied(final VKError authorizationError) {
            new AlertDialog.Builder(SplashActivity.this)
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
}
