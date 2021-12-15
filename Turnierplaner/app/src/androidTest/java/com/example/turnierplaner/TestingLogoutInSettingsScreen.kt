/* (C)2021 */
package com.example.turnierplaner

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.turnierplaner.navigation.SetupNavGraph
import com.google.firebase.auth.FirebaseAuth
import junit.framework.Assert
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TestingLogoutInSettingsScreen {

  lateinit var navController: NavHostController

  @get:Rule val composeTestRule = createAndroidComposeRule<Turnierplaner>()

  @Before
  fun Login() {
    composeTestRule.setContent {
      navController = rememberNavController()
      SetupNavGraph(navController = navController)
      navController.navigate(BottomBarScreens.Setting.route)
    }
  }

  @Test
  fun EnabledLogoutButton() {
    composeTestRule.onNodeWithContentDescription("Button for Logout").assertIsEnabled()
  }

  @Test
  fun pressLogoutButton(){
    composeTestRule.onNodeWithContentDescription("Button for Logout").performClick()
    assertEquals(FirebaseAuth.getInstance().currentUser == null, true)
    composeTestRule.waitForIdle()
    //composeTestRule.onNodeWithContentDescription("Register and Login with Google").assertIsEnabled()
  }
}
