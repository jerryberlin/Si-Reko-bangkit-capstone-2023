package com.example.itr.ui.bookmark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.itr.adapter.BookmarkListAdapter
import com.example.itr.databinding.FragmentBookmarkBinding
import com.example.itr.models.MDestination
import com.example.itr.ui.home.HomeFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

class BookmarkFragment : Fragment() {
    private lateinit var binding: FragmentBookmarkBinding
    private lateinit var listDestination: ArrayList<MDestination>
    private lateinit var adapter: BookmarkListAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager =
            GridLayoutManager(requireActivity(), 2)
        binding.rvBookmark1.layoutManager = layoutManager
        binding.rvBookmark1.setHasFixedSize(true)

        listDestination = arrayListOf()

        adapter = BookmarkListAdapter(HomeFragment.currentLocation!!, listDestination)
        binding.rvBookmark1.adapter = adapter

        getDataFromFirestore()
    }

    private fun getDataFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        db.collection("user").document(FirebaseAuth.getInstance().currentUser!!.uid).collection("destination")
            .whereEqualTo("userId", FirebaseAuth.getInstance().currentUser!!.uid)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        return
                    }

                    if (value!!.isEmpty) binding.tvNoData.visibility =
                        View.VISIBLE else binding.tvNoData.visibility = View.INVISIBLE

                    for (dc: DocumentChange in value.documentChanges) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            listDestination.add(dc.document.toObject(MDestination::class.java))
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            })
    }

    override fun onResume() {
        super.onResume()
        listDestination.clear()
        getDataFromFirestore()
    }
}