package pt.ulisboa.tecnico.cmu.ubibike.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmu.ubibike.R;
import pt.ulisboa.tecnico.cmu.ubibike.UbiBike;


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

    private void setViewElements(View view){

        EditText email;
        EditText password;
        Button signIn;
        TextView signUp;

        email = (EditText) view.findViewById(R.id.email_editText);
        password = (EditText) view.findViewById(R.id.password_editText);
        signIn = (Button) view.findViewById(R.id.sign_in_button);
        signUp = (TextView) view.findViewById(R.id.sign_up_textView);


        email.setHintTextColor(getResources().getColor(R.color.white));
        password.setHintTextColor(getResources().getColor(R.color.white));


        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "test", Toast.LENGTH_SHORT).show();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentActivity().showRegisterAccountFragment();
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getParentActivity().showToolbar(false);
    }

}
