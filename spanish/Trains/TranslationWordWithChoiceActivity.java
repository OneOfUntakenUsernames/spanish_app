package com.example.user.spanish.Trains;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.spanish.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class TranslationWordWithChoiceActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Button answ1, answ2, answ3, answ4;
    TextView tvPhrase;
    int num;
    ArrayList<String> words = new ArrayList<>();
    ArrayList<String> translations = new ArrayList<>();
    ArrayList<String> key = new ArrayList<>();
    boolean[] isLearnt;
    int next;
    String answer;


    final String wordsStr = "WORDS";
    final String translationsStr = "TRANSLATIONS";
    final String keyStr = "KEY";
    final String isLearntStr = "LEARNT";
    ProgressBar trainProgress;
    Button btnNext;
    ImageButton ibSound;
    TextToSpeech textToSpeech;
    int result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_grammar_matching);

        trainProgress = (ProgressBar) findViewById(R.id.pBarTrain);

        tvPhrase = (TextView) findViewById(R.id.tvPhrase);

        answ1 = (Button) findViewById(R.id.btnAnswer1);
        answ2 = (Button) findViewById(R.id.btnAnswer2);
        answ3 = (Button) findViewById(R.id.btnAnswer3);
        answ4 = (Button) findViewById(R.id.btnAnswer4);
        answ1.setOnClickListener(this);
        answ2.setOnClickListener(this);
        answ3.setOnClickListener(this);
        answ4.setOnClickListener(this);
        btnNext = (Button) findViewById(R.id.btnNextMatching);
        btnNext.setOnClickListener(this);
        ibSound = (ImageButton) findViewById(R.id.ibSoundMatching);
        ibSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Speech();
            }
        });


        Intent intent = getIntent();

        words = intent.getStringArrayListExtra(wordsStr);
        translations = intent.getStringArrayListExtra(translationsStr);
        key = intent.getStringArrayListExtra(keyStr);

        if (words.size() < 6) {
            next = words.size() - 1;
        } else {
            next = 5;
        }

        isLearnt = new boolean[next + 1];

        for (int i = 0; i <= next; i++) {

            isLearnt[i] = false;
        }

        num = next;

        loadExercise();
    }

    public void loadExercise() {

        tvPhrase.setText(words.get(next));
        answer = translations.get(next);

        Random random = new Random();

        //назначаем каждый вариант ответа

        int rnd;

        rnd = random.nextInt(4) + 1;

        switch (rnd) {
            case 1:
                answ1.setText(translations.get(next));
                break;
            case 2:
                answ2.setText(translations.get(next));
                break;
            case 3:
                answ3.setText(translations.get(next));
                break;
            case 4:
                answ4.setText(translations.get(next));
                break;
        }

        int x = next - 1;

        ArrayList<Integer> used = new ArrayList<>();
        used.add(next);


        if (answ1.getText().toString().equals("")) {
            for(int i = 0; i < words.size(); i++){
                if(!used.contains(i)){
                    answ1.setText(translations.get(i));
                    used.add(i);
                    break;
                }


            }
        }
        if (answ2.getText().toString().equals("")) {
            for(int i = 0; i < words.size(); i++){
                if(!used.contains(i)){
                    answ2.setText(translations.get(i));
                    used.add(i);
                    break;
                }
            }
        }
        if (answ3.getText().toString().equals("")) {
            for(int i = 0; i < words.size(); i++){
                if(!used.contains(i)){
                    answ3.setText(translations.get(i));
                    used.add(i);
                    break;
                }
            }
        }
        if (answ4.getText().toString().equals("")) {
            for(int i = 0; i < words.size(); i++){
                if(!used.contains(i)){
                    answ4.setText(translations.get(i));
                    used.add(i);
                    break;
                }
            }
        }

    }


    private void checkCorrect(){
        if(answ1.getText().toString().equals(answer)){
            answ1.setTextColor(getResources().getColor(R.color.darkGreen));
        }
        if(answ2.getText().toString().equals(answer)){
            answ2.setTextColor(getResources().getColor(R.color.darkGreen));
        }
        if(answ3.getText().toString().equals(answer)){
            answ3.setTextColor(getResources().getColor(R.color.darkGreen));
        }
        if(answ3.getText().toString().equals(answer)){
            answ3.setTextColor(getResources().getColor(R.color.darkGreen));
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btnAnswer1:
                if (answ1.getText().toString().equals(answer)) {
                    answ1.setTextColor(getResources().getColor(R.color.darkGreen));
                    isLearnt[next] = true;
                }else {
                    answ1.setTextColor(getResources().getColor(R.color.darkRed));
                    checkCorrect();
                }
                btnNext.setVisibility(View.VISIBLE);
                break;

            case R.id.btnAnswer2:
                if (answ2.getText().toString().equals(answer)) {
                    answ2.setTextColor(getResources().getColor(R.color.darkGreen));
                    isLearnt[next] = true;
                }else {
                    answ2.setTextColor(getResources().getColor(R.color.darkRed));
                    checkCorrect();
                }
                btnNext.setVisibility(View.VISIBLE);
                break;

            case R.id.btnAnswer3:
                if (answ3.getText().toString().equals(answer)) {
                    answ3.setTextColor(getResources().getColor(R.color.darkGreen));
                    isLearnt[next] = true;
                }else {
                    answ3.setTextColor(getResources().getColor(R.color.darkRed));
                    checkCorrect();
                }
                btnNext.setVisibility(View.VISIBLE);
                break;
            case R.id.btnAnswer4:
                if (answ4.getText().toString().equals(answer)) {
                    answ4.setTextColor(getResources().getColor(R.color.darkGreen));
                    isLearnt[next] = true;
                }else {
                    answ4.setTextColor(getResources().getColor(R.color.darkRed));
                    checkCorrect();
                }
                btnNext.setVisibility(View.VISIBLE);
                break;

            case R.id.btnNextMatching:
                btnNext.setVisibility(View.GONE);

                if (next > 0) {
                    next--;
                    answ1.setTextColor(getResources().getColor(R.color.colorAccent));
                    answ2.setTextColor(getResources().getColor(R.color.colorAccent));
                    answ3.setTextColor(getResources().getColor(R.color.colorAccent));
                    answ4.setTextColor(getResources().getColor(R.color.colorAccent));
                    answ1.setText("");
                    answ2.setText("");
                    answ3.setText("");
                    answ4.setText("");
                    loadExercise();
                } else {
                    finishTrain();
                    FragmentFinishWordsTrain finishWordsTrain = new FragmentFinishWordsTrain();
                    Bundle args = new Bundle();
                    args.putStringArrayList(wordsStr, words);
                    args.putStringArrayList(translationsStr, translations);
                    args.putBooleanArray(isLearntStr, isLearnt);
                    finishWordsTrain.setArguments(args);
                    FragmentTransaction fTrans = getFragmentManager().beginTransaction();
                    fTrans.replace(R.id.fragment_grammar_trains, finishWordsTrain).commit();
                    LinearLayout layout = (LinearLayout) findViewById(R.id.main_layout_matching);
                    layout.setVisibility(View.GONE);
                }
                break;
        }

    }



    private void finishTrain() {
        for (int i = num; i >= 0; i--) {
            if (isLearnt[i]) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                reference.child("users").child(user.getUid()).child("dictionary")
                        .child(key.get(i)).child("isLearntCT").setValue(true);
            }
        }
    }

    private void Speech() {
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    Locale spanish = new Locale("es", "ES");
                    result = textToSpeech.setLanguage(spanish);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(getApplicationContext(), "Feature not supported in your device", Toast.LENGTH_SHORT).show();
                    } else {
                        textToSpeech.speak(tvPhrase.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Feature not supported on your device", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}