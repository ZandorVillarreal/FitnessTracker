package com.example.fitnesstracker

import android.Manifest
import android.content.pm.PackageManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.example.fitnesstracker.ui.MainActivity

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.fitnesstracker", appContext.packageName)
    }

    @Test
    fun checkUIComponents() {
        // Check if the BottomNavigationView is displayed
        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()))

        // Check if the TextView with id 'textView2' is displayed
        onView(withId(R.id.textView2)).check(matches(isDisplayed()))

        // Check if the ProgressBar with id 'stepsProgressBar' is displayed
        onView(withId(R.id.stepsProgressBar)).check(matches(isDisplayed()))
    }

    @Test
    fun checkLocationPermission() {
        // Verify if the location permission has been granted
        val permissionCheck = InstrumentationRegistry.getInstrumentation().targetContext
            .checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)

        // Assert that the permission has been granted
        assertEquals(PackageManager.PERMISSION_GRANTED, permissionCheck)

        // Add any additional checks or UI interactions based on permission status
    }
}