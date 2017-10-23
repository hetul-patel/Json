package com.hetulpatel.okhttp.json;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import okhttp3.Call;
import okhttp3.Callback;


public class MainActivity extends AppCompatActivity {

     /*--------------------

        1. Make sure you enter below line in buid.gradle(module:app) inside dependencies and compile before runnig app

        compile 'com.squareup.okhttp3:okhttp:3.9.0'

        2. And below line into AndroidManifest.xml

        <uses-permission android:name="android.permission.INTERNET"/>

        -------------------*/

    private TextView message;
    private EditText ed_name, ed_phone;
    private Button save;
    private ListView list;
    private ArrayList<student_data_model> students;

    private String url= "https://nutechtest.000webhostapp.com/api.php";
    private String posturl = "https://nutechtest.000webhostapp.com/apipost.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        onInit(); //Calling init method to initialise components.


        //Getting exisisting data from mysql database using get request
        try {
            message.setText(message.getText() + " are being fetched....");
            getRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void onInit() {

        message = (TextView) findViewById(R.id.message);
        ed_name = (EditText) findViewById(R.id.ed_name);
        ed_phone = (EditText) findViewById(R.id.ed_phone);
        save = (Button)findViewById(R.id.save);
        list = (ListView)findViewById(R.id.list);

        save.setOnClickListener(new PostData());

    }

    //PostData is called when user clicks on Save button and enter data in MySql database....

    public class PostData implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            OkHttpClient client = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add("name", ed_name.getText().toString())
                    .add("phone", ed_phone.getText().toString())
                    .build();

            Request request = new Request.Builder()
                    .url(posturl)
                    .post(formBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    call.cancel();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    final String myResponse = response.body().string();

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Log.d("Result ",myResponse);

                            if(myResponse.equals("1")){
                                Toast.makeText(getApplicationContext(),"Data Entered",Toast.LENGTH_LONG).show();
                                try {
                                    list.invalidateViews();
                                    getRequest();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }else {
                                Toast.makeText(getApplicationContext(),"Try again later!",Toast.LENGTH_LONG).show();
                            }

                        }
                    });

                }
            });

        }
    }


    //Below three are used for getting data from the database.....


    void getRequest() throws IOException {

        students = new ArrayList<>();

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String myResponse = response.body().string();

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //txtString.setText(myResponse);
                        //Toast.makeText(getApplicationContext(),myResponse,Toast.LENGTH_LONG).show();

                        try {
                            JSONArray array = new JSONArray(myResponse);

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject student = array.getJSONObject(i);
                                students.add(new student_data_model(student.getString("name"),student.getString("phone")));
                            }

                            ListAdapter customAdapter = new ListAdapter(getApplicationContext(), R.layout.itemlistrow, students);
                            list.setAdapter(customAdapter);
                            message.setText("Exisiting Records");
                            Toast.makeText(getApplicationContext(),"Records Fetched",Toast.LENGTH_LONG).show();

                        }catch (JSONException j){
                            j.printStackTrace();
                        }


                    }
                });

            }
        });
    }

    public class ListAdapter extends ArrayAdapter<student_data_model> {

        public ListAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        public ListAdapter(Context context, int resource, List<student_data_model> items) {
            super(context, resource, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;

            if (v == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                v = vi.inflate(R.layout.itemlistrow, null);
            }

            student_data_model p = getItem(position);

            if (p != null) {
                TextView tt1 = (TextView) v.findViewById(R.id.name);
                TextView tt2 = (TextView) v.findViewById(R.id.phone);

                if (tt1 != null) {
                    tt1.setText(p.getName());
                }

                if (tt2 != null) {
                    tt2.setText(p.getPhone());
                }

            }

            return v;
        }

    }

    public class student_data_model{
        String name;
        String phone;

        student_data_model(){}

        student_data_model(String name, String phone){
            setName(name);
            setPhone(phone);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }



}
