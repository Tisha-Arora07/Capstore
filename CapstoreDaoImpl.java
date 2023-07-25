package com.cg.capstore.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.cg.capstore.bean.Address;
import com.cg.capstore.bean.Admin;
import com.cg.capstore.bean.Cart;
import com.cg.capstore.bean.Customer;
import com.cg.capstore.bean.Feedback;
import com.cg.capstore.bean.Merchant;
import com.cg.capstore.bean.Orders;
import com.cg.capstore.bean.Product;
import com.cg.capstore.bean.WishList;


@Repository
@Transactional
public class CapstoreDaoImpl implements ICapstoreDao
{
	HttpServletRequest request;
	
	@PersistenceContext
	EntityManager entityManager;


	
	@Override
	public Map<String, List<Product>> homepage() 
	{
	
		Query query=entityManager.createQuery("select distinct p.prodCategory from Product p");
		
		List<String> categoryList=query.getResultList();

		Map<String,List<Product>> categoryMap = new HashMap<>();

		for(String prodCategory:categoryList) 
		{
				List<Product> productList = new ArrayList<>();
				query=entityManager.createQuery("select p from Product p where p.prodCategory=:cat").setParameter("cat",prodCategory);
				productList=query.getResultList();
				categoryMap.put(prodCategory, productList);
		}
		
		return categoryMap;
	}



	@Override
	public String validateUser(String email, String password) 
	{
		Merchant merchant=entityManager.find(Merchant.class, email);
		if(merchant!=null && merchant.getPassword().equals(password))
		{
			return "merchantpage";
		}
		
		Customer customer=entityManager.find(Customer.class, email);

		if(customer!=null && customer.getPassword().equals(password))
		{
			 
			    return "home";
		}
		return "login";	
	}



	@Override
	public Customer registerCustomer(Customer customer) {
		
		Random random=new Random();
		String cartId="C"+Integer.toString(random.nextInt(100));
		String wishlistId="W"+Integer.toString(random.nextInt(100));
		Cart cart=new Cart();
		cart.setCartId(cartId);
		entityManager.persist(cart);
		WishList wish=new WishList();
		wish.setWishlistId(wishlistId);
		entityManager.persist(wish);
		customer.setCart(cart);
		customer.setWishlist(wish);
		customer.setActive(true);
		entityManager.persist(customer);
		return customer;
	}



	@Override
	public Address addAddress(Address address, String id) {
		Random random=new Random();
		String addressId="A"+Integer.toString(random.nextInt(100));
		address.setAddressId(addressId);
//		List<Address> list=new ArrayList<>();
//		list.add(address);
		entityManager.persist(address);
		Customer customer=entityManager.find(Customer.class,id);
		customer.setAdresses(address);
		//address.setCustomerAddress(customer);
		entityManager.merge(customer);
		return address;
	}



	@Override
	public Merchant registerMerchant(Merchant merchant) 
	{
		entityManager.persist(merchant);
		return merchant;
	}

	@Override
	public Address addMerchantAddress(Address address, String id) 
	{
		Random random=new Random();
		String addressId="A"+Integer.toString(random.nextInt(100));
		address.setAddressId(addressId);
		Merchant merchant=entityManager.find(Merchant.class,id);
		address.setMerchantAddress(merchant);
		merchant.setAddresses(address);
		entityManager.merge(merchant);
		return address;
	}


	@Override
	public Customer changeProfile(Customer customer) {
		Customer profile=entityManager.find(Customer.class,customer.getCustomerMobileNo());
		profile.setName(customer.getName());
		profile.setEmail(customer.getEmail());
		profile.setPassword(customer.getPassword());
		return profile;
			
	}



	@Override
	public List<Product> findAllProduct() 
	{
		Query query= entityManager.createQuery("select p from Product p");
		List<Product> products = query.getResultList();
	
		return products;
	}



	@Override
	public Product deleteProductById(String prodId)
	{
		Product product =entityManager.find(Product.class,prodId);
		if(product!=null)
		{
			entityManager.remove(product);
		}
		return product;
	}
		



	@Override
	public List<Orders> orders(String status) 
	{
		Query query = entityManager.createQuery("select o from Orders o where o.orderStatus=\'"+status+"\'");
		List<Orders> orderStatus = query.getResultList();		
		return orderStatus;
	}



