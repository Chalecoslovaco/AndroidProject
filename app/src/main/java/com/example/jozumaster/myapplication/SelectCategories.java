package com.example.jozumaster.myapplication;

import android.app.Activity;
import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by JozuMaster on 18/05/2015.
 */
public class SelectCategories extends AsyncTask<Void, Void, Boolean> {
    private final String urlString = "http://meetgame.es/MeetGame/SelectCategories.php";
    private Activity activity;
    private MenuFragment menuFragment;
    private ArrayList<Categorie> objectCategorie;

    public SelectCategories(Activity activity, MenuFragment menuFragment){
        this.activity = activity;
        this.menuFragment = menuFragment;
    }

    @Override
    public Boolean doInBackground(Void... params){
        this.objectCategorie = this.parseStringToCategorie(this.convertStreamToString(this.openHttpConnection(this.urlString)));
        return true;
    }

    @Override
    public void onPostExecute(Boolean result){
        if(result) {
            String[] colDB = {"_id", "nameCategorie", "imageCategorie"};
            MatrixCursor matrixCursor = new MatrixCursor(colDB);
            for (Categorie categorie : objectCategorie) {
                matrixCursor.addRow(new Object[]{categorie.getIdCategorie(), categorie.getNameCategorie(), categorie.getImageCategorie()});
            }
            String[] arrayNamesCols = {"nameCategorie", "imageCategorie"};
            int[] arrayNamesViews = {R.id.textView_MenuFragment_textOption, R.id.imageView_MenuFragment_imageOption};
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this.activity, R.layout.option_menu, matrixCursor, arrayNamesCols, arrayNamesViews, 0);
            ListView list = (ListView) this.menuFragment.getView().findViewById(R.id.listView_MenuFragment_list);
            list.setAdapter(adapter);
        }else{
            Toast.makeText(this.activity, "ERROR BASE DE DATOS", Toast.LENGTH_LONG).show();
        }
    }

    private InputStream openHttpConnection(String urlString){
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(urlString);
        try{
            HttpResponse httpResponse = httpClient.execute(httpPost);
            return httpResponse.getEntity().getContent();
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private ArrayList<Categorie> parseStringToCategorie(String data){
        ArrayList<Categorie> objectCategories = new ArrayList<Categorie>();
        String [] categories = data.split("<br>");
        for(int count = 0; count < categories.length-1; count++){
            String [] attributes = categories[count].split("-");
            Categorie categorie = new Categorie();
            categorie.setIdCategorie(Integer.parseInt(attributes[0]));
            categorie.setNameCategorie(attributes[1]);
            categorie.setImageCategorie(this.getIntResourceByName(attributes[2]));
            objectCategories.add(categorie);
        }
        return objectCategories;
    }

    private String convertStreamToString(InputStream inputStream) {
        if(inputStream != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();
        }else{
            return "";
        }
    }

    private int getIntResourceByName(String aString) {
        String packageName = this.activity.getPackageName();
        int resId = this.activity.getResources().getIdentifier("joysticks1", "drawable", packageName);
        return resId;
    }
}
