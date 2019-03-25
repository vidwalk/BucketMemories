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

import com.example.bucketnotes.bucketmemories.LoginActivity;
import com.example.bucketnotes.R;
import com.example.bucketnotes.bucketmemories.entities.User;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserEmailFragment extends Fragment {

    public interface GetEmailFromFragment {
        void getEmail(String email);
    }

    private GetEmailFromFragment emailFromFragment;

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private TextInputLayout emailWrapper;
    private EditText emailEdit;
    private Button buttonMailNext;
    private View view;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            emailFromFragment = (GetEmailFromFragment) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()+" must implements GetEmailFromFragment");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        view = inflater.inflate(R.layout.fragment_user_email, container, false);
        initUI();
        workWithEmail();
        return view;
    }

    private void initUI() {
        emailWrapper = (TextInputLayout) view.findViewById(R.id.regEmailWrapper);
        emailEdit = (EditText) view.findViewById(R.id.userEmailEdit);
        buttonMailNext = (Button) view.findViewById(R.id.buttonUserEmailNext);
    }

    private void workWithEmail() {
        buttonMailNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean valid = validateEmail(emailEdit.getText().toString().trim());
                if(valid) {
                    emailFromFragment.getEmail(emailEdit.getText().toString().trim());
                    emailWrapper.setErrorEnabled(false);
                }else {
                    emailWrapper.setErrorEnabled(true);
                    emailWrapper.setError("Email is not correct or user with this mail exists");
                }
            }
        });
    }

    private boolean validateEmail(String email){
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(email);
        return matcher.find() && !existsSameUserMail(email);
    }

    private boolean existsSameUserMail(String email){
        LoginActivity activity = (LoginActivity)getActivity();
        ArrayList<User> users = activity.getUsersFromData();
        boolean result = false;
        for(User user : users){
            if(user.getEmail().equals(email)){
                result = true;
                break;
            }
        }
        return result;
    }
}
