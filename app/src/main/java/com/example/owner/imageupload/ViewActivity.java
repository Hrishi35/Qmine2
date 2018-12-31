package com.example.owner.imageupload;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ViewActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ViewAdapter viewAdapter;
    List<ImageList> imglist;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        recyclerView = (RecyclerView) findViewById(R.id.rcy);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        getData();
//        btn = findViewById(R.id.create);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                createPDF();
//            }
//        });
    }

    private void getData() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Loading");
        dialog.setMessage("Please Wait ...");
        dialog.show();
        String url = "https://qmine.000webhostapp.com/image/retrieve.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                recyclerView.setVisibility(View.VISIBLE);
                try {
                    JSONObject object = new JSONObject(response);
                    Log.d("sdf", "response" + response);
                    imglist = new ArrayList<>();
                    JSONArray array = object.getJSONArray("images");
                    Log.d("jjk", "array" + array);

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject ob = array.getJSONObject(i);
                        ImageList img = new ImageList();
                        //img.setId(ob.getString("id"));
                        //String afterdecode = URLDecoder.decode(ob.getString("img"),"UTF-8");
                        img.setImageurl(ob.getString("url"));
                        img.setQuestion(ob.getString("question"));
                        img.setMarks(ob.getString("marks"));

                        Log.d("aa", "img" + img);
                        imglist.add(img);
                        Log.d("aa", "list" + imglist);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setupRecycler();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);

    }

    private void setupRecycler() {
        viewAdapter = new ViewAdapter(this, imglist);
        recyclerView.setAdapter(viewAdapter);
    }

    public void createPDF() {
        new CreatePdf().execute();
    }
    private class CreatePdf extends AsyncTask<String,String,String>{
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(ViewActivity.this);
            dialog.setTitle("Loading");
            dialog.setMessage("Please Wait ...");
            dialog.show();
        }
        @Override
        protected String doInBackground(String... strings) {
            Document doc = new Document();
            String path = Environment.getExternalStorageDirectory() + "/PDF";
            try {
                // PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(path));
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdir();
                }
                File file = new File(dir, "imagePaper.pdf");
                FileOutputStream fout = new FileOutputStream(file);
                PdfWriter.getInstance(doc, fout);
                doc.open();
//                String imageUrl = "https://qmine.000webhostapp.com/image/image/images.jpeg";
//                Image image = Image.getInstance(new URL(imageUrl));
//                doc.add(image);
                ImageList imgob = new ImageList();
                for (int i=0;i<imglist.size();i++)
                {

                    Paragraph p = new Paragraph();
                    String temp="Q."+i+" "+imgob.getQuestion()+"["+imgob.getMarks()+"]";
                    Log.d("l","ques"+imgob.getQuestion());
                    p.add(temp);
                    doc.add(p);
                    String imageUrl =imgob.getImageurl();
                    Image image = Image.getInstance(new URL(imageUrl));
                    doc.add(image);
                }
                doc.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
                } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
            @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
        }
    }
}
