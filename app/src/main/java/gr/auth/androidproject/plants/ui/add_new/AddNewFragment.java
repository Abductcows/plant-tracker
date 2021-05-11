package gr.auth.androidproject.plants.ui.add_new;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import gr.auth.androidproject.plants.R;

public class AddNewFragment extends Fragment {

    private AddNewViewModel addNewViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        addNewViewModel =
                new ViewModelProvider(this).get(AddNewViewModel.class);

        View root = inflater.inflate(R.layout.fragment_add_new, container, false);


        return root;
    }
}