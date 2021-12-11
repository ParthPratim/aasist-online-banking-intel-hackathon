package intel.oneman.aassist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class dialogOptions {
    public static Context appcontext;
    public dialogOptions(Context appcontext){
        this.appcontext = appcontext;
    }
    public void displayDialog(final String[] val, final String header  , final botLogic aih, final chatSpace currspace)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(appcontext);
        builder.setCancelable(false);
        builder.setTitle(header);

        int checkedItem = -1;
        builder.setSingleChoiceItems(val, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user checked an item
                ListView lv = ((AlertDialog)dialog).getListView();
                lv.setTag(new Integer(which));
            }
        });

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ListView lw = ((AlertDialog)dialog).getListView();
                if(lw.getCheckedItemPosition() >= 0 && lw.getCheckedItemPosition() < val.length){
                    Object checkedItem = lw.getAdapter().getItem(lw.getCheckedItemPosition());
                    currspace.addNewChat("CLIENT",checkedItem.toString(),Feature.INPUT_NONE);
                    aih.newcommand(checkedItem.toString(),3);
                }
                else{
                    displayDialog(val,header,aih,currspace);
                }
            }
        });

        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

}
