package gr.auth.androidproject.plants.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import gr.auth.androidproject.plants.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    FloatingActionButton fab;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // setting the floating action button to go to add new page when pressed
        FloatingActionButton fab = root.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Navigation.findNavController(v).navigate(R.id.action_nav_home_to_nav_add_new);
                    }
                }
        );


        return root;
    }

}