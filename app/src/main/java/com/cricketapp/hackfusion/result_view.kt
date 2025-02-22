package com.cricketapp.hackfusion

import Election
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cricketapp.hackfusion.databinding.FragmentResultViewBinding

class ViewResultFragment : Fragment() {

    private var _binding: FragmentResultViewBinding? = null
    private val binding get() = _binding!!

    private lateinit var election: Election  // Election data passed from previous fragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        election = arguments?.getParcelable("election") ?: return

        binding.tvElectionName.text = election.electionName

        binding.rvCandidates.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCandidates.adapter = ResultsAdapter(election.votes)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
