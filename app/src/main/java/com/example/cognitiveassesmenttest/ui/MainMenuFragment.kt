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
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Class

/**
 * A simple [Fragment] subclass.
 * Use the [MainMenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainMenuFragment : Fragment() {
    private var _binding: FragmentMainMenuBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainMenuBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val gameOne = root.findViewById<Button>(R.id.gameOne)

        gameOne.setOnClickListener{
            val intent = Intent(requireContext(), TrailMakingTestActivity::class.java)
            startActivity(intent)
        }


        return root
    }
}