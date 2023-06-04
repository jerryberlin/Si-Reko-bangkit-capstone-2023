package com.example.itr.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        val userRef = db.collection("user").document(currentUserId)

        val progressBar = binding.progressBar
        progressBar.visibility = View.VISIBLE

        userRef
            .get()
            .addOnSuccessListener {
                val userData = it.data

                userData?.let {
                    val name = userData["name"] as String
                    val email = userData["email"] as String

                    binding.textName.text = name
                    binding.textEmail.text = email
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting user data: ", exception)
            }
            .addOnCompleteListener {
                progressBar.visibility = View.GONE
            }

        btnLogoutPressed()

    }

    private fun btnLogoutPressed() {
        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), OnBoardingActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }
}