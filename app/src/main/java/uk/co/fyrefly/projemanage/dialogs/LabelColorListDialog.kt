package uk.co.fyrefly.projemanage.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.dialog_list.view.*
import uk.co.fyrefly.projemanage.R
import uk.co.fyrefly.projemanage.adaptors.LabelColorListAdaptor

abstract class LabelColorListDialog(
    context: Context,
    private var list: ArrayList<String>,
    private val title: String = "",
    private var mSelectedColor: String = ""
) : Dialog(context){

    private var adapter : LabelColorListAdaptor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = LayoutInflater.from(context).inflate(
            R.layout.dialog_list, null)

        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setUpRecyclerView(view)
    }

    private fun setUpRecyclerView(view:View){
        view.tvTitle.text = title
        view.rvList.layoutManager = LinearLayoutManager(context)
        adapter = LabelColorListAdaptor(context, list, mSelectedColor)
        view.rvList.adapter = adapter

        adapter!!.onItemClickListener = object : LabelColorListAdaptor.OnItemClickListener{
            override fun onClick(position: Int, color: String) {
                dismiss()
                onItemSelected(color)
            }

        }
    }

    protected abstract fun onItemSelected(color : String)

}