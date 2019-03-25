package com.example.bucketnotes.bucketmemories.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.bucketnotes.bucketmemories.R;
import com.example.bucketnotes.bucketmemories.entities.SubTask;
import com.example.bucketnotes.bucketmemories.validators.Validator;


public class AddSubTaskDialogFragment extends DialogFragment {

    public interface CreateSubTaskDialogListener {
        void onFinishSubTask(SubTask subTask);
    }

    private EditText subTaskDescription;
    private Button subTaskCreate;
    private Validator stringValidator = new Validator.StringValidatorBuilder()
            .setNotEmpty()
            .setMinLength(3)
            .build();

    public AddSubTaskDialogFragment() {

    }

    public static AddSubTaskDialogFragment newInstance(String description) {
        AddSubTaskDialogFragment fragment = new AddSubTaskDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("description", description);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_subtask, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        subTaskDescription = (EditText) view.findViewById(R.id.descriptionSubTaskText);
        subTaskCreate = (Button) view.findViewById(R.id.subTaskCreate);
        String title = getArguments().getString("description", "Enter description");
        getDialog().setTitle(title);

        subTaskCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateSubTaskDialogListener listener = (CreateSubTaskDialogListener) getActivity();
                SubTask subTask = new SubTask();
                if (validate(subTaskDescription)) {
                    subTask.setDescription(subTaskDescription.getText().toString());
                    listener.onFinishSubTask(subTask);
                    dismiss();
                }
            }
        });
    }

    private boolean validate(TextView subTaskDescription) {
        boolean result = stringValidator.validate(subTaskDescription.getText().toString());
        if (!result) {
            subTaskDescription.setError(stringValidator.getLastMessage());
        }
        return result;
    }
}
