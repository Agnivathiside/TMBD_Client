package com.example.tmbdclient.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.res.Configuration;
import android.os.Bundle;

import com.example.tmbdclient.Models.Movie;
import com.example.tmbdclient.Models.MovieDBResponse;
import com.example.tmbdclient.R;
import com.example.tmbdclient.adapter.MovieAdapter;
import com.example.tmbdclient.services.MovieDataService;
import com.example.tmbdclient.services.RetrofitInstance;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Movie> movies;
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Objects.requireNonNull(getSupportActionBar()).setTitle("TMDB Popular Movies Today");
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitle("TMDB Popular Movies Today");
        myToolbar.setBackgroundColor(getResources().getColor(R.color.purple));
        getPopularMovies();

        swipeRefreshLayout=findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPopularMovies();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void getPopularMovies() {
        MovieDataService movieDataService = RetrofitInstance.getService();
        Call<MovieDBResponse> call= movieDataService.getPopularMovies(this.getString(R.string.api_key));

        call.enqueue(new Callback<MovieDBResponse>() {
            @Override
            public void onResponse(Call<MovieDBResponse> call, Response<MovieDBResponse> response) {
                MovieDBResponse movieDBResponse = response.body();

                if (movieDBResponse != null && movieDBResponse.getMovies() != null) {

                    movies = (ArrayList<Movie>) movieDBResponse.getMovies();
                    showOnRecyclerView();
                }
            }

            @Override
            public void onFailure(Call<MovieDBResponse> call, Throwable t) {

            }
        });
    }

    private void showOnRecyclerView() {
        recyclerView=findViewById(R.id.rvMovies);
        movieAdapter= new MovieAdapter(this,movies);

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {


            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));


        }

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(movieAdapter);
        movieAdapter.notifyDataSetChanged();
    }
}