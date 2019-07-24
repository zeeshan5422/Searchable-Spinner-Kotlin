package com.zues.myspinner.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.zues.searchable_spinner.SearchableSpinner
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        test_sp1.setItems(getListOfItems())
        test_sp1.setOnItemSelectListener(object : SearchableSpinner.SearchableItemListener {
            override fun onItemSelected(view: View?, position: Int) {
                showToast("Selected")
            }

            override fun onSelectionClear() {
                showToast("Selection is clear")
            }
        })
        test_sp1.setSelection(myModel(6,"F"))
    }

    private fun showToast(s: String) {
        Toast.makeText(baseContext, s, Toast.LENGTH_SHORT).show()
    }

    private fun getListOfItems(): List<myModel> {
        return listOf(
            myModel(1, "A"),
            myModel(2, "B"),
            myModel(3, "C"),
            myModel(4, "D"),
            myModel(5, "E"),
            myModel(6, "F"),
            myModel(7, "Ab"),
            myModel(8, "Bb"),
            myModel(9, "Cd"),
            myModel(0, "Dasdf d"),
            myModel(11, "Easdf "),
            myModel(12, "Fasd ddff")
        )
    }
}
