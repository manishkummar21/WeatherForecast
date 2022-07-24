package com.weather.forecast

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import com.weather.forecast.data.location.model.LocationDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object Utils {


    fun AppCompatEditText.getTextChangeStateFlow(): StateFlow<String> {
        val query = MutableStateFlow("")
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                query.value = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        return query
    }

    fun Number.toMilliSecond(): Long {
        return this.toLong() * 1000
    }

    fun Context.showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    fun LocationDetails.getName(): String {
        if (locality.isNotEmpty())
            return locality
        if (city.isNotEmpty())
            return city
        if (address.isNotEmpty())
            return address
        else
            return ""
    }
}