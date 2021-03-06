package com.ezypayinc.ezypay.presenter.LoginPresenters;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ezypayinc.ezypay.R;
import com.ezypayinc.ezypay.base.UserSingleton;
import com.ezypayinc.ezypay.controllers.login.interfaceViews.LoginView;
import com.ezypayinc.ezypay.manager.DeviceTokenManager;
import com.ezypayinc.ezypay.manager.UserManager;
import com.ezypayinc.ezypay.model.LocalToken;
import com.ezypayinc.ezypay.model.User;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;


public class LoginPresenter  implements ILoginPresenter {

    private static final String DEVICE_TOKEN_ERROR = "Error saving token";
    private LoginView loginView;

    public LoginPresenter(LoginView loginView) {
        this.loginView = loginView;
    }

    /*interface methods*/
    @Override
    public void loginMethod(String email, String password) {
        if(validateEmail(email)) {
            if(validatePassword(password)) {
                login(email, password);
            }
        }

    }

    @Override
    public void onDestroy() {
        this.loginView = null;
    }


    /*Actions methods*/
    private boolean validatePassword(String password) {
        if (TextUtils.isEmpty(password) || password.length() < 4) {
            loginView.setPasswordError(R.string.error_invalid_password);
            return false;
        }
        return true;
    }

    private boolean validateEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            loginView.setUsernameError(R.string.error_field_required);
            return false;
        }

        /*if(!email.contains("@")) {
            loginView.setUsernameError(R.string.error_invalid_email);
            return false;
        }*/
        return true;
    }


    private void login(String email, String password) {
        final UserManager manager = new UserManager();
        loginView.showProgressDialog();

        try {
            manager.loginMethod(email, password, null, null, new Response.Listener<JsonElement>() {
                @Override
                public void onResponse(JsonElement response) {
                    User user = manager.parseLoginResponse(response);
                    getUserFromServer(user);
                    loginView.hideProgressDialog();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loginView.hideProgressDialog();
                    loginView.onNetworkError(error);
                }
            });
        } catch (JSONException e) {
            loginView.hideProgressDialog();
            e.printStackTrace();
        }
    }

    @Override
    public void validateFacebookLogin(final User user) {
        loginView.showProgressDialog();
        final UserManager manager = new UserManager();
        try {
            manager.validateCredentials(user, new Response.Listener<JsonElement>() {
                @Override
                public void onResponse(JsonElement response) {
                    loginView.hideProgressDialog();
                    User userFromLogin = manager.parseFacebookLogin(response);
                    if(userFromLogin != null) {
                        getUserFromServer(userFromLogin);
                    } else {
                        loginView.navigateToSignUser();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loginView.hideProgressDialog();
                    loginView.onNetworkError(error);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getUserFromServer(final User user) {
        final UserSingleton userSingleton = UserSingleton.getInstance();
        userSingleton.setUser(user);
        final UserManager manager = new UserManager();
        try {
            manager.getUserByIdFromServer(user.getId(), new Response.Listener<JsonElement>() {
                @Override
                public void onResponse(JsonElement response) {
                    JsonObject object = response.getAsJsonObject();
                    int boss = object.get("boss").getAsInt();
                    User userFromServer = manager.parseUserFromServer(response);
                    userFromServer.setToken(user.getToken());
                    userSingleton.setUser(userFromServer);
                    if(boss > 0) {
                        getUserCommerce(userFromServer, boss);
                    } else {
                        manager.deleteUser();
                        manager.addUser(userFromServer);
                        loginView.hideProgressDialog();
                        if (userFromServer.getUserType() == 1) {
                            loginView.navigateToHome();
                        } else {
                            loginView.navigateToCommerceHome();
                        }
                        registerLocalToken();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loginView.hideProgressDialog();
                    loginView.onNetworkError(error);
                }
            });
        } catch (JSONException e) {
            loginView.hideProgressDialog();
            e.printStackTrace();
        }
    }

    private void getUserCommerce(final User user, int boss) {
        final UserManager manager = new UserManager();
        final UserSingleton userSingleton = UserSingleton.getInstance();
        try {
            manager.getUserByIdFromServer(boss, new Response.Listener<JsonElement>() {
                @Override
                public void onResponse(JsonElement response) {
                    User commerce = manager.parseUserFromServer(response);
                    user.setEmployeeBoss(commerce);
                    userSingleton.setUser(user);
                    manager.deleteUser();
                    manager.addUser(user);
                    loginView.hideProgressDialog();
                    if(user.getUserType() == 1) {
                        loginView.navigateToHome();
                    } else {
                        loginView.navigateToCommerceHome();
                    }
                    registerLocalToken();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void registerLocalToken() {
        final DeviceTokenManager manager = new DeviceTokenManager();
        LocalToken tokenSaved = new LocalToken(manager.getLocalToken());
        if (tokenSaved.getDeviceToken() != null && !tokenSaved.isSaved()) {
            final LocalToken localToken = new LocalToken(tokenSaved);
            localToken.setUserId(UserSingleton.getInstance().getUser().getId());
            try {
                manager.registerDeviceToken(localToken, UserSingleton.getInstance().getUser().getToken(), new Response.Listener<JsonElement>() {
                    @Override
                    public void onResponse(JsonElement response) {
                        localToken.setSaved(true);
                        manager.updateLocalToken(localToken);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(DEVICE_TOKEN_ERROR, error.getMessage());
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
