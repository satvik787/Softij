package com.kest.softij

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kest.softij.api.model.CartItem
import com.kest.softij.vm.CartViewModel

class CartFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var cartPrice:TextView
    private lateinit var itemCount:TextView
    private val viewModel: CartViewModel by lazy{
        ViewModelProvider(this).get(CartViewModel::class.java)
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
        val checkOut = view.findViewById<Button>(R.id.cart_checkout)
        recyclerView = view.findViewById(R.id.cart_list)
        recyclerView.layoutManager = LinearLayoutManager(context)
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
    }

    override fun onDestroy() {
        super.onDestroy()
        for (i in viewModel.removedItems){
            viewModel.removeFromCart(i)
        }
        viewModel.cartItems.value?.let {
            viewModel.updateCartItems(it)
        }
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

    private fun updateCartDetails(){
        cartPrice.text = viewModel.totalPrice.toString()
        itemCount.text = getString(R.string.cart_item,viewModel.cartCount.toString())
    }

}