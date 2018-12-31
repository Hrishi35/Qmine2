package com.example.owner.imageupload;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ViewAdapter extends RecyclerView.Adapter<ViewAdapter.ViewHolder> {
    private Context context;
    private List<ImageList> imageLists;

    public ViewAdapter(Context context, List<ImageList> imageLists) {
        this.context = context;
        this.imageLists = imageLists;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.image_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String urls;
holder.itemView.setTag(imageLists.get(position));

        final ImageList il = imageLists.get(position);
        holder.tq.setText(il.getQuestion());
        holder.tm.setText("["+il.getMarks()+"]");
        urls=il.getImageurl();
        if (urls.equalsIgnoreCase("null")){
            holder.iv.setVisibility(View.GONE);
        }
        else{
        Log.d("as","url"+urls);
        Glide.with(this.context)
                .load(urls)
                .into(holder.iv);
    }}

    @Override
    public int getItemCount() {
        return imageLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv;
        TextView tq,tm;
        public ViewHolder(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.iview);
            tq=(TextView)itemView.findViewById(R.id.qs);
            tm=(TextView)itemView.findViewById(R.id.mr);
        }
    }
}
