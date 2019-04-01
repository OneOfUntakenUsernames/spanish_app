package com.example.user.spanish.Fragments;


import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.spanish.Methods;
import com.example.user.spanish.Objects.WordObject;
import com.example.user.spanish.R;
import com.example.user.spanish.Trains.TranslationWordActivity;
import com.example.user.spanish.Trains.TranslationWordWithChoiceActivity;
import com.example.user.spanish.Trains.TrueOrFalseActivity;
import com.example.user.spanish.Trains.WordTranslationActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;

import java.util.ArrayList;


public class FragmentTrains extends Fragment implements View.OnClickListener {

    final String wordsStr = "WORDS";
    final String translationsStr = "TRANSLATIONS";
    final String keyStr = "KEY";
    WordObject wordObject;
    ArrayList<String> words = new ArrayList<>();
    ArrayList<String> translations = new ArrayList<>();
    ArrayList<String> key = new ArrayList<>();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    RecyclerView recyclerView;
    private String[] Titles = {"С испанского на русский", "С русского на испанский", "Сопоставление", "Правда или ложь"};
    private Integer[] Images = {R.drawable.swap, R.drawable.swap, R.drawable.list, R.drawable.truefalse};
    private Integer[] unlearntCount = {0, 0, 0, 0};
    Button btnCheck;
    ProgressBar progress;

