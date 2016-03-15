package com.martinez.salvador.uberclone;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.firebase.client.Firebase;

import java.util.ArrayList;

public class SelectRiderActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<String> mRiderList;
    private Firebase mRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_rider);

        mRiderList = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.nearby_riders_list);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new RiderAdapter(mRiderList);
        mRecyclerView.setAdapter(mAdapter);

        mRoot = new Firebase(getString(R.string.dbroot));
    }


    public class RiderAdapter extends RecyclerView.Adapter<RiderAdapter.ViewHolder> implements View.OnClickListener {
        private ArrayList<String> mDataset;

        @Override
        public void onClick(View v) {
            TextView tv = (TextView) v.findViewById(R.id.distance);
            Log.i("Clicked", tv.getText().toString());
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mTextView;

            public ViewHolder(View v) {
                super(v);
                mTextView = (TextView) v.findViewById(R.id.distance);
            }
        }

        public RiderAdapter(ArrayList<String> d) {
            mDataset = d;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.rider_list_item, parent, false);
            v.setOnClickListener(this);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mTextView.setText(mDataset.get(position));
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }

    }
}
