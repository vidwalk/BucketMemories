package com.example.bucketnotes.bucketmemories.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bucketnotes.bucketmemories.LoginActivity;
import com.example.bucketnotes.R;
import com.example.bucketnotes.bucketmemories.entities.User;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener, TextWatcher, View.OnFocusChangeListener{

    public interface NeedRegistrationListener {
        void needRegistration(boolean needRegistration, int userCount);
    }

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private NeedRegistrationListener regListener;
    private TextInputLayout emailWrapper;
    private EditText numbOne,numbTwo, numbThree, numbFour, email;
    private Button buttonReg, buttonLog;
    private View viewFrag, viewInclude;
    private int userCount;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            regListener = (NeedRegistrationListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()+" must implements NeedRegistrationListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        viewFrag = inflater.inflate(R.layout.fragment_login, container, false);
        userCount = -1;
        initUI();
        return viewFrag;
    }


    private void initUI() {
        viewInclude = viewFrag.findViewById(R.id.pinView);
        numbOne = (EditText) viewInclude.findViewById(R.id.regFirstNumber);
        numbOne.addTextChangedListener(this);
        numbTwo = (EditText) viewInclude.findViewById(R.id.regSecondNumber);
        numbTwo.addTextChangedListener(this);
        numbThree = (EditText) viewInclude.findViewById(R.id.regThirdNumber);
        numbThree.addTextChangedListener(this);
        numbFour = (EditText) viewInclude.findViewById(R.id.regFourthNumber);
        numbFour.addTextChangedListener(this);

        emailWrapper = (TextInputLayout) viewFrag.findViewById(R.id.loginEmailWrapper);
        email = (EditText) viewFrag.findViewById(R.id.loginEmailEdit);
        email.setOnFocusChangeListener(this);
        buttonReg = (Button) viewFrag.findViewById(R.id.buttonUserRegistration);
        buttonReg.setOnClickListener(this);
        buttonLog = (Button) viewFrag.findViewById(R.id.buttonUserLogin);
        buttonLog.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonUserLogin:
                if(checkData()){
                    regListener.needRegistration(false, userCount);
                } else{
                    Toast.makeText(getActivity(), "Wrong email or password", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.buttonUserRegistration:
                regListener.needRegistration(true, userCount);
                break;
            default:

        }
    }

    private boolean checkData() {
        boolean result = false;
        String userEmail = email.getText().toString().trim();
        String userPass = numbOne.getText().toString()+numbTwo.getText().toString()
                +numbThree.getText().toString()+numbFour.getText().toString();

        LoginActivity activity = (LoginActivity) getActivity();
        ArrayList<User> users = activity.getUsersFromData();

        for(int i=0; i<users.size();i++){
            if(users.get(i).getEmail().equals(userEmail) && users.get(i).getPin().equals(userPass)){
                result = true;
                userCount = i;
                break;
            }
        }
        return result && validateEmail(userEmail);
    }

    private boolean validateEmail(String email){
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(email);
        return matcher.find();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if(numbOne.getText().length() == 1){
            numbTwo.requestFocus();
        }
        if(numbTwo.getText().length() == 1){
            numbThree.requestFocus();
        }
        if(numbThree.getText().length() == 1){
            numbFour.requestFocus();
        }
        if(numbFour.getText().length() == 1){
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(viewFrag.getWindowToken(), 0);
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if(!b && !validateEmail(email.getText().toString().trim())){
            emailWrapper.setErrorEnabled(true);
            emailWrapper.setError("Email is not correct");
        }else{
            emailWrapper.setErrorEnabled(false);
        }
    }
}
