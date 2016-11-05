package com.example.marc.codingproject.childrensblockpuzzle;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.marc.codingproject.childrensblockpuzzle.db.DatabaseHelper;
import com.example.marc.codingproject.childrensblockpuzzle.db.Model;

import java.util.List;

import static java.lang.String.format;

public class StatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        TextView totalComplete = (TextView) findViewById(R.id.text_total_puzzles);
        TextView numberTouches = (TextView) findViewById(R.id.text_total_touches);
        TextView avgTouches = (TextView) findViewById(R.id.text_average_touches);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        assert recyclerView!= null;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        DatabaseHelper database = new DatabaseHelper(this);
        List<Model> models = database.getAll();
        recyclerView.setAdapter(new CustomAdapter(this, models));

        int totalPuzzlesCompleted = models.size();
        int totalTouches = 0;
        for (Model model : models) {
            totalTouches += model.touches;
        }

        if (totalComplete != null) {
            totalComplete.setText(format("%d", totalPuzzlesCompleted));
        }
        if (numberTouches != null) {
            numberTouches.setText(format("%d", totalTouches));
        }
        if (totalPuzzlesCompleted > 0) {
            if (avgTouches != null) {
                avgTouches.setText(format("%d", totalTouches / totalPuzzlesCompleted));
            }
        }
    }

    private class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final static int HEADER_TYPE = 0;
        private final static int CONTENT_TYPE = 1;

        private final Context context;
        private final List<Model> models;

        public CustomAdapter(Context context, List<Model> models) {
            this.context = context;
            this.models = models;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == HEADER_TYPE) {
                return new HeaderViewHolder(LayoutInflater.from(context).inflate(R.layout.item_model_report_header, parent, false));
            }
            return new ContentViewHolder(LayoutInflater.from(context).inflate(R.layout.item_model_report, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (position != 0) {
                Model model = models.get(position - 1);
                ((ContentViewHolder) holder).bindView(position, model);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return HEADER_TYPE;
            }
            return CONTENT_TYPE;
        }

        @Override
        public int getItemCount() {
            return models.size() + 1;
        }

        private class HeaderViewHolder extends RecyclerView.ViewHolder {

            public HeaderViewHolder(View view) {
                super(view);
            }
        }

        private class ContentViewHolder extends RecyclerView.ViewHolder {
            private final TextView textId;
            private final ImageView imageDescription;
            private final TextView textCompletedDate;
            private final TextView textTouches;

            public ContentViewHolder(View view) {
                super(view);
                textId = (TextView) view.findViewById(R.id.text_id);
                imageDescription = (ImageView) view.findViewById(R.id.image_view);
                textCompletedDate = (TextView) view.findViewById(R.id.text_completed_date);
                textTouches = (TextView) view.findViewById(R.id.text_touches);
            }

            public void bindView(int position, Model model) {
                textId.setText(position + "");
                Glide.with(context).load(model.bitmap).into(imageDescription);
                textCompletedDate.setText(model.updateDate);
                textTouches.setText(model.touches + "");
            }
        }
    }
}
