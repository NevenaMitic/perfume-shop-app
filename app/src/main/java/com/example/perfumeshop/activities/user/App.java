package com.example.perfumeshop.activities.user;

import android.app.Application;

import com.paypal.checkout.PayPalCheckout;
import com.paypal.checkout.config.CheckoutConfig;
import com.paypal.checkout.config.Environment;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.UserAction;


public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //Connect paypal from site to the app
        PayPalCheckout.setConfig(new CheckoutConfig(
                this,
                "ATtkD3FxDqYtL8UdRpSziaSclert-_0ZF8LUCcmAsyDgNhwLOtd3EUvRiwkscURCRByJNZncDgi5wNDj",
                Environment.SANDBOX,
                CurrencyCode.EUR,
                UserAction.PAY_NOW,
                "com.paypaltest://paypalpay"
        ));
    }
}
