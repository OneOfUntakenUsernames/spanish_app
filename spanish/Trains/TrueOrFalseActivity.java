package com.example.user.spanish.Trains;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

public class TrueOrFalseActivity extends AppCompatActivity implements View.OnClickListener {

    final String wordsStr = "WORDS";
    final String translationsStr = "TRANSLATIONS";
    final String keyStr = "KEY";
    final String isLearntStr = "LEARNT";
    int num;
    ArrayList<String> words = new ArrayList<>();
    ArrayList<String> translations = new ArrayList<>();
    ArrayList<String> key = new ArrayList<>();
    boolean[] isLearnt;
    int next;
    TextToSpeech textToSpeech;
    int result;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    LinearLayout main, container, answer;
    ProgressBar progress;
    TextView tvQuestion, tvWord, tvTranslation, tvMessage, tvCorrect;
    ImageButton ibSound;
    Button btnTrue, btnFalse, btnNext;
    ImageView ivAnswer;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_true_or_falase_train);
        main = (LinearLayout) findViewById(R.id.main_layout_tf);
        container = (LinearLayout) findViewById(R.id.container_tf);
        answer = (LinearLayout) findViewById(R.id.layoutAnswerPanelTF);
        progress = (ProgressBar) findViewById(R.id.pBarTF);
        tvQuestion = (TextView) findViewById(R.id.tvQuestionTF);
        tvWord = (TextView) findViewById(R.id.tvWordTF);
        tvTranslation = (TextView) findViewById(R.id.tvTranslationTF);
        tvMessage = (TextView) findViewById(R.id.tvMessageTF);
        tvCorrect = (TextView) findViewById(R.id.tvCorrectTF);
        ibSound = (ImageButton) findViewById(R.id.ibSoundTF);
        btnTrue = (Button) findViewById(R.id.btnTrueTF);
        btnFalse = (Button) findViewById(R.id.btnFalseTF);
        btnNext = (Button) findViewById(R.id.btnNextTF);
        ivAnswer = (ImageView) findViewById(R.id.ivAnswerTF);

        btnTrue.setOnClickListener(this);
        btnFalse.setOnClickListener(this);
        btnNext.setOnClickListener(this);

        Intent intent = getIntent();

        words = intent.getStringArrayListExtra(wordsStr);
        translations = intent.getStringArrayListExtra(translationsStr);
        key = intent.getStringArrayListExtra(keyStr);

        if (words.size() < 6) {
            next = words.size()-1;
        } else {
            next = 5;
        }

        isLearnt = new boolean[next+1];

        for(int i = 0; i <= next; i++){

            isLearnt[i] = false;
        }

        progress.setMax(next+1);
        progress.setProgress(0);
        num = next;
        setPhrase();

    }


    private void setPhrase(){
        if (next >= 0) {
            tvWord.setText(words.get(next));
            Random random = new Random();
            int wrong = next;
            while (wrong == next){
                wrong = random.nextInt(num) + 1;
            }
            int answer = random.nextInt(2);
            if(answer == 0){
                tvTranslation.setText(translations.get(next));
            }else {
                tvTranslation.setText(translations.get(wrong));
            }

        } else {
            btnTrue.setEnabled(false);
            btnFalse.setEnabled(false);
            finishTrain();

            FragmentFinishWordsTrain finishWordsTrain = new FragmentFinishWordsTrain();
            Bundle args = new Bundle();
            args.putStringArrayList(wordsStr, words);
            args.putStringArrayList(translationsStr, translations);
            args.putBooleanArray(isLearntStr, isLearnt);
            finishWordsTrain.setArguments(args);
            FragmentTransaction fTrans = getFragmentManager().beginTransaction();
            fTrans.replace(R.id.main_layout_tf, finishWordsTrain).commit();
            container.setVisibility(View.GONE);
        }
    }


    private void finishTrain() {
        for (int i = num; i >= 0; i--) {
            if(isLearnt[i]) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                reference.child("users").child(user.getUid()).child("dictionary")
                        .child(key.get(i)).child("isLearntMT").setValue(true);
            }
        }
    }


    private void Speech(final String sentence) {
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    Locale spanish = new Locale("es", "ES");
                    result = textToSpeech.setLanguage(spanish);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnTrueTF:
                progress.setProgress(progress.getProgress() + 1);
                answer.setVisibility(View.VISIBLE);
                if(tvTranslation.getText().toString().equals(translations.get(next))){
                    tvMessage.setText(R.string.CorrectAnswer);
                    tvCorrect.setText("");
                    isLearnt[next] = true;
                    answer.setBackgroundColor(getResources().getColor(R.color.correct));
                    ivAnswer.setBackground(getResources().getDrawable(R.drawable.ic_correct));
                } else {
                    tvMessage.setText(R.string.WrongAnswer);
                    tvCorrect.setText(translations.get(next));
                    answer.setBackgroundColor(getResources().getColor(R.color.wrong));
                    ivAnswer.setBackground(getResources().getDrawable(R.drawable.ic_wrong));
                }
                break;
            case R.id.btnFalseTF:
                progress.setProgress(progress.getProgress() + 1);
                answer.setVisibility(View.VISIBLE);
                if(!tvTranslation.getText().toString().equals(translations.get(next))){
                    tvMessage.setText(R.string.CorrectAnswer);
                    tvCorrect.setText(translations.get(next));
                    isLearnt[next] = true;
                    answer.setBackgroundColor(getResources().getColor(R.color.correct));
                    ivAnswer.setBackground(getResources().getDrawable(R.drawable.ic_correct));
                } else {
                    tvMessage.setText(R.string.WrongAnswer);
                    tvCorrect.setText("");
                    answer.setBackgroundColor(getResources().getColor(R.color.wrong));
                    ivAnswer.setBackground(getResources().getDrawable(R.drawable.ic_wrong));
                }
                break;
            case R.id.btnNextTF:
                next--;
                btnTrue.setEnabled(true);
                btnFalse.setEnabled(true);
                setPhrase();
                answer.setVisibility(View.GONE);
                break;
            case R.id.ibSoundTF:
                Speech(tvWord.getText().toString());
                break;
        }
    }
}
