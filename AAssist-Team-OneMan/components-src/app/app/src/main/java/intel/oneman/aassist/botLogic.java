package intel.oneman.aassist;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.spec.ECField;
import java.text.DecimalFormat;
import java.util.Random;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static intel.oneman.aassist.Feature.INPUT_NONE;
import static intel.oneman.aassist.Feature.INPUT_KEYBOARD;
import static intel.oneman.aassist.Feature.INPUT_VOICE;
import static intel.oneman.aassist.Feature.INPUT_SELECT_LIST;
import static intel.oneman.aassist.Feature.NAME;
import static intel.oneman.aassist.RESTconsumer.CONNECTION_TIMEOUT;
import static intel.oneman.aassist.RESTconsumer.READ_TIMEOUT;
import static intel.oneman.aassist.RESTconsumer.REQUEST_METHOD;


public class botLogic{
    public static final String GARBAGE = "***GARBAGE#VALUE***";
    public static String currentContext = "pcontext.none" ;
    public static Feature currentFeature;
    public static Context appcontext;
    public  static RelativeLayout voiceinp , textinp;
    public static boolean expectedInput = false;
    public static String expectedfield = "",field_name_display="";// STORES NAME OF FIELD THAT IS EXPECTED AS INPUT
    public static int freq = 1; // FEILD EXPECTED TO BE INPUT

    public static String AASSIST_SERVER_IP , USER_EMAIL , ACCESS_TOKEN;

