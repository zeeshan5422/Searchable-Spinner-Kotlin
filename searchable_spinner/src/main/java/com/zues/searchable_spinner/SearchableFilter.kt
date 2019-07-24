package com.zues.searchable_spinner

import android.widget.Filter

/* ---  Created by akhtarz on 7/23/2019. ---*/
class SearchableFilter constructor(
    val mAdapter: SearchableAdapter,
    val itemList: List<Any>
) : Filter() {

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        val results = FilterResults()
        var filteredList = itemList

        if (constraint?.length == 0) {
            results.values = itemList
        } else {
            filteredList = itemList.filter {
                it.toString().toLowerCase().contains(constraint.toString().toLowerCase())
            }
            results.values = filteredList
        }
        results.count = filteredList.size
        return results
    }

    override fun publishResults(
        constraint: CharSequence?,
        results: FilterResults?
    ) {
        mAdapter.setFilteredList(results?.values as List<Any>)
        mAdapter.notifyDataSetChanged()
    }
}
