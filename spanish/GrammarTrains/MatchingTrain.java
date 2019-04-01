package com.example.user.spanish.GrammarTrains;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Locale;
import java.util.Random;

public class MatchingTrain extends Fragment implements View.OnClickListener {

    Button answ1, answ2, answ3, answ4;
    int number;
    TextView tvPhrase;
    ArrayList<String> usedPhr = new ArrayList<>();
    String answer;
    QuestionObject questionObject;
    ArrayList<Integer> PhrasesForLearning = new ArrayList<>();
    boolean[] isLearnt = new boolean[]{true, true, true, true};
    int position = 0;

    String IS_LEARNT = "isLearnt";
    String QUESTION_OBJECT = "questionObject";
    String PHRASES_FOR_LEARNING = "phrases";
    String QUERY = "query";
    String INFO = "info";
    String EXP = "exp";
    ProgressBar trainProgress;
    Button btnNext;
    ImageButton ibSound;
    TextToSpeech textToSpeech;
    int result;
    Integer exp = 4;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grammar_matching, container, false);

        number = 0;

        trainProgress = (ProgressBar) getActivity().findViewById(R.id.pBarTrain);

        tvPhrase = (TextView) view.findViewById(R.id.tvPhrase);

        answ1 = (Button) view.findViewById(R.id.btnAnswer1);
        answ2 = (Button) view.findViewById(R.id.btnAnswer2);
        answ3 = (Button) view.findViewById(R.id.btnAnswer3);
        answ4 = (Button) view.findViewById(R.id.btnAnswer4);
        answ1.setOnClickListener(this);
        answ2.setOnClickListener(this);
        answ3.setOnClickListener(this);
        answ4.setOnClickListener(this);
        btnNext = (Button) view.findViewById(R.id.btnNextMatching);
        btnNext.setOnClickListener(this);
        ibSound = (ImageButton) view.findViewById(R.id.ibSoundMatching);
        ibSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Speech();
            }
        });

        questionObject = getArguments().getParcelable(QUESTION_OBJECT);
        PhrasesForLearning = getArguments().getIntegerArrayList(PHRASES_FOR_LEARNING);
        loadExercise();


        return view;
    }


    public void loadExercise() {

        Random random = new Random();

        //назначаем каждый вариант ответа
        int rnd;
        rnd = random.nextInt(PhrasesForLearning.size());

        Boolean uniquePhr = false;

        while (!uniquePhr) {
            rnd = random.nextInt(PhrasesForLearning.size());
            uniquePhr = !usedPhr.contains(questionObject.getPhrases().get(PhrasesForLearning.get(rnd)));
        }

        usedPhr.add(questionObject.getPhrases().get(PhrasesForLearning.get(rnd)));

        tvPhrase.setText(questionObject.getPhrases().get(PhrasesForLearning.get(rnd))); //вопрос
        answer = questionObject.getTranslations().get(PhrasesForLearning.get(rnd)); //ответ

        ArrayList<Integer> answers = new ArrayList<>();
        answers.add(1);
        answers.add(2);
        answers.add(3);
        answers.add(4);

        rnd = random.nextInt(answers.size());
        position = rnd;
        int num = answers.get(rnd);
        answers.remove(rnd);

        switch (num) {
            case 1:
                answ1.setText(answer);
                break;
            case 2:
                answ2.setText(answer);
                break;
            case 3:
                answ3.setText(answer);
                break;
            case 4:
                answ4.setText(answer);
                break;
        }

        ArrayList<String> usedTr = new ArrayList<>();
        usedTr.add(answer);

        while (answers.size() > 0) {

            int number = answers.get(0);
            answers.remove(0);

            int rand = 0;

            Boolean unique = false;

            while (!unique) {
                rand = random.nextInt(PhrasesForLearning.size());
                unique = !usedTr.contains(questionObject.getTranslations().get(PhrasesForLearning.get(rand)));
            }

            usedTr.add(questionObject.getTranslations().get(PhrasesForLearning.get(rand)));

            switch (number) {
                case 1:
                    answ1.setText(questionObject.getTranslations().get(PhrasesForLearning.get(rand)));
                    break;
                case 2:
                    answ2.setText(questionObject.getTranslations().get(PhrasesForLearning.get(rand)));
                    break;
                case 3:
                    answ3.setText(questionObject.getTranslations().get(PhrasesForLearning.get(rand)));
                    break;
                case 4:
                    answ4.setText(questionObject.getTranslations().get(PhrasesForLearning.get(rand)));
                    break;
            }

        }
    }

    @Override
    public void onClick(View view) {
        Button btn = (Button) view.findViewById(view.getId());
        btn.setOnClickListener(this);

        if (btn.getId() == R.id.btnNextMatching) {

            btnNext.setVisibility(View.GONE);

            if (number < 3) {
                number++;
                answ1.setTextColor(getResources().getColor(R.color.colorAccent));
                answ2.setTextColor(getResources().getColor(R.color.colorAccent));
                answ3.setTextColor(getResources().getColor(R.color.colorAccent));
                answ4.setTextColor(getResources().getColor(R.color.colorAccent));
                btnNext.setVisibility(View.GONE);
                loadExercise();
            } else {
                SpeakingTrain speakingTrain = new SpeakingTrain();
                Bundle args = new Bundle();

                args.putBooleanArray(IS_LEARNT, isLearnt);
                args.putParcelable(QUESTION_OBJECT, questionObject);
                args.putIntegerArrayList(PHRASES_FOR_LEARNING, PhrasesForLearning);
                args.putString(QUERY, getArguments().getString(QUERY));
                args.putParcelable(INFO, getArguments().getParcelable(INFO));
                args.putInt(EXP, exp);

                speakingTrain.setArguments(args);
                FragmentTransaction fTrans = getFragmentManager().beginTransaction();
                fTrans.replace(R.id.content_grammar_trains, speakingTrain).commit();
            }

            trainProgress.setProgress(trainProgress.getProgress() + 1);
        } else {

            if (btn.getText() == answer) {
                btn.setTextColor(getResources().getColor(R.color.darkGreen));
                exp++;
            } else {
                btn.setTextColor(getResources().getColor(R.color.darkRed));
                isLearnt[position] = false;

                if (answ1.getText() == answer) {
                    answ1.setTextColor(getResources().getColor(R.color.darkGreen));
                }
                if (answ2.getText() == answer) {
                    answ2.setTextColor(getResources().getColor(R.color.darkGreen));
                }
                if (answ3.getText() == answer) {
                    answ3.setTextColor(getResources().getColor(R.color.darkGreen));
                }
                if (answ4.getText() == answer) {
                    answ4.setTextColor(getResources().getColor(R.color.darkGreen));
                }

            }

            btnNext.setVisibility(View.VISIBLE);

        }

    }

    private void Speech() {
        textToSpeech = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    Locale spanish = new Locale("es", "ES");
                    result = textToSpeech.setLanguage(spanish);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(getActivity(), "Feature not supported in your device", Toast.LENGTH_SHORT).show();
                    } else {
                        textToSpeech.speak(tvPhrase.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                    }
                } else {
                    Toast.makeText(getActivity(), "Feature not supported on your device", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}