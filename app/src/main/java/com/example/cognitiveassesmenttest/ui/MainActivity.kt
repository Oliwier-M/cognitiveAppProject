package com.example.cognitiveassesmenttest.ui
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.example.cognitiveassesmenttest.R
import com.example.cognitiveassesmenttest.databinding.ActivityMainBinding
import com.example.cognitiveassesmenttest.ui.user.ProfileActivity
import com.example.cognitiveassesmenttest.ui.user.StatsActivity
import com.google.firebase.database.FirebaseDatabase
import java.util.logging.Handler

/**
 * Main Activity class that is the entry point of the application.
 * It is responsible for setting up the navigation drawer and the toolbar.
 * It shows the buttons to start the tests, view the stats and view the profile.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    /**
     * Function that is called when the activity is created.
     * It sets up the navigation drawer and the toolbar.
     * It also sets up the buttons to start the tests, view the stats and view the profile.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)


        val stats = findViewById<ImageButton>(R.id.statsButton)
        val profile = findViewById<ImageButton>(R.id.profileButton)

        stats.setOnClickListener{
            val intent = Intent(this, StatsActivity::class.java)
            startActivity(intent)
        }

        profile.setOnClickListener{
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

    }
    /**
     * Function that is called when the activity is created.
     * It sets up the navigation drawer and the toolbar.
     * It also sets up the buttons to start the tests, view the stats and view the profile.
     */
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}