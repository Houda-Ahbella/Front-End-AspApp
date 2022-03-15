package com.example.parse_url_json;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {


    ArrayList<Livre> livres=new ArrayList<>();

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyAsyncTask asyncTask =new MyAsyncTask();
        listView=findViewById(android.R.id.list);

        asyncTask.execute("http://192.168.169.1:45455/api/Livres");
    }
    class MyAsyncTask extends AsyncTask<String, String, String> {
        String newData="";


        @Override
        protected void onPostExecute(String s) {
            new Adapter(livres,(RecyclerView) findViewById(R.id.resc));

        }

        @Override
        protected String doInBackground(String... strings) {
            publishProgress("Open connection.");
            String s="";
            try {
                URL url=new URL(strings[0]);
                HttpURLConnection urlConnection=(HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.connect();
                System.out.println("connect************");
                publishProgress("start reading!!");
                InputStream in=new BufferedInputStream(urlConnection.getInputStream());
                newData=Stream2String(in);
                JSONArray myarray=new JSONArray(newData);
                for(int i=0;i<myarray.length();i++){
                    JSONObject obj=myarray.getJSONObject(i);
                    String nom=obj.getString("nom");
                    String image=obj.getString("image");
                    System.out.println(nom + "====" + image);
                    System.out.println("****************************************************");
                    livres.add(new Livre(nom,image));

                }




            }catch (Exception exp){
                System.out.println("cant connect************");
                publishProgress("cannot connect to server");
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {

        }

        @SuppressLint("WrongThread")
        @Override
        protected void onPreExecute() {
            newData="";
            publishProgress("connecting attempt ongoing please wait !.");
        }
        public String Stream2String(InputStream in){
            BufferedReader buReader=new BufferedReader(new InputStreamReader(in));
            String text="",line;
            try {
                while ((line=buReader.readLine())!=null){
                    text+=line;
                }
            }catch (Exception exp){}
            System.out.println(text);
            System.out.println("****************************************************");
            return text;
        }
    }
}