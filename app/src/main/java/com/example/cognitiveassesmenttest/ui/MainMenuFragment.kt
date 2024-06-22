package com.example.cognitiveassesmenttest.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.cognitiveassesmenttest.R
import com.example.cognitiveassesmenttest.databinding.FragmentMainMenuBinding
import com.example.cognitiveassesmenttest.ui.gameone.TrailMakingTestActivity
import com.example.cognitiveassesmenttest.ui.hrb.ShapeActivity
import com.example.cognitiveassesmenttest.ui.mmse.RepetitionActivity
import com.google.firebase.FirebaseApp

/**
 * A simple [Fragment] subclass.
 * Use the [MainMenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainMenuFragment : Fragment() {
    private var _binding: FragmentMainMenuBinding? = null
    private val binding get() = _binding!!

    companion object {
        @JvmStatic
        fun newInstance(): MainMenuFragment {
            return MainMenuFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainMenuBinding.inflate(inflater, container, false)
        val root: View = binding.root

        FirebaseApp.initializeApp(requireContext())

        val gameOneButton = root.findViewById<Button>(R.id.gameOne)
        val gameTwoButton = root.findViewById<Button>(R.id.gameTwo)
        val gameThreeButton = root.findViewById<Button>(R.id.gameThree)

        gameOneButton.setOnClickListener{
            val intent = Intent(requireContext(), TrailMakingTestActivity::class.java)
            startActivity(intent)
            this.activity?.finish()
        }

        gameTwoButton.setOnClickListener {
            val intent = Intent(requireContext(), RepetitionActivity::class.java)
            startActivity(intent)
            this.activity?.finish()
        }

        gameThreeButton.setOnClickListener {
            val intent = Intent(requireContext(), ShapeActivity::class.java)
            startActivity(intent)
            this.activity?.finish()
        }

        return root
    }
}