package com.example.dillon.barcodescanneruser;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Dillon on 4/12/2015.
 */
public class RelatedAdapter extends BaseAdapter {
    String type="";

    Activity activity;
    String[] relatedproducts;
    LayoutInflater inflater = null;

    public RelatedAdapter(Activity a, String[] items) {
        activity = a;
        relatedproducts = items;

        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        //return the json object of items matching the type
        return null;
    }

    @Override
    public long getItemId(int position) {
//        return 0;
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        LayoutInflater inflater = (LayoutInflater)ListViewWithBaseAdapter.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View row = inflater.inflate(R.layout.relatedlayout, position, false);

//        LayoutInflater inflater = (LayoutInflater)Ac
//        LayoutInflater inflater = ((Activity)getApplicationContext()).getLayoutInflater();
        TextView text_name, text_price, text_description;
        View view = convertView;
        if (convertView==null) {
            view = inflater.inflate(R.layout.relatedlayout, null);
            text_name = (TextView)view.findViewById(R.id.rname);
            text_price = (TextView)view.findViewById(R.id.rprice);

            //set the textviews to values retrieved from the server

        }
        return null;
    }

    public void setType(String t) {
        this.type = t;
    }

    //this method is to obtain the number of related items for the getCount() method to return
    public int getRelatedCount() {
        //function uses the type from the item by using the setType method
        //a count of the items that match the type in the database is returned
        int n=0;

//        makeRequest(type);
        //get count
        // n = count
        return n;
    }

    private void makeRequest(String barcode){
        String base_url="https://steff-bood-sw-eng.herokuapp.com/gettype/";
        String url=base_url+barcode;
//        final ProgressDialog progressDialog=new ProgressDialog(this);
//        progressDialog.setMessage("loading...");
        RequestQueue requestQueue=VolleySingleton.getInstance().getRequestQueue();
        JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET,url,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                String name,price,unit,quantity;
                String t;
//                progressDialog.dismiss();
                Log.e("response", response.toString());
                if(response.length()!=0){
                    try {
                        t = response.getString("type");
//                        name = response.getString("name");
//                        price = response.getString("price");
//                        unit = response.getString("unit");
//                        quantity = response.getString("quantity");
//                        item_name.setText(name);
//                        item_price.setText("$" + price);
//                        item_desc.setText(quantity + " " + unit);
                    }
                    catch (JSONException e){
                        e.printStackTrace();
                    }
                }
                else{
//                    Toast.makeText(getApplicationContext(), "No item found", Toast.LENGTH_LONG).show();
                    Log.e("ServerError", "No item found");
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        progressDialog.dismiss();
                        error.printStackTrace();
//                        Toast.makeText(getApplicationContext(),"Sever Error",Toast.LENGTH_LONG).show();
                        Log.e("ServerError", error.toString());
//                        item_name.setVisibility(View.GONE);
//                        item_price.setVisibility(View.GONE);
//                        item_desc.setVisibility(View.GONE);
                    }
                });
        requestQueue.add(request);
//        progressDialog.show();
    }
}
