

# Searchable-Spinner-Kotlin

Android's regular spinner can be really annoying sometimes. Unwanted calls during initialization, doesn't let user to select same item etc. Searchable-Spinner-Kotlin is a simple spinner which supports item click events. You can set item click listener. and also manage callback on item selection as you want. 


## 

## Gradle Dependency

Add this in your root build.gradle file (not your module build.gradle file):

```
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```

Then, add the library to your module build.gradle

```
implementation 'com.github.zeeshan5422:Searchable-Spinner-Kotlin:v1.0'
```

## Features

* Search (hide and show)
* unselect item button
* sorting the list
* Enable and disabled spinner
* change label color
* change selected item color in the list
* label text size
* label alignment (0 for left and 1 for center)
* selection with and without callback


## Usage

There is a [sample](https://github.com/zeeshan5422/Searchable-Spinner-Kotlin/tree/master/app/src/main) provided which shows how to use the library, but for completeness, here is all that is required to get MaterialSpinner working:

### In xml layout,
```
    <com.zues.searchable_spinner.SearchableSpinner
            android:id="@+id/searchable_sp"
            android:layout_width="200dp"
            app:showSearch="true"
            app:sort="true"
            app:label_hint="Please Select"
            app:label_color="@color/colorPrimary"
            app:selected_item_color="@color/colorAccent"
            android:layout_height="30dp"/>
```

### In Kotlin/Java,
```
        searchable_sp.setItems(getListOfItems()) // Any type of list 
        searchable_sp.setOnItemSelectListener(object : SearchableSpinner.SearchableItemListener {
            override fun onItemSelected(view: View?, position: Int) {
                showToast("Selected")
            }

            override fun onSelectionClear() {
                showToast("Selection is clear")
            }
        })
        // if you want to select an item programatically
        searchable_sp.setSelection(xyz) // Any type of list item
```

That's it! :-)

if you are facing any kind of problem please let me know

