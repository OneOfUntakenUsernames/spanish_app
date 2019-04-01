package com.example.user.spanish.GrammarTrains;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ProgressBar;

import com.example.user.spanish.Objects.InfoGrammarObject;
import com.example.user.spanish.Objects.QuestionObject;
import com.example.user.spanish.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Random;

public class GrammarTrainsActivity extends AppCompatActivity {

    String QUERY = "query";
    String INFO = "info";
    String PHRASES_FOR_LEARNING = "phrases";
    String QUESTION_OBJECT = "questionObject";

    ProgressBar trainProgress;
    QuestionObject questionObject;
    ArrayList<Integer> PhrasesForLearning = new ArrayList<>();
    StartTrain startTrain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grammar_trains);

        trainProgress = (ProgressBar) findViewById(R.id.pBarTrain);
        trainProgress.setProgressDrawable(getResources().getDrawable(R.drawable.horizontalprogressbar));
        trainProgress.setProgress(0);

        Intent intent = getIntent();
        InfoGrammarObject infoGrammarObject = intent.getParcelableExtra(INFO);
        final String queryString = intent.getStringExtra("query");

        startTrain = new StartTrain();
        final Bundle args = new Bundle();
        args.putString(QUERY, queryString);
        args.putParcelable(INFO, infoGrammarObject);
        startTrain.setArguments(args);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        final Query query = reference.child("grammar").child("exercises").child(queryString);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                GenericTypeIndicator<QuestionObject> genericTypeIndicator = new GenericTypeIndicator<QuestionObject>() {
                };
                questionObject = dataSnapshot.getValue(genericTypeIndicator);
                ChooseUnlearnt();
                args.putParcelable(QUESTION_OBJECT, questionObject);
                args.putIntegerArrayList(PHRASES_FOR_LEARNING, PhrasesForLearning);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.content_grammar_trains, startTrain).commit();
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

    }


    private void ChooseUnlearnt() {
        ArrayList<Boolean> isLearnt = questionObject.getIsLearnt();
        for (int i = 0; i < isLearnt.size(); i++) { //добавление неизученных фраз
            if (PhrasesForLearning.size() == 4) {
                break;
            }
            if (!isLearnt.get(i)) {
                PhrasesForLearning.add(i);
            }
        }

        //добавление изученных фраз при недостатке неизученных
        ArrayList<Integer> chosen = new ArrayList<>();
        while (PhrasesForLearning.size() < 4) {
            Random random = new Random();
            int num;
            do {
                num = random.nextInt(isLearnt.size());
            } while (!isLearnt.get(num) || chosen.contains(num));
            chosen.add(num);
            PhrasesForLearning.add(num);
        }
    }

}