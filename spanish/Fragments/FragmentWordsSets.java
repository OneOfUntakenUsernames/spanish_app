package com.example.user.spanish.Fragments;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.spanish.Methods;
import com.example.user.spanish.Objects.WordObject;
import com.example.user.spanish.Objects.WordsSetObject;
import com.example.user.spanish.R;
import com.example.user.spanish.WordsSetActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class FragmentWordsSets extends Fragment implements View.OnClickListener {

    RecyclerView recyclerView;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference referenceDict = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("dictionary");
    Button btnCheck;
    ProgressBar progress;


    public static class WordsSetsViewHolder extends RecyclerView.ViewHolder{

        TextView tvNameSet, tvWordsCount;
        ImageView imageView;
        ImageButton ibAddSet;
        RelativeLayout relativeLayout;


        public WordsSetsViewHolder(View itemView) {
            super(itemView);
            tvNameSet = (TextView) itemView.findViewById(R.id.tvNameSet);
            tvWordsCount = (TextView) itemView.findViewById(R.id.tvWordsCountSet);
            imageView = (ImageView) itemView.findViewById(R.id.ivSet);
            ibAddSet = (ImageButton) itemView.findViewById(R.id.ibAddSet);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.rlSet);
        }
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_words_sets, container, false);
        btnCheck = (Button) view.findViewById(R.id.btnSetsConnection);
        btnCheck.setOnClickListener(this);
        progress = (ProgressBar) view.findViewById(R.id.pbSets);

        recyclerView = (RecyclerView) view.findViewById(R.id.rvWordsSets);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        checkConnection();

        return view;
    }


    private void setData(){
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("sets");
        FirebaseRecyclerAdapter<WordsSetObject, WordsSetsViewHolder> recyclerAdapter;

        recyclerAdapter = new FirebaseRecyclerAdapter<WordsSetObject, WordsSetsViewHolder>(
                WordsSetObject.class,
                R.layout.card_words_set_object,
                WordsSetsViewHolder.class,
                reference
        ) {
            @Override
            protected void populateViewHolder(WordsSetsViewHolder viewHolder, final WordsSetObject wordsSetObject, int position) {
                progress.setVisibility(View.GONE);
                viewHolder.tvNameSet.setText(wordsSetObject.getName());
                viewHolder.tvWordsCount.setText("Всего слов: " + wordsSetObject.getCount().toString());
                Picasso.get().load(wordsSetObject.getImage()).into(viewHolder.imageView);
                if(wordsSetObject.getIsAdded()) {
                    viewHolder.ibAddSet.setImageResource(R.drawable.ic_done);
                }else {
                    viewHolder.ibAddSet.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Methods methods = new Methods();
                            if(!methods.isConnected(getContext())){
                                Toast.makeText(getContext(), "Проверьте подключение к Интернету и повторите попытку", Toast.LENGTH_SHORT).show();
                            }else {
                                for (int i = 0; i < wordsSetObject.getWords().size(); i++) {
                                    WordObject wordObject = new WordObject(wordsSetObject.getWords().get(i), wordsSetObject.getTranslations().get(i),
                                            false, false, false, false);
                                    referenceDict.push().setValue(wordObject);
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("isAdded", true);
                                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("sets").child("colors");
                                    reference.updateChildren(data);
                                }
                            }
                        }
                    });
                }

                viewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), WordsSetActivity.class);
                        intent.putExtra("object", wordsSetObject);
                        startActivity(intent);
                    }
                });

            }
        };

        recyclerView.setAdapter(recyclerAdapter);
    }

    private void checkConnection(){
        Methods methods = new Methods();
        if(!methods.isConnected(getContext())){
            Toast.makeText(getContext(), "Проверьте подключение к Интернету и повторите попытку", Toast.LENGTH_SHORT).show();
            btnCheck.setVisibility(View.VISIBLE);
        }else {
            btnCheck.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
            setData();
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnSetsConnection) {
            checkConnection();
        }
    }

}
