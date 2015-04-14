package com.example.dillon.barcodescanneruser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dillon.barcodescanneruser.ZXing.IntentIntegrator;
import com.example.dillon.barcodescanneruser.ZXing.IntentResult;


public class Main extends ActionBarActivity {
//    RippleButton scan;
    ImageButton scan;
    TextView item_code,item_desc,item_price,item_name;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scan = (ImageButton)findViewById(R.id.btn_scan);

        toolbar=(Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(6);
        getSupportActionBar().setHomeButtonEnabled(true);
/*        scan = (RippleButton)findViewById(R.id.btn_scan);
        item_code = (TextView)findViewById(R.id.item_code);
        item_name = (TextView)findViewById(R.id.item_name);
        item_price = (TextView)findViewById(R.id.item_price);
        item_desc = (TextView)findViewById(R.id.item_description);

        scan = (Button)findViewById(R.id.btn_scan);
        code = (TextView)findViewById(R.id.tv_code);

        left = (LinearLayout)findViewById(R.id.leftLayout);
        right = (LinearLayout)findViewById(R.id.rightLayout);

        left.setVisibility(View.GONE);
        right.setVisibility(View.GONE);*/
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

    public void scanBarcode(View view) {
        IntentIntegrator scanIntegrator = new IntentIntegrator(Main.this);
        scanIntegrator.initiateScan();

    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //retrieve scan result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        String scanContent="";
        if (scanningResult!=null) {
            scanContent = scanningResult.getContents();
            if (scanContent==null) {
                Toast.makeText(getApplicationContext(),"Null Barcode", Toast.LENGTH_SHORT).show();
            }
            else {
                Intent result = new Intent(this, Result.class);
                result.putExtra("barcode", scanContent);
                startActivity(result);
            }
        }
        else
            Toast.makeText(getApplicationContext(),"No scan data received!", Toast.LENGTH_SHORT).show();
    }
/*
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
                Log.e("response",response.toString());
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
            }
        });
        requestQueue.add(request);
        progressDialog.show();
    }*/

}
