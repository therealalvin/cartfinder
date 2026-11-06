package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.DistanceUnit
import com.example.data.model.GolfCartLocation
import com.example.data.repository.CartLocationRepository
import com.example.location.LocationTracker
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("CartFinder", appName)
  }

  @Test
  fun `cart location repository saves and completely forgets spot`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = CartLocationRepository(context)

    // Save parked location
    val spot = GolfCartLocation(
        id = "test_cart_1",
        latitude = 28.8950,
        longitude = -81.9705,
        accuracyMeters = 2.5f,
        spotLabel = "Lake Sumter Row 3",
        photoUriString = "content://com.aistudio.golfcart.kxvlmp.fileprovider/cart_images/test.jpg"
    )
    repo.saveParkedLocation(spot)
    assertEquals(spot, repo.currentParkedLocation.value)
    assertEquals("content://com.aistudio.golfcart.kxvlmp.fileprovider/cart_images/test.jpg", repo.currentParkedLocation.value?.photoUriString)

    // Clear parked location (completely forgets the point)
    repo.clearParkedLocation()
    assertNull(repo.currentParkedLocation.value)
  }

  @Test
  fun `distance calculation is positive between distinct coordinates`() {
    val dist = LocationTracker.calculateDistanceMeters(
        36.5688, -121.9486,
        36.5694, -121.9478
    )
    assertTrue("Distance should be greater than 0", dist > 10f)
  }

  @Test
  fun `florida theme persists across repository re-initialization`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo1 = CartLocationRepository(context)

    repo1.setFloridaTheme(com.example.ui.theme.FloridaTheme.KEY_WEST)
    assertEquals(com.example.ui.theme.FloridaTheme.KEY_WEST, repo1.floridaTheme.value)

    // Reinitialize repository to test persistence from SharedPreferences
    val repo2 = CartLocationRepository(context)
    assertEquals(com.example.ui.theme.FloridaTheme.KEY_WEST, repo2.floridaTheme.value)
  }
}
