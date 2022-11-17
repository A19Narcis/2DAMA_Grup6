package com.example.projecte_2dam_grup6.ui.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.projecte_2dam_grup6.PantallaPrincipal;
import com.example.projecte_2dam_grup6.Producte;
import com.example.projecte_2dam_grup6.R;
import com.example.projecte_2dam_grup6.databinding.FragmentHomeBinding;
import com.example.projecte_2dam_grup6.ui.Adapter.MyAdapter;
import com.example.projecte_2dam_grup6.ui.viewModel.MyViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class LikesFragment extends Fragment {

    private FragmentHomeBinding binding;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private List<Producte> listItems;

    private static String url_data;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MyViewModel viewModel = new ViewModelProvider(this).get(MyViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        PantallaPrincipal pantallaPrincipal = (PantallaPrincipal) getActivity();

        url_data = pantallaPrincipal.getServerPathFragment() + pantallaPrincipal.getDadesMeGustaJSONPathFragment() + "/" + pantallaPrincipal.getIdUserLoggedIn();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        listItems = new ArrayList<>();

        loadRecycleViewData();

    }

    private void loadRecycleViewData() {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading productes");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_data, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        String categoria;
                        JSONObject o = jsonArray.getJSONObject(i);
                        Producte item = new Producte(
                                o.getString("nom"),
                                o.getString("user"),
                                o.getString("path"),
                                o.getString("preu"),
                                o.getString("id_producte")
                        );
                        listItems.add(item);
                    }

                    adapter = new MyAdapter(listItems, getContext());

                    recyclerView.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}