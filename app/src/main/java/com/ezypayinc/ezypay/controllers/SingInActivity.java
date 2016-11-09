package com.ezypayinc.ezypay.controllers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ezypayinc.ezypay.R;


public class SingInActivity extends AppCompatActivity implements SignInUserInformationFragment.OnChangeViewListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_in);
        if(savedInstanceState == null) {
            SignInUserInformationFragment userInformationFragment = SignInUserInformationFragment.newInstance();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.add(R.id.sign_in_container_view, userInformationFragment);
            fragmentTransaction.commit();
        }

    }

    @Override
    public void changeView(Fragment newFragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.sign_in_container_view, newFragment);
        fragmentTransaction.commit();
    }
}
