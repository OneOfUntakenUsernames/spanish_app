package com.example.user.spanish.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.spanish.AddWordActivity;
import com.example.user.spanish.GrammarTrains.WritingTrain;
import com.example.user.spanish.Methods;
import com.example.user.spanish.R;
import com.example.user.spanish.Objects.WordObject;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;


public class FragmentDictionary extends Fragment implements View.OnClickListener {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    TextToSpeech textToSpeech;
    int result;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("dictionary");
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<WordObject, WordsViewHolder> recyclerAdapter;
    ProgressBar progressBar;
    Button btnConnection;

    public static final String API_KEY = "dict.1.1.20170818T144857Z.bb77a19197c1cf90.f58b664b505f5c6b2aa76c082b76902b9e65be5e";


    public static class WordsViewHolder extends RecyclerView.ViewHolder{

        TextView mWords, mTranslation;
        Button btnDelete, btnSpeak;
        ImageView ivLearnt;


        public WordsViewHolder(final View itemView){
            super(itemView);

            mWords = (TextView) itemView.findViewById(R.id.tvWord);
            mTranslation = (TextView) itemView.findViewById(R.id.tvLearntTranslation);
            btnDelete = (Button) itemView.findViewById(R.id.btnDeleteWord);
            btnSpeak = (Button) itemView.findViewById(R.id.btnPlayWord);
            ivLearnt = (ImageView) itemView.findViewById(R.id.ivLearntWord);
        }
    }


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.firebase_dictionary, container, false);

        btnConnection = (Button) view.findViewById(R.id.btnDictionaryConnection);
        btnConnection.setOnClickListener(this);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fabAddWord);
        progressBar = (ProgressBar) view.findViewById(R.id.pbDictionary);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddWordActivity.class);
                startActivity(intent);
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.rvWords);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        checkDictionary();
        checkConnection();

        return view;
    }


    private void checkDictionary(){
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    progressBar.setVisibility(View.VISIBLE);
                    setData();
                }else {
                    Toast.makeText(getActivity(), "Словарь пуст!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnDictionaryConnection){
            checkConnection();
        }
    }

    private void checkConnection(){
        Methods methods = new Methods();
        if(!methods.isConnected(getActivity())){
            Toast.makeText(getActivity(), "Проверьте подключение к Интернету и повторите попытку", Toast.LENGTH_SHORT).show();
            btnConnection.setVisibility(View.VISIBLE);
        }else {
            checkDictionary();
            btnConnection.setVisibility(View.GONE);
        }
    }

    private void setData(){
        recyclerAdapter = new FirebaseRecyclerAdapter<WordObject, WordsViewHolder>(
                WordObject.class,
                R.layout.card_dictionary_word,
                WordsViewHolder.class,
                reference
        ) {
            @Override
            protected void populateViewHolder(WordsViewHolder viewHolder, final WordObject wordObject, final int position) {

                progressBar.setVisibility(View.GONE);
                btnConnection.setVisibility(View.GONE);

                if(wordObject.getIsLearntWT() && wordObject.getIsLearntTW() && wordObject.getIsLearntCT() && wordObject.getIsLearntMT()){
                    viewHolder.ivLearnt.setBackground(getResources().getDrawable(R.drawable.ic_correct));
                }else {
                    viewHolder.ivLearnt.setBackground(getResources().getDrawable(R.drawable.ic_wrong));
                }
                viewHolder.mWords.setText(wordObject.getWord());
                viewHolder.mTranslation.setText(wordObject.getTranslation());
                viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference itemRef = getRef(position);
                        itemRef.removeValue();
                    }
                });


                viewHolder.btnSpeak.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        textToSpeech = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
                            @Override
                            public void onInit(int status) {
                                if(status == TextToSpeech.SUCCESS){
                                    Locale spanish = new Locale("es", "ES");
                                    result = textToSpeech.setLanguage(spanish);
                                    if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                                        Toast.makeText(getContext(), "Feature not supported in your device", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        textToSpeech.speak(wordObject.getWord(), TextToSpeech.QUEUE_FLUSH, null);
                                    }
                                }else {
                                    Toast.makeText(getContext(), "Feature not supported on your device", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                });

            }
        };


        recyclerView.setAdapter(recyclerAdapter);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    void Translate(String textToTranslate){
        YandexTranslate yandexTranslate = new YandexTranslate();
        yandexTranslate.execute(textToTranslate);
    }



    public ArrayList fetchTranslations(String textToTranslate){

        ArrayList <String> items = new ArrayList<>();

        try {
            String url = Uri.parse("https://dictionary.yandex.net/api/v1/dicservice.json/lookup?")
                    .buildUpon()
                    .appendQueryParameter("key", API_KEY)
                    .appendQueryParameter("lang", "es-ru")
                    .appendQueryParameter("text", textToTranslate)
                    .build().toString();
            String jsonString = getUrlString(url);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(items, jsonBody);
        }catch (JSONException je){
            Log.e("YandexDictionary", "Failed to parse JSON", je);
        }catch (IOException ioe){
            Log.e("YandexDictionary", "Failed to fetch translations" ,ioe);
        }

        return items;
    }




    private void parseItems(ArrayList<String> translations, JSONObject jsonBody) throws IOException, JSONException{


        JSONArray def = jsonBody.getJSONArray("def");

        for(int i = 0; i < def.length(); i++){
            JSONObject defJSONObject = def.getJSONObject(i);

            JSONArray tr = defJSONObject.getJSONArray("tr");

            for(int j = 0; j < tr.length(); j++){
                JSONObject trJSONObject = tr.getJSONObject(j);

                translations.add(trJSONObject.getString("text"));

                JSONArray syn = trJSONObject.getJSONArray("syn");

                for(int k = 0; k < syn.length(); k++){
                    JSONObject o = syn.getJSONObject(k);
                    translations.add(o.getString("text"));
                }

            }

        }

    }


    public byte[] getUrlBytes(String urlSpec) throws IOException{
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if(connection.getResponseCode()!= HttpURLConnection.HTTP_OK ){
                throw new IOException(connection.getResponseMessage() + ": with " +
            urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer))>0){
                out.write(buffer, 0, bytesRead);
            }
            out.close();

            return out.toByteArray();
        }finally {
            connection.disconnect();
        }

    }


    public String getUrlString(String urlSpec) throws IOException{
        return new String(getUrlBytes(urlSpec));
    }



    private class YandexTranslate extends AsyncTask <String, Void, String>{

        @Override
        protected String doInBackground(String... params) {

            String textToTranslate = params[0];

            new FragmentDictionary().fetchTranslations(textToTranslate);

            return null;
        }
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
