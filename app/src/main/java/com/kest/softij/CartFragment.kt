package com.kest.softij

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kest.softij.api.model.Address
import com.kest.softij.api.model.CartItem
import com.kest.softij.vm.CartViewModel
import com.kest.softij.vm.MainViewModel

class CartFragment : Fragment() {

    private lateinit var addressRecyclerView: RecyclerView
    private var activityViewModel:MainViewModel? = null
    private lateinit var emptyMsg:TextView
    private lateinit var dialog:BottomSheetDialog
    private lateinit var recyclerView: RecyclerView
    private lateinit var cartPrice:TextView
    private lateinit var itemCount:TextView
    private lateinit var toAddressFragment: AddressFragment.ToAddressFragment
    private val viewModel: CartViewModel by lazy{
        ViewModelProvider(this).get(CartViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        toAddressFragment = context as AddressFragment.ToAddressFragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getAddress()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cartPrice = view.findViewById(R.id.cart_price)
        itemCount = view.findViewById(R.id.cart_num_items)
        recyclerView = view.findViewById(R.id.cart_list)
        recyclerView.layoutManager = LinearLayoutManager(context)

        view.findViewById<Button>(R.id.cart_checkout).setOnClickListener {
            if(viewModel.cartCount > 0) {
                dialog.show()
            }
        }

        viewModel.cartItems.observe(viewLifecycleOwner,{
            it?.let{
                viewModel.cartCount = 0
                viewModel.totalPrice = 0.0
                for (i in it) {
                    viewModel.totalPrice += (i.price * i.quantity)
                    viewModel.cartCount += i.quantity
                }
                recyclerView.adapter = CartItemAdapter(it)
                updateCartDetails()
            }
        })
        viewModel.addressList.observe(viewLifecycleOwner,{
            it?.let { res ->
                if(res.code > 0){
                    res.data?.let{ list ->
                        if(list.size > 0) {
                            emptyMsg.visibility = View.GONE
                            addressRecyclerView.adapter = AddressAdapter(list)
                        }else emptyMsg.visibility = View.VISIBLE
                    }
                }
            }
        })
        viewModel.addressId.observe(viewLifecycleOwner,{
            it?.let{ id ->
                dialog.dismiss()
                activityViewModel?.let { vm ->
                    viewModel.cartItems.value?.let { list ->
                        vm.checkout(id,list)
                    }
                }
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let {
            activityViewModel = ViewModelProvider(it).get(MainViewModel::class.java)
        }
        val addressLayout = activity?.layoutInflater?.inflate(R.layout.dialog_checkout,null)
        addressLayout?.let {
            addressRecyclerView = it.findViewById(R.id.cart_address_list)
            it.findViewById<Button>(R.id.add_address).setOnClickListener {
                toAddressFragment.launchAddress()
                dialog.dismiss()
            }
            emptyMsg = it.findViewById(R.id.empty_msg)
            addressRecyclerView.layoutManager = LinearLayoutManager(context!!)
            dialog = BottomSheetDialog(context!!,R.style.BottomSheetDialogStyle).apply {
                setContentView(it)
            }
            val parentLayout = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { pl ->
                val behaviour = BottomSheetBehavior.from(pl)
                setupFullHeight(pl)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        for (i in viewModel.removedItems){
            viewModel.removeFromCart(i)
        }
        viewModel.updateCartItems()
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }


    private inner class CartItemAdapter(var list:MutableList<CartItem>):RecyclerView.Adapter<CartItemHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemHolder {
            val view:View = LayoutInflater.from(parent.context).inflate(R.layout.item_cart,parent,false)
            return CartItemHolder(view)
        }

        override fun onBindViewHolder(holder: CartItemHolder, position: Int) {
            holder.build(list[position])
        }

        override fun getItemCount() = list.size

    }

    private inner class CartItemHolder(view:View):RecyclerView.ViewHolder(view){
        private lateinit var cartItem: CartItem
        private val itemName = view.findViewById<TextView>(R.id.item_name)
        private val desc = view.findViewById<TextView>(R.id.item_desc)
        private val model = view.findViewById<TextView>(R.id.item_model)
        private val price = view.findViewById<TextView>(R.id.item_price)
        private val quantity = view.findViewById<TextView>(R.id.item_quantity)

        init{
            view.findViewById<Button>(R.id.quantity_add).setOnClickListener {
                if (cartItem.quantity < cartItem.stock) {
                    cartItem.quantity++
                    viewModel.cartCount++
                    viewModel.totalPrice += cartItem.price
                }
                quantity.text = cartItem.quantity.toString()
                updateCartDetails()
            }
            view.findViewById<Button>(R.id.quantity_remove).setOnClickListener {
                cartItem.quantity--
                viewModel.cartCount--
                viewModel.totalPrice -= cartItem.price
                quantity.text = cartItem.quantity.toString()
                if(cartItem.quantity == 0){
                    val adapter = recyclerView.adapter as CartItemAdapter
                    viewModel.removedItems.add(cartItem)
                    adapter.list.removeAt(adapterPosition)
                    adapter.notifyItemRemoved(adapterPosition)
                }
                updateCartDetails()
            }
        }

        fun build(cartItem: CartItem){
            this.cartItem = cartItem
            itemName.text = cartItem.name
            desc.text = cartItem.description
            model.text = cartItem.model
            price.text = cartItem.price.toString()
            quantity.text = cartItem.quantity.toString()
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
            view.findViewById<Button>(R.id.btn_edit).visibility = View.GONE
            view.findViewById<Button>(R.id.btn_delete).visibility = View.GONE
            itemView.setOnClickListener {
                viewModel.addressId.value = address.addressId
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

    private fun updateCartDetails(){
        cartPrice.text = viewModel.totalPrice.toString()
        itemCount.text = getString(R.string.cart_item,viewModel.cartCount.toString())
    }

    interface ToCartFragment{
        fun launchCart()
    }

}