package com.example.user.spanish.AudioTrains;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.spanish.Objects.WordObject;
import com.example.user.spanish.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;

public class AudioTrain extends AppCompatActivity implements View.OnClickListener {

    Button btnPlay;
    SeekBar seekBar;
    String uri;
    MediaPlayer mediaPlayer;
    private final Handler handler = new Handler();
    TextToSpeech textToSpeech;
    int result;
    RecyclerView rvWords;
    Button btnReady;
    final String URI = "uri";
    final String WORDS = "words";
    final String TRANSLATIONS = "translations";
    final String QUESTIONS = "questions";
    ArrayList<String> words;
    ArrayList<String> translations;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_train);

        btnPlay = (Button) findViewById(R.id.btnStartAudio);
        btnPlay.setOnClickListener(this);
        btnReady = (Button) findViewById(R.id.btnReadyAudio);
        btnReady.setOnClickListener(this);
        seekBar = (SeekBar) findViewById(R.id.sbAudio);

        Intent intent = getIntent();
        uri = intent.getStringExtra(URI);
        words = intent.getStringArrayListExtra(WORDS);
        translations = intent.getStringArrayListExtra(TRANSLATIONS);

        mediaPlayer = MediaPlayer.create(this, Uri.parse(uri));


        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                seekChange(view);
                return false;
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stopPlay();
            }
        });

        rvWords = (RecyclerView) findViewById(R.id.rvUsefulWords);
        rvWords.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvWords.setLayoutManager(layoutManager);
        rvWords.setAdapter(new  AudioAdapter(words, translations));

    }


    public void startPlayProgressUpdater() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());

        if (mediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    startPlayProgressUpdater();
                }
            };
            handler.postDelayed(notification,1000);
        }else{
            mediaPlayer.pause();
            btnPlay.setText("Play");
            seekBar.setProgress(0);
        }
    }

    private void stopPlay() {
        mediaPlayer.stop();
        btnPlay.setEnabled(false);
        try {
            mediaPlayer.prepare();
            mediaPlayer.seekTo(0);
            btnPlay.setEnabled(true);
        }
        catch (Throwable t) {
            Toast.makeText(this, t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void seekChange(View view){
        if(mediaPlayer.isPlaying()){
            SeekBar sb = (SeekBar) view;
            mediaPlayer.seekTo(sb.getProgress());
        }
    }


    public void play(View view){

        mediaPlayer.start();
        startPlayProgressUpdater();
        btnPlay.setText("Pause");
    }

    public void pause(View view){

        mediaPlayer.pause();
        btnPlay.setText("Play");
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnStartAudio:
                if(btnPlay.getText().toString().equals("Play")){
                    play(view);
                    break;
                }
                if(btnPlay.getText().toString().equals("Pause")){
                    pause(view);
                    break;
                }
                break;
            case R.id.btnReadyAudio:
                stopPlay();
                FragmentAudioQuestions fragmentAudioQuestions = new FragmentAudioQuestions();
                Bundle args = new Bundle();
                args.putParcelableArrayList(QUESTIONS, getIntent().getParcelableArrayListExtra(QUESTIONS));
                fragmentAudioQuestions.setArguments(args);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

                fragmentTransaction.replace(R.id.container_main, fragmentAudioQuestions).commit();
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.container_audio);
                linearLayout.setVisibility(View.GONE);
                break;
        }
    }


    private void Speech(final String sentence){
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    Locale spanish = new Locale("es", "ES");
                    result = textToSpeech.setLanguage(spanish);
                    if (    result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(getApplicationContext(), "Feature not supported in your device", Toast.LENGTH_SHORT).show();
                    } else {
                        textToSpeech.speak(sentence, TextToSpeech.QUEUE_FLUSH, null);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Feature not supported on your device", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.ViewHolder>{

        private ArrayList<String> Words;
        private ArrayList<String> Translations;

        AudioAdapter(ArrayList<String> words, ArrayList<String> translations){
            Words = words;
            Translations = translations;
        }

        @Override
        public AudioAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_dictionary_word, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final AudioAdapter.ViewHolder holder, int position) {
            holder.tvWord.setText(Words.get(holder.getAdapterPosition()));
            holder.tvTranslation.setText(Translations.get(holder.getAdapterPosition()));
            holder.btnAdd.setBackground(getResources().getDrawable(R.drawable.ic_add));

            holder.btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Speech(holder.tvWord.getText().toString());
                }
            });

            holder.btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(((BitmapDrawable) holder.btnAdd.getBackground()).getBitmap() ==
                            ((BitmapDrawable) getResources().getDrawable(R.drawable.ic_done)).getBitmap()){
                        Toast.makeText(getApplicationContext(), "Слово уже в вашем словаре", Toast.LENGTH_SHORT).show();
                    }else {
                        DatabaseReference mReference;
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        mReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("dictionary");
                        WordObject wordObject;
                        wordObject = new WordObject(holder.tvWord.getText().toString(), holder.tvTranslation.getText().toString(),
                                false, false, false, false);
                        mReference.push().setValue(wordObject);
                        holder.btnAdd.setBackground(getResources().getDrawable(R.drawable.ic_done));
                    }
                }
            });


        }

        @Override
        public int getItemCount() {
            return Words.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            Button btnPlay, btnAdd;
            TextView tvWord, tvTranslation;

            public ViewHolder(View itemView) {
                super(itemView);

                btnPlay = (Button) itemView.findViewById(R.id.btnPlayWord);
                btnAdd = (Button) itemView.findViewById(R.id.btnDeleteWord);
                tvWord = (TextView) itemView.findViewById(R.id.tvWord);
                tvTranslation = (TextView) itemView.findViewById(R.id.tvLearntTranslation);
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mediaPlayer.isPlaying()){
            stopPlay();
        }
    }
}
