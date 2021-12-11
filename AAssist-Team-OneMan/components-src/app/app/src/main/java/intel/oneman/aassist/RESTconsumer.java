package intel.oneman.aassist;

import android.os.AsyncTask;
import java.net.URL;
import  java.net.HttpURLConnection;
import java.io.*;

public class RESTconsumer extends AsyncTask<String,Void,String>{
    public static final String REQUEST_METHOD = "GET";
    public static final int READ_TIMEOUT = 15000;
    public static final int CONNECTION_TIMEOUT = 15000;
    public String url = "",req_type="";
    public RESTconsumer(String url,String req_type) {
        this.url = url;
        this.req_type = req_type;
    }
    @Override
    protected String doInBackground(String... params){
        String stringUrl = this.url ,  pdata = params[0];
        String result="";
        String inputLine;
        HttpURLConnection connection = null;
        try {
            URL myUrl = new URL(stringUrl);
            connection =(HttpURLConnection) myUrl.openConnection();
            connection.setRequestMethod(this.req_type);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            if(this.req_type.compareTo("POST") == 0){
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setFixedLengthStreamingMode(pdata.getBytes().length);
                PrintWriter out = new PrintWriter(connection.getOutputStream());
                out.print(pdata);
                out.close();
                result = connection.getResponseMessage();
            }
            else{
                connection.addRequestProperty("Authorization", "Bearer 800bb18f38294b83aca955903b95402d");
                connection.connect();
            }

            InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();
            while((inputLine = reader.readLine()) != null){
                stringBuilder.append(inputLine);
            }
            reader.close();
            streamReader.close();
            result = stringBuilder.toString();
        }catch (Exception e)
        {

            e.printStackTrace();
        }finally {
            if (connection != null) {
                connection.disconnect();
            }
        };

        return result;
    }

    @Override
    protected void onPostExecute(String result){
        super.onPostExecute(result);
    }
}
