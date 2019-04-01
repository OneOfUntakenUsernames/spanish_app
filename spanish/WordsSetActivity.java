package com.example.user.spanish;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.spanish.Objects.WordObject;
import com.example.user.spanish.Objects.WordsSetObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;
import java.util.PropertyPermission;

public class WordsSetActivity extends AppCompatActivity {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("dictionary");
    ArrayList<String> Words;
    ArrayList<String> Translations;
    TextToSpeech textToSpeech;
    int result;
    String sentence;
    Boolean added;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words_set);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        final WordsSetObject wordsSetObject = intent.getParcelableExtra("object");

        ImageView iv = (ImageView) findViewById(R.id.ivPicture);
        Picasso.get().load(wordsSetObject.getImage()).into(iv);

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.toolbarTexColor));
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.toolbarTexColor));
        collapsingToolbarLayout.setTitle(wordsSetObject.getName());

        added = false;

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (added) {
                    Toast.makeText(getApplicationContext(), "Этот набор слов уже добавлен", Toast.LENGTH_SHORT).show();
                } else {
                    if(checkConnection()) {
                        uploadSet();
                    }
                    added = true;
                }

            }
        });

        Words = wordsSetObject.getWords();
        Translations = wordsSetObject.getTranslations();


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvWordsFromSet);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new WordsSetAdapter(Words, Translations));

    }

    private boolean checkConnection(){
        Methods methods = new Methods();
        if(!methods.isConnected(getApplicationContext())){
            Toast.makeText(getApplicationContext(), "Проверьте подключение к Интернету и повторите попытку", Toast.LENGTH_SHORT).show();
            return false;
        }else {
            return true;
        }
    }

    public void tts(){
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    Locale spanish = new Locale("es", "ES");
                    result = textToSpeech.setLanguage(spanish);
                    if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Toast.makeText(getApplicationContext(), "Feature not supported in your device", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        textToSpeech.speak(sentence, TextToSpeech.QUEUE_FLUSH, null);
                    }
                }else {
                    Toast.makeText(getApplicationContext(), "Feature not supported on your device", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void uploadSet() {
        for (int i = 0; i < Words.size(); i++) {
            WordObject wordObject = new WordObject(Words.get(i), Translations.get(i),
                    false, false, false, false);
            reference.push().setValue(wordObject);
            fab.setBackground(getResources().getDrawable(R.drawable.ic_done));
        }
    }


    public class WordsSetAdapter extends RecyclerView.Adapter<WordsSetAdapter.ViewHolder>{

        WordsSetAdapter(ArrayList<String> words, ArrayList<String> translations){
            Translations = translations;
            Words = words;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_dictionary_word, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final WordsSetAdapter.ViewHolder holder, int position) {
            holder.mWords.setText(Words.get(holder.getAdapterPosition()));
            holder.mTranslation.setText(Translations.get(position));

            holder.btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(((BitmapDrawable) holder.btnAdd.getBackground()).getBitmap() ==
                            ((BitmapDrawable) getResources().getDrawable(R.drawable.ic_done)).getBitmap()){
                        Toast.makeText(getApplicationContext(), "Слово уже в вашем словаре", Toast.LENGTH_SHORT).show();
                    }else {
                        if(checkConnection()) {
                            WordObject wordObject = new WordObject(holder.mWords.getText().toString(),
                                    holder.mTranslation.getText().toString(),
                                    false, false, false, false);
                            reference.push().setValue(wordObject);
                            holder.btnAdd.setBackground(getResources().getDrawable(R.drawable.ic_done));
                        }
                    }
                }
            });

            holder.btnSpeak.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sentence = holder.mWords.getText().toString();
                    tts();
                }
            });
        }

        @Override
        public int getItemCount() {
            return Words.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView mWords, mTranslation;
            Button btnAdd, btnSpeak;

            ViewHolder(final View itemView) {
                super(itemView);
                mWords = (TextView) itemView.findViewById(R.id.tvWord);
                mTranslation = (TextView) itemView.findViewById(R.id.tvLearntTranslation);
                btnAdd = (Button) itemView.findViewById(R.id.btnDeleteWord);
                btnAdd.setBackground(getResources().getDrawable(android.R.drawable.ic_input_add));
                btnSpeak = (Button) itemView.findViewById(R.id.btnPlayWord);
            }
        }
    }

}
