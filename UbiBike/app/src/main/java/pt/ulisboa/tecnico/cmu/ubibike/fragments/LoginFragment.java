package pt.ulisboa.tecnico.cmu.ubibike.fragments;


import android.media.Image;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import pt.ulisboa.tecnico.cmu.ubibike.R;


public class LoginFragment extends Fragment {

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        setViewElements(view);

        return view;
    }

    private void setViewElements(View view){

        EditText mName;
        EditText mEmail;
        final EditText mPassword;
        EditText mReenteredPassword;
        final ImageView mRightPasswordImage;

        final boolean mRightPassword = false;


        mName = (EditText) view.findViewById(R.id.name_editText);
        mEmail = (EditText) view.findViewById(R.id.email_editText);
        mPassword = (EditText) view.findViewById(R.id.password_editText);
        mReenteredPassword = (EditText) view.findViewById(R.id.reentered_password_editText);
        mRightPasswordImage = (ImageView) view.findViewById(R.id.right_password_imageView);


        mReenteredPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //empty on purpose
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.length() == 0){
                    mRightPasswordImage.setVisibility(View.INVISIBLE);
                }
                else{

                    mRightPasswordImage.setVisibility(View.VISIBLE);

                    if(!mPassword.equals(s)){
                        mRightPasswordImage.setImageResource(R.drawable.wrong);
                    }
                    else{
                        mRightPasswordImage.setImageResource(R.drawable.check);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //empty on purpose
            }
        });

    }


}
