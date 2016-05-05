package pt.ulisboa.tecnico.cmu.ubibike.fragments;


import android.graphics.Color;
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
import android.widget.Toast;

import pt.ulisboa.tecnico.cmu.ubibike.ApplicationContext;
import pt.ulisboa.tecnico.cmu.ubibike.R;
import pt.ulisboa.tecnico.cmu.ubibike.UbiBike;
import pt.ulisboa.tecnico.cmu.ubibike.utils.Validator;


public class RegisterAccountFragment extends Fragment {

    private EditText mPassword;
    private boolean mRightPassword = false;

    public RegisterAccountFragment() {
        // Required empty public constructor
    }

    private UbiBike getParentActivity(){
        return (UbiBike) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_account_fragment, container, false);

        setViewElements(view);

        return view;
    }

    private void setViewElements(View view){

        final EditText username;
        EditText reenteredPassword;
        final ImageView rightPasswordImage;
        Button createAccount;

        username = (EditText) view.findViewById(R.id.username_editText);
        mPassword = (EditText) view.findViewById(R.id.password_editText);
        reenteredPassword = (EditText) view.findViewById(R.id.reentered_password_editText);
        rightPasswordImage = (ImageView) view.findViewById(R.id.right_password_imageView);
        createAccount = (Button) view.findViewById(R.id.create_account_Button);

        int alpha = (int)(0.5 * 255.0f);
        int color = Color.argb(alpha, 255, 255, 255);   //white color

        username.setHintTextColor(color);
        mPassword.setHintTextColor(color);
        reenteredPassword.setHintTextColor(color);

        createAccount.getBackground().setAlpha(220);

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

                if(!mRightPassword){
                    return;
                }

                String usrName = username.getText().toString();
                String pssWd = mPassword.getText().toString();

                if(!Validator.isUsernameValid(usrName)){
                    Toast.makeText(getActivity(), "Username should be..." /*TODO msg*/, Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!Validator.isPasswordValid(pssWd)){
                    Toast.makeText(getActivity(), "Password should be..." /*TODO msg*/, Toast.LENGTH_SHORT).show();
                    return;
                }

                ApplicationContext.getInstance().setPassword(pssWd);

                ApplicationContext.getInstance().getServerCommunicationHandler().
                        performRegisterRequest(usrName, pssWd);

            }
        });
    }
}
