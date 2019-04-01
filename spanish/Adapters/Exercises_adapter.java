package com.example.user.spanish.Adapters;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.user.spanish.GrammarTrains.GrammarTrainsActivity;
import com.example.user.spanish.R;


public class Exercises_adapter extends RecyclerView.Adapter<Exercises_adapter.ViewHolder> {

    private String[] Exercises;

    public Exercises_adapter(String[] exercises) {
        Exercises = exercises;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_exercise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.tvTitle.setText(Exercises[holder.getAdapterPosition()]);


        holder.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(), GrammarTrainsActivity.class);

                switch (holder.getAdapterPosition()) {

                    case 0:
                        intent.putExtra("name", holder.tvTitle.getText());
                        intent.putExtra("query", "greeting");
                        break;

                    case 1:
                        intent.putExtra("name", holder.tvTitle.getText());
                        intent.putExtra("query", "acquaintance");
                        break;
                    case 2:
                        intent.putExtra("query", "farewell");
                        break;
                    case 3:
                        intent.putExtra("query", "family");
                        break;
                    case 4:
                        intent.putExtra("query", "compliments");
                        break;
                    case 5:
                        intent.putExtra("query", "time");
                        break;
                }

                view.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return Exercises.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;

        public ViewHolder(final View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvExerciseTitle);

        }
    }

}
