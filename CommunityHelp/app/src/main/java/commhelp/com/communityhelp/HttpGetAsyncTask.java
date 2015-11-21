package commhelp.com.communityhelp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpGetAsyncTask extends AsyncTask<String, Void, String> {

    public HttpGetAsyncTask() {

    }

    protected String doInBackground(String...args) {
        String targetURL = args[0];
        String urlParameters = args[1];
        /*
        HttpURLConnection urlConnection = null;
        String response = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            java.util.Scanner s = new java.util.Scanner(in).useDelimiter("\\A");
            response = s.hasNext() ? s.next() : "";
        }
        catch (Exception e) {
            Log.d("ERROR", e.toString());
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return response;
        */
        URL url;
        HttpURLConnection connection = null;
        try {
            //Create connection
            url = new URL(targetURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            connection.setRequestProperty("Content-Length", "" +
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches (false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream ());
            wr.writeBytes (urlParameters);
            wr.flush ();
            wr.close ();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            Log.i("ETWAS", "AM PRIMIT ASTA"+response.toString());
            return response.toString();

        } catch (Exception e) {

            e.printStackTrace();
            return null;

        } finally {

            if(connection != null) {
                connection.disconnect();
            }
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}
/*
class HttpPostAsyncTask extends AsyncTask<String, Void, String> {
    protected String doInBackground(String... urls) {
        String URL = urls[0];
        String data = urls[1];
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(URL);
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("data_json", data));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            return httpclient.execute(httppost).toString();
        }
        catch (Exception e) {
            Log.e("REPORT_INCIDENT", e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
    }
}
*/