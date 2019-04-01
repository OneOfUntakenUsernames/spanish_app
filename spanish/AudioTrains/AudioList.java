package com.example.user.spanish.AudioTrains;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.spanish.Methods;
import com.example.user.spanish.Objects.AudioObject;
import com.example.user.spanish.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class AudioList extends Fragment implements View.OnClickListener {

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("audio");
    RecyclerView rvAudio;
    Button btnCheckConnection;
    ProgressBar progress;
    ArrayList<AudioObject> audioObjects = new ArrayList<>();
    final String URI = "uri";
    final String WORDS = "words";
    final String TRANSLATIONS = "translations";
    final String QUESTIONS = "questions";

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_grammar, container, false);
        btnCheckConnection = (Button) view.findViewById(R.id.btnGrammarConnection);
        btnCheckConnection.setOnClickListener(this);
        progress = (ProgressBar) view.findViewById(R.id.pbGrammarTrains);

        rvAudio = (RecyclerView) view.findViewById(R.id.rvGrammar);
        rvAudio.setHasFixedSize(true);
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        rvAudio.setLayoutManager(layoutManager);

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


    private void setData(){
        FirebaseRecyclerAdapter<AudioObject, AudioViewHolder> adapter;
        adapter = new FirebaseRecyclerAdapter<AudioObject, AudioViewHolder>(
                AudioObject.class,
                R.layout.card_audio,
                AudioViewHolder.class,
                reference

        ) {
            @Override
            protected void populateViewHolder(AudioViewHolder viewHolder, AudioObject audioObject, final int position) {
                progress.setVisibility(View.GONE);
                viewHolder.tvTitleRussian.setText(audioObject.getTitleRussian());
                viewHolder.tvTitleSpanish.setText(audioObject.getTitleSpanish());
                audioObjects.add(audioObject);

                viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), AudioTrain.class);
                        String uri = audioObjects.get(position).getUri();
                        intent.putExtra(URI, uri);
                        intent.putExtra(WORDS, audioObjects.get(position).getWords());
                        intent.putExtra(TRANSLATIONS, audioObjects.get(position).getTranslations());
                        intent.putExtra(QUESTIONS, audioObjects.get(position).getQuestions());
                        startActivity(intent);
                    }
                });
            }

        };
        rvAudio.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnGrammarConnection) {
            checkConnection();
        }
    }

    public static class AudioViewHolder extends RecyclerView.ViewHolder{

        TextView tvTitleRussian, tvTitleSpanish;
        RelativeLayout layout;

        public AudioViewHolder(View itemView) {
            super(itemView);

            tvTitleRussian = (TextView) itemView.findViewById(R.id.tvTitleRussian);
            tvTitleSpanish = (TextView) itemView.findViewById(R.id.tvTitleSpanish);
            layout = (RelativeLayout) itemView.findViewById(R.id.layoutAudioList);
        }
    }

}
