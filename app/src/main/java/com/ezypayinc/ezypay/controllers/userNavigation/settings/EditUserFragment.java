package com.ezypayinc.ezypay.controllers.userNavigation.settings;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.ezypayinc.ezypay.R;
import com.ezypayinc.ezypay.base.UserSingleton;
import com.ezypayinc.ezypay.connection.ErrorHelper;
import com.ezypayinc.ezypay.controllers.userNavigation.settings.interfaceViews.IEditUserView;
import com.ezypayinc.ezypay.model.User;
import com.ezypayinc.ezypay.presenter.SettingsPresenters.EditUserPresenter;
import com.ezypayinc.ezypay.presenter.SettingsPresenters.IEditUserPresenter;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class EditUserFragment extends Fragment implements IEditUserView, View.OnClickListener{
    private EditText mEdtName, mEdtLastName, mEdtEmail, mEdtPhoneNumber, mEdtPassword;
    private ImageView mProfileImage;
    private View rootView;
    private ProgressDialog mProgressDialog;
    private IEditUserPresenter presenter;

    public EditUserFragment() {
        // Required empty public constructor
    }

    public static EditUserFragment newInstance() {
        EditUserFragment fragment = new EditUserFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_edit_user, container, false);
        presenter = new EditUserPresenter(this);
        initUIComponents();
        getUser();
        setupProgressDialog();
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
        presenter = null;
    }

    private void initUIComponents() {
        mEdtName = (EditText) rootView.findViewById(R.id.name_editText_edit_user_fragment);
        mEdtLastName = (EditText) rootView.findViewById(R.id.lastName_editText_edit_user_fragment);
        mEdtEmail = (EditText) rootView.findViewById(R.id.email_editText_edit_user_fragment);
        mEdtPhoneNumber = (EditText) rootView.findViewById(R.id.phoneNumber_editText_edit_user_fragment);
        mEdtPassword = (EditText) rootView.findViewById(R.id.password_editText_edit_user_fragment);
        mProfileImage = (ImageView) rootView.findViewById(R.id.image_profile_view_edit_user_fragment);
        Button mBtnSave = (Button) rootView.findViewById(R.id.save_button_edit_user_fragment);
        mBtnSave.setOnClickListener(this);
        mEdtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    GoToPasswordFragment();
                }
            }
        });
    }

    private void getUser() {
        User user = UserSingleton.getInstance().getUser();
        mEdtName.setText(user.getName());
        mEdtLastName.setText(user.getLastName());
        mEdtEmail.setText(user.getEmail());
        mEdtPhoneNumber.setText(user.getPhoneNumber());
        getImage(user);
    }

    private void getImage(User user) {
        Picasso.with(getContext()).load(user.getAvatar()).transform(new CropCircleTransformation()).into(mProfileImage);
    }

    private void setupProgressDialog(){
        mProgressDialog = new ProgressDialog(this.getActivity());
        mProgressDialog.setCancelable(false);
    }

    @Override
    public void setErrorMessage(int component, int errorMessage) {
        EditText editText = (EditText) rootView.findViewById(component);
        editText.setError(getString(errorMessage));
        editText.requestFocus();
    }

    @Override
    public void onNetworkError(Object error) {
        ErrorHelper.handleError(error, this.getContext());
    }


    @Override
    public void navigateToSettingsView() {
        super.getActivity().onBackPressed();
    }

    @Override
    public void showProgressDialog() {
        mProgressDialog.show();
        mProgressDialog.setContentView(R.layout.custom_progress_dialog);
    }

    @Override
    public void hideProgressDialog() {
        mProgressDialog.dismiss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_button_edit_user_fragment:
                updateUser();
                break;
            default:
                break;
        }
    }

    private void GoToPasswordFragment() {
        Fragment fragment = EditUserPasswordFragment.newInstance();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().
                replace(R.id.settings_main_container, fragment).
                addToBackStack(null).
                commit();
    }

    private void updateUser() {
        String name = mEdtName.getText().toString();
        String lastName = mEdtLastName.getText().toString();
        String email = mEdtEmail.getText().toString();
        String phoneNumber = mEdtPhoneNumber.getText().toString();
        presenter.updateUser(name, lastName, email, phoneNumber);
    }
}
