package com.example.user.spanish.AudioTrains;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.user.spanish.Objects.AudioQuestionObject;
import com.example.user.spanish.R;
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


public class FragmentAudioQuestions extends Fragment implements View.OnClickListener {

    Button btnAnswer1, btnAnswer2, btnAnswer3, btnAnswer4, btnNext;
    TextView tvQuestion;
    ImageButton ibSound;
    ArrayList<AudioQuestionObject> audioQuestionObjects;
    int next = 0;
    final String QUESTIONS = "questions";
    int exp = 10;
    boolean exist = false;
    int points = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grammar_matching, container, false);

        btnAnswer1 = (Button) view.findViewById(R.id.btnAnswer1);
        btnAnswer2 = (Button) view.findViewById(R.id.btnAnswer2);
        btnAnswer3 = (Button) view.findViewById(R.id.btnAnswer3);
        btnAnswer4 = (Button) view.findViewById(R.id.btnAnswer4);
        btnNext = (Button) view.findViewById(R.id.btnNextMatching);
        ibSound = (ImageButton) view.findViewById(R.id.ibSoundMatching);
        ibSound.setVisibility(View.GONE);
        tvQuestion = (TextView) view.findViewById(R.id.tvPhrase);

        btnNext.setOnClickListener(this);
        btnAnswer1.setOnClickListener(this);
        btnAnswer2.setOnClickListener(this);
        btnAnswer3.setOnClickListener(this);
        btnAnswer4.setOnClickListener(this);

        audioQuestionObjects = getArguments().getParcelableArrayList(QUESTIONS);
        setQuestion();

        return view;
    }


    private void setQuestion() {
        if (next < audioQuestionObjects.size()) {
            tvQuestion.setText(audioQuestionObjects.get(next).getQuestion());
            btnAnswer1.setTextColor(getResources().getColor(R.color.colorAccent));
            btnAnswer2.setTextColor(getResources().getColor(R.color.colorAccent));
            btnAnswer3.setTextColor(getResources().getColor(R.color.colorAccent));
            btnAnswer4.setTextColor(getResources().getColor(R.color.colorAccent));
            btnAnswer1.setText(audioQuestionObjects.get(next).getAnswers().get(0));
            btnAnswer2.setText(audioQuestionObjects.get(next).getAnswers().get(1));
            btnAnswer3.setText(audioQuestionObjects.get(next).getAnswers().get(2));
            btnAnswer4.setText(audioQuestionObjects.get(next).getAnswers().get(3));
        } else {
            btnAnswer1.setEnabled(false);
            btnAnswer2.setEnabled(false);
            btnAnswer3.setEnabled(false);
            btnAnswer4.setEnabled(false);
            btnNext.setVisibility(View.GONE);
            points = setLevel(exp);

            createDialog();
        }
    }


    public void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_finish_train, null);
        TextView tvExp = (TextView) layout.findViewById(R.id.tvExp);
        tvExp.setText("Вы заработали " + exp + " очков опыта!");
        builder.setView(layout)
                .setPositiveButton(R.string.Finish, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (!exist) {
                            points += exp;
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            final DatabaseReference mReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
                            mReference.child("userInfo").child("points").setValue(points);
                            exist = true;
                        }
                        getActivity().finish();
                    }
                });

        builder.create();
        builder.show();
    }


    private int setLevel(final int exp) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference mReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

        final int[] points = {0};

        final Query query = mReference.child("userInfo");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                GenericTypeIndicator<Integer> genericTypeIndicator = new GenericTypeIndicator<Integer>() {
                };
                if (!exist) {
                    points[0] = dataSnapshot.getValue(genericTypeIndicator);
                    points[0] += exp;
                    mReference.child("userInfo").child("points").setValue(points[0]);
                    exist = true;
                }
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

        return points[0];
    }


    private void checkCorrect() {
        if (btnAnswer1.getText().toString().equals(audioQuestionObjects.get(next).getCorrect())) {
            btnAnswer1.setTextColor(getResources().getColor(R.color.darkGreen));
        }
        if (btnAnswer2.getText().toString().equals(audioQuestionObjects.get(next).getCorrect())) {
            btnAnswer2.setTextColor(getResources().getColor(R.color.darkGreen));
        }
        if (btnAnswer3.getText().toString().equals(audioQuestionObjects.get(next).getCorrect())) {
            btnAnswer3.setTextColor(getResources().getColor(R.color.darkGreen));
        }
        if (btnAnswer4.getText().toString().equals(audioQuestionObjects.get(next).getCorrect())) {
            btnAnswer4.setTextColor(getResources().getColor(R.color.darkGreen));
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAnswer1:
                if (btnAnswer1.getText().toString().equals(audioQuestionObjects.get(next).getCorrect())) {
                    btnAnswer1.setTextColor(getResources().getColor(R.color.darkGreen));
                    exp+=5;
                } else {
                    btnAnswer1.setTextColor(getResources().getColor(R.color.darkRed));
                    checkCorrect();
                }
                btnNext.setVisibility(View.VISIBLE);
                break;
            case R.id.btnAnswer2:
                if (btnAnswer2.getText().toString().equals(audioQuestionObjects.get(next).getCorrect())) {
                    btnAnswer2.setTextColor(getResources().getColor(R.color.darkGreen));
                    exp+=5;
                } else {
                    btnAnswer2.setTextColor(getResources().getColor(R.color.darkRed));
                    checkCorrect();
                }
                btnNext.setVisibility(View.VISIBLE);
                break;
            case R.id.btnAnswer3:
                if (btnAnswer3.getText().toString().equals(audioQuestionObjects.get(next).getCorrect())) {
                    btnAnswer3.setTextColor(getResources().getColor(R.color.darkGreen));
                    exp+=5;
                } else {
                    btnAnswer3.setTextColor(getResources().getColor(R.color.darkRed));
                    checkCorrect();
                }
                btnNext.setVisibility(View.VISIBLE);
                break;
            case R.id.btnAnswer4:
                if (btnAnswer4.getText().toString().equals(audioQuestionObjects.get(next).getCorrect())) {
                    btnAnswer4.setTextColor(getResources().getColor(R.color.darkGreen));
                    exp+=5;
                } else {
                    btnAnswer4.setTextColor(getResources().getColor(R.color.darkRed));
                    checkCorrect();
                }
                btnNext.setVisibility(View.VISIBLE);
                break;
            case R.id.btnNextMatching:
                next++;
                setQuestion();
                break;
        }
    }
}
