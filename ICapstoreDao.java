package com.cg.capstore.dao;

import java.util.List;
import java.util.Map;

import com.cg.capstore.bean.Address;
import com.cg.capstore.bean.Customer;
import com.cg.capstore.bean.Feedback;
import com.cg.capstore.bean.Merchant;
import com.cg.capstore.bean.Orders;
import com.cg.capstore.bean.Product;

public interface ICapstoreDao {

	public Map<String,List<Product>> homepage(); 
	public String validateUser(String email,String password); 
	
	public Customer registerCustomer(Customer customer) ;
	public Address addAddress(Address address, String id);

	public Merchant registerMerchant(Merchant merchant) ;
	public Address addMerchantAddress(Address address, String id);
	
	
	public Customer changeProfile(Customer customer);
	
	public List<Product> findAllProduct();

	public List<Merchant> findAllMerchant();
	
	public List<Customer> findAllCustomer();
	
	public Product deleteProductById(String prodId);
	public List<Orders> orders(String status);
	
	public String addMerchantProduct(Product product);
	public String addAdminProduct(Product product, String admin_id);
	
	
	public void returnGood(String productid);
	public List<Product> findAllProductsBasedOnId(String mobile);
	public Merchant deleteMerchantById(String merchantId);
	public Map<String, Integer> businessAnalyst();
	public List<Product> findAllProductsMerchant(String mobileNo);
	
	
	public Product addProduct(Product product,String merchantMobileNo);
	public void deleteProduct(String product);
	public Orders checkOrderDetails(Orders order);
	public Merchant changePassword(Merchant merchant);
	public Product updateProduct(Product product,String prodId);
	public Product addDiscount(Product product);
	List<Product> getCategoryProduct(String prodCategory);
	Product getProductPage(String productId);
	List<Product> findAllProductsCustomer(String customerMobileNo);
	void removeCustomerProduct(String customerMobileNo, String productId);
	void addCartProduct(String customerMobileNo, String productId);
	public List<Product> findProductsByName(String name);
	public  List<Feedback> productFeedback(String prodId);
	
}
