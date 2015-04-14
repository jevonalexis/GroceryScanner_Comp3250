package com.project.t1.barcodescanneradmin;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.ColorRes;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.xgc1986.ripplebutton.widget.RippleButton;

import org.json.JSONException;
import org.json.JSONObject;

import ZXing.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AddItem extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Toolbar toolbar;
    RippleButton scan,save;
    EditText type,quantity,price,brand;
    Spinner unitsSpinner;
    TextView code;
    DataBaseOps DB;
    Context ctx=this;
    Resources res;
    String i_unit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        toolbar=(Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        save=(RippleButton) findViewById(R.id.btn_save);
        scan=(RippleButton) findViewById(R.id.btn_scan);
        type= (EditText)findViewById(R.id.et_itemType);
        brand = (EditText)findViewById(R.id.et_itemBrand);
        price= (EditText)findViewById(R.id.et_price);
        quantity=(EditText)findViewById(R.id.et_quantity);
        unitsSpinner=(Spinner) findViewById(R.id.spin_unit);
        code=(TextView)findViewById(R.id.tv_code);
        spinnerFn();
        save.setOnClickListener(this);
        scan.setOnClickListener(this);

        DB=new DataBaseOps(ctx);
    }

    private void spinnerFn(){
        res=getResources();
        String[] units=res.getStringArray(R.array.units);
        unitsSpinner.setPrompt("Select a unit");
        ArrayAdapter<String> adapter_units=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_activated_1,units);
        unitsSpinner.setAdapter(adapter_units);
        unitsSpinner.setOnItemSelectedListener(this);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //retrieve scan result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        String scanContent="";
        if (scanningResult != null) {
            scanContent = scanningResult.getContents();
            code.setText(scanContent);
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_item, menu);
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
        if(id==R.id.action_ViewDB){
            startActivity(new Intent(AddItem.this,ViewItems.class));
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_scan:

                IntentIntegrator scanIntegrator = new IntentIntegrator(AddItem.this);
                scanIntegrator.initiateScan();
                break;
            case R.id.btn_save:

               /* String i_name=name.getText().toString();
                String i_code=code.getText().toString();
                String i_quantity=quantity.getText().toString();
                String i_price=price.getText().toString();
                String i_brand=brand.getText().toString();
                int err=-1;
                String[] errors={"Item brand and type containing letters must be entered","Barcode must be captured","Price must be entered","Price must be greater than 0"};
                //check to make sure not empty and contains alphanumeric data
                Pattern p = Pattern.compile(".*[a-zA-Z0-9].*");
                Matcher m = p.matcher(i_name);
                Matcher m2= p.matcher(i_brand);

                if(i_name.length()==0 || !m.find() || i_brand.length()==0 || !m2.find())
                    err=0;
                else if(i_code.length()==0 || i_code.equals("Barcode"))
                    err=1;
                else if(i_price.length()==0)
                    err=2;
                else if(Double.parseDouble(i_price)<=0)
                    err=3;

                if(err==-1){
                    //DB.insertRow(DB,i_code,i_name,Double.parseDouble(i_price),i_unit,Double.parseDouble(i_quantity));
                    Toast.makeText(this,"Saved",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this,errors[err],Toast.LENGTH_SHORT).show();
                }*/
                validateAndSave();
                break;
        }


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int pos=unitsSpinner.getSelectedItemPosition();

        String[] units=res.getStringArray(R.array.units);
        i_unit=units[pos];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void validateAndSave(){
        //get info that was entered from views
        String i_type=type.getText().toString();
        String i_code=code.getText().toString();
        String i_brand = brand.getText().toString();
        String i_quantity=quantity.getText().toString();
        if(i_quantity.equals("Quantity")) i_quantity="";
        String i_price=price.getText().toString();

        int err=-1;
        //array containing error messages that would displayed if input is invalid
        String[] errors={"Item name containing letters must be entered","Barcode must be captured","Price must be entered","Price must be greater than 0"};
        //check to make sure not empty and contains alphanumeric data
        Pattern p = Pattern.compile(".*[a-zA-Z0-9].*");
        Matcher m = p.matcher(i_type);
        Matcher m2 = p.matcher(i_brand);

        //validate input and record errors
        if(i_type.length()==0 || !m.find() || i_brand.length()==0 || !m2.find())
            err=0;
        else if(i_code.length()==0 ||i_code.equals("Barcode"))
            err=1;
        else if(i_price.length()==0)
            err=2;
        else if(Double.parseDouble(i_price)<=0)
            err=3;
        //if no errors then it is safe to send to server
        if(err==-1){
            JSONObject obj=new JSONObject();
            try{
                obj.put("type", i_type);
                obj.put("brand",i_brand);
                obj.put("price",Double.parseDouble(i_price));
                if(i_quantity.equals(""))
                    obj.put("quantity",0.0);
                else
                    obj.put("quantity",Double.parseDouble(i_quantity));
                obj.put("unit",i_unit);
                obj.put("code",Long.parseLong(i_code));
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            Log.e("json obj", obj.toString());
            sendToServer(obj);
            //if(i_quantity.equals("")) i_quantity="0";
            //DB.insertRow(DB,i_code,i_name,Double.parseDouble(i_price),i_unit,Double.parseDouble(i_quantity));
            //Toast.makeText(this,"Saved",Toast.LENGTH_SHORT).show();
        }
        //if error was found notify user
        else{
            Toast.makeText(this,errors[err],Toast.LENGTH_SHORT).show();
        }
    }

    private void sendToServer(JSONObject obj){
        RequestQueue requestQueue=VolleySingleton.getInstance().getRequestQueue();
        String url="https://steff-bood-sw-eng.herokuapp.com/newproduct";
        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST,url,obj,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getApplicationContext(),"Saved",Toast.LENGTH_SHORT).show();
                //Log.e("response",response.toString());
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(request);
    }
}
