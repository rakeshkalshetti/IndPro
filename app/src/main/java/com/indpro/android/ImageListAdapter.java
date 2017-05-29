package com.indpro.android;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.indpro.ImageInfoActivity;
import com.indpro.R;
import com.indpro.model.ImageDetails;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Rakesh on 27/05/17.
 */
public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> implements View.OnClickListener {

    private static final int TYPE_FULL = 0;
    private static final int TYPE_HALF = 1;
    private static final int TYPE_QUARTER = 2;


    //private final ImageLoader imageLoader;  volley image loader

    private int visibleThreshold = 10;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;

    private List<ImageDetails> imageDetailsList;

    private RecyclerView imageListViewR;
    private OnLoadMoreListener onLoadMoreListener;


    public ImageListAdapter(List<ImageDetails> imageDetailsList, RecyclerView imageListView) {
        this.imageDetailsList = imageDetailsList;
        this.imageListViewR = imageListView;

//        imageLoader = CustomVolleyRequest.getInstance().getImageLoader();   // loading images for volley


        if (imageListViewR.getLayoutManager() instanceof WrapStaggeredGridLayoutManager) {

            final WrapStaggeredGridLayoutManager staggeredGridLayoutManager = (WrapStaggeredGridLayoutManager) imageListViewR.getLayoutManager();

            imageListViewR.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = staggeredGridLayoutManager.getItemCount();

                    int[] firstVisibleItems = null;
                    firstVisibleItems = staggeredGridLayoutManager.findFirstVisibleItemPositions(firstVisibleItems);
                    if (firstVisibleItems != null && firstVisibleItems.length > 0) {
                        lastVisibleItem = firstVisibleItems[0];
                    }

                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {

        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_layout, parent, false);

        itemView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                final int type = viewType;

                final ViewGroup.LayoutParams lp = itemView.getLayoutParams();

                if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {

                    StaggeredGridLayoutManager.LayoutParams sglp = (StaggeredGridLayoutManager.LayoutParams) lp;
                    sglp.setFullSpan(false);

                    switch (type) {
                        case TYPE_FULL:
                            sglp.setFullSpan(true);
                            break;
                        case TYPE_HALF:
                            sglp.setFullSpan(false);
                            sglp.width = itemView.getWidth();
                            break;
                        case TYPE_QUARTER:
                            sglp.setFullSpan(false);
                            sglp.width = itemView.getWidth();
                            sglp.height = itemView.getHeight() / 2;
                            break;
                    }
                    itemView.setLayoutParams(sglp);
                    final WrapStaggeredGridLayoutManager lm =
                            (WrapStaggeredGridLayoutManager) ((RecyclerView) parent).getLayoutManager();

                    lm.invalidateSpanAssignments();
                }
                itemView.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });

        ViewHolder viewHolder = new ViewHolder(itemView);
        viewHolder.imageView.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ImageDetails imageDetails = imageDetailsList.get(position);

        holder.bindView(imageDetails);
    }

    @Override
    public int getItemCount() {
        return imageDetailsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        final int modeEight = position % 9;
        switch (modeEight) {

            case 2:
            case 4:
                return TYPE_HALF;
            case 1:
            case 3:
            case 5:
            case 6:
            case 7:
            case 8:

                return TYPE_QUARTER;
        }
        return TYPE_FULL;
    }

    public void setLoaded() {
        loading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public void onClick(View v) {
        ImageDetails imageDetails = (ImageDetails) v.getTag();
        Intent im = new Intent(v.getContext(), ImageInfoActivity.class);
        int count = imageDetails.getClick() + 1;
        ImageDatabase.getInstance().updateClick(imageDetails.getId(), count);
        imageDetails.setClick(count);
        im.putExtra("imageDetails", imageDetails);
        v.getContext().startActivity(im);
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }

        public void bindView(ImageDetails imageDetails) {

            imageView.setImageResource(R.drawable.nophotos);


            if (imageDetails != null) {
                if (!TextUtils.isEmpty(imageDetails.getImageUrl())) {

                    /**
                     * in this example volley and picasso both implemented volley code is in comment
                     *
                     * for loading images with volley having issues it places images in wrong place inside recyclerview
                     *
                     * So picasso is better to way to solve this issues
                     *
                     * if you want check volley example just replace ImageView with NetworkImageView in XML and Java files
                     */
                    /*imageLoader.get(imageDetails.getImageUrl(), ImageLoader.getImageListener(imageView,
                            R.drawable.nophotos, R.drawable.nophotos));

                    imageView.setImageUrl(imageDetails.getImageUrl(), imageLoader);*/

                    Picasso.with(imageView.getContext())
                            .load(imageDetails.getImageUrl())
                            .placeholder(R.drawable.nophotos)
                            .error(R.drawable.nophotos)
                            .into(imageView);

                    imageView.setTag(imageDetails);
                }
            }
        }
    }
}
