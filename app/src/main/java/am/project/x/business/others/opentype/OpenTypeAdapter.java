package am.project.x.business.others.opentype;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

class OpenTypeAdapter extends RecyclerView.Adapter<OpenTypeViewHolder> {

    @NonNull
    @Override
    public OpenTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        return new OpenTypeViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull OpenTypeViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 20;
    }
}
