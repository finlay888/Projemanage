package uk.co.fyrefly.projemanage.adaptors

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_task.view.*
import uk.co.fyrefly.projemanage.R
import uk.co.fyrefly.projemanage.activities.TaskListActivity
import uk.co.fyrefly.projemanage.models.Task
import java.util.*
import kotlin.collections.ArrayList

open class TaskListAdaptor (private val context : Context, private var list : ArrayList<Task>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var mPositionDraggedFrom = -1
    private var mPositionDraggedTo = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_task, parent, false)
        val layoutParams = LinearLayout.LayoutParams(
            (parent.width * 0.7).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins((15.toDp()).toPx(), 0 , (40.toDp()).toPx(), 0)
        view.layoutParams = layoutParams

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){
            if(position == list.size - 1){
                holder.itemView.tv_add_task_list.visibility = View.VISIBLE
                holder.itemView.ll_task_item.visibility = View.GONE
            }else{
                holder.itemView.tv_add_task_list.visibility = View.GONE
                holder.itemView.ll_task_item.visibility = View.VISIBLE
            }

            holder.itemView.tv_task_list_title.text = model.title
            holder.itemView.tv_add_task_list.setOnClickListener {
                holder.itemView.tv_add_task_list.visibility = View.GONE
                holder.itemView.cv_add_task_list_name.visibility = View.VISIBLE
            }

            holder.itemView.ib_close_list_name.setOnClickListener {
                holder.itemView.tv_add_task_list.visibility = View.VISIBLE
                holder.itemView.cv_add_task_list_name.visibility = View.GONE
            }

            holder.itemView.ib_done_list_name.setOnClickListener {
                val listName = holder.itemView.et_task_list_name.text.toString()

                if(listName.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.createTaskList(listName)
                    }
                }else{
                    Toast.makeText(context, "Please Enter List Name.",
                        Toast.LENGTH_SHORT).show()
                }
            }

            holder.itemView.ib_edit_list_name.setOnClickListener {
                holder.itemView.et_edit_task_list_name.setText(model.title)
                holder.itemView.ll_title_view.visibility = View.GONE
                holder.itemView.cv_edit_task_list_name.visibility = View.VISIBLE
            }

            holder.itemView.ib_close_editable_view.setOnClickListener {
                holder.itemView.ll_title_view.visibility = View.VISIBLE
                holder.itemView.cv_edit_task_list_name.visibility = View.GONE
            }

            holder.itemView.ib_done_edit_list_name.setOnClickListener {
                val listName = holder.itemView.et_edit_task_list_name.text.toString()

                if(listName.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.updateTaskList(position, listName, model)
                    }
                }else{
                    Toast.makeText(context, "Please Enter List Name.",
                        Toast.LENGTH_SHORT).show()
                }
            }

            holder.itemView.ib_delete_list.setOnClickListener {
                alertDialogForDeleteList(position, model.title)
            }

            holder.itemView.tv_add_card.setOnClickListener {
                holder.itemView.tv_add_card.visibility = View.GONE
                holder.itemView.cv_add_card.visibility = View.VISIBLE
            }

            holder.itemView.ib_close_card_name.setOnClickListener {
                holder.itemView.tv_add_card.visibility = View.VISIBLE
                holder.itemView.cv_add_card.visibility = View.GONE
            }

            holder.itemView.ib_done_card_name.setOnClickListener {
                val cardName = holder.itemView.et_card_name.text.toString()

                if (cardName.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.addCardToList(position, cardName)
                    }
                }else{
                    Toast.makeText(
                        context,
                        "Please enter a card name",
                        Toast.LENGTH_SHORT).show()
                }
            }

            holder.itemView.rv_card_list.layoutManager =
                LinearLayoutManager(context)
            holder.itemView.rv_card_list.setHasFixedSize(true)

            val adaptor = CardListAdaptor(context, model.cards)
            holder.itemView.rv_card_list.adapter = adaptor

            adaptor.setOnClickListener(
                object : CardListAdaptor.OnClickListener{
                    override fun onClick(cardPosition: Int) {
                        if(context is TaskListActivity){
                            context.cardDetails(position, cardPosition)
                        }
                    }
                }
            )

            val dividerItemDecoration = DividerItemDecoration(context,
                DividerItemDecoration.VERTICAL)
            holder.itemView.rv_card_list.addItemDecoration(dividerItemDecoration)
            val helper = ItemTouchHelper(
                object : ItemTouchHelper.SimpleCallback(
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
                ){
                    override fun onMove(
                        recyclerView: RecyclerView,
                        dragged: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean {
                        val draggedPosition = dragged.adapterPosition
                        val targetPosition = target.adapterPosition

                        if(mPositionDraggedFrom == -1){
                            mPositionDraggedFrom == draggedPosition
                        }
                        mPositionDraggedTo = targetPosition
                        Collections.swap(list[position].cards,
                            draggedPosition, targetPosition)
                        adaptor.notifyItemMoved(draggedPosition,targetPosition)
                        return false
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    }

                    override fun clearView(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder
                    ) {
                        super.clearView(recyclerView, viewHolder)
                        if(mPositionDraggedFrom != -1 &&
                            mPositionDraggedTo != -1 &&
                            mPositionDraggedFrom != mPositionDraggedTo){
                            (context as TaskListActivity).updateCardsInTaskList(
                                position,
                                list[position].cards
                            )
                        }
                        mPositionDraggedFrom = -1
                        mPositionDraggedTo = -1
                    }

                }
            )
            helper.attachToRecyclerView(holder.itemView.rv_card_list)
        }
    }

    private fun alertDialogForDeleteList(position: Int, title: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Alert")
        builder.setMessage("Are you sure you want to delete $title?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            dialogInterface.dismiss()

            if(context is TaskListActivity) {
                context.deleteTaskList(position)
            }
        }
        builder.setNegativeButton("No") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun Int.toDp(): Int =
        (this / Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()

    class MyViewHolder(view:View): RecyclerView.ViewHolder(view)
}