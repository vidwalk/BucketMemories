package com.example.bucketnotes.bucketmemories.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.bucketnotes.R;
import com.example.bucketnotes.bucketmemories.validators.Validator;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserNameFragment extends Fragment {

    private GetNameFromFragment nameFromFragment;
    private Validator stringValidator;
    private TextInputLayout nameEditWrapper;
    private EditText regNameEdit;
    private View view;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            nameFromFragment = (GetNameFromFragment) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implements GetNameFromFragment");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        view = inflater.inflate(R.layout.fragment_user_name, container, false);
        stringValidator = new Validator.StringValidatorBuilder().setNotEmpty().build();
        initUI();
        return view;
    }

    private void initUI() {
        nameEditWrapper = (TextInputLayout) view.findViewById(R.id.regNameWrapper);
        regNameEdit = (EditText) view.findViewById(R.id.regNameEdit);
        Button buttonUserName = (Button) view.findViewById(R.id.buttonUserNameNext);
        buttonUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean valid = stringValidator.validate(nameEditWrapper.getEditText().getText().toString(),
                        nameEditWrapper.getHint().toString());
                if (valid) {
                    nameFromFragment.getName(regNameEdit.getText().toString().trim());
                } else {
                    nameEditWrapper.setErrorEnabled(true);
                    nameEditWrapper.setError(stringValidator.getLastMessage());
                }
            }
        });
    }

    public interface GetNameFromFragment {
        void getName(String name);
    }
}
