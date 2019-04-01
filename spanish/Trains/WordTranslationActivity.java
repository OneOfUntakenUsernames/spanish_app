package com.example.user.spanish.Trains;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class WordTranslationActivity extends AppCompatActivity implements View.OnClickListener {

    final String wordsStr = "WORDS";
    final String translationsStr = "TRANSLATIONS";
    final String keyStr = "KEY";
    final String isLearntStr = "LEARNT";
    int num;
    ArrayList<String> words = new ArrayList<>();
    ArrayList<String> translations = new ArrayList<>();
    ArrayList<String> key = new ArrayList<>();
    boolean[] isLearnt;
    TextView tvWord, tvMessage, tvCorrect;
    EditText etAnswer;
    ImageView ivAnswer;
    Button btnNext, btnCheck;
    ImageButton ibSound;
    ProgressBar progressBar;
    LinearLayout layout;
    int next;
    TextToSpeech textToSpeech;
    int result;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Button btnA, btnE, btnO, btnU, btnU2, btnN, btnI;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_translation);
        tvWord = (TextView) findViewById(R.id.tvWordWT);
        tvCorrect = (TextView) findViewById(R.id.tvCorrectWT);
        tvMessage = (TextView) findViewById(R.id.tvMessageWT);
        etAnswer = (EditText) findViewById(R.id.etAnswerWT);
        btnNext = (Button) findViewById(R.id.btnNextWT);
        btnCheck = (Button) findViewById(R.id.btnCheckWT);
        ivAnswer = (ImageView) findViewById(R.id.ivAnswerWT);
        ibSound = (ImageButton) findViewById(R.id.ibSoundWT);
        btnNext.setOnClickListener(this);
        btnCheck.setOnClickListener(this);
        ibSound.setOnClickListener(this);
        layout = (LinearLayout) findViewById(R.id.layoutAnswerPanel);
        progressBar = (ProgressBar) findViewById(R.id.pBarWordsTrain);
        progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.horizontalprogressbar));

        btnA = (Button) findViewById(R.id.btnA);
        btnI = (Button) findViewById(R.id.btnI);
        btnU = (Button) findViewById(R.id.btnU);
        btnU2 = (Button) findViewById(R.id.btnU2);
        btnE = (Button) findViewById(R.id.btnE);
        btnN = (Button) findViewById(R.id.btnN);
        btnO = (Button) findViewById(R.id.btnO);
        btnA.setOnClickListener(this);
        btnI.setOnClickListener(this);
        btnU.setOnClickListener(this);
        btnU2.setOnClickListener(this);
        btnE.setOnClickListener(this);
        btnN.setOnClickListener(this);
        btnO.setOnClickListener(this);

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

        progressBar.setMax(next+1);
        progressBar.setProgress(0);
        tvWord.setText(words.get(next));
        num = next;

    }


    private void setPhrase() {
        if (next >= 0) {
            tvWord.setText(words.get(next));
        } else {
            btnCheck.setVisibility(View.GONE);
            finishTrain();

            FragmentFinishWordsTrain finishWordsTrain = new FragmentFinishWordsTrain();
            Bundle args = new Bundle();
            args.putStringArrayList(wordsStr, words);
            args.putStringArrayList(translationsStr, translations);
            args.putBooleanArray(isLearntStr, isLearnt);
            finishWordsTrain.setArguments(args);
            FragmentTransaction fTrans = getFragmentManager().beginTransaction();
            fTrans.replace(R.id.main_layout, finishWordsTrain).commit();
            LinearLayout layout = (LinearLayout) findViewById(R.id.container_word_trains);
            layout.setVisibility(View.GONE);
        }
    }


    private void finishTrain() {
        for (int i = num; i >= 0; i--) {
            if(isLearnt[i]) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                reference.child("users").child(user.getUid()).child("dictionary")
                        .child(key.get(i)).child("isLearntWT").setValue(true);
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
        switch (view.getId()) {
            case R.id.btnA:
                etAnswer.append(btnA.getText());
                break;
            case R.id.btnE:
                etAnswer.append(btnE.getText());
                break;
            case R.id.btnI:
                etAnswer.append(btnI.getText());
                break;
            case R.id.btnU:
                etAnswer.append(btnU.getText());
                break;
            case R.id.btnU2:
                etAnswer.append(btnU2.getText());
                break;
            case R.id.btnO:
                etAnswer.append(btnO.getText());
                break;
            case R.id.btnN:
                etAnswer.append(btnN.getText());
                break;
            case R.id.ibSoundWT:
                Speech(tvWord.getText().toString());
                break;

            case R.id.btnNextWT:
                next--;
                etAnswer.setText("");
                tvCorrect.setText("");
                setPhrase();
                layout.setVisibility(View.GONE);
                btnCheck.setEnabled(true);
                break;

            case R.id.btnCheckWT:
                progressBar.setProgress(progressBar.getProgress() + 1);
                layout.setVisibility(View.VISIBLE);
                btnCheck.setEnabled(false);
                if (etAnswer.getText().toString().equals(translations.get(next))) {
                    tvMessage.setText(R.string.CorrectAnswer);
                    isLearnt[next] = true;
                    layout.setBackgroundColor(getResources().getColor(R.color.correct));
                    ivAnswer.setBackground(getResources().getDrawable(R.drawable.ic_correct));
                } else {
                    tvMessage.setText(R.string.WrongAnswer);
                    tvCorrect.setText(translations.get(next));
                    layout.setBackgroundColor(getResources().getColor(R.color.wrong));
                    ivAnswer.setBackground(getResources().getDrawable(R.drawable.ic_wrong));
                }
                break;

        }
    }

}