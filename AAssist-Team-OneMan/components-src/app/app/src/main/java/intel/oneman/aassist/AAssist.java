package intel.oneman.aassist;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.PeriodicSync;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.FaceDetector;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.Manifest;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.os.Build;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;


import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;
import java.util.List;
import java.util.Random;

import static android.provider.Telephony.ServiceStateTable.AUTHORITY;
import static intel.oneman.aassist.Feature.INPUT_NONE;
import static intel.oneman.aassist.Feature.NAME;
import static intel.oneman.aassist.botLogic.GARBAGE;

public class AAssist extends AppCompatActivity {
    private ContentObserver mObserver;
    List<Message> MsgList = new ArrayList<Message>();
    public LinearLayout chatArea ;
    final Context this_ctx = this;
    Intent pf_intent ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aassist);
        /*getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                        */
        final TextView userInput = (TextView) findViewById(R.id.userInput);
        final ImageView micbutton = (ImageView) findViewById(R.id.voiceInput);
        final RelativeLayout releasemic = (RelativeLayout) findViewById(R.id.release_mic);
        final SpeechRecognizer mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        final Intent mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        final ImageView mode1chat = (ImageView) findViewById(R.id.edittextsend);
        final TextView mode1chatv = (TextView) findViewById(R.id.edittextu);
        chatArea = (LinearLayout) findViewById(R.id.chat_area);
        ScrollView jab = (ScrollView) findViewById(R.id.scroll_view);
        checkTTS();
        final chatSpace newspace = new chatSpace(this,chatArea,jab);
        newspace.addNewChat("AASSIST_BOT","Hello, I'm Susan. How may I help you ?",Feature.INPUT_VOICE);
        final botLogic susan = new botLogic(this);
        susan.voiceinp = (RelativeLayout) findViewById(R.id.voiceinputmode);
        susan.textinp = (RelativeLayout) findViewById(R.id.textinputmode);
        final View ball1 = (View) findViewById(R.id.loader_ball1);
        final View ball2 = (View) findViewById(R.id.loader_ball2);
        final View ball3 = (View) findViewById(R.id.loader_ball3);
        final View ball4 = (View) findViewById(R.id.loader_ball4);
        final ObjectAnimator up1 = ObjectAnimator.ofFloat(ball1,"translationY", 0, -15, 0);
        final ObjectAnimator up2 = ObjectAnimator.ofFloat(ball2,"translationY", 0, -15, 0);
        final ObjectAnimator up3 = ObjectAnimator.ofFloat(ball3,"translationY", 0, -15, 0);
        final ObjectAnimator up4 = ObjectAnimator.ofFloat(ball4,"translationY", 0, -15, 0);
        final Mic mic = new Mic(this,mSpeechRecognizer,mSpeechRecognizerIntent,up1,up2,up3,up4);
        final ImageView load_settings = (ImageView)findViewById(R.id.settingsedit);

        mode1chat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //v.getId() will give you the image id
                String textval = mode1chatv.getText().toString();
                if(textval.length() > 0){
                    newspace.addNewChat("CLIENT",susan.field_name_display+" : "+textval, INPUT_NONE);
                    susan.newcommand(textval,1);
                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
                else{
                    mode1chatv.setHint("Invalid value");
                }
            }
        });
        checkPermission();


        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,100000);

        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.ENGLISH);

        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {

            @Override
            public void onReadyForSpeech(Bundle bundle) {
            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {
                mic.stop();
            }

            @Override
            public void onError(int i) {
                if(i == mSpeechRecognizer.ERROR_SPEECH_TIMEOUT || i == mSpeechRecognizer.ERROR_NO_MATCH){
                        mic.stop();
                }
            }

            @Override
            public void onResults(Bundle bundle) {
                //getting all the matches
                ArrayList<String> matches = bundle
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                //displaying the first match
                if (matches != null) {
                    String match = matches.get(0)+" " ,  token="" , final_match="" , num_part="";
                    boolean num_start = false;
                    for(int i  =0 ; i < match.length() ; i++){
                        char ch = match.charAt(i);
                        if(ch == ' '){
                            if(num_start){
                                int nxt_bl = match.indexOf(' ',(i+1));
                                if(nxt_bl >= 0 && nxt_bl < match.length()){
                                    String prob_curr = match.substring(i+1,nxt_bl);
                                    if(prob_curr.compareTo("lacs") == 0 || prob_curr.compareTo("lakhs") == 0){
                                        String val = Double.toString(Double.parseDouble(num_part)*Math.pow(10,5));
                                        final_match+=val.substring(0,val.length()-2);
                                        i=match.indexOf(' ',(i+1))-1;
                                    }
                                    else if(prob_curr.compareTo("crores") == 0 || prob_curr.compareTo("crore") == 0){
                                        String val = Double.toString(Double.parseDouble(num_part)*Math.pow(10,7));
                                        final_match+=val.substring(0,val.length()-2);
                                        i=match.indexOf(' ',(i+1))-1;
                                    }
                                    else{
                                        final_match+=num_part;
                                    }
                                }else{
                                    final_match+=num_part;
                                }
                                num_start = false;
                                num_part="";
                            }
                        }
                        if(Character.isDigit(ch) || (num_start == true && ch == '.')){
                            num_start = true;
                            num_part += ch;
                        }
                        else{
                            final_match+=ch;
                        }
                    }
                    newspace.addNewChat("CLIENT", final_match, Feature.INPUT_VOICE);
                    susan.newcommand(final_match,2);
                    userInput.setText("");

                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });
        /*pf = new PullFeeds(getBaseContext());
        pf_intent = new Intent(getBaseContext(), pf.getClass());

        if (!isMyServiceRunning(pf.getClass())) {
            startService(pf_intent);
        }*/
        /*mObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
            public void onChange(boolean selfChange) {
                // Do something.
            }
        };
        pullSync.initializeSyncAdapter(getApplicationContext());*/

        mic.listen();
        final int ANIM_TIME = 800; //200ms
        up1.setDuration(ANIM_TIME);
        up2.setDuration(ANIM_TIME);
        up3.setDuration(ANIM_TIME);
        up4.setDuration(ANIM_TIME);
        up1.setRepeatCount(ValueAnimator.INFINITE);
        up2.setRepeatCount(ValueAnimator.INFINITE);
        up3.setRepeatCount(ValueAnimator.INFINITE);
        up4.setRepeatCount(ValueAnimator.INFINITE);
        up1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        up2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                up2.setStartDelay(ANIM_TIME);
                up2.start();
            }
        });
        up3.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                up3.setStartDelay(ANIM_TIME*2);
                up3.start();
            }
        });
        up4.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                up4.setStartDelay(ANIM_TIME*3);
                up4.start();
            }
        });


        //do1.start();
        //do2.start();
        //do3.start();
        //do4.start();

        /*micbutton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Toast.makeText(this,"HELLO BROO",Toast.LENGTH_LONG);
                        mic.listen();
                        return true;
                }
                return false;
            }
        });*/

        micbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mic.listen();
            }
        });
        load_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // LOAD SETTINGS ACTIVITY
                Intent intent = new Intent(AAssist.this,AppSettings.class);
                startActivity(intent);
            }
        });

        /*newspace.addNewChat("CLIENT","show me my quick send contacts");
        tr
        y {
            susan.newcommand("can you show me my transaction history for the previous 4 days", 2);
        }catch (Exception ex){
            ex.printStackTrace();
        }*/

        boolean pull= getIntent().getBooleanExtra("PullTransfer",false);
        if(pull){
            String toemail= getIntent().getStringExtra("toemail");
            int amount= getIntent().getIntExtra("amount",0);
            newspace.addNewChat("AASSIST_BOT","Completing pull transfer of "+amount+" rupees to "+toemail,INPUT_NONE);
            susan.newcommand("transfer "+amount+" rupees to email", 2);
            botLogic.currentFeature.fvalues[botLogic.currentFeature.getIndex(NAME,"b_email")] = toemail;
            botLogic.currentFeature.fvalues[botLogic.currentFeature.getIndex(Feature.NAME,"benef_mode")] = "Email";
            botLogic.currentFeature.fvalues[botLogic.currentFeature.getIndex(Feature.NAME,"b_accno")] = GARBAGE;
            botLogic.currentFeature.fvalues[botLogic.currentFeature.getIndex(Feature.NAME,"b_ifsc")] = GARBAGE;
            botLogic.currentFeature.fvalues[botLogic.currentFeature.getIndex(Feature.NAME,"qsend")] = GARBAGE;
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();
            }
        }
    }
    public void checkTTS() {
        Intent check = new Intent();
        check.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(check, speechIntegration.CHECK_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == speechIntegration.CHECK_CODE){
            if(resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS){
                speechIntegration.installed=true;
                speechIntegration si  = new speechIntegration(this_ctx);
                chatSpace.susan = si;
                chatSpace.canspeak = true;
            }else {
                Intent install = new Intent();
                install.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(install);
            }
        }
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }
    @Override
    protected void onDestroy() {
        //stopService(pf_intent);
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();
    }
}