    public static Feature[] flist = new Feature[]{
            new Feature("registerbank-followup","RegisterBank","register an internet banking account", "/regsaccount/", 1 , R.string.RegisterBank ,
                    new String[]{"bank"},
                    new int[]{1},
                    3,
                    new String[]{"bankid","buser","bpasswd"},
                    new String[]{"Bank Name","Bank's internet banking username","Bank's internet banking Password"},
                    new String[]{"string","string","string"},
                    new int[]{INPUT_VOICE,INPUT_KEYBOARD,INPUT_KEYBOARD}),
            new Feature("fetchlinkedaccounts-followup","FetchLinkedAccounts","fetch your linked accounts","/linkedaccounts/",1,R.string.FetchLinkedAccoounts,
                    new String[]{"bank"},
                    new int[]{1},
                    1,
                    new String[]{"bankid"},
                    new String[]{"Bank Name"},
                    new String[]{"string"},
                    new int[]{INPUT_VOICE}),
            new Feature("balanceenquiry-followup","BalanceEnquiry","check you balance","/enqbalance/",1,R.string.BalanceEnquiry,
                    new String[]{"bank"},
                    new int[]{1},
                    2,
                    new String[]{"bankid","accno"},
                    new String[]{"Bank Name","Account Number"},
                    new String[]{"string","int"},
                    new int[]{INPUT_VOICE,INPUT_SELECT_LIST}),
            new Feature("quicksend-followup","QuickSend","fetch your quick send contacts","/quicksend/fetchall",0,R.string.QuickSendContacts,
                    new String[]{},
                    new int[]{},
                    0,
                    new String[]{},
                    new String[]{},
                    new String[]{},
                    new int[]{}),
            new Feature("transacthistory-followup","TransactHistory","find your transaction history","/transacthistory/",2,R.string.TransactionHistory,
                    new String[]{"timespan","spantype"},
                    new int[]{1,2},
                    2,
                    new String[]{"timespan","spantype"},
                    new String[]{"Time span fetch history for","Sorting type from amoung days, months and year"},
                    new String[]{"int","string"},
                    new int[]{INPUT_VOICE,INPUT_VOICE}),
            new Feature("quicksendregister-followup","QuickSendRegister","add a quick send contact","/quicksend/create",0,R.string.QuickSendContactAdd,
                    new String[]{},
                    new int[]{},
                    3,
                    new String[]{"name","accno","ifsc"},
                    new String[]{"Contact Name","Account Number of the Contact","IFSC of the branch"},
                    new String[]{"string","int","string"},
                    new int[]{INPUT_VOICE,INPUT_KEYBOARD,INPUT_KEYBOARD}),
            new Feature("setdefaultaccount-followup","SetDefaultAccount","set a default account","/defaultaccount/",1,R.string.SetDefaultAccount,
                    new String[]{"bank"},
                    new int[]{1},
                    3,
                    new String[]{"bankid","sda_accno","ifsc"},
                    new String[]{"Bank Name","Account Number to mark as default","IFSC of the account number"},
                    new String[]{"string","int","string"},
                    new int[]{INPUT_VOICE,INPUT_SELECT_LIST,INPUT_KEYBOARD}),
            new Feature("pulltransfer-followup","PullTransfer","make a pull transfer","/pulltransfer/",1,R.string.PullTransfer,
                    new String[]{"amount"},
                    new int[]{1},
                    2,
                    new String[]{"amount","toemail"},
                    new String[]{"Amount to request for","E-mail address of the Payee"},
                    new String[]{"int","string"},
                    new int[]{INPUT_VOICE,INPUT_KEYBOARD}),
            new Feature("fetchdefaultacc-followup","FtechDefaultAcc","fetch your default account","/fetchdefaultaccount/",0,R.string.FetchDefaultAcc,
                    new String[]{},
                    new int[]{},
                    0,
                    new String[]{},
                    new String[]{},
                    new String[]{},
                    new int[]{}),
            new Feature("fundtransfer-followup","FundTransfer","make a fund transfer","/fundtransfer/",5,R.string.FundTransfer ,
                     new String[]{"amount","bank","receiver","default_account","sender"},
                     new int[]{1,2,3,8,9},
                     11,
                     new String[]{"amount","bankid","benef_mode","qsend","b_email",
                             "b_accno","b_ifsc","def_acc","trans_type","payment_mode","r_accno"} ,
                     new String[]{"Amount","Bank Name","Receiver Mode","QuickSend Contact Name","Beneficiary Email address", "Beneficiary Account Number",
                             "Beneficiary IFSC","Default Account",
                             "Transaction Type","Payment Mode","Remitance Account Number"},
                     new String[]{"int","string","string","string","string","string",
                             "string","string","string","string","string"},
                     new int[]{INPUT_VOICE,INPUT_VOICE,INPUT_SELECT_LIST,INPUT_VOICE,INPUT_KEYBOARD,INPUT_KEYBOARD,
                             INPUT_KEYBOARD,INPUT_NONE,INPUT_SELECT_LIST,INPUT_SELECT_LIST,
                             INPUT_SELECT_LIST}),
            new Feature("upi-followup","UPI","make a UPI transaction","",1,R.string.UPI,
                    new String[]{"amount"},
                    new int[]{1},
                    3,
                    new String[]{"amount","b_vpa","b_name"},
                    new String[]{"amount","Virtual Private Address of Payee","Name of Payee"},
                    new String[]{"int","string","string"},
                    new int[]{INPUT_VOICE,INPUT_KEYBOARD,INPUT_KEYBOARD})
    };
    static  Random rnd = new Random();
    public static int client_id=10000 + rnd.nextInt(90000);

