package intel.oneman.aassist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import java.util.HashMap;
import java.util.Locale;


public class speechIntegration implements TextToSpeech.OnInitListener {
    private TextToSpeech tts;
    static boolean ready = false, allowed = false , installed = false;
    private static Context ctx;
    static final int CHECK_CODE = 0x1;

    speechIntegration(Context ctx) {
        tts = new TextToSpeech(ctx, this);
        this.ctx = ctx;

    }

    public boolean isAllowed() {
        return allowed;
    }

    public void allow(boolean allowed) {
        this.allowed = allowed;
    }

    public void speakBaby(String msg, boolean autostartmic) {
        HashMap<String, String> hash = new HashMap<String, String>();
        hash.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_NOTIFICATION));
        tts.speak(msg, TextToSpeech.QUEUE_ADD, hash);
        final boolean voicein = autostartmic;

        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                Mic.stop();
            }

            @Override
            public void onDone(String utteranceId) {

            }

            @Override
            public void onError(String utteranceId) {

            }
        });
    }

    public void stopspeech(){
        tts.stop();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.US);
            tts.setPitch(0.85f);
            ready = true;
        } else {
            ready = false;
        }
    }
}
