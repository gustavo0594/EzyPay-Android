package com.ezypayinc.ezypay.controllers.userNavigation.payment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ezypayinc.ezypay.R;
import com.ezypayinc.ezypay.base.UserSingleton;
import com.ezypayinc.ezypay.controllers.userNavigation.navigation.MainUserActivity;
import com.ezypayinc.ezypay.controllers.userNavigation.payment.Adapters.PaymentAdapter;
import com.ezypayinc.ezypay.model.Payment;

public class PaymentFragment extends Fragment  {

    private RecyclerView paymentRecyclerView;
    private Payment mPayment;


    public PaymentFragment() {
        // Required empty public constructor
    }

    public static PaymentFragment newInstance(Payment payment) {
        PaymentFragment fragment = new PaymentFragment();
        Bundle args = new Bundle();
        args.putParcelable(PaymentMainActivity.PAYMENT_KEY, payment);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            mPayment = getArguments().getParcelable(PaymentMainActivity.PAYMENT_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_payment, container, false);
        paymentRecyclerView = (RecyclerView) rootView.findViewById(R.id.payment_fragment_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        paymentRecyclerView.setLayoutManager(mLayoutManager);
        paymentRecyclerView.setItemAnimator(new DefaultItemAnimator());
        paymentRecyclerView.setAdapter(new PaymentAdapter(mPayment, UserSingleton.getInstance().getUser(),getContext()));
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getActivity().setTitle(R.string.payment_view_title);
    }
}
