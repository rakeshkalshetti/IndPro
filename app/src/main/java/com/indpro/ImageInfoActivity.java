package com.indpro;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.indpro.android.ApplicationLoader;
import com.indpro.android.CustomVolleyRequest;
import com.indpro.android.ImageDatabase;
import com.indpro.model.ImageDetails;
import com.squareup.picasso.Picasso;

public class ImageInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView lastUpdateTextView;

    private ImageView networkImageView;

    private Button likeButton;
    private Button disLikeButton;

    private ImageDetails imageDetails;
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_image_info);
        ApplicationLoader.lockOrientation (this);

        lastUpdateTextView = (TextView) findViewById (R.id.lastUpdatedTextView);

        networkImageView = (ImageView) findViewById (R.id.networkImageView);

        likeButton = (Button) findViewById (R.id.likeButton);
        disLikeButton = (Button) findViewById (R.id.disLikeButton);

        likeButton.setOnClickListener (this);
        disLikeButton.setOnClickListener (this);


        imageLoader = CustomVolleyRequest.getInstance ().getImageLoader ();

        imageDetails = getIntent ().getParcelableExtra ("imageDetails");
        updateUI (imageDetails);
    }

    @Override
    public void onClick(View v) {
        int type;
        if (imageDetails != null) {
            if (v.getId () == R.id.likeButton) {
                type = 0;
            } else {
                type = 1;
            }
            ImageDetails updatedImageDetail = ImageDatabase.getInstance ().updateLike (imageDetails, type);
            updateUI (updatedImageDetail);
        }
    }

    private void updateUI(ImageDetails imageDetails) {
        if (imageDetails != null) {
            if (!TextUtils.isEmpty (imageDetails.getImageUrl ())) {

                /*imageLoader.get (imageDetails.getImageUrl (), ImageLoader.getImageListener (networkImageView,
                        R.drawable.nophotos, R.drawable.nophotos));

                networkImageView.setImageUrl (imageDetails.getImageUrl (), imageLoader);*/

                Picasso.with(this)
                        .load(imageDetails.getImageUrl())
                        .placeholder(R.drawable.nophotos)
                        .error(R.drawable.nophotos)
                        .into(networkImageView);

            }
            if (!TextUtils.isEmpty (imageDetails.getUpdatedAt ())) {
                lastUpdateTextView.setText (String.format (getString (R.string.updatedAt), imageDetails.getUpdatedAt ()));
            }
            likeButton.setText (String.format (getString (R.string.likes), imageDetails.getLike ()));
            disLikeButton.setText (String.format (getString (R.string.disLikes), imageDetails.getDisLike ()));
        }
    }
}
