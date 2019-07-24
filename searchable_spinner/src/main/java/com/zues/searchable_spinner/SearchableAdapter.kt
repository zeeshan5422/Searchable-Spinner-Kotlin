package com.zues.searchable_spinner

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.RelativeLayout
import android.widget.TextView
import com.zues.searchable_spinner.utils.SpinnerUtils

/* ---  Created by akhtarz on 7/23/2019. ---*/
class SearchableAdapter private constructor() : RecyclerView.Adapter<SearchableAdapter.MyViewable>(), Filterable {

    private lateinit var mFilter: Filter
    private lateinit var mlist: List<Any>
    private lateinit var orginalList: List<Any>
    private var mTextColor = Color.rgb(0, 0, 0)
    private lateinit var context: Context
    private lateinit var searchableSpinner: SearchableSpinner
    private lateinit var onItemOptSelectedListener: OnItemOptSelectedListener

    constructor(
        mlist: List<Any>,
        context: Context,
        searchableSpinner: SearchableSpinner,
        onItemOptSelectedListener: OnItemOptSelectedListener,
        mTextColor: Int
    ) : this() {
        this.mlist = mlist
        this.orginalList = mlist
        mFilter = SearchableFilter(this, this.orginalList)
        this.context = context
        this.searchableSpinner = searchableSpinner
        this.onItemOptSelectedListener = onItemOptSelectedListener
        this.mTextColor = mTextColor
    }

    override fun getFilter(): Filter = mFilter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewable {
        val view = View.inflate(parent.context, R.layout.popup_menu_item, null)
        val lp = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        view.layoutParams = lp
        return MyViewable(view)
    }

    override fun getItemCount(): Int {
        return mlist.size
    }

    override fun onBindViewHolder(holder: MyViewable, position: Int) {
        holder.mTextView.text = mlist.get(position).toString()
        holder.mTextView.setTextColor(Color.BLACK)
        searchableSpinner.mSelectedItem?.let {
            when {
                it.equals(mlist.get(position)) ->
                    holder.mTextView.setTextColor(
                        mTextColor
                    )
            }
        }
    }

    fun setFilteredList(arrayList: List<Any>) {
        this.mlist = arrayList
    }

    inner class MyViewable(parent: View) : RecyclerView.ViewHolder(parent) {
        internal val mTextView: TextView

        init {
            with(parent) {
                mTextView = this.findViewById(R.id.popup_item_title)
                this.setOnClickListener {
                    val item = mlist.get(adapterPosition)
                    onItemOptSelectedListener.onOptSelected(item)
                    notifyDataSetChanged()
                }
            }
            when (searchableSpinner.labelAlignment) {
                SearchableSpinner.LABEL_ALIGNMENT_CENTER -> {
                    val params =
                        RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE)
                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                    params.leftMargin = SpinnerUtils.dpToPx(context = context, dp = 5)
                    mTextView.layoutParams = params
                }
                SearchableSpinner.LABEL_ALIGNMENT_LEFT -> {
                }
                else -> {
                }
            }
        }
    }

    interface OnItemOptSelectedListener {
        fun onOptSelected(position: Any)
    }
}