	@Override
	public List<Merchant> findAllMerchant()
	{
		Query query= entityManager.createQuery("select m from Merchant m");
		List<Merchant> merchants = query.getResultList();
	
		return merchants;
	}



	@Override
	public List<Customer> findAllCustomer() {
		Query query= entityManager.createQuery("select c from Customer c");
		List<Customer> customers = query.getResultList();
	
		return customers;
	}

	
	@Override
	public String addAdminProduct(Product product,String admin_id)
	{
		Random random=new Random();
		String productId="P"+Integer.toString(random.nextInt(10000));
		product.setProdId(productId);
		product.setInitialQuantity(product.getAvailableQuantity());
		Admin admin=entityManager.find(Admin.class,admin_id);
		admin.setProducts(product);
		product.setAdminProduct(admin);
		entityManager.persist(admin);
		entityManager.persist(product);
		return productId;
	}
	
	public String addMerchantProduct(Product product)
	{
		Random random=new Random();
		String productId="P"+Integer.toString(random.nextInt(10000));
		product.setProdId(productId);
		product.setInitialQuantity(product.getAvailableQuantity());
		entityManager.persist(product);
		
		return productId;
	}



	@Override
	public void returnGood(String productid) {
		
		String orderId=(String) entityManager.createNativeQuery("Select p.order_id from orders_product p where p.prod_id =:productid").setParameter("productid", productid).getSingleResult();
		Orders order=entityManager.find(Orders.class, orderId);
		order.setOrderStatus("Returned");
		entityManager.merge(order);
				
	}



	@Override
	public List<Product> findAllProductsBasedOnId(String mobile) {
		List<Product> list=new ArrayList<>();
		List<String> myProducts=new ArrayList<>();
		String customer_mobile_no=mobile;
		Customer customer=entityManager.find(Customer.class, customer_mobile_no);
		String cartId=customer.getCart().getCartId();
		
		List<String> order=entityManager.createNativeQuery("Select p.order_id from cart_order p where p.cart_id=:cartid").setParameter("cartid", cartId).getResultList();
		for(String orderId: order)
		{
		
			myProducts.addAll(entityManager.createNativeQuery("Select p.prod_id from orders_product p where p.order_id=:orderId").setParameter("orderId", orderId).getResultList());
		
		}
		
		for(String productId:myProducts)
		{	
			list.addAll(entityManager.createQuery("select p from Product p where p.prodId=:id").setParameter("id", productId).getResultList());
		}
		
		
		return list;	
	}



	@Override
	public Merchant deleteMerchantById(String merchantId)
	{
		Merchant merchant =entityManager.find(Merchant.class,merchantId);
		if(merchant!=null)
		{
			entityManager.remove(merchant);
		}
		return merchant;
	}



	@Override
	public Map<String, Integer> businessAnalyst() {
		Map<String,Integer> map=new HashMap<>();
		List<Product> products=entityManager.createQuery("select p from Product p").getResultList();
		for(Product i:products)
		{
			map.put(i.getName(), i.getInitialQuantity()-i.getAvailableQuantity());
		}
		return map;
	}



	@Override
	public List<Product> findAllProductsMerchant(String mobileNo) {
		System.out.println(mobileNo);
		List<Product> products=new ArrayList<>();
		List<String> productIds=entityManager.createNativeQuery("select p.prod_id from merchant_product p where p.merchant_id=:merchantId").setParameter("merchantId",mobileNo ).getResultList();
		for(String i:productIds)
		{	
			
			products.add(entityManager.find(Product.class, i));
			
		}
		return products;
	}


	
	
	
	
	
	
	@Override
	public Product addProduct(Product product,String merchantMobileNo)
	{
//		String merchantMobileNo="9079296110";
		entityManager.persist(product);
		Merchant merchant=entityManager.find(Merchant.class, merchantMobileNo);
		int c=entityManager.createNativeQuery("INSERT INTO merchant_product (prod_id,merchant_id) values(?,?)")
		.setParameter(1, product.getProdId())
		.setParameter(2, merchantMobileNo).executeUpdate();
		System.out.println("no of rows inserted"+c);
				return product;
	}

	@Override
	public void deleteProduct(String prodId) 
	{
	
		System.out.println(prodId);
		Product product =entityManager.find(Product.class,prodId);
		if(product!=null)
		{
			entityManager.remove(product);
		}
	}

	@Override
	public Orders checkOrderDetails(Orders order) 
	{
		Orders order1 = entityManager.find(Orders.class, order.getOrderId());
		return order1;
	}

