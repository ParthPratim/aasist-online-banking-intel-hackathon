package intel.oneman.aassist;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import static intel.oneman.aassist.Feature.INPUT_VOICE;

public class AppSettings extends AppCompatActivity {
    SharedPreferences pref ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);
        final ImageView editsettings = (ImageView)findViewById(R.id.editsettings);
        final ImageView editemail = (ImageView)findViewById(R.id.editemailsettings);
        final TextView em_n = (TextView)findViewById(R.id.logged_email);
        final TextView ip_n = (TextView)findViewById(R.id.server_ip);
        pref = getApplicationContext().getSharedPreferences("AassistAppSettings", Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS); // 0 - for private mode
        ip_n.setText(pref.getString("AassistServer","13.231.103.199"));
        em_n.setText(pref.getString("email","friends@intel.com"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        editsettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askServerUrl();
            }
        });
        editemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askEmail();
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    void askServerUrl(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Server Address");

        final EditText input = new EditText(this);
        final TextView sip = (TextView) findViewById(R.id.server_ip);
        input.setHint(R.string.addrinput);
        input.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
        builder.setView(input);
        final Activity curract = this;
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String ip = input.getText().toString();
                RESTconsumer  rc = new RESTconsumer("http://"+ip+":2018/servertest","GET");
                try{
                    Toast.makeText(curract,"Trying to reach server at "+ip+" ...",Toast.LENGTH_LONG).show();
                    String response = rc.execute("").get();
                    JSONObject j = new JSONObject(response);
                    if(j.has("status")){
                        if(j.getString("status").compareTo("running") == 0){
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("AassistServer",ip);
                            editor.commit();
                            sip.setText(ip);
                            botLogic.AASSIST_SERVER_IP = ip;
                            Toast.makeText(curract,"Server reached ... Updating Server address ... Please Wait",Toast.LENGTH_LONG).show();
                            Toast.makeText(curract,"Server IP Updated",Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        Toast.makeText(curract,"Improper Response...check the address again",Toast.LENGTH_LONG).show();
                    }
                }catch(Exception ex){
                    ex.printStackTrace();
                    Toast.makeText(curract,"Failed to reach Aassist server host...check the address again",Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    void askEmail(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Server Address");

        final EditText input = new EditText(this);
        final TextView sip = (TextView) findViewById(R.id.server_ip);
        final Activity curract = this;
        final String []val = {"friends@intel.com","www.ppc007logs@gmail.com"};
        final String []token= {"fa08f366b068e7e679df10492e1337598a6f27d19aa6258f3c8a48cc8bbb1b04","94df97959544eb298a740b495bba949759f6aade088bfa72dd14693bab4e1688"};
        int checkedItem = -1;
        builder.setSingleChoiceItems(val, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user checked an item

            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ListView lw = ((AlertDialog)dialog).getListView();
                //Object checkedItem = lw.getAdapter().getItem(lw.getCheckedItemPosition());
                String em = val[lw.getCheckedItemPosition()];
                String tok = token[lw.getCheckedItemPosition()];
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("email", em);
                editor.putString("access_token", tok);
                editor.commit();
                botLogic.USER_EMAIL = em;
                botLogic.ACCESS_TOKEN = tok;
                Toast.makeText(curract,em+" has been selected as the current user",Toast.LENGTH_LONG).show();
                ((TextView)findViewById(R.id.logged_email)).setText(em);
                FMS.updateTopic(pref);
                new chatSpace().addNewChat("AASSIST_BOT","The logged-in user was changed to "+em,INPUT_VOICE);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }
}
