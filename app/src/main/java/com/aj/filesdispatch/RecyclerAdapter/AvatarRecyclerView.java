package com.aj.filesdispatch.RecyclerAdapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.aj.filesdispatch.R;

import java.util.Arrays;

import static com.aj.filesdispatch.ApplicationActivity.Avatars;

public class AvatarRecyclerView extends ListAdapter<Integer, AvatarRecyclerView.Image> {
    private onItemClick onItemClick;
    private int selected;

    public AvatarRecyclerView(Activity activity,int currentId) {
        super(diffutil);
        selected=currentId;
        onItemClick = (AvatarRecyclerView.onItemClick) activity;
        submitList(Arrays.asList(Avatars));
    }

    private static final DiffUtil.ItemCallback<Integer> diffutil = new DiffUtil.ItemCallback<Integer>() {
        @Override
        public boolean areItemsTheSame(@NonNull Integer oldItem, @NonNull Integer newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Integer oldItem, @NonNull Integer newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public Image onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Image(LayoutInflater.from(parent.getContext()).inflate(R.layout.avatar_icon_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Image holder, int position) {
        holder.imageView.setImageResource(getItem(position));
        holder.imageView.setOnClickListener(v ->{
            selected=getItem(position);
            holder.layout.setSelected(true);
            onItemClick.onClick(selected,holder.imageView);
        });
        holder.layout.setSelected(selected==getItem(position));
    }

    public static class Image extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private ConstraintLayout layout;

        public Image(@NonNull View itemView) {
            super(itemView);
            imageView =  itemView.findViewById(R.id.avatar_icon);
            layout= (ConstraintLayout) itemView;
        }
    }

    public interface onItemClick {
        void onClick(int id,ImageView imageView);
    }

    public void setSelected(int selected) {
        this.selected = selected;
        notifyDataSetChanged();
    }
}
