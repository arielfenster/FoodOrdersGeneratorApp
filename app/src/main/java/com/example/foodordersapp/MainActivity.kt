package com.example.foodordersapp

import android.annotation.SuppressLint
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.SparseArray
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var textNumItems: EditText
    private lateinit var textNumToppings: EditText
    private lateinit var btnGenerate: Button
    private lateinit var inputTextWatcher: TextWatcher
    private lateinit var focusChangeListener: View.OnFocusChangeListener

    private var isTextInputSatisfied: Boolean = false
    private var isFoodInputSatisfied: Boolean = false

    private var foodTypeParsed: Int = 0
    private var toppingsSpinnerPosToString: SparseArray<String>? = null
    private var toppings: JSONArray? = null


    companion object {
        const val MAX_PER_ROW: Int = 4
        const val TEXT_VIEW_ID: Int = 10000
        const val MARGIN: Int = 15
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initializing the main members
        this.textNumItems = findViewById(R.id.editText_num_items)
        this.textNumToppings = findViewById(R.id.editText_num_toppings)
        this.btnGenerate = findViewById(R.id.btn_generate)

        // Adding text watch listener to the edit texts
        this.inputTextWatcher = this.initializeTextWatcher()
        this.textNumItems.addTextChangedListener(this.inputTextWatcher)
        this.textNumToppings.addTextChangedListener(this.inputTextWatcher)

        // Adding a drop-down list of types of foods
        this.initializeSpinner()

        // Adding a focus change listener to the edit texts (general app context)
        this.initializeFocusListener()
        this.textNumItems.onFocusChangeListener = this.focusChangeListener
        this.textNumToppings.onFocusChangeListener = this.focusChangeListener

        this.toppingsSpinnerPosToString = SparseArray()
        this.toppingsSpinnerPosToString!!.put(0, "default")
        this.toppingsSpinnerPosToString!!.put(1, "pizza")
        this.toppingsSpinnerPosToString!!.put(2, "sushi")
        this.foodTypeParsed = 0
    }

    /**
     * Setting a text watcher that listens to the user's input
     */
    private fun initializeTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                val itemsInput: String = textNumItems.text.toString().trim()
                val toppingsInput: String = textNumToppings.text.toString().trim()
                isTextInputSatisfied = (itemsInput.isNotEmpty()) && (toppingsInput.isNotEmpty())
                updateButtonEnabler()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
    }

    /**
     * Setting a spinner object, allowing to view the food types in a drop-down list view
     */
    private fun initializeSpinner() {
        val spinner = findViewById<Spinner>(R.id.spinner)
        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            this, R.array.foods,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val textViewToppings = findViewById<TextView>(R.id.textView_toppings_options)
                // If the default display option is selected then don't display any text
                if (position == 0) {
                    textViewToppings.text = ""
                } else {
                    // Generating and displaying the toppings
                    readToppingsFromFile(spinner.selectedItemPosition)
                    textViewToppings.text = jsonArrayToString()
                }
                foodTypeParsed = position
                isFoodInputSatisfied = (position > 0)
                updateButtonEnabler()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })
    }

    /**
     * Setting a focus listener to the input's text views that hides the keyboard when the user
     * presses outside the text view.
     */
    private fun initializeFocusListener() {
        this.focusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val inputMethodManager =
                    getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
            }
        }
    }

    /**
     * Enabling/disabling whether or not the 'generate' button should be clickable.
     */
    private fun updateButtonEnabler() {
        this.btnGenerate.isEnabled = (this.isTextInputSatisfied) && (this.isFoodInputSatisfied)
    }

    /**
     * Reading the toppings from the appropriate json file.
     *
     * @param foodType - representing the position of the food type selected from the spinner
     */
    fun readToppingsFromFile(foodType: Int) {
        // If the default option was selected but the array was generated from before, then treat it as a newly generated array
        if (foodTypeParsed == 0 && toppings != null) {
            return
        }
        // Reading the data only if it's a new type of request
        if (foodType != foodTypeParsed) {
            try {
                val istream: InputStream =
                    assets.open(this.toppingsSpinnerPosToString!!.get(foodType) + ".json")
                val buffer = ByteArray(istream.available())
                istream.read(buffer)
                istream.close()

                // Creating a string of the whole data
                val json = String(buffer, StandardCharsets.UTF_8)
                // Extracting the values from the string into an array
                this.toppings = JSONObject(json).getJSONArray("toppings")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Creating a string containing all the elements in a json array.
     *
     * @return a string representation of the array's contents
     */
    fun jsonArrayToString(): String {
        val stringBuilder = StringBuilder(toppings!!.length() * 2)
        try {
            for (i in 0..toppings!!.length()) {
                stringBuilder.append(toppings!!.getString(i)).append(", ")
                // Writing 2 toppings in a line
                if (i % 2 == 1) {
                    stringBuilder.append("\n")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return stringBuilder.toString()
    }

    /**
     * Generating and displaying the orders.
     *
     * @param view - the view that called this function
     */
    fun generate(view: View) {
        val myLayout = findViewById<RelativeLayout>(R.id.relativeLayout)
        myLayout.removeAllViews()

        val numItems = this.textNumItems.text.toString().trim().toInt()
        val numToppings = this.textNumToppings.text.toString().trim().toInt()
        var L = 0   // acts as the left barrier of the random choices
        val rnd = Random()

        val views = ArrayList<TextView>(numItems)
        var i = 0
        while (i < numItems) {
            val foodItem = Food()
            var k = 0
            while (k < numToppings) {
                // If we used all the available toppings then make the order smaller
                if (k >= this.toppings!!.length()) {
                    break
                }
                // Checking if we used all the toppings during the current item
                if (L >= this.toppings!!.length()) {
                    L = 0
                }
                try {
                    // Getting the next topping to put and checking if it is already present
                    val index = rnd.nextInt(this.toppings!!.length() - L) + L
                    if (!foodItem.addTopping(this.toppings!!.getString(index))) {
                        k--
                        continue
                    }
                    // Moving the used topping outside the range of the random index
                    this.swapToppings(L, index)
                    L++
                } catch (e: Exception) {
                    k--
                    e.printStackTrace()
                }
                k++
            }
            // Creating the next text view with its associated view parameters
            val params = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            views[i] = this.createNextTextView(i, foodItem.toString(), params)
            myLayout.addView(views[i], params)
            i++
        }
    }


    @SuppressLint("SetTextI18n")
    fun createNextTextView(
        index: Int,
        text: String,
        params: RelativeLayout.LayoutParams
    ): TextView {
        val textView = TextView(this)
        textView.text = "Item no. " + (index + 1) + ":\n" + text
        textView.id = TEXT_VIEW_ID + index

        params.setMargins(MARGIN, MARGIN, MARGIN, MARGIN)

        // The maximum items in a row is 4, so when we reach that limit we move to another row
        if (index % MAX_PER_ROW == 0) {
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
        }
        // Each new text view is added to the right of the previous one
        if (index > 0) {
            params.addRule(RelativeLayout.RIGHT_OF, TEXT_VIEW_ID + (index - 1))
        }
        if (index >= MAX_PER_ROW) {
            params.addRule(RelativeLayout.BELOW, TEXT_VIEW_ID + (index - MAX_PER_ROW))
        }
        return textView
    }

    /**
     * Swapping between two values in the json array
     *
     * @param i - index #1
     * @param j - index #2
     */
    private fun swapToppings(i: Int, j: Int) {
        try {
            val temp = toppings!!.getString(i)
            toppings!!.put(i, toppings!!.getString(j))
            toppings!!.put(j, temp)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}