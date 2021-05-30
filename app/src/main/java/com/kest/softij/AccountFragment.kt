package com.kest.softij


import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kest.softij.vm.AccountViewModel
import com.kest.softij.vm.MainViewModel


class AccountFragment : Fragment() {

    private lateinit var editDialog: Dialog
    private lateinit var passwordDialog:Dialog
    private lateinit var toAddress:AddressFragment.ToAddressFragment
    private lateinit var activityViewModel: MainViewModel

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
        activityViewModel = ViewModelProvider(activity!!).get(MainViewModel::class.java)
        val editLayout = activity?.layoutInflater?.inflate(R.layout.dialog_edit_user, null)
        val passwordLayout = activity?.layoutInflater?.inflate(R.layout.dialog_change_password,null)

        if(activity?.resources?.configuration?.orientation == Configuration.ORIENTATION_PORTRAIT) {
            passwordDialog = BottomSheetDialog(context!!, R.style.BottomSheetDialogStyle).apply {
                setContentView(passwordLayout!!)
            }
            editDialog = BottomSheetDialog(context!!, R.style.BottomSheetDialogStyle).apply {
                setContentView(editLayout!!)
            }
        }else if(activity?.resources?.configuration?.orientation == Configuration.ORIENTATION_LANDSCAPE){
            passwordDialog = AlertDialog.Builder(context!!).apply { setView(passwordLayout) }.create()
            editDialog  = AlertDialog.Builder(context!!).apply { setView(editLayout) }.create()
            editDialog.window?.setBackgroundDrawableResource(R.drawable.round_dialog)
            passwordDialog.window?.setBackgroundDrawableResource(R.drawable.search_background)
        }
        viewModel.userData.observe(viewLifecycleOwner,{
            it?.let { user ->
                viewModel.user = user
                viewModel.updateUser = user.copy()
                loadUI(layout,editLayout,passwordLayout)
            }
        })
        viewModel.updateStatus.observe(viewLifecycleOwner,{
            it?.let { res->
                if(res.code > 0) {
                    viewModel.user = viewModel.updateUser.copy()
                    viewModel.updateLocal(viewModel.user)
                }
                else viewModel.updateUser = viewModel.user.copy()
                Toast.makeText(context,res.msg,Toast.LENGTH_LONG).show()
            }
        })
        viewModel.updateSubData.observe(viewLifecycleOwner,{
            it?.let { res->
                Toast.makeText(context,res.msg,Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun loadUI(view:View,editLayout:View?,passwordLayout:View?){
        view.findViewById<SwitchCompat>(R.id.switch_news).apply {
            isChecked = viewModel.user.newsletter == 1
            setOnCheckedChangeListener(object :CompoundButton.OnCheckedChangeListener{
                override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                    viewModel.user.newsletter = if (isChecked) 1 else 0
                    viewModel.postUpdateSub()
                }
            })
        }
        view.findViewById<TextView>(R.id.account_name)
            .text = viewModel.user.firstName
        view.findViewById<TextView>(R.id.account_email)
            .text = viewModel.user.email
        view.findViewById<Button>(R.id.btn_acc_edit).setOnClickListener {
            initEditLayout(editLayout)
            editDialog.show()
        }
        view.findViewById<Button>(R.id.btn_cng_pass).setOnClickListener {
            initPasswordLayout(passwordLayout)
            passwordDialog.show()
        }
        view.findViewById<Button>(R.id.btn_acc_address).setOnClickListener {
            toAddress.launchAddress()
        }
    }

    private fun initPasswordLayout(layout:View?):View{
        val password = layout?.findViewById<EditText>(R.id.old_password)
        val newPassword = layout?.findViewById<EditText>(R.id.cng_password)
        val passwordCnf = layout?.findViewById<EditText>(R.id.cng_password_cnf)
        layout?.findViewById<Button>(R.id.cng_save)?.setOnClickListener {
            if(validatePassword(newPassword!!,passwordCnf!!)){
                activityViewModel.changePassword(password?.text.toString(),newPassword.text.toString())
                passwordDialog.dismiss()
            }
        }
        return layout!!
    }

    private fun validatePassword(password:EditText,confirmPassword:EditText) : Boolean {
        if(password.text.length in 4..20){
            if (password.text.toString() == confirmPassword.text.toString()){
                return true
            }
            confirmPassword.error = "Passwords do not match"
            return false
        }
        password.error = "password length should be in between 4 and 20"
        return false
    }

    private fun initEditLayout(view:View?):View{
        val fname = view?.findViewById<EditText>(R.id.acc_first_name)?.apply { setText(viewModel.user.firstName) }
        val lname = view?.findViewById<EditText>(R.id.acc_last_name)?.apply { setText(viewModel.user.lastName) }
        val email = view?.findViewById<EditText>(R.id.acc_edit_email)?.apply { setText(viewModel.user.email) }
        val phone = view?.findViewById<EditText>(R.id.acc_edit_phone)?.apply { setText(viewModel.user.telephone) }
        val textMsg = view?.findViewById<TextView>(R.id.acc_msg)
        view?.findViewById<Button>(R.id.cng_save)?.setOnClickListener {
            viewModel.updateUser.firstName = fname?.text.toString()
            viewModel.updateUser.lastName  = lname?.text.toString()
            viewModel.updateUser.email     = email?.text.toString()
            viewModel.updateUser.telephone = phone?.text.toString()
            var msg = ""
            if(SoftijRepository.validateEmail(email?.text.toString())){
                if(fname?.text?.length!! in 4..30 && lname?.text?.length!! in 4..30){
                    if(phone?.text?.length!! == 10){
                        editDialog.dismiss()
                        viewModel.updateUserInfo()
                    }else msg = getString(R.string.msg_invalid_phone)
                }else msg = getString(R.string.msg_invalid_name)
            }else msg = getString(R.string.msg_invalid_email)
            textMsg?.text = msg
        }
        return view!!
    }
    

}