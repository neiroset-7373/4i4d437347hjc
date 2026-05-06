package com.wintozo.spidi

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import java.io.File

class MainActivity : AppCompatActivity() {

    private var coins: Long = 0
    private var totalCoins: Long = 0
    private var totalClicks: Long = 0
    private var clickPower: Long = 1
    private var baseClickPower: Long = 1
    private var autoClickPerSecond: Int = 0
    private var baseAutoClick: Int = 0
    private var selectedWallpaper: Int = 0
    private var selectedMusic: Int = 0
    private var musicEnabled: Boolean = true
    private var musicVolume: Float = 0.3f
    private var completedOnboarding: Boolean = false
    private var deviceType: String = "pc"
    private var clickMultiplier: Float = 1f
    private var activeUpgrade: Upgrade? = null

    private lateinit var coinsTextView: TextView
    private lateinit var totalCoinsTextView: TextView
    private lateinit var clickButton: ImageView
    private lateinit var bottomNavGame: CardView
    private lateinit var bottomNavUpgrades: CardView
    private lateinit var bottomNavGifts: CardView
    private lateinit var bottomNavSettings: CardView
    private lateinit var gameSection: View
    private lateinit var upgradesSection: View
    private lateinit var giftsSection: View
    private lateinit var settingsSection: View
    private lateinit var musicSeekBar: SeekBar
    private lateinit var musicToggleButton: ImageView
    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())

    data class Upgrade(
        val id: String,
        val name: String,
        val description: String,
        var cost: Long,
        val multiplier: Float,
        val isAutoClicker: Boolean,
        val duration: Long,
        var expiresAt: Long? = null,
        var active: Boolean = false
    )

    private val upgrades = mutableListOf<Upgrade>(
        Upgrade("mult_x2", "Множитель x2", "Удваивает силу клика на 24 часа", 250, 2f, false, 24 * 60 * 60 * 1000),
        Upgrade("mult_x3", "Множитель x3", "Утраивает силу клика на 24 часа", 450, 3f, false, 24 * 60 * 60 * 1000),
        Upgrade("mult_x4", "Множитель x4", "Учетверяет силу клика на 24 часа", 760, 4f, false, 24 * 60 * 60 * 1000),
        Upgrade("mult_x5", "Множитель x5", "Увеличивает силу клика в 5 раз на 24 часа", 1000, 5f, false, 24 * 60 * 60 * 1000),
        Upgrade("mult_x10", "Множитель x10", "Увеличивает силу клика в 10 раз на 24 часа", 10500, 10f, false, 24 * 60 * 60 * 1000),
        Upgrade("mult_x100", "Множитель x100", "Увеличивает силу клика в 100 раз на 24 часа", 11500, 100f, false, 24 * 60 * 60 * 1000),
        Upgrade("mult_x1000", "Множитель x1000", "Увеличивает силу клика в 1000 раз на 24 часа", 13000, 1000f, false, 24 * 60 * 60 * 1000),
        Upgrade("autoclicker", "Автокликер", "Автоматически кликает 1 раз в секунду на 24 часа", 500, 1f, true, 24 * 60 * 60 * 1000)
    )

    private val wallpapers = listOf(
        R.drawable.wallpaper_1,
        R.drawable.wallpaper_2,
        R.drawable.wallpaper_3,
        R.drawable.wallpaper_4,
        R.drawable.wallpaper_5,
        R.drawable.wallpaper_6,
        R.drawable.wallpaper_7,
        R.drawable.wallpaper_8
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadProgress()
        setContentView(R.layout.activity_main)

        initViews()
        setupListeners()
        updateUI()
        startAutoClicker()

        if (!completedOnboarding) {
            showOnboarding()
        }
    }

    private fun initViews() {
        coinsTextView = findViewById(R.id.coinsTextView)
        totalCoinsTextView = findViewById(R.id.totalCoinsTextView)
        clickButton = findViewById(R.id.clickButton)
        bottomNavGame = findViewById(R.id.bottomNavGame)
        bottomNavUpgrades = findViewById(R.id.bottomNavUpgrades)
        bottomNavGifts = findViewById(R.id.bottomNavGifts)
        bottomNavSettings = findViewById(R.id.bottomNavSettings)
        gameSection = findViewById(R.id.gameSection)
        upgradesSection = findViewById(R.id.upgradesSection)
        giftsSection = findViewById(R.id.giftsSection)
        settingsSection = findViewById(R.id.settingsSection)
        musicSeekBar = findViewById(R.id.musicSeekBar)
        musicToggleButton = findViewById(R.id.musicToggleButton)
    }

    private fun setupListeners() {
        clickButton.setOnClickListener {
            coins += (clickPower * clickMultiplier).toLong()
            totalCoins += (clickPower * clickMultiplier).toLong()
            totalClicks++
            playClickSound()
            updateUI()
            saveProgress()
        }

        bottomNavGame.setOnClickListener { showSection(R.id.gameSection) }
        bottomNavUpgrades.setOnClickListener { showSection(R.id.upgradesSection) }
        bottomNavGifts.setOnClickListener { showSection(R.id.giftsSection) }
        bottomNavSettings.setOnClickListener { showSection(R.id.settingsSection) }

        musicToggleButton.setOnClickListener {
            musicEnabled = !musicEnabled
            if (musicEnabled) {
                playMusic()
                musicToggleButton.setImageResource(R.drawable.ic_music_on)
            } else {
                stopMusic()
                musicToggleButton.setImageResource(R.drawable.ic_music_off)
            }
        }

        musicSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                musicVolume = progress / 100f
                mediaPlayer?.setVolume(musicVolume, musicVolume)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun showSection(sectionId: Int) {
        gameSection.visibility = View.GONE
        upgradesSection.visibility = View.GONE
        giftsSection.visibility = View.GONE
        settingsSection.visibility = View.GONE
        findViewById<View>(sectionId).visibility = View.VISIBLE
    }

    private fun startAutoClicker() {
        if (autoClickPerSecond > 0) {
            val runnable = object : Runnable {
                override fun run() {
                    coins += autoClickPerSecond.toLong()
                    totalCoins += autoClickPerSecond.toLong()
                    updateUI()
                    saveProgress()
                    handler.postDelayed(this, 1000)
                }
            }
            handler.postDelayed(runnable, 1000)
        }
    }

    private fun playClickSound() {
        if (musicEnabled) {
            try {
                val clickSound = MediaPlayer.create(this, R.raw.click)
                clickSound.setVolume(musicVolume, musicVolume)
                clickSound.start()
                clickSound.setOnCompletionListener { it.release() }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun playMusic() {
        stopMusic()
        try {
            val musicRes = if (selectedMusic == 0) R.raw.unofficial else R.raw.spidi_official
            mediaPlayer = MediaPlayer.create(this, musicRes)
            mediaPlayer?.setVolume(musicVolume, musicVolume)
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun updateUI() {
        coinsTextView.text = formatNumber(coins)
        totalCoinsTextView.text = formatNumber(totalCoins)
        musicSeekBar.progress = (musicVolume * 100).toInt()
    }

    private fun formatNumber(n: Long): String {
        return when {
            n >= 1_000_000_000 -> "${n / 1_000_000_000}B"
            n >= 1_000_000 -> "${n / 1_000_000}M"
            n >= 1_000 -> "${n / 1_000}K"
            else -> n.toString()
        }
    }

    private fun saveProgress() {
        val prefs = getSharedPreferences("spidi_clicker", MODE_PRIVATE)
        with(prefs.edit()) {
            putLong("coins", coins)
            putLong("totalCoins", totalCoins)
            putLong("totalClicks", totalClicks)
            putLong("clickPower", clickPower)
            putLong("baseClickPower", baseClickPower)
            putInt("autoClickPerSecond", autoClickPerSecond)
            putInt("baseAutoClick", baseAutoClick)
            putInt("selectedWallpaper", selectedWallpaper)
            putInt("selectedMusic", selectedMusic)
            putBoolean("musicEnabled", musicEnabled)
            putFloat("musicVolume", musicVolume)
            putBoolean("completedOnboarding", completedOnboarding)
            putString("deviceType", deviceType)
            putFloat("clickMultiplier", clickMultiplier)
            apply()
        }
    }

    private fun loadProgress() {
        val prefs = getSharedPreferences("spidi_clicker", MODE_PRIVATE)
        coins = prefs.getLong("coins", 0)
        totalCoins = prefs.getLong("totalCoins", 0)
        totalClicks = prefs.getLong("totalClicks", 0)
        clickPower = prefs.getLong("clickPower", 1)
        baseClickPower = prefs.getLong("baseClickPower", 1)
        autoClickPerSecond = prefs.getInt("autoClickPerSecond", 0)
        baseAutoClick = prefs.getInt("baseAutoClick", 0)
        selectedWallpaper = prefs.getInt("selectedWallpaper", 0)
        selectedMusic = prefs.getInt("selectedMusic", 0)
        musicEnabled = prefs.getBoolean("musicEnabled", true)
        musicVolume = prefs.getFloat("musicVolume", 0.3f)
        completedOnboarding = prefs.getBoolean("completedOnboarding", false)
        deviceType = prefs.getString("deviceType", "pc") ?: "pc"
        clickMultiplier = prefs.getFloat("clickMultiplier", 1f)
    }

    private fun showOnboarding() {
        AlertDialog.Builder(this)
            .setTitle("Добро пожаловать в Spidi Кликер!")
            .setMessage("Кликай, зарабатывай монеты, покупай улучшения и становись богаче!")
            .setPositiveButton("Начать") { _, _ ->
                completedOnboarding = true
                saveProgress()
            }
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        stopMusic()
        saveProgress()
    }
}
