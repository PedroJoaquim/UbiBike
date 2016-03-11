package pt.ulisboa.tecnico.cmu.ubibike.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import pt.ulisboa.tecnico.cmu.ubibike.R;


public class RegisterAccountFragment extends Fragment {

    private EditText mPassword;
    private boolean mRightPassword = false;

    public RegisterAccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_account_fragment, container, false);

        setViewElements(view);

        return view;
    }

    private void setViewElements(View view){

        EditText name;
        EditText email;
        EditText reenteredPassword;
        final ImageView rightPasswordImage;
        Button createAccount;

        name = (EditText) view.findViewById(R.id.name_editText);
        email = (EditText) view.findViewById(R.id.email_editText);
        mPassword = (EditText) view.findViewById(R.id.password_editText);
        reenteredPassword = (EditText) view.findViewById(R.id.reentered_password_editText);
        rightPasswordImage = (ImageView) view.findViewById(R.id.right_password_imageView);
        createAccount = (Button) view.findViewById(R.id.create_account_Button);

        name.setHintTextColor(getResources().getColor(R.color.white));
        email.setHintTextColor(getResources().getColor(R.color.white));
        mPassword.setHintTextColor(getResources().getColor(R.color.white));
        reenteredPassword.setHintTextColor(getResources().getColor(R.color.white));

        reenteredPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //empty on purpose
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.length() == 0){
                    rightPasswordImage.setVisibility(View.INVISIBLE);
                }
                else{

                    rightPasswordImage.setVisibility(View.VISIBLE);

                    if(!mPassword.getText().toString().equals(s.toString())){
                        rightPasswordImage.setImageResource(R.drawable.wrong);
                    }
                    else{
                        rightPasswordImage.setImageResource(R.drawable.check);
                        mRightPassword = true;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //empty on purpose
            }
        });


        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO action
            }
        });
    }


}