    final static String DIALOGFLOW = "https://api.dialogflow.com/v1/query?v=20150910&sessionId="+client_id+"&lang=en&timezone=Asia/Kolkata&query=";
    chatSpace chatview = new chatSpace();
    public static netbAccount bacc = new netbAccount("","","");
    public void newcommand(String msg,int src) {
        String message=msg;
        try {
            message = URLEncoder.encode(msg,"UTF-8");
        }catch (Exception e) {
            e.printStackTrace();
        }

        if(expectedInput){
            boolean process = false;
            String process_data="",process_field=expectedfield;
            if(src==1){
                msg = msg.trim();
                if(msg.length() > 0){
                    int index = currentFeature.getIndex(Feature.NAME,expectedfield);
                    String dtype = currentFeature.fdtypes[index];
                    if(dtype.compareTo("int")==0){
                        boolean bad=false;
                        for(int i = 0 ; i < msg.length();i++) {
                            if (Character.isLetter(msg.charAt(i))) {
                                bad = true;
                                break;
                            }
                        }
                        if(bad){
                            // INCORRECT INPUT SUPPLIED
                            chatview.addNewChat("AASSIST_BOT","This field requires only digits between 0 to 9. Please tell me the "+field_name_display+" again",INPUT_KEYBOARD);
                            process = false;
                        }
                        else{
                            process = true;
                            process_data = msg;
                        }
                    }
                    else{
                        process = true;
                        process_data = msg;
                    }
                }
            }
            else if(src == 2){
                // VOICE INPUT
                String response = fetchResponse(DIALOGFLOW+message);
                System.out.println(response);
                nlpData vdata = new nlpData(response);
                if(vdata.context.compareTo(currentContext)==0){
                    boolean action_valid= true;
                    String action_name="" , f_name="";
                    try {
                        action_name = vdata.action.substring(0, currentFeature.faprefix.length());
                        f_name = vdata.action.substring(currentFeature.faprefix.length());

                    }catch(Exception ex){
                        action_valid=false;
                    }
                    if(action_valid && action_name.compareTo(currentFeature.faprefix) == 0) {
                        if (f_name.compareTo("finalize") != 0) {
                            for(int i = 0 ; i < currentFeature.fields;i++){
                                String f_exp = currentFeature.fnames[i];
                                if(f_name.compareTo(f_exp)==0){
                                    String val_to_p = vdata.pvalue(currentFeature.cparams[i]);
                                    if(f_exp.compareTo("bankid") == 0){
                                        for(int x = 0 ; x < 2 ; x++){
                                            if(val_to_p.compareTo(nlpData.validbanks[x]) == 0){
                                                process = true;
                                                process_data = val_to_p;
                                                process_field = f_exp;
                                                break;
                                            }
                                        }
                                        if(process == false){
                                            if(expectedfield.compareTo("bankid") != 0){
                                                process = true;
                                                process_data = msg;
                                                process_field = expectedfield;
                                            }
                                            else
                                                chatview.addNewChat("AASSIST_BOT","This bank is not supported by us. You can choose between State Bank Of India and Axis Bank.",INPUT_VOICE);
                                        }
                                    }
                                    else{
                                        process = true;
                                        process_data = val_to_p;
                                        process_field = f_exp;
                                    }
                                    //chatview.addNewChat("AASSIST_BOT",process_data);

                                    break;
                                }
                            }
                        }
                        else if (f_name.compareTo("no")==0){
                            // the user declines to answer the question
                            chatview.addNewChat("AASSIST_BOT","Okay",INPUT_NONE);
                            process = true;
                            process_data = this.GARBAGE;
                            process_field = expectedfield;
                        }
                        else if (f_name.compareTo("yes")==0){
                            // the user acceps to answer the question
                            chatview.addNewChat("AASSIST_BOT","Okay, Please tell me the "+field_name_display,INPUT_VOICE);
                        }
                        else {
                            boolean incomplete = false;
                            for (int i = 0; i < currentFeature.fields; i++) {
                                if (currentFeature.fvalues[i].compareTo("") == 0 && currentFeature.fvalues[i].compareTo(GARBAGE) == 0) {
                                    //field number i is not set so ask user to enter
                                    chatview.addNewChat("AASSIST_BOT","I would finalize the process but first tell me the "+field_name_display,INPUT_VOICE);
                                    incomplete = true;
                                    break;
                                }
                            }
                            if (!incomplete) {
                                // COMPLETE TRANSACTION BECAUSE ALL DETAILS HAVE BEEN ENTERED
                                chatview.addNewChat("AASSIST_BOT","Great...Let me process it",INPUT_NONE);
                                finalizeFeature();
                            }
                        }
                    }else if(!action_valid){
                            boolean not_a_dg_var = false;
                            for(int ij = 0 ; ij < currentFeature.params ; ij++){
                                if(currentFeature.storein[ij]-1 == currentFeature.getIndex(NAME,expectedfield)){
                                    not_a_dg_var = true;
                                    break;
                                }
                            }
                            if(not_a_dg_var == false ){
                                process = true;
                                process_data = msg;
                                process_field = expectedfield;
                            }else{
                                chatview.addNewChat("AASSIST_BOT","Uhmmm....i didn't quite get you",INPUT_VOICE);
                            }
                    }
                }else{
                        boolean found_somethin = false;
                        for(int fi = 0 ; fi < flist.length ; fi++){
                            if(flist[fi].context.compareTo(currentFeature.context) != 0 && flist[fi].context.compareTo(vdata.context) == 0){
                                // FOUND IT BRO
                                found_somethin = true;
                                currentContext = vdata.context;
                                currentFeature.cleanUp();
                                chatview.addNewChat("AASSIST_BOT","It seems you don't want to continue with the previous transaction. Fine",INPUT_NONE);
                                currentFeature = flist[fi];
                                expectedfield = "";
                                expectedInput=false;
                                field_name_display="";
                                changeCurrentFeature(vdata);
                                break;
                            }
                        }
                        if(found_somethin == false){
                            chatview.addNewChat("AASSIST_BOT","Uhmmm....i didn't quite get you",INPUT_VOICE);
                        }
                }
            }
            else if(src == 3){
                msg = msg.trim();
                if(currentFeature.context.compareTo("fundtransfer-followup") == 0) {

                }
                if(msg.length() >= 0 ){
                    process = true;
                    process_data = msg;
                    process_field = expectedfield;
                    if(currentFeature.context.compareTo("fundtransfer-followup")== 0){
                        if(expectedfield.compareTo("benef_mode") == 0){
                            // MSG = SELECTED ITEM
                            if(msg.compareTo("Account Number and IFSC") == 0){
                                // USER WANTS TO ENTER ACCOUNT NO. AND IFSC CODE
                                currentFeature.fvalues[currentFeature.getIndex(Feature.NAME,"qsend")] = this.GARBAGE;
                                currentFeature.fvalues[currentFeature.getIndex(Feature.NAME,"b_email")] = this.GARBAGE;
                            }
                            else if(msg.compareTo("Email") == 0){
                                currentFeature.fvalues[currentFeature.getIndex(Feature.NAME,"b_accno")] = this.GARBAGE;
                                currentFeature.fvalues[currentFeature.getIndex(Feature.NAME,"b_ifsc")] = this.GARBAGE;
                                currentFeature.fvalues[currentFeature.getIndex(Feature.NAME,"qsend")] = this.GARBAGE;
                            }
                            else if(msg.compareTo("QuickSend Contact") == 0){
                                currentFeature.fvalues[currentFeature.getIndex(Feature.NAME,"b_accno")] = this.GARBAGE;
                                currentFeature.fvalues[currentFeature.getIndex(Feature.NAME,"b_ifsc")] = this.GARBAGE;
                                currentFeature.fvalues[currentFeature.getIndex(Feature.NAME,"b_email")] = this.GARBAGE;
                            }
                        }
                        else if(expectedfield.compareTo("payment_mode") == 0){
                            if(msg.compareTo("Use Default Account")== 0){
                                currentFeature.fvalues[currentFeature.getIndex(Feature.NAME,"r_accno")] = this.GARBAGE;
                                currentFeature.fvalues[currentFeature.getIndex(Feature.NAME,"def_acc")] = "True";
                            }
                            else{
                                currentFeature.fvalues[currentFeature.getIndex(Feature.NAME,"def_acc")] = "False";
                            }
                        }
                    }
                    else if(currentFeature.context.compareTo("setdefaultaccount-followup")== 0){
                        if(expectedfield.compareTo("sda_accno") == 0) {
                            String accn = msg.substring(0,msg.indexOf(' '));
                            String ifsc = msg.substring(msg.indexOf(':')+2);
                            currentFeature.fvalues[currentFeature.getIndex(Feature.NAME,"ifsc")] = ifsc;
                            process_data = accn;
                        }
                    }
                }
            }
            if(process == true) {
                if (process_data.length() > 0 ) {
                    // PROCESSing OF FIELD INPUT TO BE DONE HERE

                    if (process_field.compareTo(expectedfield) == 0) {
                        // USER SENT DATA FOR EXPECTED FIELD
                        int index = currentFeature.getIndex(Feature.NAME, expectedfield);
                        currentFeature.fvalues[index] = process_data;
                    } else {
                        // USER SENT OTHER FEILD INPUT
                        int index = currentFeature.getIndex(Feature.NAME, process_field);
                        currentFeature.fvalues[index] = process_data;
                    }
                    process = false;
                    process_data = process_field = "";
                    expectedInput = false;
                    expectedfield = "";
                    field_name_display = "";
                    String val_x = "";
                    int input_type = INPUT_NONE ;
                    for (int i = 0; i < currentFeature.fields; i++) {
                        if (currentFeature.fvalues[i].length() == 0 && currentFeature.finputm[i] != INPUT_NONE && currentFeature.fvalues[i].compareTo(this.GARBAGE) != 0) {
                            input_type = currentFeature.finputm[i];
                            expectedInput = true;
                            expectedfield = currentFeature.fnames[i];
                            field_name_display = currentFeature.fdsispn[i];
                            val_x = currentFeature.fvalues[i];
                            // ASK USER FOR EXPECTED FIELD
                            break;
                        }
                    }
                    if (expectedInput) {
                        chatview.addNewChat("AASSIST_BOT", "Can you please tell me the " + field_name_display,input_type);
                        int findex = currentFeature.getIndex(Feature.NAME, expectedfield);
                        if (currentFeature.finputm[findex] == INPUT_KEYBOARD) {
                            // IF INPUT MODE IN TEXT
                            voiceinp.setVisibility(View.GONE);
                            textinp.setVisibility(View.VISIBLE);
                            EditText tv = (EditText) textinp.findViewById(R.id.edittextu);
                            tv.setHint("Enter " + field_name_display);
                            tv.setText("");
                        } else if (currentFeature.finputm[findex] == INPUT_VOICE) {
                            // IF INPUT MODE IN VOICE
                            voiceinp.setVisibility(View.VISIBLE);
                            textinp.setVisibility(View.GONE);

                        } else if (currentFeature.finputm[findex] == 3) {
                            voiceinp.setVisibility(View.VISIBLE);
                            textinp.setVisibility(View.GONE);

                            // IF INPUT MODE IS DIALOG SELECT
                            String[] options;
                            try {
                                options = getListItems(expectedfield);
                                dialogOptions dialogs = new dialogOptions(this.appcontext);
                                dialogs.displayDialog(options, "Select " + field_name_display, this, chatview);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            // CODE LIST DYNAMIC CREATION UTIL WHERE LISTS ITEMS ARE NNEDED TO BE DECIDED OVER
                        }
                    } else {
                        chatview.addNewChat("AASSIST_BOT", "Superb, Let me proceed",INPUT_NONE);
                        finalizeFeature();
                    }
                }
            }
        }
        else{
            // NO FIELD INPUT IS EXPECTED
            String response = fetchResponse(DIALOGFLOW+message);
            if(response.compareTo("") != 0) {
                nlpData vdata = new nlpData(response);
                String action = vdata.action;
                int dot1 = action.indexOf('.');
                String action_name_1st = action.substring(0, dot1); // till first dot
                String action_name_2st = action.substring(0, action.lastIndexOf('.')); // till second dot
                if ((action_name_1st.compareTo("smalltalk") == 0) || (action_name_2st.compareTo("action.basic") == 0)) {
                    // USUAL TALKING...JUST PRINT OUT THE RESPONSE MESSAGE FROM DIALOGFLOW
                    chatview.addNewChat("AASSIST_BOT", vdata.speech, INPUT_VOICE);

                } else {
                    currentContext = vdata.context;
                    currentFeature = null;
                    expectedfield = "";
                    expectedInput = false;
                    field_name_display = "";
                    for (int i = 0; i < flist.length; i++) {
                        if (flist[i].context.compareTo(currentContext) == 0) {
                            // THIS IS THE FEATURE I WAS LOOKING FOR...SO LOAD IT
                            currentFeature = flist[i];
                            break;
                        }
                    }

                    if (currentFeature != null) {
                        // IF SOME CORRECT CONTEXT IS LOADED LOAD PARAMS ALREADY MENTIONED AND ASK FOR THE ONES WHICH ARE NOT SET
                        // COPIED TO changefeature()
                        changeCurrentFeature(vdata);
                    } else {
                        chatview.addNewChat("AASSIST_BOT", "Sorry, i didn't quite get you ", INPUT_VOICE);
                        voiceinp.setVisibility(View.VISIBLE);
                        textinp.setVisibility(View.GONE);

                    }
                }
            }
            else{
                chatview.addNewChat("AASSIST_BOT", "I'm facing connectivity issues. Please turn on your mobile data or connect to a wifi network", INPUT_VOICE);
            }
        }
    }
    public String fetchResponse(String url)
    {
        try
        {
            return new RESTconsumer(url,"GET").execute("").get();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }
    public  botLogic(Context context){
        this.appcontext = context;

        SharedPreferences pref = appcontext.getApplicationContext().getSharedPreferences("AassistAppSettings", Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS); // 0 - for private mode
        AASSIST_SERVER_IP = pref.getString("AassistServer","13.231.103.199");
        USER_EMAIL = pref.getString("email","friends@intel.com");
        ACCESS_TOKEN = pref.getString("access_token","fa08f366b068e7e679df10492e1337598a6f27d19aa6258f3c8a48cc8bbb1b04");
    }
    public String[] getListItems(String field_name) throws Exception {
        if(field_name.compareTo("benef_mode") == 0){
            return new String[]{"Account Number and IFSC","QuickSend Contact","Email"};
        }
        else if(field_name.compareTo("payment_mode") == 0){
            return new String[]{"Use Default Account","Select Account number"};
        }
        else if(field_name.compareTo("trans_type") == 0){
            return new String[]{"NEFT","RTGS"};
        }
        else if(field_name.compareTo("sda_accno") == 0){
            String bank=currentFeature.fvalues[currentFeature.getIndex(Feature.NAME,"bankid")];int bank_code=0;
            //chatview.addNewChat("AASSIST_BOT",bank);

            if(bank.compareTo("State Bank Of India") == 0)
                bank_code = 2018001;
            else if(bank.compareTo("Axis Bank") == 0)
                bank_code = 2018002;


            String params="email="+USER_EMAIL+"&token="+ACCESS_TOKEN+"&bankid="+bank_code , response;
            try{
                response = new RESTconsumer("http://"+AASSIST_SERVER_IP+":2018/linkedaccountswithifsc/","POST").execute(params).get();

                JSONObject j = new JSONObject(response);
                if(j.getString("status").compareTo("success")==0){
                    JSONArray jacc = j.getJSONArray("accounts");
                    String[] result = new String[jacc.length()];
                    for(int i = 0 ; i  < jacc.length() ;i++){
                            JSONArray j_res = jacc.getJSONArray(i);
                            result[i] = j_res.getString(0) + " \n IFSC : " + j_res.getString(1);
                    }
                    return result;
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return new String[]{""};
        }
        else if(field_name.compareTo("r_accno") ==  0 || field_name.compareTo("accno") ==  0 ){
            String bank=currentFeature.fvalues[currentFeature.getIndex(Feature.NAME,"bankid")];int bank_code=0;
            //chatview.addNewChat("AASSIST_BOT",bank);

            if(bank.compareTo("State Bank Of India") == 0)
                bank_code = 2018001;
            else if(bank.compareTo("Axis Bank") == 0)
                bank_code = 2018002;


            String params="email="+USER_EMAIL+"&token="+ACCESS_TOKEN+"&bankid="+bank_code , response;
            try{
                response = new RESTconsumer("http://"+AASSIST_SERVER_IP+":2018/linkedaccounts/","POST").execute(params).get();

                JSONObject j = new JSONObject(response);
                if(j.getString("status").compareTo("success")==0){
                    JSONArray jacc = j.getJSONArray("accounts");
                    String[] result = new String[jacc.length()];
                    for(int i = 0 ; i < jacc.length();i++){
                        result[i] = jacc.getJSONArray(i).getString(0);
                    }
                    return result;
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return new String[]{""};
        }
        return new String[]{};
    }
    void finalizeFeature(){
        try {
            if(currentFeature.name.compareTo("UPI") != 0) {
                String params = "email="+USER_EMAIL+"&token="+ACCESS_TOKEN+"&";
                for (int i = 0; i < currentFeature.fields; i++) {
                    if (currentFeature.fnames[i].compareTo("bankid") == 0) {
                        if (currentFeature.fvalues[i].compareTo("State Bank Of India") == 0) {
                            currentFeature.fvalues[i] = "2018001";
                        } else {
                            currentFeature.fvalues[i] = "2018002";
                        }
                    }
                    params += currentFeature.fnames[i] + "=" + currentFeature.fvalues[i];
                    if (i != currentFeature.fields - 1) {
                        params += "&";
                    }
                }
                String resp = new RESTconsumer("http://" + AASSIST_SERVER_IP + ":2018" + currentFeature.submit_url, "POST").execute(params).get();
                if (currentFeature.name.compareTo("FetchLinkedAccounts") == 0) {
                    JSONObject j = new JSONObject(resp);
                    String fresult=appcontext.getApplicationContext().getString(currentFeature.format);
                    if(j.getString("status").compareTo("success")==0){
                        JSONArray jacc = j.getJSONArray("accounts");
                        for(int i = 0 ; i < jacc.length();i++){
                            fresult += (i+1)+") "+jacc.getJSONArray(i).getString(0)+"\n";
                        }
                    }
                    chatview.addNewChat("AASSIST_BOT", fresult,INPUT_VOICE);
                }
                else if (currentFeature.name.compareTo("QuickSend") == 0) {
                    JSONObject j = new JSONObject(resp);
                    String fresult=appcontext.getApplicationContext().getString(currentFeature.format);
                    fresult += " \n Name  \t Account number\n";
                    if(j.getString("status").compareTo("success") == 0){
                        JSONArray ja = j.getJSONArray("quicksendcontacts");
                        for(int i = 0 ; i < ja.length() ; i++){
                            JSONArray qs = ja.getJSONArray(i);
                            fresult += (i+1)+") "+qs.getString(0)+" : "+qs.getString(1)+"@"+qs.getString(2)+"\n";
                        }
                    }
                    chatview.addNewChat("AASSIST_.BOT",fresult,INPUT_VOICE);
                }
                else {
                    JSONObject ad = new JSONObject(resp);
                    JSONArray adn = ad.names();
                    String[] str_p = new String[adn.length()];
                    for (int i = 0; i < adn.length(); i++) {
                        str_p[i] = ad.getString(adn.getString(i));
                    }
                    String fin_str = "";
                    if (str_p[0].compareTo("success") == 0)
                        fin_str = appcontext.getApplicationContext().getString(currentFeature.format, (Object[]) str_p);
                    else if (str_p[0].compareTo("failure") == 0)
                        fin_str = str_p[1];

                    chatview.addNewChat("AASSIST_BOT", fin_str,INPUT_VOICE);
                }
            }
            else{
                String upi_url = "upi://pay?pa="+currentFeature.fvalues[currentFeature.getIndex(NAME,"b_vpa")]+"&pn="+currentFeature.fvalues[currentFeature.getIndex(NAME,"b_name")]+"&tn=UPI Payment from AAssist One-Stop Banking Solution&am="+Double.parseDouble(currentFeature.fvalues[currentFeature.getIndex(NAME,"amount")]);
                Intent intent = new Intent();
                intent.setData(Uri.parse(upi_url));
                Intent chooser = Intent.createChooser(intent, "Pay with...");
                ((Activity)appcontext).startActivityForResult(chooser, 1, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        currentFeature.cleanUp();
        currentFeature = null;
        currentContext = "";
        expectedfield = "";
        expectedInput = false;
        voiceinp.setVisibility(View.VISIBLE);
        textinp.setVisibility(View.GONE);

    }
    void changeCurrentFeature(nlpData vdata){
        for(int i = 0 ; i <  currentFeature.params;i++) {
            String find = currentFeature.cparams[i];
            String pval = vdata.pvalue(find);

            if (pval != null) {
                // PARAM IS PRESENT IN NLPDATA...so copy param from
                boolean isclean = true;
                if (find.compareTo("bank") == 0) {
                    if (!(pval.compareTo("State Bank Of India") == 0 || pval.compareTo("Axis Bank") == 0)) {
                        isclean = false;
                    }
                }
                if(find.compareTo("amount") == 0){
                    try{
                        int int_eq = Integer.parseInt(pval);
                        isclean = true;
                    }catch(NumberFormatException e){
                        e.printStackTrace();
                    }
                }
                if(find.compareTo("receiver") == 0){
                    //chatview.addNewChat("AASSIST_BOT","FROM LIST" + pval);
                    if(pval.compareTo("QuickSend Contact") == 0){
                        currentFeature.fvalues[currentFeature.getIndex(Feature.NAME,"b_accno")] = this.GARBAGE;
                        currentFeature.fvalues[currentFeature.getIndex(Feature.NAME,"b_ifsc")] = this.GARBAGE;
                        currentFeature.fvalues[currentFeature.getIndex(Feature.NAME,"b_email")] = this.GARBAGE;
                        isclean = true;
                        //  chatview.addNewChat("AASSIST_BOT",  "QSC");
                    }
                    else if(pval.compareTo("Email") == 0){
                        currentFeature.fvalues[currentFeature.getIndex(Feature.NAME,"b_accno")] = this.GARBAGE;
                        currentFeature.fvalues[currentFeature.getIndex(Feature.NAME,"b_ifsc")] = this.GARBAGE;
                        currentFeature.fvalues[currentFeature.getIndex(Feature.NAME,"qsend")] = this.GARBAGE;
                        isclean = true;
                    }
                    else if(pval.compareTo("Account Number and IFSC") == 0){
                        currentFeature.fvalues[currentFeature.getIndex(Feature.NAME,"qsend")] = this.GARBAGE;
                        currentFeature.fvalues[currentFeature.getIndex(Feature.NAME,"b_email")] = this.GARBAGE;
                        isclean = true;
                    }
                }
                if(find.compareTo("default_account") == 0){
                    isclean = true;
                    if(pval.compareTo("True") == 0) {
                        //chatview.addNewChat("AASSIST_BOT","DEFDDD");
                        currentFeature.fvalues[currentFeature.getIndex(Feature.NAME, "bankid")] = this.GARBAGE;
                        currentFeature.fvalues[currentFeature.getIndex(Feature.NAME, "payment_mode")] = "Use Default Account";
                        currentFeature.fvalues[currentFeature.getIndex(Feature.NAME, "r_accno")] = this.GARBAGE;
                    }
                }
                if(find.compareTo("sender") == 0){
                    if(pval.compareTo("NEFT")==0 || pval.compareTo("RTGS") == 0 || pval.compareTo("UPI") == 0){
                        isclean = true;
                    }
                }
                if (isclean && currentFeature.storein[i] > 0) {
                    currentFeature.fvalues[currentFeature.storein[i]-1] = pval;
                }
            }
        }
        // ITERATE OVER ALL FIELD VALUES OF CURRENT FEATURES AND SET THE FIRST EMPTY FIELD AS EXPECTED FIELD
        String val_x = "";
        int input_type = INPUT_NONE;
        for(int i = 0 ; i < currentFeature.fields;i++){
            if(currentFeature.fvalues[i].trim().length() == 0  && currentFeature.fvalues[i].compareTo(this.GARBAGE) != 0){
                expectedInput = true;
                expectedfield = currentFeature.fnames[i];
                field_name_display = currentFeature.fdsispn[i];
                val_x = currentFeature.fvalues[i];
                input_type = currentFeature.finputm[i];
                // ASK USER FOR EXPECTED FIELD
                break;
            }
        }
        if(expectedInput){
            chatview.addNewChat("AASSIST_BOT","Lets "+currentFeature.fn,INPUT_NONE);
            chatview.addNewChat("AASSIST_BOT","Okay, Please tell me the "+field_name_display,input_type);
            int findex = currentFeature.getIndex(Feature.NAME,expectedfield);
            if(findex > 0 && currentFeature.finputm[findex] == 1){
                // IF INPUT MODE IN TEXT
                voiceinp.setVisibility(View.GONE);
                textinp.setVisibility(View.VISIBLE);

                EditText tv = (EditText) textinp.findViewById(R.id.edittextu);
                tv.setHint("Enter " + field_name_display);
            }
            else if(currentFeature.finputm[findex] == 2){
                // IF INPUT MODE IN VOICE
                voiceinp.setVisibility(View.VISIBLE);
                textinp.setVisibility(View.GONE);

            }
            else if(currentFeature.finputm[findex] == 3){
                voiceinp.setVisibility(View.VISIBLE);
                textinp.setVisibility(View.GONE);

                // IF INPUT MODE IS DIALOG SELECT
                try {
                    String[] options = getListItems(expectedfield);
                    dialogOptions dialogs = new dialogOptions(this.appcontext);
                    dialogs.displayDialog(options,"Select "+field_name_display,this,chatview);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                // CODE LIST DYNAMIC CREATION UTIL WHERE LISTS ITEMS ARE NEEDED TO BE DECIDED OVER
            }
        }
        else{
            finalizeFeature();
        }
    }
}
