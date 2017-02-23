package com.example.dell.w2b;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;

import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;

/*class Obj_details extends AppCompatActivity
{
    String curr;
    String title;
    String text;
    String link;
    int amount;

    Obj_details()
    {
        curr = null;
        title = null;
        text = null;
        link = null;
        amount = 0;
    }
}*/

public class img_display extends AppCompatActivity {
    static final int CAM_REQUEST = 1;
    ImageView img;
    Button upload;
    Calendar c = Calendar.getInstance();
    //Obj_details obj = new Obj_details();
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        AndroidNetworking.initialize(getApplicationContext());
        setContentView(R.layout.activity_img_display);
        img = (ImageView) findViewById(R.id.imageView);

        final TextView list = (TextView) findViewById(R.id.textView7);
        final TextView url1 = (TextView) findViewById(R.id.textView8);
        final TextView url2 = (TextView) findViewById(R.id.textView9);

        list.setVisibility(View.GONE);
        url1.setVisibility(View.GONE);
        url2.setVisibility(View.GONE);
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final File imgfile = getFile();
        camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imgfile));
        startActivityForResult(camera,CAM_REQUEST);

        upload = (Button) findViewById(R.id.upload_button);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               final TextView load= (TextView) findViewById(R.id.textView3);
               final TextView failload = (TextView) findViewById(R.id.textView);
                load.setText("");
                failload.setText("");

                AndroidNetworking.upload("http://where2buy.azurewebsites.net/imgread/")
                        .addMultipartFile("image",imgfile)
                        .addMultipartParameter("key","value")
                        .setTag("uploadTest")
                        .setPriority(Priority.HIGH)
                        .build()
                        .setUploadProgressListener(new UploadProgressListener() {
                            @Override
                            public void onProgress(long bytesUploaded, long totalBytes) {
                                // do anything with progress
                            }
                        })
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {

                                String title = response.optString("title");
                                String text = response.optString("text");
                                String curr = response.optString("curr");
                                String flplink = response.optString("link");
                                String amzlink =response.optString("amazonlink");
                                int amount = response.optInt("amount");

                                img.setImageDrawable(null);
                                upload.setVisibility(View.GONE);
                                list.setVisibility(View.VISIBLE);
                                url1.setVisibility(View.VISIBLE);
                                url2.setVisibility(View.VISIBLE);
                                load.setVisibility(View.GONE);
                                list.setText("Title : "+ title +"\nPrice : "+amount+" "+curr+"\nText : "+text);
                                url1.setText("Flipkart Link : "+flplink);
                                url2.setText("Amazon Link : "+amzlink);

                            }
                            @Override
                            public void onError(ANError error) {
                                failload.setText("Image upload Failed");
                            }
                        });
                load.setText("Loading");
            }
        });

    }

    private File getFile() {
        File folder = new File("/storage/emulated/0/W2B");
        if (!folder.exists()) {
            folder.mkdir();
        }

        File myimage = new File(folder,"w2b_image"+c.getTime()+".jpg");
        return myimage;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String path = "/storage/emulated/0/W2B/w2b_image"+c.getTime()+".jpg";
        img.setImageDrawable(Drawable.createFromPath(path));
    }

}

