package com.kest.softij

import android.app.AlertDialog
import android.app.Dialog
import android.content.res.Configuration
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kest.softij.api.model.Address
import com.kest.softij.vm.AddressViewModel

class AddressFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var addressDialog:Dialog
    private lateinit var dialogLayoutHolder:DialogLayoutHolder
    private var fromCart = false
    private val viewModel: AddressViewModel by lazy {
        ViewModelProvider(this).get(AddressViewModel::class.java)
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_address, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.address_list)
        recyclerView.layoutManager = LinearLayoutManager(context)
        view.findViewById<View>(R.id.btn_address_add).setOnClickListener {
            dialogLayoutHolder.build(
                Address.default(),
                -1
            )
            addressDialog.show()
        }
        viewModel.addressList.observe(viewLifecycleOwner,{
            it?.let{ res ->
                if(res.code > 0){
                    recyclerView.adapter = AddressAdapter(res.data!!)
                }else{

                    Toast.makeText(context,res.msg,Toast.LENGTH_LONG).show()
                }
            }
        })
        viewModel.postLiveData.observe(viewLifecycleOwner,{
            it?.let { res ->

                Toast.makeText(context,res.msg,Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialogLayoutHolder = DialogLayoutHolder(activity?.layoutInflater?.inflate(R.layout.dialog_address,null)!!)
        if(activity?.resources?.configuration?.orientation == Configuration.ORIENTATION_PORTRAIT){
            addressDialog = BottomSheetDialog(context!!,R.style.BottomSheetDialogStyle).apply {
                setContentView(dialogLayoutHolder.view)
            }
        }else if(activity?.resources?.configuration?.orientation == Configuration.ORIENTATION_LANDSCAPE){
            addressDialog = AlertDialog.Builder(context).apply {
                setView(dialogLayoutHolder.view)
            }.create().apply {
                window?.setBackgroundDrawableResource(R.drawable.round_dialog)
            }
        }
    }


    private inner class AddressAdapter(var list:MutableList<Address>):RecyclerView.Adapter<AddressViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
            val view:View = LayoutInflater.from(parent.context).inflate(R.layout.item_address,parent,false)
            return AddressViewHolder(view)
        }

        override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
            holder.build(list[position])
        }

        override fun getItemCount() = list.size

    }

    private inner class AddressViewHolder(view:View):RecyclerView.ViewHolder(view){
        private lateinit var address: Address
        private val address1 = view.findViewById<TextView>(R.id.address1)
        private val address2 = view.findViewById<TextView>(R.id.address2)
        private val city = view.findViewById<TextView>(R.id.city)
        private val state = view.findViewById<TextView>(R.id.state)
        private val firstname = view.findViewById<TextView>(R.id.firstname)
        private val lastname = view.findViewById<TextView>(R.id.lastname)
        private val postcode = view.findViewById<TextView>(R.id.postcode)
        init {
            view.findViewById<Button>(R.id.btn_edit).setOnClickListener{
                dialogLayoutHolder.build(address,adapterPosition)
                addressDialog.show()
            }
            view.findViewById<Button>(R.id.btn_delete).setOnClickListener {
                val adapter = recyclerView.adapter as AddressAdapter
                adapter.list.removeAt(adapterPosition)
                adapter.notifyItemRemoved(adapterPosition)
                viewModel.postDeleteAddress(address.addressId)
            }
        }

        fun build(address: Address){
            this.address = address
            address1.text = address.address1
            address2.text =  address.address2
            city.text = address.city
            state.text = address.state
            firstname.text = address.firstname
            lastname.text = address.lastname
            postcode.text = address.postCode
        }


    }


    private inner class DialogLayoutHolder(val view:View){
        private var index:Int? = null
        private lateinit var address:Address
        private val title = view.findViewById<TextView>(R.id.addy_title)
        private val address1 = view.findViewById<EditText>(R.id.addy_address1)
        private val address2 = view.findViewById<EditText>(R.id.addy_address2)
        private val city  = view.findViewById<EditText>(R.id.addy_city)
        private val state = view.findViewById<EditText>(R.id.addy_state)
        private val firstname  = view.findViewById<EditText>(R.id.addy_firstname)
        private val lastname  = view.findViewById<EditText>(R.id.addy_lastname)
        private val postcode = view.findViewById<EditText>(R.id.addy_postcode)
        private val textMsg = view.findViewById<TextView>(R.id.addy_msg)
        init {
            view.findViewById<Button>(R.id.addy_save).setOnClickListener {
                address.firstname = firstname.text.toString()
                address.lastname = lastname.text.toString()
                address.address1 = address1.text.toString()
                address.address2 = address2.text.toString()
                address.city = city.text.toString()
                address.state = state.text.toString()
                address.postCode = postcode.text.toString()
                if(check()) {
                    if (index!! >= 0) {
                        viewModel.postEditAddress(address)
                        recyclerView.adapter?.notifyItemChanged(index!!)
                    } else {
                        val adapter = recyclerView.adapter as AddressAdapter
                        adapter.list.add(address)
                        adapter.notifyItemInserted(adapter.list.size - 1)
                        viewModel.postInsertAddress(address)
                    }
                    addressDialog.dismiss()
                    clean()
                }
            }
        }

        fun check():Boolean{
            var msg = ""
            msg = if(address.firstname.length >= 4 && address.lastname.length >=4){
                if(address.address1.length in 4..30 && address.address2.length in 4..30){
                    if(address.city.length >= 4){
                        if(address.postCode.length == 6){
                            return true
                        }else getString(R.string.msg_invalid_postcode)
                    } else getString(R.string.msg_invalid_city)
                }else getString(R.string.msg_invalid_addy)
            }else getString(R.string.msg_invalid_name)
            textMsg.text = msg
            return false
        }

        fun build(address: Address,index: Int){
            this.address = address
            this.index = index
            title.text = if(index >= 0) getString(R.string.title_edit_addy)
            else getString(R.string.title_add_addy)
            firstname.setText(address.firstname)
            lastname.setText(address.lastname)
            address1.setText(address.address1)
            address2.setText(address.address2)
            city.setText(address.city)
            state.setText(address.state)
            postcode.setText(address.postCode)
        }
        fun clean(){
            title.text = ""
            firstname.setText("")
            lastname.setText("")
            address1.setText("")
            address2.setText("")
            city.setText("")
            state.setText("")
            postcode.setText("")
        }

    }

    interface ToAddressFragment{
        fun launchAddress()
    }

    interface AddressInserted{
        fun onInsert(address: Address)
    }

}