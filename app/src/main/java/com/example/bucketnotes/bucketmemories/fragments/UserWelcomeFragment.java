package com.example.bucketnotes.bucketmemories.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.bucketnotes.bucketmemories.LoginActivity;
import com.example.bucketnotes.bucketmemories.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserWelcomeFragment extends Fragment implements View.OnClickListener {

    private RunMainActivity runMainActivity;
    private View view;
    private TextView name, email;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            runMainActivity = (RunMainActivity) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implements RunMainActivity");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        view = inflater.inflate(R.layout.fragment_user_welcome, container, false);
        initUI();
        showUser();

        return view;
    }

    private void showUser() {
        LoginActivity activity = (LoginActivity) getActivity();
        name.setText(activity.getCurrentUser().getName());
        email.setText(activity.getCurrentUser().getEmail());
    }

    private void initUI() {
        name = (TextView) view.findViewById(R.id.name);
        email = (TextView) view.findViewById(R.id.email);
        Button buttonWelcome = (Button) view.findViewById(R.id.buttonWelcome);
        buttonWelcome.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        runMainActivity.runMainActivity();
    }

    public interface RunMainActivity {
        void runMainActivity();
    }
}
