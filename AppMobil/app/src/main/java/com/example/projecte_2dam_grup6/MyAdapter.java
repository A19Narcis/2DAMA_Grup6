package com.example.projecte_2dam_grup6;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;


import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<Producte> listItems;
    private Context context;

    public MyAdapter(List<Producte> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_producte, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Producte listItem = listItems.get(position);

        holder.textViewHead.setText(listItem.getHead());
        holder.textViewUser.setText(listItem.getUser());

        Picasso.with(context).load(listItem.getImageURL()).into(holder.imageView);

        holder.textViewPreu.setText(listItem.getPreu() + " €");

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Info: " + listItem.getId_producte(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textViewHead;
        public TextView textViewUser;
        public ImageView imageView;
        public TextView textViewPreu;

        public LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewHead = itemView.findViewById(R.id.textViewHead);
            textViewUser = itemView.findViewById(R.id.textViewUser);
            imageView = itemView.findViewById(R.id.imageView);
            textViewPreu = itemView.findViewById(R.id.textViewPreu);
            linearLayout = itemView.findViewById(R.id.linearLayout);
        }
    }

}
