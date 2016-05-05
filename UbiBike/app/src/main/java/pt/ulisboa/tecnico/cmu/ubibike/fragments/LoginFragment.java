package pt.ulisboa.tecnico.cmu.ubibike.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmu.ubibike.ApplicationContext;
import pt.ulisboa.tecnico.cmu.ubibike.R;
import pt.ulisboa.tecnico.cmu.ubibike.UbiBike;
import pt.ulisboa.tecnico.cmu.ubibike.domain.Data;
import pt.ulisboa.tecnico.cmu.ubibike.managers.MobileConnectionManager;
import pt.ulisboa.tecnico.cmu.ubibike.utils.Validator;


public class LoginFragment extends Fragment {

    public LoginFragment() {
        // Required empty public constructor
    }

    private UbiBike getParentActivity(){
        return (UbiBike) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);

        setViewElements(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        getParentActivity().getSupportActionBar().hide();
    }

    private void setViewElements(View view){

        final EditText username;
        final EditText password;
        Button signIn;
        TextView signUp;

        username = (EditText) view.findViewById(R.id.username_editText);
        password = (EditText) view.findViewById(R.id.password_editText);
        signIn = (Button) view.findViewById(R.id.sign_in_button);
        signUp = (TextView) view.findViewById(R.id.sign_up_textView);


        int alpha = (int)(0.5 * 255.0f);
        int color = Color.argb(alpha, 255, 255, 255);   //white color

        username.setHintTextColor(color);
        password.setHintTextColor(color);

        signIn.getBackground().setAlpha(220);


        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String usrName = username.getText().toString();
                String pssWd = password.getText().toString();

                if (!Validator.isUsernameValid(usrName)) {
                    Toast.makeText(getActivity(), "Username should be..." /*TODO msg*/, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!Validator.isPasswordValid(pssWd)) {
                    Toast.makeText(getActivity(), "Password should be..." /*TODO msg*/, Toast.LENGTH_SHORT).show();
                    return;
                }

                ApplicationContext.getInstance().setPassword(pssWd);

                if (MobileConnectionManager.isOnline(getActivity())) {

                    ApplicationContext.getInstance().getServerCommunicationHandler().
                            performLoginRequest(usrName, pssWd);
                } else {

                    Boolean passwordValid = ApplicationContext.getInstance().getStorageManager().
                            checkIsExpectedPassword(usrName, pssWd);

                    if (passwordValid == null) {
                        Toast.makeText(getActivity(), "No login record found for current user", Toast.LENGTH_SHORT).show();
                    } else if (passwordValid) {

                        int clientID = ApplicationContext.getInstance().getStorageManager().
                                getClientIDGivenUsernameFromDB(usrName);


                        Data appData = ApplicationContext.getInstance().getStorageManager().
                                                                        getAppDataFromDB(clientID);

                        ApplicationContext.getInstance().getServerCommunicationHandler().
                                                                            setUid(appData.getUID());
                        ApplicationContext.getInstance().getServerCommunicationHandler().
                                                            setSessionToken(appData.getSessionToken());

                        ApplicationContext.getInstance().setData(appData);

                        getParentActivity().finishLogin();


                    } else {
                        Toast.makeText(getActivity(), "Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                }



            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentActivity().showRegisterAccountFragment();
            }
        });
    }

}
