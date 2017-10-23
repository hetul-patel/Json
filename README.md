# Android MySQL Connection Tutorial

![MacDown Screenshot](http://iste-itnu.com/homey/OKHTTP.png)

## Prerequisites for this tutorial

1. MySQL + PHP hosting ( https://in.000webhost.com provides free MySQL Hosting and Storage space for PHP file hosting )
2. OkHTTP3 library for android ( compile 'com.squareup.okhttp3:okhttp:3.9.0' )

## Server side work first !!

*  Create a new MySQL Database and create new table as follow.

```
CREATE TABLE `student` (
  `id` int(5) NOT NULL,
  `name` varchar(25) COLLATE utf8_unicode_ci NOT NULL,
  `phone` varchar(10) COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
```

```
ALTER TABLE `student`
  ADD PRIMARY KEY (`id`);
```
```  
ALTER TABLE `student`
  MODIFY `id` int(5) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;
```

* Upload `api.php` file to server for fetching the students' data and displaying it in JSON format.

### api.php
```
<?php
$host = "localhost";	
$user = "id1739352_admin"; 		//enter your own user name
$pass = "admin";						//enter your own password
$database = "id1739352_okhttp";  	//enter your own database name

$conn = mysqli_connect($host, $user, $pass,$database) or die("Could not connect to host.");


$sth = mysqli_query($conn,"SELECT * FROM student");
$rows = array();
while($r = mysqli_fetch_assoc($sth)) {
   $rows[] = $r;
}
print json_encode($rows);
?>
```

* Upload apipost.php file to ENTER DATA INTO DATABASE.

### apipost.php

```
<?php
$host = "localhost";	
$user = "id1739352_admin"; 		//enter your own user name
$pass = "admin";						//enter your own password
$database = "id1739352_okhttp";  	//enter your own database name

$conn = mysqli_connect($host, $user, $pass,$database) or die("Could not connect to host.");

// Escape user inputs for security
$name = mysqli_real_escape_string($conn, $_REQUEST['name']);
$phone = mysqli_real_escape_string($conn, $_REQUEST['phone']);


// attempt insert query execution
$sql = "INSERT INTO student (name, phone) VALUES ('$name', '$phone')";
if(mysqli_query($conn, $sql)){
    echo "1"; //1 for success
} else{
    echo "0"; //0 for fail
}
// close connection
mysqli_close($conn);
?>

``` 

## First let us see how to use OkHTTP3.

#### Get data as JSON from `https://nutechtest.000webhostapp.com/api.php` ( Use your own `url` )

```
void getRequest() throws IOException {
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
	        	
	        	//Parse myResponse here as shown below.
	
				}
	        });
	    }
	});
}
```  

#### Parse JSON and retrive each row as JSONObjecta and add to List.

```
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
```

### To post data you will require FormBody object to add parameters to request.

Here `posturl` is : `https://nutechtest.000webhostapp.com/apipost.php`

```
OkHttpClient client = new OkHttpClient();

RequestBody formBody = new FormBody.Builder()
        .add("name", ed_name.getText().toString())
        .add("phone", ed_phone.getText().toString())
        .build();
	
Request request = new Request.Builder()
        .url(posturl)
        .post(formBody)
        .build();
```
 
## Now open your Android Studio :)

We will use **OkHTTP3** library to send GET and POST request to our PHP scripts.

* Add  `compile 'com.squareup.okhttp3:okhttp:3.9.0'` to **buid.gradle(module:app)** inside **dependencies** block.

*  Add this `<uses-permission android:name="android.permission.INTERNET"/>` into  **AndroidManifest.xml** above **Application** tag.

*  Add files to your new project as mentioned below.

### Activity_main.xml

```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/activity_main"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:paddingLeft="@dimen/activity_horizontal_margin"
    android:paddingRight="@dimen/activity_horizontal_margin"
    android:paddingTop="@dimen/activity_vertical_margin"
    android:paddingBottom="@dimen/activity_vertical_margin"
    tools:context="com.hetulpatel.okhttp.json.MainActivity">

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:gravity="center_horizontal"
        android:layout_marginBottom="10dp"
        android:textColor="@color/colorPrimaryDark"
        android:text="JSON DEMO" />

    <View
        android:layout_width="match_parent"
        android:layout_height="1dp"
        android:background="@color/colorPrimary"
        android:layout_marginBottom="@dimen/activity_vertical_margin"
        />

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:gravity="center_horizontal"
        android:layout_marginBottom="@dimen/activity_vertical_margin"
        android:textColor="@color/colorAccent"
        android:text="New Entry" />

    <EditText
        android:id="@+id/ed_name"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Enter name"
        android:layout_marginBottom="@dimen/activity_vertical_margin"
        />

    <EditText
        android:id="@+id/ed_phone"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Enter phone number"
        />

    <Button
        android:id="@+id/save"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="right"
        android:layout_marginBottom="@dimen/activity_vertical_margin"
        android:background="@color/colorPrimary"
        android:text="Save"
        android:textColor="#ffffff"/>

    <View
        android:layout_width="match_parent"
        android:layout_height="1dp"
        android:background="@color/colorPrimary"
        android:layout_marginBottom="@dimen/activity_vertical_margin"
        />

    <TextView
        android:id="@+id/message"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:gravity="center_horizontal"
        android:layout_marginBottom="@dimen/activity_vertical_margin"
        android:textColor="@color/colorAccent"
        android:text="Existing Records" />

    <ListView
        android:id="@+id/list"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />


</LinearLayout>
```

###itemlistrow.xml ( Listview Layout )

```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical" android:layout_width="match_parent"
    android:layout_height="match_parent">

    <TextView
        android:id="@+id/name"
        android:textColor="#000000"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginBottom="10px"
        android:text="Hetul Patel"/>

    <TextView
        android:id="@+id/phone"
        android:textColor="#000000"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginBottom="10px"
        android:text="999999999"/>

</LinearLayout>
```

### MainActivity.java

```
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
```
