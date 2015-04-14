package com.example.dillon.barcodescanneruser;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class Result extends ActionBarActivity {
    private String barcode="";
    private TextView item_brand,item_desc,item_price,item_type, text_related,txt_brand, txt_type, txt_price, txt_desc;
    private ListView listView;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(6);

        item_brand = (TextView)findViewById(R.id.brand);
        item_type = (TextView)findViewById(R.id.type);
        item_price = (TextView)findViewById(R.id.price);
        item_desc = (TextView)findViewById(R.id.description);

        txt_type = (TextView)findViewById(R.id.text_type);
        txt_price = (TextView)findViewById(R.id.text_price);
        txt_desc = (TextView)findViewById(R.id.text_description);
        txt_brand = (TextView) findViewById(R.id.text_brand);

        listView = (ListView) findViewById(R.id.related);


        //Hide the related text field until ready to use
        text_related = (TextView)findViewById(R.id.related_text);
        text_related.setVisibility(View.INVISIBLE);
        listView.setVisibility(View.INVISIBLE);

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

        /*if (realted items found then) {
            //handling the listview
            text_related.setVisibility(View.VISIBLE);
            RelatedAdapter relatedItems = new RelatedAdapter();
            ListView listView = (ListView)findViewById(R.id.related);
            listView.setAdapter(relatedItems);
        }*/
    }

    private class ListRow{
        String brand,type,price,description;

        private ListRow(String brand, String type, String price, String description) {
            this.brand = brand;
            this.type = type;
            this.price = price;
            this.description = description;
        }
    }


    private void makeRequest(String barcode){
        String base_url="https://steff-bood-sw-eng.herokuapp.com/getproduct/";
        String url=base_url+barcode;
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("loading...");
        final RequestQueue requestQueue=VolleySingleton.getInstance().getRequestQueue();
        JsonArrayRequest request=new JsonArrayRequest(Request.Method.GET,url,new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                String type,price,unit,quantity,brand;
                progressDialog.dismiss();
                Log.e("response", response.toString());
                if(response.length()>0){
                    try {
                        JSONObject requested_item = (JSONObject)response.get(0);
                        type = requested_item.getString("type");
                        price = requested_item.getString("price");
                        unit = requested_item.getString("unit");
                        brand = requested_item.getString("brand");
                        quantity = requested_item.getString("quantity");
                        item_type.setText(type);
                        item_brand.setText(brand);
                        item_price.setText("$" + price);
                        item_desc.setText(quantity + " " + unit);
                        if(response.length()>1){
                            listView.setVisibility(View.VISIBLE);
                            text_related.setVisibility(View.VISIBLE);
                            ArrayList<ListRow> related_list= new ArrayList<ListRow>();
                            for(int i=1;i<response.length();i++){
                                JSONObject temp_obj = (JSONObject)response.get(i);
                                ListRow row = new ListRow(temp_obj.getString("brand"),temp_obj.getString("type"),
                                        temp_obj.getString("price"),temp_obj.getString("quantity")+" "+temp_obj.getString("unit"));
                                related_list.add(row);
                            }

                            MyAdapter myAdapter = new MyAdapter(getApplicationContext(),related_list);
                            listView.setAdapter(myAdapter);
                        }

                    }
                    catch (JSONException e){
                        e.printStackTrace();
                    }
                }
                else{
                    new SweetAlertDialog(getApplicationContext(),SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Item not found")
                            .setCancelText("Ok")
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    startActivity(new Intent(Result.this,Main.class));
                                }
                            })
                            .show();
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
                item_type.setVisibility(View.GONE);
                item_brand.setVisibility(View.GONE);
                item_price.setVisibility(View.GONE);
                item_desc.setVisibility(View.GONE);
                txt_type.setVisibility(View.GONE);
                txt_price.setVisibility(View.GONE);
                txt_desc.setVisibility(View.GONE);
                txt_brand.setVisibility(View.GONE);
            }
        });
        requestQueue.add(request);
        progressDialog.show();
    }


    class MyAdapter extends BaseAdapter {
        ArrayList<ListRow> list;
        Context ctx;
        MyAdapter(Context context,ArrayList<ListRow> list) { // constructor
            ctx=context;
            this.list=list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater= (LayoutInflater) ctx.getSystemService(LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.relatedlayout,parent,false);
            TextView rbrand=(TextView) convertView.findViewById(R.id.rbrand);
            TextView rtype=(TextView) convertView.findViewById(R.id.rtype);
            TextView rprice=(TextView) convertView.findViewById(R.id.rprice);
            TextView rdesc=(TextView) convertView.findViewById(R.id.rdescription);
            ListRow temp= list.get(position);

            rbrand.setText(temp.brand);
            rtype.setText(temp.type);
            rprice.setText("$"+temp.price);
            rdesc.setText(temp.description);
            return convertView;
        }
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
