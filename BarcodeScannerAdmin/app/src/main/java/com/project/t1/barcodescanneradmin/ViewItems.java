package com.project.t1.barcodescanneradmin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class ViewItems extends ActionBarActivity {
    Toolbar toolbar;
    Button btn_search;
    ListView listView;
    EditText et_type;
    Context ctx=this;
    RequestQueue queue;
    MyAdapter myAdapter;
    ArrayList<ListRow> item_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_items);
        toolbar=(Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(6);
        item_list = new ArrayList<ListRow>();
        queue = VolleySingleton.getInstance().getRequestQueue();
        et_type = (EditText) findViewById(R.id.et_type_search);
        listView=(ListView) findViewById(R.id.listView);
        btn_search= (Button) findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type= et_type.getText().toString();
                if(type.equals(""))
                    type = "all";
                requestAll(type);
            }
        });
        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, int position, long id) {
                final ListRow selectedItem = (ListRow) parent.getAdapter().getItem(position);
                final int pos=position;
                new SweetAlertDialog(ctx, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Delete item")
                        .setContentText("Are you sure you want to delete this item?")
                        .setConfirmText("Yes,delete it!")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                deleteReq(selectedItem.barcode);
                                item_list.remove(pos);
                                myAdapter.notifyDataSetChanged();
                            }
                        })
                        .show();
                return true;
            }
        });
    }


  /*  class myAdapter extends BaseAdapter {

        ArrayList<ListRow> list;
        DataBaseOps DBop;
        Cursor cursor;

        myAdapter(Context c) { // constructor
            list = new ArrayList<ListRow>();

            DBop = new DataBaseOps(ctx);
            cursor = DBop.getData(DBop);
            if(cursor.moveToFirst())
                do{
                   list.add(new ListRow(cursor.getString(0),cursor.getString(1),cursor.getDouble(2),cursor.getString(3),cursor.getDouble(4)));
                }while (cursor.moveToNext());
            else
                Toast.makeText(ctx,"Database is empty",Toast.LENGTH_LONG).show();
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

            View row = inflater.inflate(R.layout.listview_row,parent,false);

            TextView Iname=(TextView) row.findViewById(R.id.tv_item_name);
            TextView Icode=(TextView) row.findViewById(R.id.tv_code);
            TextView Iqty=(TextView) row.findViewById(R.id.tv_qty_unit);
            TextView Iprice=(TextView) row.findViewById(R.id.tv_item_price);

            ListRow temp= list.get(position);
            DecimalFormat df = new DecimalFormat("#0.00");

            Iname.setText("Product name: "+temp.itemName);
            Icode.setText("Product code: "+temp.code);
            Iprice.setText("Price: $"+df.format(temp.price));
            String u=temp.unit;
            double q=temp.qty;
            if(q==0 || u=="N/A")
                Iqty.setText("Product size: N/A");
            else
                Iqty.setText("Product size: "+q+" "+u);
            return row;
        }

    }*/

    private void requestAll(final String type){
        String url = "https://steff-bood-sw-eng.herokuapp.com/getall/"+type;
        final ProgressDialog pd = new ProgressDialog(ctx);
        pd.setMessage("loading...");

        JsonArrayRequest request= new JsonArrayRequest(Request.Method.GET,url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        pd.dismiss();
                        Log.e("response", response.toString());
                        if(response.length()>0) {
                            try {
                                item_list =  new ArrayList<ListRow>();
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject temp_obj = (JSONObject) response.get(i);
                                    ListRow row = new ListRow(temp_obj.getString("brand"), temp_obj.getString("type"),
                                            temp_obj.getString("price"), temp_obj.getString("quantity") + " " + temp_obj.getString("unit"),temp_obj.getString("code"));
                                    item_list.add(row);
                                }
                                Collections.sort(item_list);
                                myAdapter = new MyAdapter(getApplicationContext(), item_list);
                                listView.setAdapter(myAdapter);
                                //myAdapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            Toast.makeText(ctx,"No items of type "+type+" found",Toast.LENGTH_LONG).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(ctx,"Server error",Toast.LENGTH_LONG).show();
                        pd.dismiss();
                    }
                });
        queue.add(request);
        pd.show();
    }

    private void deleteReq(String barcode){
        String url= "https://steff-bood-sw-eng.herokuapp.com/deleteproduct/"+barcode;
        JsonObjectRequest delReq= new JsonObjectRequest(Request.Method.DELETE,url,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    String status = response.getString("status");
                    if(status.equals("failure"))
                        Toast.makeText(ctx,"Item was not found",Toast.LENGTH_LONG).show();
                    else{
                        Toast.makeText(ctx,"Item was deleted",Toast.LENGTH_LONG).show();
                    }
                }
                catch (JSONException e){
                    e.printStackTrace();
                }

            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("volley",error.getMessage());
                Toast.makeText(ctx,"Server Error",Toast.LENGTH_LONG).show();
            }
        });
        queue.add(delReq);
    }


    private class ListRow implements Comparable<ListRow>{
        String brand,type,price,description,barcode;

        private ListRow(String brand, String type, String price, String description, String barcode) {
            this.brand = brand;
            this.type = type;
            this.price = price;
            this.description = description;
            this.barcode = barcode;

        }

        @Override
        public int compareTo(ListRow another) {
            return Double.parseDouble(this.price) <= Double.parseDouble(another.price)?-1:1;

        }
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
            convertView = inflater.inflate(R.layout.list_row,parent,false);
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
        getMenuInflater().inflate(R.menu.menu_view_items, menu);
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
