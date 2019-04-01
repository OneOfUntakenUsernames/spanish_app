package com.example.user.spanish.Trains;

import android.app.Fragment;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.spanish.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Locale;

public class FragmentFinishWordsTrain extends Fragment implements View.OnClickListener {

    final String wordsStr = "WORDS";
    final String translationsStr = "TRANSLATIONS";
    final String isLearntStr = "LEARNT";

    Button btnFinish;
    TextView tvPhrase, tvExp;
    ArrayList<String> words = new ArrayList<>();
    ArrayList<String> translations = new ArrayList<>();
    boolean[] isLearnt;
    TextToSpeech textToSpeech;
    int result;
    boolean exist = false;
    int points = 0, n = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grammar_finish, container, false);

        btnFinish = (Button) view.findViewById(R.id.btnOkFinish);
        btnFinish.setOnClickListener(this);
        tvPhrase = (TextView) view.findViewById(R.id.tvPhraseFinish);
        tvPhrase.setGravity(Gravity.CENTER);
        tvExp = (TextView) view.findViewById(R.id.tvExpTrains);

        words = getArguments().getStringArrayList(wordsStr);
        translations = getArguments().getStringArrayList(translationsStr);
        isLearnt = getArguments().getBooleanArray(isLearntStr);

        n = 0;

        if (isLearnt != null) {
            for (boolean learnt : isLearnt) {
                if (learnt) {
                    n++;
                }
            }
        }


        points = setLevel(n);


        tvPhrase.setText("Изучено слов: " + n + "/" + words.size());
        tvExp.setText("Вы заработали " + n + " ед. опыта!");

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rvLearntWordsFinish);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new LearntWordsAdapter(words, translations));

        return view;
    }


    private int setLevel(final int exp) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference mReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

        final int[] points = {0};

        final Query query = mReference.child("userInfo");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                GenericTypeIndicator<Integer> genericTypeIndicator = new GenericTypeIndicator<Integer>() {
                };
                if (!exist) {
                    points[0] = dataSnapshot.getValue(genericTypeIndicator);
                    points[0] += exp;
                    mReference.child("userInfo").child("points").setValue(points[0]);
                    exist = true;
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return points[0];
    }


    private void Speech(final String sentence) {
        textToSpeech = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    Locale spanish = new Locale("es", "ES");
                    result = textToSpeech.setLanguage(spanish);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(getActivity(), "Feature not supported in your device", Toast.LENGTH_SHORT).show();
                    } else {
                        textToSpeech.speak(sentence, TextToSpeech.QUEUE_FLUSH, null);
                    }
                } else {
                    Toast.makeText(getActivity(), "Feature not supported on your device", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnOkFinish) {
            getActivity().finish();
            if (!exist) {
                points += n;
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                final DatabaseReference mReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
                mReference.child("userInfo").child("points").setValue(points);
                exist = true;
            }
        }
    }

    public class LearntWordsAdapter extends RecyclerView.Adapter<LearntWordsAdapter.ViewHolder> {

        private ArrayList<String> Words;
        private ArrayList<String> Translations;

        LearntWordsAdapter(ArrayList<String> words, ArrayList<String> translations) {
            Words = words;
            Translations = translations;
        }

        @Override
        public LearntWordsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_dictionary_word, parent, false);
            return new LearntWordsAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final LearntWordsAdapter.ViewHolder holder, int position) {
            holder.mWords.setText(Words.get(position));
            holder.mTranslation.setText(Translations.get(position));
            if (isLearnt[position]) {
                holder.btnDelete.setBackground(getResources().getDrawable(R.drawable.ic_correct));
            } else {
                holder.btnDelete.setBackground(getResources().getDrawable(R.drawable.ic_wrong));
            }


            holder.btnSpeak.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Speech(holder.mWords.getText().toString());
                }
            });
        }

        @Override
        public int getItemCount() {
            return Words.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView mWords, mTranslation;
            Button btnDelete, btnSpeak;

            public ViewHolder(View itemView) {
                super(itemView);

                mWords = (TextView) itemView.findViewById(R.id.tvWord);
                mTranslation = (TextView) itemView.findViewById(R.id.tvLearntTranslation);
                btnDelete = (Button) itemView.findViewById(R.id.btnDeleteWord);
                btnSpeak = (Button) itemView.findViewById(R.id.btnPlayWord);
            }
        }
    }

}