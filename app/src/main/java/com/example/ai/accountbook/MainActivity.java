package com.example.ai.accountbook;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<CostBean> mCostBeanList;
    private DatabaseHelper mDatabaseHelper;
    private CostListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDatabaseHelper=new DatabaseHelper(this);
        mCostBeanList=new ArrayList<>();
        ListView costList=findViewById(R.id.lv_main);
        initCostData();
        adapter = new CostListAdapter(MainActivity.this, mCostBeanList);
        costList.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                /**
                 * 自定义对话框的布局AlertDialog,用setView方法设置
                 */
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater=LayoutInflater.from(MainActivity.this);
                View viewDialog=inflater.inflate(R.layout.new_cost_data,null);
                final EditText title=viewDialog.findViewById(R.id.et_cost_title);
                final EditText money=viewDialog.findViewById(R.id.et_cost_money);
                final DatePicker date=viewDialog.findViewById(R.id.datePicker);
                builder.setView(viewDialog);
                builder.setTitle("New Cost");

                /**
                 * 将数据存到数据库中
                 */
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CostBean costBean=new CostBean();
                        costBean.costTitle=title.getText().toString();
                        costBean.costMoney=money.getText().toString();
                        /**
                         * 注意字符串的拼接，月份得到的是0-11，所以要加一
                         */
                        costBean.costDate=date.getYear()+"-"+(date.getMonth()+1)+"-"+date.getDayOfMonth();
                        mDatabaseHelper.insertCost(costBean);
                        mCostBeanList.add(costBean);
                        /**
                         * 刷新listView,原理就在于list数据源改变时，notifyDataSetChanged方法就会同时adapter
                         */
                        adapter.notifyDataSetChanged();
                    }
                });
                /**
                 * 不存数据
                 */
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();
            }
        });
    }

    /**
     * Alt+Enter创建方法
     * Ctrl+Alt+t创建for循环
     */
    private void initCostData() {
        //mDatabaseHelper.deleteAllData();
        /**for (int i=0;i<6;i++) {
            CostBean costBean=new CostBean();
            costBean.costTitle=i+"mock";
            costBean.costDate="11-11";
            costBean.costMoney="20";
            mDatabaseHelper.insertCost(costBean);
        }*/

        Cursor cursor=mDatabaseHelper.getAllCostData();
        if(cursor!=null){
            while(cursor.moveToNext()){
                CostBean costBean=new CostBean();
                costBean.costTitle=cursor.getString(cursor.getColumnIndex("cost_title"));
                costBean.costDate=cursor.getString(cursor.getColumnIndex("cost_date"));
                costBean.costMoney=cursor.getString(cursor.getColumnIndex("cost_money"));
                mCostBeanList.add(costBean);
            }
            cursor.close();
        }
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
        if (id == R.id.action_chart) {
            Intent intent=new Intent(MainActivity.this,ChartsActivity.class);
            intent.putExtra("cost_list",(Serializable)mCostBeanList);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
