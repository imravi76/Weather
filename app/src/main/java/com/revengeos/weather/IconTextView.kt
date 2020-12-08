package com.revengeos.weather

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.core.widget.ImageViewCompat

class IconTextView : LinearLayout {

    val iconView : ImageView
    val textView : TextView

    constructor (context: Context) : this(context, null) {
    }

    constructor(context: Context, @Nullable attrs: AttributeSet?) : this(context, attrs, 0) {
    }

    constructor(context: Context, @Nullable attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0) {
    }

    constructor(context: Context, @Nullable attrs: AttributeSet?, defStyleAttr: Int, defStyleRes : Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        LayoutInflater.from(context).inflate(R.layout.icon_text_view, this, true);
        iconView = findViewById(R.id.icon)
        textView = findViewById(R.id.text)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.IconTextView, 0, 0)
        val iconSize = typedArray.getDimensionPixelSize(R.styleable.IconTextView_iconSize, ViewGroup.LayoutParams.WRAP_CONTENT)
        iconView.layoutParams = LayoutParams(iconSize, iconSize)
        val margins = iconView.layoutParams as MarginLayoutParams
        margins.marginEnd = typedArray.getDimensionPixelSize(R.styleable.IconTextView_iconTextHorizontalMargin, 0)
        margins.bottomMargin = typedArray.getDimensionPixelSize(R.styleable.IconTextView_iconTextVerticalMargin, 0)
        val iconTint = typedArray.getColorStateList(R.styleable.IconTextView_iconTintColor)
        ImageViewCompat.setImageTintList(iconView, iconTint);
        iconView.setImageDrawable(typedArray.getDrawable(R.styleable.IconTextView_src))
        textView.setTextAppearance(context, typedArray.getResourceId(R.styleable.IconTextView_textAppearance, 0))
        textView.text = typedArray.getString(R.styleable.IconTextView_text)
    }
}