	@Override
	public Merchant changePassword(Merchant merchant) 
	{
		Merchant merchant1=entityManager.find(Merchant.class, merchant.getMerchantMobileNo());
		merchant1.setPassword(merchant.getPassword());
		entityManager.persist(merchant1);
		return merchant1;
		
		
	}
	
	
	@Override
	public Product updateProduct(Product product,String prodId) 
	{
		Product productexist=entityManager.find(Product.class,prodId);
		System.out.println(product.getName()+","+product.getProdId());
		productexist.setName(product.getName());
		productexist.setSizes(product.getSizes());
		productexist.setAvailableQuantity(product.getAvailableQuantity());
		
		productexist.setProdCategory(product.getProdCategory());
		productexist.setPrice(product.getPrice());
		productexist.setProdDiscount(product.getProdDiscount());
		productexist.setUrl(product.getUrl());;
		entityManager.merge(productexist);
		return productexist;
		
	}

	@Override
	public Product addDiscount(Product product) 
	{
		Product prod=entityManager.find(Product.class, product.getProdId());
		prod.setProdDiscount(product.getProdDiscount());
		entityManager.merge(prod);
		return prod;
	}
	
//====================================================================================// 	  
		 

		@Override
		public List<Product> getCategoryProduct(String prodCategory) {
			System.out.println(prodCategory);
			
			List<Product> productList = new ArrayList<>();
			
			
			Query query = entityManager.createQuery("select p from Product p where p.prodCategory=:cat").setParameter("cat",prodCategory);
			
			 productList = query.getResultList();
			
			/*query=entityManager.createQuery("select p from Product p where p.prodCategory=:cat").setParameter("cat",prodCategory);
			productList=query.getResultList();*/
			
			return productList;
		}

		@Override
		public Product getProductPage(String productId) {
			Product product = entityManager.find(Product.class, productId);

			return product;
		}

		@Override
		public List<Product> findAllProductsCustomer(String customerMobileNo) {

			List<Product> products = new ArrayList<>();
			String cartId = entityManager.find(Customer.class, customerMobileNo).getCart().getCartId();
			//System.out.println(cartId);
			List<String> productIds = entityManager
					.createNativeQuery("select p.prod_id from cart_product p where p.cart_id=:cartId")
					.setParameter("cartId", cartId).getResultList();
			for (String i : productIds) {
				//System.out.println(i + "sssssssssssssssssssss");

				products.add(entityManager.find(Product.class, i));

			}
			// System.out.println(products);
			return products;

		}

		@Override
		public void removeCustomerProduct(String customerMobileNo, String productId) {

			List<Product> products = new ArrayList<>();
			String cartId = entityManager.find(Customer.class, customerMobileNo).getCart().getCartId();
			System.out.println(cartId);
			Query query = entityManager.createNativeQuery(
					"Delete from  cart_product where cart_product.cart_id=:cartId AND cart_product.prod_id=:productId");

			query.setParameter("cartId", cartId);
			query.setParameter("productId", productId);
			int c = query.executeUpdate();

			System.out.println("no of rows deleted=" + c);
			System.out.println("in remove cart");

		}

		@Override
		public void addCartProduct(String customerMobileNo, String productId) {
			System.out.println("in addtocart");
			String cartId = entityManager.find(Customer.class, customerMobileNo).getCart().getCartId();

			entityManager.createNativeQuery("INSERT INTO cart_product (prod_id,cart_id) VALUES (?,?)")
					.setParameter(1, productId).setParameter(2, cartId).executeUpdate();

		}
		public List<Product> findProductsByName(String name) 
	    {
	        List<Product> products=entityManager.createQuery("SELECT p from Product p where p.name=:name").setParameter("name", name).getResultList();
	        return products;
	    }
		@Override
	    public List<Feedback> productFeedback(String prodId) 
	    {
	        
	        List<Feedback> feedbacks=new ArrayList<>();
	    
	        List<String> feedbackIds=entityManager.createNativeQuery("Select p.feedback_id from product_feedback p where p.prod_id=:prodid").setParameter("prodid", prodId).getResultList();
	        
	        
	        
	        
	        for(String i:feedbackIds)
	        {
	            feedbacks.add(entityManager.find(Feedback.class,i));
	        }
	        
	        return feedbacks;
	    }



	
}

