package com.example.user.spanish.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.sax.TextElementListener;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.spanish.GrammarTrains.GrammarTrainsActivity;
import com.example.user.spanish.Methods;
import com.example.user.spanish.Objects.InfoGrammarObject;
import com.example.user.spanish.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FragmentGrammar extends Fragment implements View.OnClickListener {


    RecyclerView rvGrammar;
    private String[] titles = {"Приветствие", "Знакомство", "Прощание", "Семья", "Комплименты", "Время", "Качества", "Календарь", "" +
            "Числа", "Погода", "В аэропорту"};
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("grammar").child("info");
    Button btnCheckConnection;
    ProgressBar progress;


    public static class InfoViewHolder extends RecyclerView.ViewHolder{

        TextView tvDays, tvTitle, tvProgress;
        ImageView ivImage, ivIsForgotten;
        LinearLayout linearLayout;

        public InfoViewHolder(final View itemView) {
            super(itemView);

            tvDays = (TextView) itemView.findViewById(R.id.tvDays);
            tvTitle = (TextView) itemView.findViewById(R.id.tvExerciseTitle);
            tvProgress = (TextView) itemView.findViewById(R.id.tvProgress);
            ivImage = (ImageView) itemView.findViewById(R.id.ivExerciseImage);
            ivIsForgotten = (ImageView) itemView.findViewById(R.id.ivIsForgotten);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.llExercise);
        }
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_grammar, container, false);
        btnCheckConnection = (Button) view.findViewById(R.id.btnGrammarConnection);
        btnCheckConnection.setOnClickListener(this);
        progress = (ProgressBar) view.findViewById(R.id.pbGrammarTrains);

        rvGrammar = (RecyclerView) view.findViewById(R.id.rvGrammar);
        rvGrammar.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvGrammar.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(view.getContext(), 1);
        rvGrammar.setLayoutManager(layoutManager);

        checkConnection();

        return view;
    }


    private void checkConnection(){
        Methods methods = new Methods();
        if(!methods.isConnected(getActivity())){
            Toast.makeText(getActivity(), "Проверьте подключение к Интернету и повторите попытку", Toast.LENGTH_SHORT).show();
            btnCheckConnection.setVisibility(View.VISIBLE);
        }else {
            btnCheckConnection.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
            setData();
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnGrammarConnection) {
            checkConnection();
        }
    }

    private void setData(){
        FirebaseRecyclerAdapter<InfoGrammarObject, InfoViewHolder> recyclerAdapter
                = new FirebaseRecyclerAdapter<InfoGrammarObject, InfoViewHolder>(
                InfoGrammarObject.class,
                R.layout.card_exercise,
                InfoViewHolder.class,
                reference
        ) {
            @Override
            protected void populateViewHolder(final InfoViewHolder viewHolder, final InfoGrammarObject infoGrammarObject, final int position) {

                progress.setVisibility(View.GONE);
                viewHolder.tvProgress.setText(infoGrammarObject.getProgress() + "%");
                viewHolder.tvTitle.setText(titles[position]);

                String s2 = infoGrammarObject.getDate();

                GregorianCalendar calendarNow = new GregorianCalendar();

                Pattern pattern = Pattern.compile("[.]");
                Matcher matcher1 = pattern.matcher(s2);
                s2 = matcher1.replaceAll(" ");

                ArrayList<String> date2 = new ArrayList<>(Arrays.asList(s2.split(" ")));

                GregorianCalendar calendarDate = new GregorianCalendar();
                calendarDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date2.get(0)));
                calendarDate.set(Calendar.MONTH, Integer.parseInt(date2.get(1)) - 1);
                calendarDate.set(Calendar.YEAR, Integer.parseInt(date2.get(2)));

                int daysNow = calendarNow.get(Calendar.DAY_OF_YEAR);
                int daysDate = calendarDate.get(Calendar.DAY_OF_YEAR);
                int days = daysDate - daysNow;
                if(infoGrammarObject.getProgress() == 100) {
                    if (days > 0) {
                        viewHolder.ivIsForgotten.setBackground(getResources().getDrawable(R.drawable.ic_time_green));
                        viewHolder.tvDays.setText("Повторите упражнение через " + days + " дн.");
                    } else {
                        viewHolder.ivIsForgotten.setBackground(getResources().getDrawable(R.drawable.ic_time_orange));
                        viewHolder.tvDays.setText("Нужно повторить упражнение.");
                    }
                }else {
                    viewHolder.tvDays.setText("Есть неизученные фразы.");
                    viewHolder.ivIsForgotten.setBackground(getResources().getDrawable(R.drawable.ic_time_orange));
                }


                Picasso.get().load(infoGrammarObject.getImage()).into(viewHolder.ivImage);

                final Intent intent = new Intent(getView().getContext(), GrammarTrainsActivity.class);

                viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (position) {

                            case 0:
                                intent.putExtra("query", "0_greeting");
                                break;

                            case 1:
                                intent.putExtra("query", "1_acquaintance");
                                break;
                            case 2:
                                intent.putExtra("query", "2_farewell");
                                break;
                            case 3:
                                intent.putExtra("query", "3_family");
                                break;
                            case 4:
                                intent.putExtra("query", "4_compliments");
                                break;
                            case 5:
                                intent.putExtra("query", "5_time");
                                break;
                            case 6:
                                intent.putExtra("query", "6_qualities");
                                break;
                            case 7:
                                intent.putExtra("query", "7_calendar");
                                break;
                            case 8:
                                intent.putExtra("query", "8_numbers");
                                break;
                            case 9:
                                intent.putExtra("query", "9_weather");
                                break;
                            case 10:
                                intent.putExtra("query", "a_airport");
                                break;
                        }

                        intent.putExtra("info", infoGrammarObject);

                        getActivity().startActivity(intent);
                    }
                });


            }
        };

        rvGrammar.setAdapter(recyclerAdapter);
    }


}