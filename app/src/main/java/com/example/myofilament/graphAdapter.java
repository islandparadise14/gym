package com.example.myofilament;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class graphAdapter extends RecyclerView.Adapter< graphAdapter.ViewHolder> {

    private ArrayList<Integer> mData;
    private Context mContext;

    public interface OnItemClickListener{
        void onItemClick(View view, String position);
    }
    private OnItemClickListener onItemClickListener;

    public class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.percentProgressBar)
        ProgressBar percentProgress;
        @BindView(R.id.dataPercent)
        TextView dataPercent;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public graphAdapter(ArrayList<Integer> Data) {
        mData = Data;
    }

    @Override
    public graphAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_graph
                        , parent, false);
        mContext = parent.getContext();

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(graphAdapter.ViewHolder holder, final int position) {
        holder.percentProgress.setProgress(mData.get(position));
        holder.dataPercent.setText(mData.get(position)+" %");
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }
}