    ArrayList<WordObject> wordObjects = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trains, container, false);
        btnCheck = (Button) view.findViewById(R.id.btnTrainsConnection);
        btnCheck.setOnClickListener(this);
        progress = (ProgressBar) view.findViewById(R.id.pbTrains);
        recyclerView = (RecyclerView) view.findViewById(R.id.rvTrains);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
        wordObjects.clear();
        checkConnection();
    }


    private void checkConnection(){
        Methods methods = new Methods();
        if(!methods.isConnected(getActivity())){
            Toast.makeText(getActivity(), "Проверьте подключение к Интернету и повторите попытку", Toast.LENGTH_SHORT).show();
            btnCheck.setVisibility(View.VISIBLE);
        }else {
            btnCheck.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
            getData();
        }
    }

    private void getData() {
        words.clear();
        translations.clear();
        key.clear();

        recyclerView.setAdapter(new adapter(Titles, Images, unlearntCount));

        final Query query = reference.child("users").child(user.getUid()).child("dictionary");

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                GenericTypeIndicator<WordObject> genericTypeIndicator = new GenericTypeIndicator<WordObject>() {
                };
                wordObject = dataSnapshot.getValue(genericTypeIndicator);
                wordObjects.add(wordObject);
                key.add(dataSnapshot.getKey());
                unlearntCount[0] = 0;
                unlearntCount[1] = 0;
                unlearntCount[2] = 0;
                unlearntCount[3] = 0;
                for(int i = 0; i < wordObjects.size(); i++) {
                    if (!wordObjects.get(i).getIsLearntWT()) {
                        unlearntCount[0] = unlearntCount[0]+1;
                    }
                    if (!wordObjects.get(i).getIsLearntTW()) {
                        unlearntCount[1] = unlearntCount[1]+1;
                    }
                    if (!wordObjects.get(i).getIsLearntCT()) {
                        unlearntCount[2] = unlearntCount[2]+1;
                    }
                    if (!wordObjects.get(i).getIsLearntMT()) {
                        unlearntCount[3] = unlearntCount[3]+1;
                    }
                }
                progress.setVisibility(View.GONE);
                recyclerView.setAdapter(new adapter(Titles, Images, unlearntCount));
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


    private ArrayList<String> getWordsWT(){
        ArrayList<String> keysWT = new ArrayList<>();
        words.clear();
        translations.clear();

        for(int i = 0; i < wordObjects.size(); i++){
            if(words.size() < 6) {
                if (!wordObjects.get(i).getIsLearntWT()) {
                    words.add(wordObjects.get(i).getWord());
                    translations.add(wordObjects.get(i).getTranslation());
                    keysWT.add(key.get(i));
                }
            }else {
                break;
            }
        }

        if(wordObjects.size() == 0){

        }

        return keysWT;
    }


    private ArrayList<String> getWordsTW(){
        ArrayList<String> keysTW = new ArrayList<>();
        words.clear();
        translations.clear();

        for(int i = 0; i < wordObjects.size(); i++){
            if(words.size() < 6) {
                if (!wordObjects.get(i).getIsLearntTW()) {
                    words.add(wordObjects.get(i).getWord());
                    translations.add(wordObjects.get(i).getTranslation());
                    keysTW.add(key.get(i));
                }
            }else {
                break;
            }
        }

        return keysTW;
    }


    private ArrayList<String> getWordsMT(){
        ArrayList<String> keysMT = new ArrayList<>();
        words.clear();
        translations.clear();

        for(int i = 0; i < wordObjects.size(); i++){
            if(words.size() < 6) {
                if (!wordObjects.get(i).getIsLearntMT()) {
                    words.add(wordObjects.get(i).getWord());
                    translations.add(wordObjects.get(i).getTranslation());
                    keysMT.add(key.get(i));
                }
            }else {
                break;
            }
        }

        return keysMT;
    }


    private ArrayList<String> getWordsCT(){
        ArrayList<String> keysCT = new ArrayList<>();
        words.clear();
        translations.clear();

        for(int i = 0; i < wordObjects.size(); i++){
            if(words.size() < 6) {
                if (!wordObjects.get(i).getIsLearntCT()) {
                    words.add(wordObjects.get(i).getWord());
                    translations.add(wordObjects.get(i).getTranslation());
                    keysCT.add(key.get(i));
                }
            }else {
                break;
            }
        }

        return keysCT;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnTrainsConnection) {
            checkConnection();
        }
    }


    public class adapter extends RecyclerView.Adapter<adapter.ViewHolder> {

        private String[] Titles;
        private Integer[] Images;
        private Integer[] UnlearntCount;

        @Override
        public FragmentTrains.adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(FragmentTrains.adapter.ViewHolder holder, int position) {
            holder.tvTitle.setText(Titles[position]);
            holder.ivImage.setImageDrawable(getResources().getDrawable(Images[position]));
            holder.tvWordsCount.setText("слов на изучении: " + UnlearntCount[position].toString());
        }

        adapter(String[] titles, Integer[] imageId, Integer[] unlearntCount) {
            Titles = titles;
            Images = imageId;
            UnlearntCount = unlearntCount;
        }

        @Override
        public int getItemCount() {
            return Titles.length;
        }


        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvTitle, tvWordsCount;
            ImageView ivImage;

            ViewHolder(final View itemView) {
                super(itemView);
                tvTitle = (TextView) itemView.findViewById(R.id.tvTrainTitle);
                ivImage = (ImageView) itemView.findViewById(R.id.ivTrainImage);
                tvWordsCount = (TextView) itemView.findViewById(R.id.tvWordsCount);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String s = tvTitle.getText().toString();
                        Intent intent;

                            switch (s) {
                                case "С испанского на русский":
                                    ArrayList<String> keysWT = getWordsWT();
                                    if(keysWT.size() > 0) {
                                        intent = new Intent(getActivity(), WordTranslationActivity.class);
                                        intent.putExtra(keyStr, keysWT);
                                        intent.putExtra(wordsStr, words);
                                        intent.putExtra(translationsStr, translations);
                                        startActivity(intent);
                                    }else {
                                        Toast.makeText(getContext(), "У вас нет неизученных слов!", Toast.LENGTH_SHORT).show();
                                    }

                                    break;
                                case "С русского на испанский":
                                    ArrayList<String> keysTW = getWordsTW();
                                    if(keysTW.size() > 0) {
                                        intent = new Intent(getActivity(), TranslationWordActivity.class);
                                        intent.putExtra(wordsStr, words);
                                        intent.putExtra(translationsStr, translations);
                                        intent.putExtra(keyStr, keysTW);
                                        startActivity(intent);
                                    }else {
                                        Toast.makeText(getContext(), "У вас нет неизученных слов!", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case "Сопоставление":
                                    ArrayList<String> keysCT = getWordsCT();
                                    if (keysCT.size() >= 4) {
                                        intent = new Intent(getActivity(), TranslationWordWithChoiceActivity.class);
                                        intent.putExtra(wordsStr, words);
                                        intent.putExtra(translationsStr, translations);
                                        intent.putExtra(keyStr, keysCT);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(getContext(), "Недостаточно неизученных слов для тренировки", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case "Правда или ложь":
                                    ArrayList<String> keysMT = getWordsMT();
                                    if(keysMT.size() > 0) {
                                        intent = new Intent(getActivity(), TrueOrFalseActivity.class);
                                        intent.putExtra(wordsStr, words);
                                        intent.putExtra(translationsStr, translations);
                                        intent.putExtra(keyStr, keysMT);
                                        startActivity(intent);
                                    }else {
                                        Toast.makeText(getContext(), "У вас нет неизученных слов!", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                            }

                    }
                });
            }


        }
    }

}
