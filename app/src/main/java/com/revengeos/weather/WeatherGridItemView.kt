package com.revengeos.weather

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.core.widget.TextViewCompat

class WeatherGridItemView : LinearLayout {

    val unitView: TextView
    val valueView: TextView

    constructor (context: Context) : this(context, null)

    constructor(context: Context, @Nullable attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, @Nullable attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    constructor(context: Context, @Nullable attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        LayoutInflater.from(context).inflate(R.layout.weather_grid_item, this, true)

        orientation = VERTICAL

        unitView = findViewById(R.id.unit)
        valueView = findViewById(R.id.value)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.WeatherGridItemView, 0, 0)

        TextViewCompat.setTextAppearance(unitView, typedArray.getResourceId(R.styleable.WeatherGridItemView_unitTextAppearance, 0))
        unitView.text = typedArray.getString(R.styleable.WeatherGridItemView_unit)
        TextViewCompat.setTextAppearance(valueView, typedArray.getResourceId(R.styleable.WeatherGridItemView_valueTextAppearance, 0))
        valueView.text = typedArray.getString(R.styleable.WeatherGridItemView_value)
        val unitViewMargins = unitView.layoutParams as MarginLayoutParams
        unitViewMargins.bottomMargin = typedArray.getDimensionPixelSize(R.styleable.WeatherGridItemView_unitValueHorizontalMargin, 0)

        typedArray.recycle()
    }
}