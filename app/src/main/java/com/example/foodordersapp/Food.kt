package com.example.foodordersapp

class Food {
    private var toppings = arrayListOf<String>()

    fun addTopping(t: String): Boolean {
        if (!toppings.contains(t)) {
            toppings.add(t)
            return true
        }
        return false
    }

    override fun toString(): String {
        val strBuilder = StringBuilder()
        for (topping in toppings) {
            strBuilder.append(topping)
        }
        return strBuilder.toString()
    }
}