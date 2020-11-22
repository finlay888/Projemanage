package uk.co.fyrefly.projemanage.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.dialog_list.view.*
import kotlinx.android.synthetic.main.dialog_list.view.tvTitle
import uk.co.fyrefly.projemanage.R
import uk.co.fyrefly.projemanage.adaptors.LabelColorListAdaptor
import uk.co.fyrefly.projemanage.adaptors.MembersListAdaptor
import uk.co.fyrefly.projemanage.models.User

abstract class MembersListDialog(
    context: Context,
    private var MemberList: ArrayList<User>,
    private val title: String = ""
) : Dialog(context){

    private var adapter : MembersListAdaptor? = null

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
        adapter = MembersListAdaptor(context, MemberList)
        view.rvList.adapter = adapter

        adapter!!.setOnClickListener(object :
            MembersListAdaptor.OnClickListener{
            override fun onClick(position: Int, user: User, action: String) {
                dismiss()
                onItemSelected(user, action)
            }
        })
    }

    protected abstract fun onItemSelected(user: User, action: String)

}