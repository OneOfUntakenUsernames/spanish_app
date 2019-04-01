package com.example.user.spanish;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.user.spanish.Objects.WordObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

public class AddWordActivity extends AppCompatActivity {

    public static final String API_KEY = "dict.1.1.20170818T144857Z.bb77a19197c1cf90.f58b664b505f5c6b2aa76c082b76902b9e65be5e";


    EditText mWord;
    RecyclerView rvResults;

    ArrayList<String> items = new ArrayList<>();
    String word;

    ProgressBar mProgressBar;
    TextView tvNote;
    String fromTo;
    int lang;
    public final String ES = "Введите слово на испанском";
    public final String RU = "Введите слово на русском";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);


        mProgressBar = (ProgressBar) findViewById(R.id.progressBar2);
        tvNote = (TextView) findViewById(R.id.tvNote);

        mWord = (EditText) findViewById(R.id.etSearchWord);

        fromTo = "es-ru";

        final Button button = (Button) findViewById(R.id.button2);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Methods methods = new Methods();
                if (!methods.isConnected(v.getContext())) {
                    Toast.makeText(v.getContext(), "Проверьте подключение к Интернету и повторите попытку", Toast.LENGTH_SHORT).show();
                } else {
                    String s = mWord.getText().toString();

                    if (!Objects.equals(s, "")) {
                        Translate(s);
                        word = mWord.getText().toString();
                    }

                    tvNote.setText("Выберите вариант перевода:");
                    tvNote.setVisibility(View.VISIBLE);

                    rvResults = (RecyclerView) findViewById(R.id.rvResults);
                    RecyclerView.LayoutManager rvLayoutManager = new LinearLayoutManager(getApplicationContext());
                    rvResults.setHasFixedSize(true);
                    rvResults.setLayoutManager(rvLayoutManager);

                    rvResults.setVisibility(View.VISIBLE);

                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(button.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

                }
            }
        });


        Button btnSwap = (Button) findViewById(R.id.btnSwipeLang);

        btnSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lang == 0) {
                    fromTo = "ru-es";
                    mWord.setHint(RU);
                    lang = 1;
                } else {
                    fromTo = "es-ru";
                    mWord.setHint(ES);
                    lang = 0;
                }
            }
        });

    }


    class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ViewHolder> {
        private ArrayList<String> items;


        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvResult;

            ViewHolder(View itemView) {
                super(itemView);
                tvResult = (TextView) itemView.findViewById(R.id.tvResultWord);
            }
        }


        ResultsAdapter(ArrayList<String> ResItems) {
            items = ResItems;
        }


        @Override
        public ResultsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_word, parent, false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.tvResult.setText(items.get(position));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String translation = items.get(position);

                    DatabaseReference mReference;
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    mReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("dictionary");
                    WordObject wordObject;
                    if (lang == 0) {
                        wordObject = new WordObject(word, translation,
                                false, false, false, false);
                    } else {
                        wordObject = new WordObject(translation, word,
                                false, false, false, false);
                    }
                    mReference.push().setValue(wordObject);

                    finish();
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }


    void Translate(String textToTranslate) {
        YandexTranslate yandexTranslate = new YandexTranslate();
        yandexTranslate.execute(textToTranslate);

    }


    public ArrayList fetchTranslations(String textToTranslate, String fromTo) {

        items.clear();

        try {
            String url = Uri.parse("https://dictionary.yandex.net/api/v1/dicservice.json/lookup?")
                    .buildUpon()
                    .appendQueryParameter("key", API_KEY)
                    .appendQueryParameter("lang", fromTo)
                    .appendQueryParameter("text", textToTranslate)
                    .build().toString();
            String jsonString = getUrlString(url);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(items, jsonBody);
        } catch (JSONException je) {
            Log.e("YandexDictionary", "Failed to parse JSON", je);
        } catch (IOException ioe) {
            Log.e("YandexDictionary", "Failed to fetch translations", ioe);
        }

        return items;
    }


    private void parseItems(ArrayList<String> translations, JSONObject jsonBody) throws IOException, JSONException {


        JSONArray def = jsonBody.getJSONArray("def");


        for (int i = 0; i < def.length(); i++) {
            JSONObject defJSONObject = def.getJSONObject(i);

            JSONArray tr = defJSONObject.getJSONArray("tr");

            for (int j = 0; j < tr.length(); j++) {
                JSONObject trJSONObject = tr.getJSONObject(j);

                translations.add(trJSONObject.getString("text"));

                JSONArray syn = trJSONObject.getJSONArray("syn");

                for (int k = 0; k < syn.length(); k++) {
                    JSONObject o = syn.getJSONObject(k);
                    translations.add(o.getString("text"));
                }

            }

        }

    }


    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " +
                        urlSpec);
            }

            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();

            return out.toByteArray();
        } finally {
            connection.disconnect();
        }

    }


    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }


    public class YandexTranslate extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {

            String textToTranslate = params[0];

            fetchTranslations(textToTranslate, fromTo);

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mProgressBar.setVisibility(View.GONE);
            if(items.size() == 0){
                tvNote.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Не найдено переводов для данного слова. " +
                        "Проверьте правильность ввода и язык поиска, затем повторите попытку.", Toast.LENGTH_LONG).show();
            }
            ResultsAdapter resultsAdapter = new ResultsAdapter(items);
            rvResults.setAdapter(resultsAdapter);
        }
    }


}