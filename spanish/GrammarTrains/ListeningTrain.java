package com.example.user.spanish.GrammarTrains;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.spanish.Objects.QuestionObject;
import com.example.user.spanish.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class ListeningTrain extends Fragment implements View.OnClickListener {

    QuestionObject questionObject;
    TextView tvTranslation;
    EditText etAnswer;
    ImageButton ibListen;
    RecyclerView rvParts;
    Button btnNext, btnRemove, btnCheck;
    TextToSpeech textToSpeech;
    int result;
    String sentence;
    ArrayList<String> words;
    ArrayList<Integer> chosen;
    int next = 0;
    ProgressBar trainProgress;
    Integer exp;
    boolean[] isLearnt = new boolean[4];

    String IS_LEARNT = "isLearnt";
    String QUESTION_OBJECT = "questionObject";
    String PHRASES_FOR_LEARNING = "phrases";
    String QUERY = "query";
    String INFO = "info";
    String EXP = "exp";
    GrammarListeningAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_grammar_listening, container, false);
        rvParts = (RecyclerView) view.findViewById(R.id.rvPartsListening);
        etAnswer = (EditText) view.findViewById(R.id.etAnswerListening);
        tvTranslation = (TextView) view.findViewById(R.id.tvTranslationListening);
        ibListen = (ImageButton) view.findViewById(R.id.ibSentenceListening);
        btnNext = (Button) view.findViewById(R.id.btnNextListening);
        btnRemove = (Button) view.findViewById(R.id.btnRemoveListening);
        btnCheck = (Button) view.findViewById(R.id.btnCheckListening);
        btnCheck.setOnClickListener(this);
        ibListen.setOnClickListener(this);
        btnRemove.setOnClickListener(this);
        btnNext.setOnClickListener(this);

        trainProgress = (ProgressBar) getActivity().findViewById(R.id.pBarTrain);

        exp = getArguments().getInt(EXP);
        questionObject = getArguments().getParcelable(QUESTION_OBJECT);
        chosen = getArguments().getIntegerArrayList(PHRASES_FOR_LEARNING);
        isLearnt = getArguments().getBooleanArray(IS_LEARNT);

        sentence = questionObject.getPhrases().get(chosen.get(0));

        words = new ArrayList<>(Arrays.asList(sentence.split(" ")));
        String wrongPhrase = "";
        for (int i = 0; i < questionObject.getPhrases().size(); i++) {
            if (!((wrongPhrase = questionObject.getPhrases().get(i)).equals(sentence))) {
                break;
            }
        }
        String[] arrayList = wrongPhrase.split(" ");
        words.addAll(Arrays.asList(arrayList));
        Collections.shuffle(words);


        rvParts.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL);
        rvParts.setLayoutManager(layoutManager);
        adapter = new GrammarListeningAdapter(words);
        rvParts.setAdapter(adapter);

        return view;
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
                        textToSpeech.speak(sentence, TextToSpeech.QUEUE_FLUSH, null);
                    }
                } else {
                    Toast.makeText(getActivity(), "Feature not supported on your device", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void Remove() {
        if (!etAnswer.getText().toString().equals("")) {
            ArrayList<String> answer = new ArrayList<>(Arrays.asList(etAnswer.getText().toString().split(" ")));
            words.add(answer.get(answer.size() - 1));
            answer.remove(answer.size() - 1);
            etAnswer.setText("");
            if (answer.size() != 0) {
                etAnswer.setText(answer.get(0));
            }
            for (int i = 1; i < answer.size(); i++) {
                etAnswer.append(" " + answer.get(i));
            }

            adapter.notifyDataSetChanged();
        }
    }

    //удаление из списка
    private void RemoveFromList(int pos) {
        words.remove(pos);
        adapter.notifyDataSetChanged();
    }


    private boolean isCorrect() {
        String answ = etAnswer.getText().toString();
        return answ.equals(sentence);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibSentenceListening:
                Speech();
                break;
            case R.id.btnRemoveListening:
                Remove();
                break;
            case R.id.btnNextListening:
                btnNext.setVisibility(View.GONE);
                btnCheck.setVisibility(View.VISIBLE);
                btnRemove.setEnabled(true);
                rvParts.setVisibility(View.VISIBLE);
                tvTranslation.setText("");
                next();
                trainProgress.setProgress(trainProgress.getProgress() + 1);
                break;

            case R.id.btnCheckListening:
                btnNext.setVisibility(View.VISIBLE);
                btnCheck.setVisibility(View.GONE);
                tvTranslation.setText(questionObject.getTranslations().get(chosen.get(next)));
                btnRemove.setEnabled(false);
                rvParts.setVisibility(View.INVISIBLE);

                if (isCorrect()) {
                    Toast.makeText(getActivity(), "Correct", Toast.LENGTH_SHORT).show();
                    exp++;
                } else {
                    Toast.makeText(getActivity(), "Wrong", Toast.LENGTH_SHORT).show();
                    etAnswer.setText(questionObject.getPhrases().get(chosen.get(next)));
                    isLearnt[next] = false;
                }
                rvParts.setEnabled(false);
                break;
        }
    }


    private void next() {
        words.clear();
        etAnswer.setText("");
        next++;
        if (next < 4) {

            sentence = questionObject.getPhrases().get(chosen.get(next));
            ArrayList<String> newWords = new ArrayList<>(Arrays.asList(sentence.split(" ")));

            Random random = new Random();
            int num;

            do {
                num = random.nextInt(chosen.size());
            } while (num == next);

            String wrongPhrase = questionObject.getPhrases().get(num);
            String[] arrayList = wrongPhrase.split(" ");
            newWords.addAll(Arrays.asList(arrayList));
            words.addAll(newWords);
            Collections.shuffle(words);

            adapter.notifyDataSetChanged();

        } else {
            FinishTrain finishTrain = new FinishTrain();
            Bundle args = new Bundle();

            String query = getArguments().getString(QUERY);
            args.putString(QUERY, query);
            args.putBooleanArray(IS_LEARNT, isLearnt);
            args.putParcelable(INFO, getArguments().getParcelable(INFO));
            args.putIntegerArrayList(PHRASES_FOR_LEARNING, chosen);
            args.putParcelable(QUESTION_OBJECT, questionObject);
            args.putInt(EXP, exp);
            finishTrain.setArguments(args);
            FragmentTransaction fTrans = getFragmentManager().beginTransaction();
            fTrans.replace(R.id.content_grammar_trains, finishTrain).commit();
        }
    }


    public class GrammarListeningAdapter extends RecyclerView.Adapter<GrammarListeningAdapter.ViewHolder> {

        private ArrayList<String> Words;

        public GrammarListeningAdapter(ArrayList<String> words) {
            Words = words;
        }

        @Override
        public GrammarListeningAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_grammar_listening, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final GrammarListeningAdapter.ViewHolder holder, final int position) {
            holder.btnWord.setText(Words.get(holder.getAdapterPosition()));

            holder.btnWord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!etAnswer.getText().toString().equals("")) {
                        etAnswer.append(" " + Words.get(position));
                    } else {
                        etAnswer.append(Words.get(position));
                    }
                    RemoveFromList(position);
                    if (isCorrect()) {
                        Toast.makeText(getActivity(), "Correct", Toast.LENGTH_SHORT).show();
                        next();
                        trainProgress.setProgress(trainProgress.getProgress() + 1);
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return Words.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            Button btnWord;

            public ViewHolder(View itemView) {
                super(itemView);

                btnWord = (Button) itemView.findViewById(R.id.btnWordGrammarListening);
            }
        }
    }

}
