package com.example.newocrapp.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.BitmapCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.newocrapp.FileActivity;
import com.example.newocrapp.Model.Document;
import com.example.newocrapp.R;

import java.io.IOException;
import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {
    ArrayList<Document> arrayList;
    Context context;

    public ListAdapter(ArrayList<Document> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_card, parent, false);

        return new ListViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {

        Document document = arrayList.get(position);
        holder.name.setText(document.getName());
        holder.date.setText(document.getDate());
        holder.size.setText(String.format("%.3f", document.getSize() / 1000000) + "MB");

        holder.layout.setOnClickListener(view -> {
            Intent intent = new Intent(context, FileActivity.class);
            intent.putExtra("name", document.getName());
            intent.putExtra("image", document.getImage());
            intent.putExtra("text", document.getText());
            intent.putExtra("date", document.getDate());
            intent.putExtra("size", document.getSize());

            context.startActivity(intent);
            ((Activity) context).finish();

        });


        try {
            Glide.with(context).load(document.getImage())
                    .placeholder(R.drawable.common_google_signin_btn_icon_dark)
                    .apply(new RequestOptions().override(60, 60))
                    .centerCrop()
                    .into(holder.imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public Document removeItem(int position) {
        final Document model = arrayList.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, Document model) {
        arrayList.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final Document model = arrayList.remove(fromPosition);
        arrayList.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }


    public void animateTo(ArrayList<Document> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(ArrayList<Document> newModels) {
        for (int i = arrayList.size() - 1; i >= 0; i--) {
            final Document model = arrayList.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(ArrayList<Document> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final Document model = newModels.get(i);
            if (!arrayList.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(ArrayList<Document> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final Document model = newModels.get(toPosition);
            final int fromPosition = arrayList.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder {
        private TextView name, date, size;
        private RelativeLayout layout;
        private ImageView imageView;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            size = itemView.findViewById(R.id.size);
            imageView = itemView.findViewById(R.id.card_image);
            layout = itemView.findViewById(R.id.card);
        }
    }

}
