package com.example.owner.imageupload;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
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

public class GenerateActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    GenerateAdapter generateAdapter;
    List<ImageList> imglist;
    Button btn,btn_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate);
        recyclerView = (RecyclerView) findViewById(R.id.rcy1);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        getData();
        btn = findViewById(R.id.create);
        btn_send=findViewById(R.id.send);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPDF();
            }
        });
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"kedar.hrishi35@gmail.com"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Question Paper");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                File root = Environment.getExternalStorageDirectory();
                String pathToMyAttachedFile = "/PDF/questionPaper.pdf";
                File file = new File(root, pathToMyAttachedFile);
                if (!file.exists() || !file.canRead()) {
                    return;
                }
                Uri uri = Uri.fromFile(file);
                emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(emailIntent, "www.gmail.com"));
                Toast.makeText(getApplicationContext(),"sending..",Toast.LENGTH_SHORT).show();
            }
        });
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
        generateAdapter = new GenerateAdapter(this, imglist);
        recyclerView.setAdapter(generateAdapter);
    }

    public void createPDF() {
        new CreatePdf().execute();
    }

    private class CreatePdf extends AsyncTask<String, String, String> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
//            dialog = new ProgressDialog(GenerateActivity.this);
//            dialog.setTitle("Loading");
//            dialog.setMessage("Please Wait ...");
//            dialog.show();
            Toast.makeText(getApplicationContext(),"Generating PDF..!!",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... strings) {
            List<ImageList> queslist = ((GenerateAdapter) generateAdapter).getQuestionN();
            List<String> qList = new ArrayList<>();
            List<String> mList = new ArrayList<>();
            List<String> iList = new ArrayList<>();
            for (int j = 0; j < queslist.size(); j++) {
                ImageList ques = queslist.get(j);
                if (ques.isSelected() == true) {
                    String myques = ques.getQuestion();
                    qList.add(myques);
                    // Log.d("asd", "ques" + strArray[i]);
                    Log.d("asds", "q" + qList);
                    String mymarks = ques.getMarks();
                    mList.add(mymarks);
                    Log.d("asds", "m" + mList);
                    String myimage = ques.getImageurl();
                    iList.add(myimage);
                    Log.d("asds", "m" + iList);
                }
            }

            Document doc = new Document();
            String path = Environment.getExternalStorageDirectory() + "/PDF";
            try {
                // PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(path));
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdir();
                }
                File file = new File(dir, "questionPaper.pdf");
                FileOutputStream fout = new FileOutputStream(file);
                PdfWriter.getInstance(doc, fout);
                doc.open();

                Paragraph a = new Paragraph();
                a.setAlignment(Element.ALIGN_CENTER);
                Font font = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.BLACK);

                Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLACK);
                a.add(createBgChunk("XYZ Academy of Engineering and Technology,Nagpur", font1));
                Paragraph b = new Paragraph();
                b.setAlignment(Element.ALIGN_CENTER);
                b.add(createBgChunk("Department of Computer Science and Engineering", font1));
                Paragraph c = new Paragraph();
                c.setAlignment(Element.ALIGN_CENTER);
                c.add(createBgChunk("SESSION:2017-18(EVEN Sem)", font1));
                Paragraph d = new Paragraph();
                d.setAlignment(Element.ALIGN_CENTER);
                d.add(createBgChunk("Pre-University Exam", font1));
                Paragraph e = new Paragraph();
                e.setAlignment(Element.ALIGN_CENTER);
                e.add(createBgChunk("Subject : Cloud Computing", font1));
                Paragraph f = new Paragraph();
                f.setAlignment(Element.ALIGN_CENTER);
                f.add(createBgChunk("VIII Semest5er", font));
                doc.add(a);
                doc.add(b);
                doc.add(c);
                doc.add(d);
                doc.add(e);
                doc.add(f);

                doc.add(new Paragraph("----------------------------------------------------------------------------------------------------------------------------"));
                doc.add(new Paragraph("Time:3hrs                                                                                                       Max.Marks:80"));

                doc.add(new Paragraph("----------------------------------------------------------------------------------------------------------------------------"));


                ImageList imgob = new ImageList();
                for (int i = 0; i < qList.size(); i++) {

                    Paragraph p = new Paragraph();
                    String temp = "Q." + (i+1) + " " + qList.get(i) + "        [" + mList.get(i) + "]";
                    Log.d("l", "ques" + imgob.getQuestion());
                    p.add(temp);
                    doc.add(p);
                    String imageUrl = iList.get(i);
                    Image image = Image.getInstance(new URL(imageUrl));
                    image.scaleToFit(100f,200f);
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
//            dialog.dismiss();
            Toast.makeText(getApplicationContext(),"PDF Generated..!!",Toast.LENGTH_SHORT).show();
        }
    }
    public Chunk createBgChunk(String s, Font font) {
        Chunk chunk = new Chunk(s, font);
        return chunk;
    }
}

