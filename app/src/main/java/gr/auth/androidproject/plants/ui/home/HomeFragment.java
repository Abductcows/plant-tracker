package gr.auth.androidproject.plants.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import gr.auth.androidproject.plants.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    FloatingActionButton fab;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        Context context = getContext();

        recyclerView = root.findViewById(R.id.recyclerView);

        //Set the layout of the items in the RecyclerView
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        //Set my Adapter for the RecyclerView
        adapter = new RecyclerAdapter(context);
        recyclerView.setAdapter(adapter);




        // setting the floating action button to go to add new page when pressed
        fab = root.findViewById(R.id.floatingActionButton);
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