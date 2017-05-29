package com.indpro.android;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by Rakesh on 28/05/17.
 */

public class WrapStaggeredGridLayoutManager extends StaggeredGridLayoutManager {

    public WrapStaggeredGridLayoutManager(int spanCount, int orientation) {
        super(spanCount, orientation);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            Log.e("TextNS", "In library it crashes for so this file is created and still they not solved this problem meet a IOOBE in RecyclerView");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
