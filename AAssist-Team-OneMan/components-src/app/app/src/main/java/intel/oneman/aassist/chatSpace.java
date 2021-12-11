package intel.oneman.aassist;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class chatSpace {
    static Context mcontext;
    public static List<Message> MsgList = new ArrayList<Message>();
    public static LinearLayout chatArea ;
    public static ScrollView jab;
    public static speechIntegration susan;
    public  static boolean canspeak = false;
    public chatSpace(Context cont , LinearLayout chatarea  , ScrollView jab)
    {
        this.mcontext = cont;
        this.chatArea = chatarea;
        this.jab = jab;
    }
    public chatSpace()
    {
        //Initial Connstructor
    }
    public TextView addNewChat(String sender, String message , int input_type)
    {

        LayoutInflater inflater = (LayoutInflater) this.mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.layout, null);
        LinearLayout userinput  = (LinearLayout) rowView.findViewById(R.id.msgparent);
        TextView msgtext = (TextView) rowView.findViewById(R.id.chat_message);
        ImageView sendericon = (ImageView) rowView.findViewById(R.id.aassisticon);
        GradientDrawable gd = (GradientDrawable) msgtext.getBackground().getCurrent();
        msgtext.setText(message);
        if(MsgList.size() > 1)
        {
            Message latest = MsgList.get(MsgList.size()-1);
            if(latest.getSender().compareTo(sender) == 0)
            {
                sendericon.setVisibility(View.GONE);
                userinput.setPadding(6,0,6,0);
                LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                llp.setMargins(0, 4, 0, 0); // llp.setMargins(left, top, right, bottom);
                msgtext.setLayoutParams(llp);
            }
        }
        if(sender.compareTo("CLIENT") == 0)
        {
            userinput.setGravity(Gravity.RIGHT);
            gd.mutate();
            gd.setColor(Color.parseColor("#D3D3D3"));
            sendericon.setBackgroundResource(R.drawable.sender_me);
            sendericon.setImageResource(R.drawable.user);
        }
        Message newmsg = new Message(sender,message);
        MsgList.add(newmsg);
        chatArea.addView(rowView);
        jab.postDelayed(new Runnable() {
            @Override
            public void run() {
                //replace this line to scroll up or down
                jab.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, 100L);

        boolean autostartmic = ((input_type == Feature.INPUT_VOICE)?true:false);
        if(canspeak && sender.compareTo("AASSIST_BOT")==0) {
            susan.speakBaby(message,autostartmic);
        }
        return msgtext;
    }
}
