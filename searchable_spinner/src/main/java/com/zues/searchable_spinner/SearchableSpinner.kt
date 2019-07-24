package com.zues.searchable_spinner

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.StateListDrawable
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.*
import com.zues.searchable_spinner.utils.SpinnerUtils

/* ---  Created by akhtarz on 7/22/2019. ---*/
class SearchableSpinner : RelativeLayout {

    private var mLabelView: TextView? = null
    private var dropdown: View? = null

    private var itemList = listOf<Any>()

    private var mItemSelectListener: SearchableItemListener? = null
    private var popupWindow: PopupWindow? = null
    private var myItemsAdapter: SearchableAdapter? = null
    private var isSort = false
    private var showSearch: Boolean = false
    var labelAlignment = LABEL_ALIGNMENT_CENTER
    var originalList = listOf<Any>()
    var mSelectedItem: Any? = null
        get() = field
        set(value) {
            field = value
        }

    private var mTextColor = Color.rgb(0, 0, 0)
    private var mSelectedTextColor = Color.rgb(0, 0, 0)
    private var textSize = 50.0f
    private var mHint: String? = "Please Select"
    var isDisabled = false

    companion object {
        const val POPUP_DISMISS_DELAY = 200L
        const val LABEL_ALIGNMENT_LEFT = 1
        const val LABEL_ALIGNMENT_CENTER = 0
    }

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initAttrbs(attrs)
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initAttrbs(attrs)
        init()
    }

    private fun initAttrbs(attrs: AttributeSet?) {
        val a: TypedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.SearchableSpinner,
            0, 0
        )
        try {
            isSort = a.getBoolean(R.styleable.SearchableSpinner_sort , true)
            showSearch = a.getBoolean(R.styleable.SearchableSpinner_showSearch, false)
            mTextColor = a.getColor(R.styleable.SearchableSpinner_label_color, mTextColor)
            mSelectedTextColor = a.getColor(R.styleable.SearchableSpinner_selected_item_color, mSelectedTextColor)
            textSize = a.getDimension(R.styleable.SearchableSpinner_label_text_size, textSize)
            mHint = a.getString(R.styleable.SearchableSpinner_label_hint)
            labelAlignment = a.getInt(R.styleable.SearchableSpinner_label_alignment, LABEL_ALIGNMENT_CENTER)
            isDisabled = a.getBoolean(R.styleable.SearchableSpinner_disabled, false)
        } finally {
            a.recycle()
        }
    }

    private fun init() {
        prepareSpinner()
        prepareDropDown()
        prepareTextview()
    }

    private fun prepareDropDown() {
        dropdown = ImageView(context)
        dropdown?.id = R.id.vip_id
        val states = StateListDrawable()
        states.addState(
            intArrayOf(android.R.attr.state_pressed)
            , resources.getDrawable(R.drawable.dropdown_pressed)
        )
        states.addState(
            intArrayOf(),
            resources.getDrawable(R.drawable.dropdown)
        )
        dropdown?.background = states

        val params =
            LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.addRule(ALIGN_PARENT_RIGHT, TRUE)
        params.addRule(CENTER_VERTICAL, TRUE)
        params.rightMargin = 10

        isClickable = true
        addView(dropdown, params)
    }

    private fun prepareTextview() {
        mLabelView = TextView(context)
        mLabelView?.setTextColor(getTextColor())
        mLabelView?.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        mLabelView?.hint = mHint
        mLabelView?.maxLines = 1
        mLabelView?.setLines(1)
        mLabelView?.ellipsize = TextUtils.TruncateAt.END

        val params =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.addRule(CENTER_VERTICAL, TRUE)
        if (labelAlignment == LABEL_ALIGNMENT_CENTER) {
            mLabelView?.gravity = Gravity.CENTER
            params.addRule(CENTER_IN_PARENT, TRUE)
        } else if (labelAlignment == LABEL_ALIGNMENT_LEFT) {
            params.addRule(ALIGN_PARENT_LEFT)
            params.leftMargin = SpinnerUtils.dpToPx(context, 0)
        } else {
            params.addRule(CENTER_IN_PARENT, TRUE)
        }
        addView(mLabelView, params)
    }

    private fun getTextColor() = mTextColor

    private fun prepareSpinner() {
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300)
        layoutParams = params
        isClickable = true
        super.setOnClickListener(mClickListener)
    }

    val mClickListener = OnClickListener {
        if (!isDisabled) {
            showPopup()
        }
    }

    private fun showPopup() {
        val view = View.inflate(context, R.layout.popup_menu, null)
        val recyclerView: RecyclerView
        val clearSelection: ImageView
        val searchField: EditText
        with(view) {
            recyclerView = this.findViewById(R.id.recyclerview)
            clearSelection = this.findViewById(R.id.clear_selection)
            searchField = this.findViewById(R.id.search_field)
        }
        clearSelection.setOnClickListener { clearView ->
            onClearClicked(clearSelection)
        }
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        myItemsAdapter = SearchableAdapter(
            mlist = itemList,
            context = context,
            searchableSpinner = this,
            onItemOptSelectedListener = mListener,
            mTextColor = mSelectedTextColor
        )
        recyclerView.adapter = myItemsAdapter

        if (showSearch) {
            searchField.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    myItemsAdapter?.filter?.filter(
                        searchField.text.toString()
                    )
                }
            })
        } else {
            searchField.visibility = View.GONE
        }

        popupWindow = PopupWindow(view, this.width, 600)
        popupWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow?.isOutsideTouchable = true
        popupWindow?.isFocusable = true
        popupWindow?.isClippingEnabled = false
        popupWindow?.update()
        popupWindow?.showAsDropDown(this)
    }

    private val mListener = object : SearchableAdapter.OnItemOptSelectedListener {
        override fun onOptSelected(position: Any) {
            setSelection(originalList.indexOf(position), true)
            rootView.postDelayed(Runnable {
                popupWindow?.dismiss()
            }, SearchableSpinner.POPUP_DISMISS_DELAY)
        }
    }

    fun onClearClicked(clearSelection: ImageView) {
        mSelectedItem?.let {
            mSelectedItem = null
            mLabelView?.text = ""
            clearSelection.postDelayed(object : Runnable {
                override fun run() {
                    popupWindow?.dismiss()
                }
            }, SearchableSpinner.POPUP_DISMISS_DELAY)
            return
        }

        mSelectedItem = null
        mLabelView?.text = ""
        clearSelection.postDelayed(object : Runnable {
            override fun run() {
                popupWindow?.dismiss()
            }
        }, SearchableSpinner.POPUP_DISMISS_DELAY)

        mItemSelectListener?.onSelectionClear()
    }

    fun setItems(items: List<Any>?) {
        clear()
        if (items.isNullOrEmpty()) {
            setSelection(null)
            return
        }
        this.itemList = items
        this.originalList = items
        if (isSort) {
            val sortedList = this.itemList.sortedBy {
                it.toString()
            }
            this.itemList = sortedList
            this.originalList = sortedList
        }
        myItemsAdapter?.notifyDataSetChanged()
    }

    fun setOnItemSelectListener(mItemSelectListener: SearchableItemListener) {
        this.mItemSelectListener = mItemSelectListener
    }

    private fun clear() {
        mSelectedItem = null
        mLabelView?.text = ""
        itemList = emptyList()
        myItemsAdapter = null
        myItemsAdapter?.notifyDataSetChanged()
    }

    private fun setSelection(position: Int, callback: Boolean) {
        if (position < 0 || position >= itemList.size) {
            return
        }
        setSelection(itemList.get(position), callback)
    }

    fun setSelection(item: Any?, withCallback: Boolean = false) {

        if (item == null || originalList.isNullOrEmpty()) {
            mSelectedItem = null
            mLabelView?.text = ""
        } else {
            if (mSelectedItem != null && item.equals(mSelectedItem)) {
                return
            }
            var pos: Int = -1

            for (i in itemList.indices) {
                val obj = itemList.get(i)
                if (item == obj) {
                    mSelectedItem = obj
                    pos = i
                    break
                }
            }
            if (withCallback && mItemSelectListener != null && pos != -1) {
                mItemSelectListener?.onItemSelected(this, pos)
            }
            if (mSelectedItem != null) {
                mLabelView?.text = mSelectedItem.toString()
            } else {
                mLabelView?.text = ""
            }
        }
    }

    interface SearchableItemListener {
        fun onItemSelected(view: View?, position: Int)
        fun onSelectionClear()
    }


}