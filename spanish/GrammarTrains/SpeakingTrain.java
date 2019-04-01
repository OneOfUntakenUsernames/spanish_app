package com.example.user.spanish.GrammarTrains;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.spanish.Objects.QuestionObject;
import com.example.user.spanish.R;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpeakingTrain extends Fragment implements View.OnTouchListener, View.OnClickListener {

    QuestionObject questionObject;
    TextView phrase;
    ImageButton btnVoice, btnSpeak;
    SpeechRecognizer mSpeechRecognizer;
    Intent mSpeechRecognizerIntent;
    TextToSpeech textToSpeech;
    int result;
    ArrayList<String> results;
    int next = 0;
    Integer exp;
    boolean[] isLearnt = new boolean[4];

    ArrayList<Integer> chosen;
    ProgressBar trainProgress;

    String IS_LEARNT = "isLearnt";
    String QUESTION_OBJECT = "questionObject";
    String PHRASES_FOR_LEARNING = "phrases";
    String QUERY = "query";
    String INFO = "info";
    String EXP = "exp";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grammar_speaking, container, false);

        trainProgress = (ProgressBar) getActivity().findViewById(R.id.pBarTrain);

        checkPermission();
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(getActivity());
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES");
        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {}
            @Override
            public void onBeginningOfSpeech() {}
            @Override
            public void onRmsChanged(float v) { }
            @Override
            public void onBufferReceived(byte[] bytes) {}
            @Override
            public void onEndOfSpeech() { }
            @Override
            public void onError(int i) { }
            @Override
            public void onResults(Bundle bundle) {
                results = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if(results != null){
                    if(checkAnswer()){
                        Toast.makeText(getActivity(), "Correct", Toast.LENGTH_SHORT).show();
                        exp++;
                    }else {
                        Toast.makeText(getActivity(), "Wrong", Toast.LENGTH_SHORT).show();
                    }
                    next++;
                    if(next < 4) {
                        phrase.setText(questionObject.getPhrases().get(chosen.get(next)));
                    }else{
                        startNextTraining();
                    }

                }else {
                    Toast.makeText(getActivity(), "Wrong", Toast.LENGTH_SHORT).show();
                    phrase.setText(questionObject.getPhrases().get(chosen.get(next)));
                }
                trainProgress.setProgress(trainProgress.getProgress()+1);
            }
            @Override
            public void onPartialResults(Bundle bundle) { }
            @Override
            public void onEvent(int i, Bundle bundle) { }
        });


        phrase = (TextView) view.findViewById(R.id.tvPhraseVoice);
        btnVoice = (ImageButton) view.findViewById(R.id.imageBtnVoice);
        btnSpeak = (ImageButton) view.findViewById(R.id.imageBtnSpeak);
        btnVoice.setOnClickListener(this);
        btnSpeak.setOnTouchListener(this);


        Button btnSkip = (Button) view.findViewById(R.id.btnSkipSpeaking);
        btnSkip.setOnClickListener(this);

        exp = getArguments().getInt(EXP);
        questionObject = getArguments().getParcelable(QUESTION_OBJECT);
        chosen = getArguments().getIntegerArrayList(PHRASES_FOR_LEARNING);
        isLearnt = getArguments().getBooleanArray(IS_LEARNT);

        if (questionObject != null) {
            phrase.setText(questionObject.getPhrases().get(chosen.get(0)));
        }else {
            Toast.makeText(getActivity(),"Что-то не так с подключением", Toast.LENGTH_SHORT).show();
        }

        return view;
    }


    private void startNextTraining(){
        WritingTrain writingTrain = new WritingTrain();
        Bundle args = new Bundle();
        String query = getArguments().getString(QUERY);
        args.putString(QUERY, query);
        args.putBooleanArray(IS_LEARNT, isLearnt);
        args.putParcelable(INFO, getArguments().getParcelable(INFO));
        args.putParcelable(QUESTION_OBJECT, questionObject);
        args.putIntegerArrayList(PHRASES_FOR_LEARNING, chosen);
        args.putInt(EXP, exp);
        writingTrain.setArguments(args);

        FragmentTransaction fTrans = getFragmentManager().beginTransaction();
        fTrans.replace(R.id.content_grammar_trains, writingTrain).commit();
    }

    private String transformText(){
        String text = phrase.getText().toString().toLowerCase();
        Pattern pattern = Pattern.compile("[?¿!¡.,]");
        Matcher matcher = pattern.matcher(text);
        text = matcher.replaceAll("");

        return text;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        if(view.getId() == R.id.imageBtnSpeak) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_UP:
                    mSpeechRecognizer.stopListening();
                    break;

                case MotionEvent.ACTION_DOWN:
                        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                    break;
            }

        }
        return false;
    }


    private void textToSpeech(){
        textToSpeech = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    Locale spanish = new Locale("es", "ES");
                    result = textToSpeech.setLanguage(spanish);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(getActivity(), "Feature not supported in your device", Toast.LENGTH_SHORT).show();
                    } else {
                        textToSpeech.speak(phrase.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                    }
                } else {
                    Toast.makeText(getActivity(), "Feature not supported on your device", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.imageBtnVoice) {
            textToSpeech();
        }
        if(view.getId() == R.id.btnSkipSpeaking){
            trainProgress.setProgress(12);
            startNextTraining();
        }
    }


    private void checkPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(!(ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.RECORD_AUDIO) ==
                    PackageManager.PERMISSION_GRANTED)) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + getContext().getPackageName()));
                startActivity(intent);
            }
        }
    }


    private boolean checkAnswer() {

        Pattern pattern = Pattern.compile(" ");

        String[] words = pattern.split(transformText());

        String res = null;
        int counter = 0;

        for (int i = 0; i < results.size(); i++) {
            res += " " + results.get(i);
        }

        for (String word : words) {
            if (res.contains(word)) {
                counter++;
            }
        }

        float length = (float) counter / (float) words.length;

        return length >= 0.7;

    }

}
