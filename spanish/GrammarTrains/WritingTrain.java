package com.example.user.spanish.GrammarTrains;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.spanish.Objects.QuestionObject;
import com.example.user.spanish.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WritingTrain extends Fragment implements View.OnClickListener {

    String IS_LEARNT = "isLearnt";
    String QUESTION_OBJECT = "questionObject";
    String PHRASES_FOR_LEARNING = "phrases";
    String QUERY = "query";
    String INFO = "info";
    String EXP = "exp";
    QuestionObject questionObject;
    ArrayList<Integer> chosen;
    TextToSpeech textToSpeech;
    int result, next, exp;
    ProgressBar trainProgress;
    boolean[] isLearnt = new boolean[4];

    TextView tvPhrase, tvCorrect;
    EditText etAnswer;
    Button btnSound, btnCheck, btnNext;

    private void startNextTraining() {
        ListeningTrain listeningTrain = new ListeningTrain();
        Bundle args = new Bundle();
        args.putBooleanArray(IS_LEARNT, isLearnt);
        args.putParcelable(QUESTION_OBJECT, questionObject);
        args.putIntegerArrayList(PHRASES_FOR_LEARNING, chosen);
        String query = getArguments().getString(QUERY);
        args.putString(QUERY, query);
        args.putParcelable(INFO, getArguments().getParcelable(INFO));
        args.putInt(EXP, exp);
        listeningTrain.setArguments(args);
        FragmentTransaction fTrans = getFragmentManager().beginTransaction();
        fTrans.replace(R.id.content_grammar_trains, listeningTrain).commit();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isLearnt = getArguments().getBooleanArray(IS_LEARNT);
            exp = getArguments().getInt(EXP);
            questionObject = getArguments().getParcelable(QUESTION_OBJECT);
            chosen = getArguments().getIntegerArrayList(PHRASES_FOR_LEARNING);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grammar_writing, container, false);
        tvPhrase = (TextView) view.findViewById(R.id.tvPhraseWriting);
        tvCorrect = (TextView) view.findViewById(R.id.tvCoorectWriting);
        etAnswer = (EditText) view.findViewById(R.id.etAnswerWriting);
        etAnswer.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if((keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) || (i == EditorInfo.IME_ACTION_DONE)) {
                    btnNext.setVisibility(View.VISIBLE);
                    btnCheck.setVisibility(View.GONE);
                    etAnswer.setEnabled(false);
                    if (checkAnswer()) {
                        etAnswer.setTextColor(getResources().getColor(R.color.darkGreen));
                        exp++;
                    } else {
                        etAnswer.setTextColor(getResources().getColor(R.color.darkRed));
                        tvCorrect.setText("Ответ: " + questionObject.getTranslations().get(chosen.get(next)));
                    }
                    return true;
                }
                return false;
            }
        });

        btnSound = (Button) view.findViewById(R.id.btnSoundWriting);
        btnSound.setOnClickListener(this);
        btnCheck = (Button) view.findViewById(R.id.btnCheckWriting);
        btnCheck.setOnClickListener(this);
        btnNext = (Button) view.findViewById(R.id.btnNextWriting);
        btnNext.setOnClickListener(this);
        trainProgress = (ProgressBar) getActivity().findViewById(R.id.pBarTrain);

        next = 0;
        setPhrase();
        return view;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSoundWriting:
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
                break;

            case R.id.btnCheckWriting:
                btnNext.setVisibility(View.VISIBLE);
                btnCheck.setVisibility(View.GONE);
                etAnswer.setEnabled(false);
                if(checkAnswer()){
                    etAnswer.setTextColor(getResources().getColor(R.color.darkGreen));
                }else {
                    etAnswer.setTextColor(getResources().getColor(R.color.darkRed));
                    tvCorrect.setText("Ответ: " + questionObject.getTranslations().get(chosen.get(next)));
                    isLearnt[next] = false;
                }
                break;

            case R.id.btnNextWriting:
                etAnswer.setEnabled(true);
                etAnswer.setTextColor(getResources().getColor(R.color.colorAccent));
                tvCorrect.setText("");
                btnCheck.setVisibility(View.VISIBLE);
                btnNext.setVisibility(View.GONE);
                etAnswer.setText("");
                trainProgress.setProgress(trainProgress.getProgress()+1);
                next++;
                setPhrase();
                break;
        }
    }


    private boolean checkAnswer(){
        String correctAnswer = transformText(questionObject.getTranslations().get(chosen.get(next)));
        String userAnswer = transformText(etAnswer.getText().toString());
        return correctAnswer.equalsIgnoreCase(userAnswer);
    }

    private void setPhrase(){
        if(next < 4) {
            tvPhrase.setText(questionObject.getPhrases().get(chosen.get(next)));
        }else{
            startNextTraining();
        }
    }


    private String transformText(String text){
        Pattern pattern = Pattern.compile("[?¿!¡.,/()]");
        Matcher matcher = pattern.matcher(text);
        text = matcher.replaceAll("");
        return text;
    }

}
