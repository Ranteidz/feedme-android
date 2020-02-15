package com.example.feedme


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.rule.ActivityTestRule

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
@SmallTest
class ChangeTextBehaviorTest {

    private lateinit var stringToBeTyped : String

    @get:Rule
    var activityRule : ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @Before
    fun initValidString() {
        stringToBeTyped = "Hej"
    }


    @Test
    fun changeText_sameActivity(){
        onView(withId(R.id.questionSpinner))
            .perform(typeText(stringToBeTyped), closeSoftKeyboard())
        onView(withId(R.id.adjust_width)).perform(click())
    }
}
