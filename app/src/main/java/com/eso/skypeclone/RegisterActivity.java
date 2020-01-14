package com.eso.skypeclone;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

@SuppressLint("SetTextI18n")
public class RegisterActivity extends AppCompatActivity {

    TextView mTextview1;
    CountryCodePicker mCcp;
    EditText mPhoneText,mCodeText;
    RelativeLayout relativeLayout;
    Button mContinueNextButton;
    String checker = "", phoneNumber = "",mVerificationId;
    FirebaseAuth mAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        initView();
    }

    private void initView() {
        loadingBar = new ProgressDialog(this);
        mTextview1 = findViewById(R.id.textview1);
        mCcp = findViewById(R.id.ccp);
        mPhoneText = findViewById(R.id.phoneText);
        mCcp.registerCarrierNumberEditText(mPhoneText);
        relativeLayout = findViewById(R.id.phoneAuth);
        mCodeText = findViewById(R.id.codeText);
        mContinueNextButton = findViewById(R.id.continueNextButton);
        mContinueNextButton.setOnClickListener(v -> {
            if (mContinueNextButton.getText().equals("Submit") || checker.equals("Code Sent")){
                String verificationCode = mCodeText.getText().toString();
                if (verificationCode.equals(""))
                    Toast.makeText(this, "Please write verification code first", Toast.LENGTH_SHORT).show();
                else {
                    loadingBar.setTitle("Code Verification");
                    loadingBar.setMessage("Please with, while we are verifying your code..");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }else {
                phoneNumber = mCcp.getFullNumberWithPlus();
                if (!phoneNumber.equals("")){
                    loadingBar.setTitle("Phone Number Verification");
                    loadingBar.setMessage("Please with, while we are verifying your phone number");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,
                            60,
                            TimeUnit.SECONDS,
                            this,
                            callbacks);
                }else
                    Toast.makeText(this, "Please write valid phone number", Toast.LENGTH_SHORT).show();
            }
        });
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(RegisterActivity.this, "Invalid Phone Number..", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
                relativeLayout.setVisibility(View.VISIBLE);
                mContinueNextButton.setText("Continue");
                mCodeText.setVisibility(View.GONE);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                mVerificationId = s;
                mResendToken = forceResendingToken;
                relativeLayout.setVisibility(View.GONE);
                checker = "Code Sent";
                mContinueNextButton.setText("Submit");
                mCodeText.setVisibility(View.VISIBLE);
                Toast.makeText(RegisterActivity.this, "Code has been sent, please  check.", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        loadingBar.dismiss();
                        Toast.makeText(this, "Congratulation", Toast.LENGTH_SHORT).show();
                        sendUserToMainActivity();
                    } else {
                       loadingBar.dismiss();
                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendUserToMainActivity() {
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null){
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
    }
}
