package com.project.t1.barcodescanneradmin;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class ViewItems extends ActionBarActivity {
    Toolbar toolbar;
    ListView listView;
    Context ctx=this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_items);
        toolbar=(Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        listView=(ListView) findViewById(R.id.listView);
        listView.setAdapter(new myAdapter(ctx));
    }

    class ListRow{
        String itemName,unit,code;
        double qty,price;


        ListRow(String code, String itemName,double price, String unit, double qty){
            this.itemName=itemName;
            this.unit=unit;
            this.qty=qty;
            this.code=code;
            this.price=price;

        }
    }
    class myAdapter extends BaseAdapter {

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
