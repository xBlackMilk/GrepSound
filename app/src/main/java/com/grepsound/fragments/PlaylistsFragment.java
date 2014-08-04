package com.grepsound.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.grepsound.R;
import com.grepsound.adapters.PlaylistAdapter;
import com.grepsound.model.Playlists;
import com.grepsound.model.Playlist;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

/**
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <phk@FreeBSD.ORG> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return
 *
 * Alexandre Lision on 2014-04-21.
 */

public class PlaylistsFragment extends ScrollTabHolderFragment implements AbsListView.OnScrollListener, RequestListener<Playlists> {

    private Callbacks mCallbacks = sDummyCallbacks;

    private static final String TAG = PlaylistsFragment.class.getSimpleName();
    private PlaylistAdapter mAdapter;
    private ListView mListView;

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mCallbacks.displayPlaylistDetails(mAdapter.getItem(position));
        }
    };

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mScrollTabHolder != null)
            mScrollTabHolder.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount, 1);
    }

    @Override
    public void adjustScroll(int scrollHeight) {
        if (scrollHeight == 0 && mListView.getFirstVisiblePosition() >= 1) {
            return;
        }

        mListView.setSelectionFromTop(1, scrollHeight);

    }

    public interface Callbacks {
        public void getPlaylists(RequestListener<Playlists> cb);

        public void displayPlaylistDetails(Playlist selected);

    }

    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void getPlaylists(RequestListener<Playlists> cb) {
        }

        @Override
        public void displayPlaylistDetails(Playlist selected) {

        }

    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
        mCallbacks.getPlaylists(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new PlaylistAdapter(getActivity(), true);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.frag_playlists, null);

        LinearLayout viewHeader = new LinearLayout(getActivity());
        viewHeader.setOrientation(LinearLayout.HORIZONTAL);
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.header_height));
        viewHeader.setLayoutParams(lp);


        mListView = (ListView) rootView.findViewById(R.id.playlists_grid);
        mListView.setOnScrollListener(this);
        mListView.setOnItemClickListener(mItemClickListener);
        mListView.addHeaderView(viewHeader, null, false);
        mListView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onRequestFailure(SpiceException e) {
        Log.e(TAG, "Failure");
    }

    @Override
    public void onRequestSuccess(Playlists tr) {
        Log.e(TAG, "Success");
        mAdapter.addAll(tr);
        mAdapter.notifyDataSetChanged();
    }
}
