package com.kest.softij

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.kest.softij.api.model.User

class AccountFragment : Fragment() {

    private lateinit var user: User

    private val viewModel: AccountViewModel by lazy {
        ViewModelProvider(this).get(AccountViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.account_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.userData.observe(viewLifecycleOwner,{
            it?.let { res ->
                if (res.code > 0){
                    user = res.data!!
                }else Toast.makeText(context,res.msg,Toast.LENGTH_LONG).show()
            }
        })
    }

}