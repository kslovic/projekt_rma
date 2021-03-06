package hr.ferit.kslovic.petsmissingorfound.Activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import hr.ferit.kslovic.petsmissingorfound.Models.UploadPicture;
import hr.ferit.kslovic.petsmissingorfound.R;

public class ImageSwitch extends MenuActivity {
    private ImageSwitcher isPetPic;
    private Button bPrevious;
    private Button bNext;
    ArrayList<String> pList;
    private int i=0;
    Context mcontext;
    private DatabaseReference picRef;
    private ValueEventListener picListener;
    private String pid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_switcher_layout);
        mcontext = this ;
        pList = new ArrayList<>();
        pid = getIntent().getStringExtra("pictureList");
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPictures();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (picListener != null) {
            picRef.removeEventListener(picListener);
        }
    }

    private void setUI() {
        bPrevious = (Button) findViewById(R.id.bPrevious);
        bNext = (Button) findViewById(R.id.bNext);
        isPetPic = (ImageSwitcher) findViewById(R.id.isPetPic);
        Animation in = AnimationUtils.loadAnimation(this,android.R.anim.slide_in_left);
        isPetPic.setInAnimation(in);
        isPetPic.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView myView = new ImageView(getApplicationContext());
                myView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                myView.setLayoutParams(new ImageSwitcher.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                return myView;
            }
        });
            loadImage(pList.get(0));
        bPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(i>0){
                    i--;
                    String picture = pList.get(i);
                    loadImage(picture);}

            }
        });
        bNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(i<pList.size()-1){
                    i++;
                   String picture = pList.get(i);
                    loadImage(picture);}
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
    private void loadPictures() {
        picRef = FirebaseDatabase.getInstance().getReference("pets").child(pid);
        picListener = picRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pList.clear();

                for (DataSnapshot PicSnapshot :dataSnapshot.child("pictures").getChildren()) {
                    UploadPicture upPic = PicSnapshot.getValue(UploadPicture.class);

                    pList.add(upPic.getUrl());
                }
                if(pList.size()>0)
                    setUI();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });
    }
}
