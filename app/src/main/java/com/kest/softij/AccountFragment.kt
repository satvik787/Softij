package com.kest.softij


import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kest.softij.vm.AccountViewModel


class AccountFragment : Fragment() {

    private lateinit var editDialog: Dialog
    private lateinit var passwordDialog:Dialog
    private lateinit var toAddress:AddressFragment.ToAddressFragment

    private lateinit var layout:View
    private val viewModel: AccountViewModel by lazy {
        ViewModelProvider(this).get(AccountViewModel::class.java)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        toAddress = context as AddressFragment.ToAddressFragment
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        layout = inflater.inflate(R.layout.fragment_account, container, false)
        return layout
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val editLayout = activity?.layoutInflater?.inflate(R.layout.dialog_edit_user, null)
        val passwordLayout = activity?.layoutInflater?.inflate(R.layout.dialog_change_password,null)

        if(activity?.resources?.configuration?.orientation == Configuration.ORIENTATION_PORTRAIT) {
            passwordDialog = BottomSheetDialog(context!!, R.style.BottomSheetDialogStyle).apply {
                setContentView(editLayout!!)
            }
            editDialog = BottomSheetDialog(context!!, R.style.BottomSheetDialogStyle).apply {
                setContentView(passwordLayout!!)
            }
        }else if(activity?.resources?.configuration?.orientation == Configuration.ORIENTATION_LANDSCAPE){
            passwordDialog = AlertDialog.Builder(context!!).apply { setView(editLayout) }.create()
            editDialog  = AlertDialog.Builder(context!!).apply { setView(passwordLayout) }.create()
            editDialog.window?.setBackgroundDrawableResource(R.drawable.round_dialog)
            passwordDialog.window?.setBackgroundDrawableResource(R.drawable.search_background)
        }
        viewModel.userData.observe(viewLifecycleOwner,{
            it?.let { res ->
                if (res.code > 0){
                    viewModel.user = res.data!!
                    loadUI(layout,editLayout,passwordLayout)
                }else Toast.makeText(context,res.msg,Toast.LENGTH_LONG).show()
            }
        })
        viewModel.updateStatus.observe(viewLifecycleOwner,{
            it?.let { res->
                Toast.makeText(context,res.msg,Toast.LENGTH_LONG).show()
            }
        })
    }


    private fun loadUI(view:View,editLayout:View?,passwordLayout:View?){
        initEditLayout(editLayout)
        initPasswordLayout(passwordLayout)
        view.findViewById<TextView>(R.id.account_name)
            .text = viewModel.user.firstName
        view.findViewById<TextView>(R.id.account_email)
            .text = viewModel.user.email
        view.findViewById<Button>(R.id.btn_acc_edit).setOnClickListener {
            editDialog.show()
        }
        view.findViewById<Button>(R.id.btn_cng_pass).setOnClickListener {
            passwordDialog.show()
        }
        view.findViewById<Button>(R.id.btn_acc_address).setOnClickListener {
            toAddress.launchAddress()
        }
    }

    private fun initPasswordLayout(layout:View?):View{
        val password = layout?.findViewById<EditText>(R.id.acc_first_name)
        val passwordCnf = layout?.findViewById<EditText>(R.id.acc_last_name)
        layout?.findViewById<Button>(R.id.cng_save)?.setOnClickListener {
            passwordDialog.dismiss()
        }
        return layout!!
    }

    private fun initEditLayout(view:View?):View{
        val fname = view?.findViewById<EditText>(R.id.acc_first_name)?.apply { setText(viewModel.user.firstName) }
        val lname = view?.findViewById<EditText>(R.id.acc_last_name)?.apply { setText(viewModel.user.lastName) }
        val email = view?.findViewById<EditText>(R.id.acc_edit_email)?.apply { setText(viewModel.user.email) }
        val phone = view?.findViewById<EditText>(R.id.acc_edit_phone)?.apply { setText(viewModel.user.telephone) }
        view?.findViewById<Button>(R.id.cng_save)?.setOnClickListener {
            viewModel.user.firstName = fname?.text.toString()
            viewModel.user.lastName  = lname?.text.toString()
            viewModel.user.email     = email?.text.toString()
            viewModel.user.telephone = phone?.text.toString()
            editDialog.dismiss()
            viewModel.updateUserInfo()

        }
        return view!!
    }
    

}