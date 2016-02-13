package com.nullaxis.tos.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nullaxis.tos.R;

public class ContainerFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_container, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        String className = args.getString("className");
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        try {
            transaction.add(R.id.mainFragmentContainer,
                    (Fragment) Class.forName(className).newInstance(), "mainFragment");
        }catch (ClassNotFoundException e){
            Log.e("ClassContainer", e.toString());
        }catch (java.lang.InstantiationException e){
            Log.e("ClassContainer", e.toString());
        }catch (IllegalAccessException e){
            Log.e("ClassContainer", e.toString());
        }
        transaction.commit();
    }

    public void addFragment(Fragment newFragment){
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(R.anim.fragment_bottom_enter, R.anim.fragment_bottom_exit);
        transaction.replace(R.id.extraFragmentContainer, newFragment, "extraFragment");
        transaction.commit();
        getView().findViewById(R.id.extraFragmentContainer).setVisibility(View.VISIBLE);
    }

    public void hideExtraFragment(){
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.remove(fm.findFragmentByTag("extraFragment"));
        transaction.commit();
        getView().findViewById(R.id.extraFragmentContainer).setVisibility(View.GONE);
    }
}
