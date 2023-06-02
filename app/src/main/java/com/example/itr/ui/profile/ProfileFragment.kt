package com.example.itr.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.itr.databinding.FragmentProfileBinding
import com.example.itr.ui.onboarding.OnBoardingActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val db = Firebase.firestore
        val userRef = db.collection("user")
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email

        val progressBar = binding.progressBar
        progressBar.visibility = View.VISIBLE

        userRef.whereEqualTo("email", currentUserEmail)
            .get()
            .addOnSuccessListener {
                for (document in it) {
                    val userData = document.data

                    val name = userData["name"] as String

                    binding.textName.text = name
                    binding.textEmail.text = currentUserEmail
                }
                progressBar.visibility = View.GONE
            }

        logout()

    }

    private fun logout() {
        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), OnBoardingActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }
}