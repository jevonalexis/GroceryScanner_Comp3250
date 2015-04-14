package com.example.dillon.barcodescanneruser;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;


public class Result extends ActionBarActivity {
    String barcode="";
    TextView item_brand,item_desc,item_price,item_name, text_related, txt_name, txt_price, txt_desc;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(6);

        item_brand = (TextView)findViewById(R.id.brand);
        item_name = (TextView)findViewById(R.id.name);
        item_price = (TextView)findViewById(R.id.price);
        item_desc = (TextView)findViewById(R.id.description);

        txt_name = (TextView)findViewById(R.id.text_name);
        txt_price = (TextView)findViewById(R.id.text_price);
        txt_desc = (TextView)findViewById(R.id.text_description);

        //Hide the related text field until ready to use
        text_related = (TextView)findViewById(R.id.related_text);
        text_related.setVisibility(View.INVISIBLE);

        //get data from previous screen
        Bundle extra = getIntent().getExtras();
        barcode = extra.getString("barcode");

        //get information from server
        if (barcode==null) {
            Toast.makeText(getApplicationContext(),"The barcode is null", Toast.LENGTH_SHORT).show();
        }
        else {
            ConnectivityManager check = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            Boolean connected = false;
            NetworkInfo networkInfo = check.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
               // item_brand.setText(barcode);
                makeRequest(barcode);
                connected=true;
            }
            if(!connected)
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
//            item_code.setText(scanContent);
        }

//        if (realted items found then) {
//            //handling the listview
//            text_related.setVisibility(View.VISIBLE);
//            RelatedAdapter relatedItems = new RelatedAdapter();
//            ListView listView = (ListView)findViewById(R.id.related);
//            listView.setAdapter(relatedItems);
//        }
    }

    private void makeRequest(String barcode){
        String base_url="https://steff-bood-sw-eng.herokuapp.com/getproduct/";
        String url=base_url+barcode;
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("loading...");
        RequestQueue requestQueue=VolleySingleton.getInstance().getRequestQueue();
        JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET,url,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String name,price,unit,quantity;
                progressDialog.dismiss();
                Log.e("response", response.toString());
                if(response.length()!=0){
                    try {
                        name = response.getString("name");
                        price = response.getString("price");
                        unit = response.getString("unit");
                        quantity = response.getString("quantity");
                        item_name.setText(name);
                        item_price.setText("$" + price);
                        item_desc.setText(quantity + " " + unit);
                    }
                    catch (JSONException e){
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"No item found",Toast.LENGTH_LONG).show();
                }
            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                error.printStackTrace();
                Toast.makeText(getApplicationContext(),"Sever Error",Toast.LENGTH_LONG).show();
                Log.e("ServerError", error.toString());
                item_name.setVisibility(View.GONE);
                item_price.setVisibility(View.GONE);
                item_desc.setVisibility(View.GONE);
                txt_name.setVisibility(View.GONE);
                txt_price.setVisibility(View.GONE);
                txt_desc.setVisibility(View.GONE);

            }
        });
        requestQueue.add(request);
        progressDialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result, menu);
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
