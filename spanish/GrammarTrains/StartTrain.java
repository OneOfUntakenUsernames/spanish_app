package com.example.user.spanish.GrammarTrains;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.spanish.AddWordActivity;
import com.example.user.spanish.Objects.QuestionObject;
import com.example.user.spanish.R;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;

public class StartTrain extends Fragment implements View.OnClickListener{

    String QUESTION_OBJECT = "questionObject";
    String PHRASES_FOR_LEARNING = "phrases";
    String QUERY = "query";
    String INFO = "info";
    QuestionObject questionObject;
    ArrayList<Integer> PhrasesForLearning = new ArrayList<>();
    TextToSpeech textToSpeech;
    int result;
    int next;
    TextView tvWord, tvTranslation;
    ImageButton ibSound;
    Button btnNext;
    ProgressBar trainProgress;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            questionObject = getArguments().getParcelable(QUESTION_OBJECT);
            PhrasesForLearning = getArguments().getIntegerArrayList(PHRASES_FOR_LEARNING);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start_train, container, false);
        tvWord = (TextView) view.findViewById(R.id.tvWordStart);
        tvTranslation = (TextView) view.findViewById(R.id.tvTranslationStart);
        ibSound = (ImageButton) view.findViewById(R.id.ibSoundStart);
        btnNext = (Button) view.findViewById(R.id.btnNextStart);
        ibSound.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        next = 0;
        trainProgress = (ProgressBar) getActivity().findViewById(R.id.pBarTrain);

        setPhrase();
        return view;
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


    private void startMatchingTrain(){
        MatchingTrain matchingTrain = new MatchingTrain();
        final Bundle args = new Bundle();
        args.putString(QUERY, getArguments().getString(QUERY));
        args.putParcelable(INFO, getArguments().getParcelable(INFO));
        args.putParcelable(QUESTION_OBJECT, questionObject);
        args.putIntegerArrayList(PHRASES_FOR_LEARNING, PhrasesForLearning);
        matchingTrain.setArguments(args);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_grammar_trains, matchingTrain).commit();
    }


    private void setPhrase(){
        tvWord.setText(questionObject.getPhrases().get(PhrasesForLearning.get(next)));
        tvTranslation.setText(questionObject.getTranslations().get(PhrasesForLearning.get(next)));
        Speech(tvWord.getText().toString());
        next++;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnNextStart:
                if(next < 4){
                    setPhrase();
                    trainProgress.setProgress(trainProgress.getProgress()+1);
                }else {
                    startMatchingTrain();
                }
                break;

            case R.id.ibSoundStart:
                Speech(tvWord.getText().toString());
                break;

        }
    }
}
