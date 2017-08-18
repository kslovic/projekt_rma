package hr.ferit.kslovic.petsmissingorfound;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.UriLoader;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ImageSwitch extends Activity {
    private ImageSwitcher isPetPic;
    private Button bPrevious;
    private Button bNext;
    ArrayList<String> pList;
    private int i=0;
    private Bitmap bm;
    Context mcontext;
    private StorageReference mStorageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_switcher_layout);

        mcontext = this ;
        setUI();
    }

    private void setUI() {
        pList = new ArrayList<>();
        pList = getIntent().getStringArrayListExtra("pictureList");
        bPrevious = (Button) findViewById(R.id.bPrevious);
        bNext = (Button) findViewById(R.id.bNext);

        isPetPic = (ImageSwitcher) findViewById(R.id.isPetPic);
        isPetPic.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView myView = new ImageView(getApplicationContext());
                myView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                myView.setLayoutParams(new
                        ImageSwitcher.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                return myView;
            }
        });
        if(pList.size()>0) {
            loadImage(pList.get(0));
        }
        bPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(i>0){
                    Toast.makeText(getApplicationContext(), "previous Image"+i,
                            Toast.LENGTH_LONG).show();
                    i--;
                    String picture = pList.get(i);
                    loadImage(picture);
                }
            }
        });
        bNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(i<pList.size()-1){
                    Toast.makeText(getApplicationContext(), "next Image+i",
                            Toast.LENGTH_LONG).show();
                    i++;
                   String picture = pList.get(i);
                    loadImage(picture);
                }
            }
        });
    }


    public void loadImage(final String pPicture){

                Uri uri = Uri.parse(pPicture);
                Glide.with(mcontext)
                        .asBitmap()
                        .load(uri)
                        .listener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                isPetPic.setImageDrawable(new BitmapDrawable(getResources(), resource));
                                return false;
                            }
                        }).into((ImageView) isPetPic.getCurrentView());


    }
}
