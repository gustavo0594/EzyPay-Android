package com.ezypayinc.ezypay.presenter.LoginPresenters;

/**
 * Created by gustavoquesada on 11/23/16.
 */

public interface ILoginPresenter {

    void loginMethod(String email, String password);

    void validateUser();

    void onDestroy();
}