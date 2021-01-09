package com.example.noteapp.adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.noteapp.R;
import com.example.noteapp.entities.Note;
import com.example.noteapp.listeners.NotesListeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.LogRecord;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private List <Note> notes;
    private NotesListeners notesListeners;
    private Timer timer;
    private List<Note> notesSource;

    public NotesAdapter(List<Note> notes, NotesListeners notesListeners) {
        this.notes = notes;
        this.notesListeners = notesListeners;
        notesSource=notes;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_note , parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.setNote(notes.get(position));
        holder.layoutnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               notesListeners.onNoteClicked(notes.get(position), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder{

        TextView textTitle , textSubtitle , textDate;
        LinearLayout layoutnote;

        NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textSubtitle = itemView.findViewById(R.id.textSubTitle);
            textDate = itemView.findViewById(R.id.textDate);
            layoutnote = itemView.findViewById(R.id.layoutNote);
        }

        void setNote(Note note){
            textTitle.setText(note.getTitle());
            if(note.getSubtitle().trim().isEmpty()){
                textSubtitle.setVisibility(View.GONE);
            }

            else {
                textSubtitle.setText(note.getSubtitle());
            }

            textDate.setText(note.getDateTime());
            GradientDrawable gradientDrawable=(GradientDrawable) layoutnote.getBackground();
            if (note.getColor() != null){
                gradientDrawable.setColor(Color.parseColor(note.getColor()));
            }
            else{
                gradientDrawable.setColor(Color.parseColor("#333333"));
            }


        }

    }

    public void searchNotes(final String keyword){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (keyword.trim().isEmpty()){
                    notes= notesSource;
                }
                else {
                    ArrayList<Note> temp = new ArrayList<>();
                    for (Note note: notesSource){
                        if(note.getTitle().toLowerCase().contains(keyword.toLowerCase())
                                || note.getSubtitle().toLowerCase().contains(keyword.toLowerCase())
                                || note.getNoteText().toLowerCase().contains(keyword.toLowerCase())){
                            temp.add((note));
                        }
                    }
                    notes= temp;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });


                }
        },500);
    }

    public void cancelTimer (){
        if (timer!= null){
            timer.cancel();
        }
    }

}
