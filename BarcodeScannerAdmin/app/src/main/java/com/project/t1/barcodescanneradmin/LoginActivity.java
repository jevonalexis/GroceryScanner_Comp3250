package com.project.t1.barcodescanneradmin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.xgc1986.ripplebutton.widget.RippleButton;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends ActionBarActivity {
    EditText et_email, et_password;
    Toolbar toolbar;
    RippleButton btn_signin;
    Context ctx = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(6);
        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_signin = (RippleButton) findViewById(R.id.btn_signin);
        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_email.getText().toString().length() > 0 && et_password.getText().length() > 0) {

                    if (isConnected()) {
                        signIn(et_email.getText().toString(), et_password.getText().toString());
                    }
                } else
                    Toast.makeText(getApplicationContext(), "invalid email or password", Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean isConnected() {
        ConnectivityManager check = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        Boolean connected = false;
        NetworkInfo networkInfo = check.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            connected = true;
        }
        if (!connected)
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        return connected;
    }

    private void signIn(String u, String p) {
        Log.e("params", u+" "+p);
        final ProgressDialog progressDialog = new ProgressDialog(ctx);
        progressDialog.setMessage("validating...");
        RequestQueue queue = VolleySingleton.getInstance().getRequestQueue();

        String baseurl = "https://steff-bood-sw-eng.herokuapp.com/validateuser/";
        String url= baseurl+u+"/"+p;
        Log.e("url",url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();
                try {
                    String status = response.getString("status");
                    if (status.equals("success")) {
                        et_email.setText("");
                        et_password.setText("");
                        startActivity(new Intent(LoginActivity.this, AddItem.class));
                    } else if (status.equals("failure"))
                        Toast.makeText(getApplicationContext(), "Incorrect username or password", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        if (error.getMessage() != null) {
                            Log.e("volley", error.getMessage());
                            Toast.makeText(getApplicationContext(), "Server error", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "An error occured", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
        queue.add(request);
        progressDialog.show();
    }

    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
