package com.ezypayinc.ezypay.presenter.SettingsPresenters;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ezypayinc.ezypay.base.UserSingleton;
import com.ezypayinc.ezypay.controllers.userNavigation.settings.interfaceViews.ISettingsView;
import com.ezypayinc.ezypay.manager.DeviceTokenManager;
import com.ezypayinc.ezypay.manager.UserManager;
import com.ezypayinc.ezypay.model.LocalToken;
import com.ezypayinc.ezypay.model.User;
import com.google.gson.JsonElement;

import org.json.JSONException;

/**
 * Created by gustavoquesada on 1/21/18.
 */

public class SettingsPresenter implements ISettingsPresenter {

    private ISettingsView mView;

    public SettingsPresenter(ISettingsView view) {
        mView = view;
    }

    @Override
    public void logOutAction() {
        DeviceTokenManager manager = new DeviceTokenManager();
        final UserManager userManager = new UserManager();
        final LocalToken token = manager.getLocalToken();
        User currentUser = UserSingleton.getInstance().getUser();
        if(token == null) {
            userManager.deleteUser();
            updateDeviceToken(token);
            UserSingleton.getInstance().setUser(null);
            mView.logOutAction();
            return;
        }
        try {
            manager.deleteDeviceToken(token.getDeviceId(), currentUser, new Response.Listener<JsonElement>() {
                @Override
                public void onResponse(JsonElement response) {
                    userManager.deleteUser();
                    updateDeviceToken(token);
                    UserSingleton.getInstance().setUser(null);
                    mView.logOutAction();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    userManager.deleteUser();
                    updateDeviceToken(token);
                    UserSingleton.getInstance().setUser(null);
                    mView.logOutAction();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateDeviceToken(LocalToken token) {
        DeviceTokenManager manager = new DeviceTokenManager();
        LocalToken localToken = new LocalToken(token);
        localToken.setUserId(0);
        localToken.setSaved(false);
        manager.updateLocalToken(localToken);
    }
}
