package intel.oneman.aassist;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;

public class nlpData{
    public String action="",context="",intent="",speech="";
    HashMap<String,String> Params=new HashMap<String,String>();
    String pfirst;
    static String[] validbanks = {"State Bank Of India","Axis Bank"};
    public nlpData(String json){
        try{
            JSONObject resp = new JSONObject(json);
            JSONObject resp2 = resp.getJSONObject("result");
            action = resp2.getString("action");
            speech = (resp2.getJSONObject("fulfillment")).getString("speech");
            JSONArray cont = resp2.getJSONArray("contexts");
            JSONObject use_cont = null;
            int max_life = 0;
            if(cont.length() > 0){
                for(int i = 0 ; i < cont.length();i++){
                    JSONObject curr_cont = cont.getJSONObject(i);
                    int cont_life = Integer.parseInt(curr_cont.getString("lifespan"));
                    if(cont_life >  max_life){
                        max_life = cont_life;
                        use_cont = curr_cont;
                    }
                }
            }
            if(use_cont.has("name"))
            context = use_cont.getString("name");

            if(resp2.getJSONObject("metadata").has("intentName"))
            intent = resp2.getJSONObject("metadata").getString("intentName");

            JSONObject p = resp2.getJSONObject("parameters");
            JSONArray namesl = p.names();
            for(int i = 0 ; i < namesl.length();i++){
                String key = (String)namesl.get(i);
                if(p.getJSONArray(key).length() > 0){
                    String val="";
                    JSONArray val_elements = p.getJSONArray(key);
                    if(key.compareTo("bank") == 0){
                        arr_loop:
                        for(int j = 0 ; j < val_elements.length() ; j++){
                            String vval = val_elements.getString(j);
                            for(int k = 0 ; k < 2 ; k++){
                                if(vval.compareTo(validbanks[k]) == 0){
                                    val = vval;
                                    break arr_loop;
                                }
                            }
                        }
                    }
                    else if(key.compareTo("amount") == 0){
                        arr_loop:
                        for(int j = 0 ; j < val_elements.length() ; j++) {
                            try{
                                int num_eq = Integer.parseInt(val_elements.getString(j));
                                val = Integer.toString(num_eq);
                                break arr_loop;
                            }catch(Exception nex){
                                nex.printStackTrace();
                            }
                        }
                    }
                    else{
                        val = val_elements.getString(0);
                    }
                    Params.put(key,val);
                }else{
                    Params.put(key,"");
                }
                if(i == 0)
                    pfirst = key;
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public String pvalue(String key){
        return ((String)Params.get(key));
    }
}
