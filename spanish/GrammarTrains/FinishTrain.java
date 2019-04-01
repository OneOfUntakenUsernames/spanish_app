package com.example.user.spanish.GrammarTrains;

import android.app.Fragment;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.spanish.Objects.InfoGrammarObject;
import com.example.user.spanish.Objects.QuestionObject;
import com.example.user.spanish.R;
import com.example.user.spanish.Trains.FragmentFinishWordsTrain;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;

import java.sql.DatabaseMetaData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FinishTrain extends Fragment implements View.OnClickListener {

    TextToSpeech textToSpeech;
    int result;
    String IS_LEARNT = "isLearnt";
    String QUESTION_OBJECT = "questionObject";
    String PHRASES_FOR_LEARNING = "phrases";
    String QUERY = "query";
    String INFO = "info";
    String EXP = "exp";
    Integer exp;
    int points;
    boolean[] isLearnt = new boolean[4];
    boolean exist = false;
    TextView tvExp;


    ProgressBar trainProgress;
    QuestionObject questionObject;
    String query;
    InfoGrammarObject infoGrammarObject;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grammar_finish, container, false);

        RecyclerView rvLearntWords = (RecyclerView) view.findViewById(R.id.rvLearntWordsFinish);
        Button btnFinish = (Button) view.findViewById(R.id.btnOkFinish);
        btnFinish.setOnClickListener(this);
        tvExp = (TextView) view.findViewById(R.id.tvExpTrains);

        trainProgress = (ProgressBar) getActivity().findViewById(R.id.pBarTrain);
        trainProgress.setVisibility(View.GONE);

        exp = getArguments().getInt(EXP);
        points = setLevel(exp);
        tvExp.setText("Вы заработали " + exp + " ед. опыта!");

        questionObject = getArguments().getParcelable(QUESTION_OBJECT);
        ArrayList<Integer> pos = getArguments().getIntegerArrayList(PHRASES_FOR_LEARNING);
        ArrayList<String> learntWords = new ArrayList<>();
        ArrayList<String> learntTranslations = new ArrayList<>();

        infoGrammarObject = getArguments().getParcelable(INFO);
        isLearnt = getArguments().getBooleanArray(IS_LEARNT);
        query = getArguments().getString(QUERY);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("grammar").child("exercises").child(query).child("questions").child("isLearnt");

        isLearnt = getArguments().getBooleanArray(IS_LEARNT);

        for(int i = 0; i < pos.size(); i++){
            reference.child(pos.get(i).toString()).setValue(isLearnt[i]);
            learntWords.add(questionObject.getPhrases().get(pos.get(i)));
            learntTranslations.add(questionObject.getTranslations().get(pos.get(i)));
        }


        rvLearntWords.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvLearntWords.setLayoutManager(layoutManager);
        rvLearntWords.setAdapter(new LearntWordsAdapter(learntWords, learntTranslations));

        uploadInfo();

        return view;
    }



    private void uploadInfo(){
        int counter = 0;
        for(int i = 0; i < isLearnt.length; i++){
            if(isLearnt[i]){
                counter++;
            }
        }

        float progress = ((float) counter/ (float) isLearnt.length)*100;
        int progressInt = (int) progress;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("grammar").child("info").child(query).child("progress").setValue(progressInt);

        if(progressInt == 100) {
            double days = infoGrammarObject.getDaysCount();

            if(days < 120) {
                if (days <= 3) {
                    days += 3;
                } else {
                    days = days * (1.5);
                }
            }else {
                days+=3;
            }

            int daysInt = (int) days;


            GregorianCalendar calendar = new GregorianCalendar();
            calendar.add(Calendar.DAY_OF_MONTH, daysInt);
            int s1 = calendar.get(Calendar.DAY_OF_MONTH);
            int s2 = calendar.get(Calendar.MONTH);
            int s3 = calendar.get(Calendar.YEAR);

            String date = s1 + "." + (s2 + 1) + "." + s3;

            ref.child("grammar").child("info").child(query).child("date").setValue(date);
            ref.child("grammar").child("info").child(query).child("daysCount").setValue(daysInt);
            ref.child("grammar").child("info").child(query).child("isForgotten").setValue(false);
        }

    }

    private void Speech(final String sentence){
        textToSpeech = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    Locale spanish = new Locale("es", "ES");
                    result = textToSpeech.setLanguage(spanish);
                    if (    result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
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

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnOkFinish){
            getActivity().finish();
            if (!exist) {
                points += exp;
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                final DatabaseReference mReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
                mReference.child("userInfo").child("points").setValue(points);
                exist = true;
            }
        }
    }


    public class LearntWordsAdapter extends RecyclerView.Adapter<LearntWordsAdapter.ViewHolder>{

        private ArrayList<String> LearntWords;
        private ArrayList<String> LearntTranslations;

        LearntWordsAdapter(ArrayList<String> learntWords, ArrayList<String> learntTranslations){
            LearntWords = learntWords;
            LearntTranslations = learntTranslations;
        }

        @Override
        public LearntWordsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_dictionary_word, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final LearntWordsAdapter.ViewHolder holder, int position) {
            holder.mWords.setText(LearntWords.get(position));
            holder.mTranslation.setText(LearntTranslations.get(position));
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
            return LearntWords.size();
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