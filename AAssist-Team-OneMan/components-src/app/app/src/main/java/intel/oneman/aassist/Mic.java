package intel.oneman.aassist;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.speech.SpeechRecognizer;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by wwwpp on 18/07/2018.
 */

public class Mic {
    static SpeechRecognizer speechhandler;
    static Intent speechIntent;
    public static Activity at;
    static ObjectAnimator b1 , b2 ,b3 , b4;
    static View fb1 , fb2 , fb3 , fb4;
    static ImageView click_speak ;
    static Date lasttime;
    Mic(Activity atv , SpeechRecognizer sr , Intent si , ObjectAnimator ub1 , ObjectAnimator ub2 , ObjectAnimator ub3 , ObjectAnimator ub4){
        at = atv;
        speechhandler = sr;
        speechIntent = si;
        b1 = ub1;
        b2 = ub2;
        b3 = ub3;
        b4 = ub4;
        fb1 = (View)at.findViewById(R.id.loader_ball1);
        fb2 = (View)at.findViewById(R.id.loader_ball2);
        fb3 = (View)at.findViewById(R.id.loader_ball3);
        fb4 = (View)at.findViewById(R.id.loader_ball4);
        click_speak = (ImageView) at.findViewById(R.id.voiceInput);
    }
    static void listen(){

        speechhandler.startListening(speechIntent);
        click_speak.setVisibility(View.INVISIBLE);
        fb1.setVisibility(View.VISIBLE);
        fb2.setVisibility(View.VISIBLE);
        fb3.setVisibility(View.VISIBLE);
        fb4.setVisibility(View.VISIBLE);
        fb1.animate().alpha(1.0f).setDuration(1000).start();
        fb2.animate().alpha(1.0f).setDuration(1000).start();
        fb3.animate().alpha(1.0f).setDuration(1000).start();
        fb4.animate().alpha(1.0f).setDuration(1000).start();
        b1.start();b2.start();b3.start();b4.start();
        if(chatSpace.canspeak)
        chatSpace.susan.stopspeech();
    }
    static void stop(){
            click_speak.setVisibility(View.VISIBLE);
            speechhandler.stopListening();
            fb1.animate().alpha(0.0f).setDuration(1000).start();
            fb2.animate().alpha(0.0f).setDuration(1000).start();
            fb3.animate().alpha(0.0f).setDuration(1000).start();
            fb4.animate().alpha(0.0f).setDuration(1000).start();
            fb1.setVisibility(View.GONE);
            fb2.setVisibility(View.GONE);
            fb3.setVisibility(View.GONE);
            fb4.setVisibility(View.GONE);
            b1.cancel();b2.cancel();b3.cancel();b4.cancel();
    }
}
