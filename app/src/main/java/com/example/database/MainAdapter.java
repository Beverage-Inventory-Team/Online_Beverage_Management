package com.example.database;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;



public class MainAdapter extends FirestoreRecyclerAdapter<MainData, MainAdapter.NoteHolder> {

    private OnItemClickListener listener;
    public MainAdapter(@NonNull FirestoreRecyclerOptions<MainData> options) {
        super(options);
    }
    @Override
    protected void onBindViewHolder(@NonNull NoteHolder holder, int position, @NonNull MainData model) {

        holder.textView.setText(model.getText());
        holder.NameView.setText(model.getName());
        holder.StyleView.setText(model.getStyle());
        holder.VolumeView.setText(String.valueOf(model.getVolume()));
        holder.BrewedView.setText(model.getBrewed());
        holder.BestView.setText(model.getExpdate());
        holder.BreweryView.setText(model.getBrewery());

        //holder.BreweryView.setText("watrer");
        Picasso.get().load(model.getBest()).into(holder.IView);

    }
    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_main,
                parent, false);
        return new NoteHolder(v);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }
    class NoteHolder extends RecyclerView.ViewHolder {

        TextView textView, NameView,StyleView,VolumeView,BrewedView,BestView,BreweryView;
        ImageView IView, btDelete;
        public NoteHolder(View itemView) {
            super(itemView);


            textView = itemView.findViewById(R.id.text_view);
            NameView = itemView.findViewById(R.id.name_view);
            StyleView = itemView.findViewById(R.id.style_view);
            VolumeView = itemView.findViewById(R.id.volume_view);
            BrewedView = itemView.findViewById(R.id.brewed_view);
            BestView = itemView.findViewById(R.id.best_view);
            BreweryView = itemView.findViewById(R.id.brewery_view);
            //btDelete = itemView.findViewById(R.id.bt_delete);
            IView = itemView.findViewById(R.id.image_taken);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }
    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
