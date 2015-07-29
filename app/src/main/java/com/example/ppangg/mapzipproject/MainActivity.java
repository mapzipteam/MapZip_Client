package com.example.ppangg.mapzipproject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.ppangg.mapzipproject.network.MyVolley;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends ActionBarActivity {



    private TextView state ;

    private EditText inputID;
    private EditText inputPW;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // new line

        state = (TextView)findViewById(R.id.TextState);
        inputID = (EditText)findViewById(R.id.InputID);
        inputPW = (EditText)findViewById(R.id.InputPW);






    }

    public void DoLogin(View v) {
//        Toast.makeText(getApplicationContext(),"Btn Clicked!", Toast.LENGTH_LONG).show();
//
//        Intent intent = new Intent(getApplicationContext(),Tabactivity.class);
//        startActivity(intent);
        RequestQueue queue = MyVolley.getInstance(this.getApplicationContext()).getRequestQueue();
        String url = SystemMain.SERVER_JOIN_URL;
        final String userid = inputID.getText().toString();
        final String userpw = inputPW.getText().toString();
        if(userid !=null && !userid.equals("")&& userpw !=null && !userpw.equals("")){
            StringRequest myReq = new StringRequest(Request.Method.POST,
                    SystemMain.SERVER_JOIN_URL,
                    NetSuccessListener(),
                    NetErrorListener()){

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String,String>();
                    params.put("userid", userid);
                    params.put("userpw",userpw);

                    return params;
                }
            };
            queue.add(myReq);
        }



    }
    public void DoJoin(View v){
        String strUrl = SystemMain.SERVER_JOIN_URL;
        //String params = "userid="+ URLEncoder.encode(inputID.getText(),"UTF-8")+"&userpw="+inputPW.getText();
        String params = "userid="+inputID.getText().toString()+"&userpw="+inputPW.getText().toString();
        ConnectWebpage testcw = new ConnectWebpage(this,params);
        try{
            if(strUrl!=null && strUrl.length()>0){
                ConnectivityManager conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netState = conMgr.getActiveNetworkInfo();
                if(netState!=null && netState.isConnected()){
                    testcw.execute(strUrl);
                }
                else{
                    throw new Exception("NETWORK ERROR");
                }
            }
            else{
                throw new Exception("BAD URL");
            }
        }
        catch(Exception e){
            state.setText(e.getMessage());
        }

    }

    private Response.Listener<String> NetSuccessListener(){
        return new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                state.setText(response);
            }
        };
    }
    private Response.ErrorListener NetErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                state.setText(error.getMessage());
            }
        };
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}