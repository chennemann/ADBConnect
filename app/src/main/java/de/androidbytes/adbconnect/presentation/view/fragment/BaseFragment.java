package de.androidbytes.adbconnect.presentation.view.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import de.androidbytes.adbconnect.presentation.di.HasComponent;


/**
 * Created by Christoph on 17.09.2015.
 */
public class BaseFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    /**
     * Gets a component for dependency injection by its type.
     */
    @SuppressWarnings("unchecked")
    protected <C> C getComponent(Class<C> componentType) {
        return componentType.cast(((HasComponent<C>)getActivity()).getComponent());
    }
}
