package cmpt276.as2.parentapp.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cmpt276.as2.parentapp.R;

/**
 * Adapter for the coin flip history ui, display string to state the result, also display different icons for picker win and loss.
 */
public class TaskMenuAdapter extends RecyclerView.Adapter<TaskMenuAdapter.TaskViewHolder>
{
    private final ArrayList<TaskMenuAdapter.clickObserverViewDetail> observerViewDetail = new ArrayList<>();
    private final ArrayList<TaskMenuAdapter.clickObserverEditTask> observerEditTask = new ArrayList<>();

    private Context context;
    private List<Task> taskList;
    private List<Child> childList;
    private int pick;

    public TaskMenuAdapter(Context context, List<Task> taskList, List<Child> childList)
    {
        this.context = context;
        this.taskList = taskList;
        this.childList = childList;
    }
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new TaskViewHolder(LayoutInflater.from(context).inflate((R.layout.task_sigle_view), parent, false));
    }

    public int getPick()
    {
        return pick;
    }
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position)
    {
        View.OnClickListener listenerViewDetail = view ->
        {
            pick = holder.getAdapterPosition();
            for(TaskMenuAdapter.clickObserverViewDetail obs :observerViewDetail)
            {
                obs.notifyViewDetail();
            }
        };

        holder.nameOfTask.setText(taskList.get(position).getTaskTitle());
        holder.nameOfTask.setOnClickListener(view ->
        {
            pick = holder.getAdapterPosition();
            for(TaskMenuAdapter.clickObserverEditTask obs :observerEditTask)
            {
                obs.notifyClickerEditTask();
            }
        });

        holder.detail.setText("Detail");
        holder.detail.setOnClickListener(listenerViewDetail);

        /**
         * holder.childPhoto.setImageResource();
         * set child photo
         */
        holder.childPhoto.setOnClickListener(listenerViewDetail);

        holder.nameOfChild.setText(taskList.get(position).getChildName());
        holder.nameOfChild.setOnClickListener(listenerViewDetail);
    }

    @Override
    public int getItemCount()
    {
        return taskList.size();
    }

    protected class TaskViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView childPhoto;
        public TextView nameOfTask;
        public TextView nameOfChild;
        public TextView detail;

        public TaskViewHolder(@NonNull View itemView)
        {
            super(itemView);
            childPhoto = itemView.findViewById(R.id.task_view_child_photo);
            nameOfTask = itemView.findViewById(R.id.task_view_task_name);
            nameOfChild = itemView.findViewById(R.id.task_view_child_name);
            detail = itemView.findViewById(R.id.task_view_task_Detail);
        }
    }

    public interface clickObserverViewDetail
    {
        void notifyViewDetail();
    }

    public interface clickObserverEditTask
    {
        void notifyClickerEditTask();
    }

    public void registerViewDetail(TaskMenuAdapter.clickObserverViewDetail obs)
    {
        observerViewDetail.add(obs);
    }

    public void registerEditTask(TaskMenuAdapter.clickObserverEditTask obs)
    {
        observerEditTask.add(obs);
    }
}
