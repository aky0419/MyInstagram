package com.example.instagramclone.Dialogs;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.instagramclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ConfirmPasswordDialog extends DialogFragment {
    private static final String TAG = "ConfirmPasswordDialog";
    private TextView cancelDialog, confirmDialog;
    private EditText mPassword;

    public interface OnConfirmPasswordListener{
        public void onConfirmPassword(String password);



    }

    OnConfirmPasswordListener OnConfirmPasswordListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_confirm_password, container, false);
        mPassword = view.findViewById(R.id.confirm_password);
        Log.d(TAG, "onCreateView: started.");
        cancelDialog = view.findViewById(R.id.dialogCancel);
        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the dialog.");
                getDialog().dismiss();
            }
        });

        confirmDialog = view.findViewById(R.id.dialogConfirm);
        confirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: captured password and confirming");

                String password = mPassword.getText().toString();
                if (!password.equals("")) {
                    OnConfirmPasswordListener.onConfirmPassword(password);
                    getDialog().dismiss();
                }else {
                    Toast.makeText(getActivity(),"you must enter a password", Toast.LENGTH_SHORT).show();
                }


                
            }
        });



        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            OnConfirmPasswordListener = (OnConfirmPasswordListener) getTargetFragment();
        }catch (ClassCastException e) {
            Log.d(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
    }
}
