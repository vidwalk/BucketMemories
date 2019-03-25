package com.example.bucketnotes.bucketmemories.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.bucketnotes.bucketmemories.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserPinFragment extends Fragment implements TextWatcher {

    private GetPinFromFragment pinFromFragment;
    private EditText first, second, third, fourth;
    private View view, viewInclude;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            pinFromFragment = (GetPinFromFragment) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implements GetPinFromFragment");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        view = inflater.inflate(R.layout.fragment_user_pin, container, false);
        initUI();
        return view;
    }

    private void initUI() {
        viewInclude = view.findViewById(R.id.pinView);
        first = (EditText) viewInclude.findViewById(R.id.regFirstNumber);
        first.addTextChangedListener(this);
        second = (EditText) viewInclude.findViewById(R.id.regSecondNumber);
        second.addTextChangedListener(this);
        third = (EditText) viewInclude.findViewById(R.id.regThirdNumber);
        third.addTextChangedListener(this);
        fourth = (EditText) viewInclude.findViewById(R.id.regFourthNumber);
        fourth.addTextChangedListener(this);
        Button buttonPinNext = (Button) view.findViewById(R.id.buttonUserPinNext);
        buttonPinNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pinFromFragment.getPin(first.getText().toString() + second.getText().toString() +
                        third.getText().toString() + fourth.getText().toString());
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (first.getText().length() == 1) {
            second.requestFocus();
        }
        if (second.getText().length() == 1) {
            third.requestFocus();
        }
        if (third.getText().length() == 1) {
            fourth.requestFocus();
        }
        if (fourth.getText().length() == 1) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public interface GetPinFromFragment {
        void getPin(String pin);
    }
}
