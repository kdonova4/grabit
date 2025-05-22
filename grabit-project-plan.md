# GrabIt Project Plan

## High-Level Overview

This web app will serve as a place where users can be buyers or sellers, or both. They will be able to bid on items, buy items, make offers. They can also list their own items and monitor them, select and respond to offers, see bids on their items in real time. Users can wishlist items, apply coupon codes, browse by category and location, dicover items listed near them. 

## Technologies
- **Backend**
    - **Java**
    - **JUnit**
    - **Mockito**
    - **Spring Boot**
    - **JPA**
    - **PostgreSQL**
    - **Swagger**
    - **Docker**
- **Frontend**
    - **JavaScript**
    - **React**
    - **Node.js**
    - **WebSockets**

## Buyers
- **Browse/Search Items**
    - **Search by category, keywords, or location**
    - **Filter by price, condition, seller rating**
    - **Wishlist items**
    - **Discover nearby listings based on your location**
- **Bid on Items**
    - **Ability to place bid on items**
    - **Track your bids through your profile page**
    - **Options to increase bids automatically**
- **Make Offers**
    - **Buyers can send offers to sellers, sellers can counter-offer**
    - **Buyers can Accept or Reject counter-offers**
- **Checkout & Payment**
    - **Add items to cart and go to checkout**
    - **Apply coupon codes and see your discounted price**
    - **Fake payemnts with validation**
- **View Orders & Shipping Status**
    - **Track orders, view your purchase history**
    - **Track shipment status, with tracking number**
- **Leave Seller Reviews and Ratings**
    - **Buying from a seller give you the ability to rate and review that seller**

## Sellers
- **List Items For Sale**
    - **Give products descriptions, images, price**
    - **List items as fixed-price or auctions**
- **Manage Active Listings**
    - **View/edit, extend, or relist items**
    - **View bids in real-time and respond to offers**
    - **Track Views, bids and offers on each product**
- **Respond To Offers**
    - **Accept or reject offers made by buyers**
    - **Make counter-offers**
- **View Sales and Analytics**
    - **View sales history and You seller rating**

## Admin
- **Manage and Moderate Content**
    - **Monitor user activity, items listings, and reviews**
    - **Block or flag users who violate TOS**
    - **Add/Remove coupon codes**
- **User Management**
    - **Manage buyer and seller accounts (suspend, ban, verify, etc.)**
- **Reports and Analytics**
    - **A dashboard with platform-wide metrics like revenue, active listings, and user activity**

## Core Functionality
- **Authentication and User Management**
    - **Register/login/logout, account settings, password recovery, 2FA**
    - **Different roles for admin, buyers, and sellers**
    - **User profile management (payment methods, shipping addresses)**
- **Real-time Updates**
    - **Use WebSockets for real-time bid updates**
    - **Real-time inventory updates for sellers when items are purchased or bid on**
- **Coupon and Discount System**
    - **Allow users to apply promotional codes**
    - **Track and manage coupon codes from an admin dashboard**
- **Search and Filter Functionality**
    - **Powerful search engine to browse by keywords, category, price, distance, etc.**
    - **Auto-suggestions for categories and active auctions**


# Models

- **User**
- **Product**
- **Category**
- **Bid**
- **Offer**
- **Shopping Cart**
- **Watchlist**
- **Order**
- **OrderItem**
- **Payment**
- **Shipment**
- **Review**
- **Coupon**
- **Image**
- **Address**

## User Model

Holds data pertaining to users of the application

### Data
- **userId**: Uniquely identifies each user 
- **username**: unique username for each user - **[REQUIRED]**
- **email**: unique email for each user - **[REQUIRED]**
- **password**: used to verify user on login - **[REQUIRED]**
- **createdAt**: Timestamp for account creation - **[REQUIRED]**

## Product Model

Holds data that pertains to the products of the application

### Data
- **productId**: Uniquely identifies each product
- **postedAt**: Timestamp the product was posted at - **[REQUIRED]**
- **saleType**: Type of product - **[REQUIRED]**
- **name**: The product name - **[REQUIRED]**
- **description**: describes the product - **[REQUIRED]**
- **location**: Address product is shipping from - **[REQUIRED]**
- **price**: The price of the product - **[REQUIRED]**
- **condition**: The condition of the product - **[REQUIRED]** 
- **quantity**: Amount of this product availiable - **[REQUIRED]**
- **productStatus**: The status of the Product, e.g sold, active, removed - **[REQUIRED]**
- **auctionEndTime**: The time that the auction will end
- **userId**: The user who posted the product for sale - **[REQUIRED]**

## Category Model

Holds the potential categories for all products

### Data
- **categoryId**: Uniquely identifies each category
- **categoryName**: Name of category - **[REQUIRED]**

## Bid Model

Holds the information on a bid

### Data
- **bidId**: Uniquely identifies each bid
- **amount**: Amount of the bid - **[REQUIRED]**
- **placedAt**: Timestamp for when the bid was placed - **[REQUIRED]** 
- **productId**: Product that the bid was placed on - **[REQUIRED]**
- **userId**: User that placed the bid - **[REQUIRED]**

## Offer Model

Holds the information of an offer

### Data
- **offerId**: Uniquely identifies each offer
- **amount**: Amount offered for product - **[REQUIRED]**
- **message**: Message sent with offer
- **userId**: User who sent offer - **[REQUIRED]**
- **productId**: Product the offer is sent about - **[REQUIRED]**

## ShoppingCart Model

Holds the products for a user

### Data
- **shoppingCartId**: Uniquely Identifes each cart item
- **productId**: The product - **[REQUIRED]**
- **quantity**: quantity of item - **[REQUIRED]**
- **userId**: The user - **[REQUIRED]**

## Watchlist Model

Holds product in a list for the user

### Data
- **watchId**: Uniquely Identifes each watched item
- **productId**: The product  - **[REQUIRED]**
- **userId**: The user - **[REQUIRED]**

## Order Model

Holds information for an Order

### Data
- **orderId**: Uniquely identifies each order
- **userId**: User who placed the order - **[REQUIRED]**
- **orderedAt**: Timestamp for when the order was placed - **[REQUIRED]**
- **shippingAddress**: Shipping address - **[REQUIRED]**
- **billingAddress**: Billing address - **[REQUIRED]**
- **totalAmount**: Total cost of order - **[REQUIRED]**
- **paymentId**: the payment used - **[REQUIRED]**
- **shipmentId**: The shipment for order 
- **orderStatus**: Status of order - **[REQUIRED]**

## OrderItem Model

Represents a product and its quantity within a given order

### Data
- **orderItemId**: Uniquely defines an order item
- **orderId**: The order the item belongs to - **[REQUIRED]**
- **productId**: The product that was ordered - **[REQUIRED]**
- **quantity**: number of units of the product ordered - **[REQUIRED]**
- **unitPrice**: Price per unit at the time of purchase - **[REQUIRED]**
- **subTotal**: quantity * unitPrice - **[REQUIRED]**

## Payment Model

Holds information on a payment made

### Data
- **paymentId**: Uniquely identifies each payment
- **orderId**: The order the payment is for - **[REQUIRED]**
- **method**: The method the payment was made with - **[REQUIRED]**
- **amount**: amount paid - **[REQUIRED]** 
- **paidAt**: Timestamp for when the payment was made - **[REQUIRED]**

## Shipment Model

Holds information for the shipment of an order

### Data
- **shipmentId**: Uniquely identifies each shipment
- **orderId**: Order the shipment is for - **[REQUIRED]**
- **shipmentStatus**: The status for shipment - **[REQUIRED]**
- **trackingNumber**: Tracking number generated for shipment - **[REQUIRED]**
- **shippedAt**: Timestamp for when shipment was sent - **[REQUIRED]**
- **deliveredAt**: Timestamp for when shipment was delivered

## Review Model

Holds information for reviews for a seller

### Data
- **reviewId**: Uniquely identifies each review
- **rating**: Score for the seller - **[REQUIRED]**
- **reviewText**: The body of the review - **[REQUIRED]**
- **userId**: User who made the review - **[REQUIRED]**
- **sellerId**: The seller that is being reviewed - **[REQUIRED]**
- **productId**: The product the review was made off of - **[REQUIRED]**
- **createdAt**: Timestamp for review - **[REQUIRED]**

## Coupon Model

Holds coupons for the buyers to use at checkout

### Data
- **couponId**: Uniquely identifies each coupon
- **couponCode**: Code for coupon - **[REQUIRED]**
- **discount**: The discount applied by coupon - **[REQUIRED]**
- **discountType**: type of discount e.g. fixed, or percentage - **[REQUIRED]**
- **expireDate**: Time the coupon will expire
- **isActive**: Is the coupon active or not - **[REQUIRED]**

## Image Model

Holds information for each image uploaded

### Data
- **imageId**: Uniquely Identifies each Image
- **imageURL**: url for image - **[REQUIRED]**
- **productId**: the product the image is for - **[REQUIRED]**

## Address Model

Holds information for an address of a user

### Data
- **addressId**: Uniquely Identifies each Address
- **street**: street name - **[REQUIRED]**
- **city**: city name - **[REQUIRED]**
- **state**: state - **[REQUIRED]**
- **zip_code**: zip code - **[REQUIRED]**
- **country**: country name - **[REQUIRED]**
- **userId**: the user the address belongs to - **[REQUIRED]**

## Other Validation Rules



## Other Classes

### SaleType Enum

- **BUYNOW**
- **AUCTION**

### DiscountType Enum

- **FIXED**
- **PERCENTAGE**

### OrderStatus Enum

- **PENDING**
- **SUCCESS**
- **FAILED**

### ConditionType Enum

- **NEW**
- **GOOD**
- **EXCELLENT**
- **FAIR**
- **USED**
- **REFURBISHED**
- **DAMAGED**

### ProductStatus Enum

- **ACTIVE**
- **SOLD**
- **REMOVED**
- **EXPIRED**

### ShipmentStatus Enum

- **PENDING**
- **SHIPPED**
- **INTRANSIT**
- **OUTFORDELIVERY**
- **DELIVERED**


## Database Tables
- **app_user**
- **app_role**
- **app_user_role**
- **product**
- **category**
- **product_category**
- **bid**
- **offer**
- **shopping_cart**
- **watchlist**
- **order**
- **order_item**
- **payment**
- **shipment**
- **review**
- **coupon**
- **image**
- **address**

```
src
├───main
│   └───java
│       └───project
│           └───grabit
│               │   App.java                      -- app entry point
|               |   AppConfig.java
|               |   SwaggerConfig.java
│               │
│               ├───controllers
│               │       GlobalExceptionHandler.java
│               │       ErrorResponse.java
│               │       ProductController.java
│               │       CategoryController.java
│               │       ProductCategoryController.java
│               │       BidController.java
│               │       OfferController.java
│               │       ShoppingCartController.java
│               │       WatchlistController.java
│               │       OrderController.java
│               │       OrderItemController.java
│               │       PaymentController.java
│               │       ShipmentController.java
│               │       ReviewController.java
│               │       CouponController.java
│               │       ImageController.java
|               |       AddressController.java
│               │       
│               ├───data
│               │       AppUserRepository.java      
│               │       ProductRepository.java      
│               │       CategoryRepository.java      
│               │       ProductCategoryRepository.java      
│               │       BidRepository.java      
│               │       OfferRepository.java      
│               │       ShoppingCartRepository.java      
│               │       WatchlistRepository.java      
│               │       OrderRepository.java      
│               │       OrderItemRepository.java      
│               │       PaymentRepository.java      
│               │       ShipmentRepository.java      
│               │       ReviewRepository.java      
│               │       CouponRepository.java      
│               │       ImageRepository.java
|               |       AddressRepository.java      
|               |
│               ├───domain
│               │       Result.java          -- domain result for handling success/failure
│               │       ResultType.java         -- enum value for Result
│               │       ProductService.java -- holds validation/business logic for Product and ProductCategory
│               │       CategoryService.java
│               │       BidService.java
│               │       OfferService.java
│               │       ShoppingCartService.java
│               │       WatchlistService.java
│               │       OrderService.java
│               │       OrderItemService.java
│               │       PaymentService.java
│               │       ShipmentService.java
│               │       ReviewService.java
│               │       CouponService.java
│               │       ImageService.java
|               |       AddressService.java
│               │
│               ├───models
│               │       User.java     
│               │       Product.java
│               │       Category.java
│               │       ProductCategory.java
│               │       Bid.java
│               │       Offer.java
│               │       ShoppingCart.java
│               │       Watchlist.java
│               │       Order.java
│               │       OrderItem.java
│               │       Payment.java
│               │       Shipment.java
│               │       Review.java
│               │       Coupon.java
│               │       Image.java
|               |       Address.java
│               |
|               ├───Enums
|               |     SaleType.java
|               |     DiscountType.java
|               |     OrderStatus.java
|               |     ProductStatus.java
|               |     ConditionType.java
|               |     ShipmentStatus.java
|               |       
|               |
|               └───security
│                       AppUserService.java         -- user validation/rules
│                       JwtConverter.java         
│                       JwtRequestFilter.java         
│                       SecurityConfig.java         
|
|
└───test
    └───java
        └───project
            └───grabit
                ├───controller
│               │       ProductControllerTest.java
│               │       CategoryControllerTest.java
│               │       ProductCategoryControllerTest.java
│               │       BidControllerTest.java
│               │       OfferControllerTest.java
│               │       ShoppingCartControllerTest.java
│               │       WatchlistControllerTest.java
│               │       OrderControllerTest.java
│               │       OrderItemControllerTest.java
│               │       PaymentControllerTest.java
│               │       ShipmentControllerTest.java
│               │       ReviewControllerTest.java
│               │       CouponControllerTest.java
│               │       ImageControllerTest.java
|               |       AddressControllerTest.java
                │
                ├───domain
│               │       ProductServiceTest.java 
│               │       CategoryServiceTest.java
│               │       BidServiceTest.java
│               │       OfferServiceTest.java
│               │       ShoppingCartServiceTest.java
│               │       WatchlistServiceTest.java
│               │       OrderServiceTest.java
│               │       OrderItemRServiceTest.java
│               │       PaymentServiceTest.java
│               │       ShipmentServiceTest.java
│               │       ReviewServiceTest.java
│               │       CouponServiceTest.java
│               │       ImageServiceTest.java
|               |       AddressServiceTest.java
│               │            
                └───data
│               │       AppUserRepositoryTest.java      
│               │       ProductRepositoryTest.java      
│               │       CategoryRepositoryTest.java      
│               │       ProductCategoryRepositoryTest.java      
│               │       BidRepositoryTest.java      
│               │       OfferRepositoryTest.java      
│               │       ShoppingCartRepositoryTest.java      
│               │       WatchlistRepositoryTest.java      
│               │       OrderRepositoryTest.java      
│               │       OrderItemRepositoryTest.java      
│               │       PaymentRepositoryTest.java      
│               │       ShipmentRepositoryTest.java      
│               │       ReviewRepositoryTest.java      
│               │       CouponRepositoryTest.java      
│               │       ImageRepositoryTest.java  
|               |       AddressRepositoryTest.java
│               │       
```

# Class Details

## Models

### Model.Product
- `private int productId`
- `private Timestamp postedAt`
- `private SaleType saleType`
- `private String name`
- `private String description`
- `private BigDecimal price`
- `private ConditionType condition`
- `private int quantity`
- `private ProductStatus status`
- `private LocalDate auctionEndTime`
- `private int userId`

### Model.Category
- `private int categoryId`
- `private String categoryName`

### Model.Bid
- `private int bidId`
- `private BigDecimal amount`
- `private Timestamp placedAt`
- `private Product product`
- `private User user`

### Model.Offer
- `private int offerId`
- `private BigDecimal amount`
- `private String message`
- `private Product product`
- `private User user`

### Model.ShoppingCart
- `private int shoopingCarId`
- `private Product product`
- `private int quantity`
- `private User user`

### Model.Watchlist
- `private int watchId`
- `private Product product`
- `private User user`

### Model.Order
- `private int orderId`
- `private User user`
- `private Timestamp orderedAt`
- `private Address shippingAddress`
- `private Address billingAddress`
- `private BigDecimal totalAmount`
- `private Payment payment`
- `private Shipment shipment`
- `private OrderStatus status`

### Model.OrderItem
- `private int orderItemId`
- `private Order order`
- `private Product product`
- `private int quantity`
- `private BigDecimal unitPrice`
- `private BigDecimal subTotal`

### Model.Payment
- `private int paymentId`
- `private Order order`
- `private BigDecimal amount`
- `private Timestamp paidAt`

### Model.Shipment
- `private int shipmentId`
- `private Order order`
- `private ShipmentStatus status`
- `private String trackingNumber`
- `private Timestamp shippedAt`
- `private Timestamp deliveredAt`

### Model.Review
- `private int reviewId`
- `private int rating`
- `private String reviewText`
- `private User user`
- `private User seller`
- `private Product product`
- `private Timestamp createdAt`

### Model.Coupon
- `private int couponId`
- `private String couponCode`
- `private int discount`
- `private DiscountType type`
- `private LocalDate expireDate`
- `private boolean isActive`

### Model.Image
- `private int imageId`
- `private String imageURL`
- `private Product product`

### Model.Address
- `private int addressId`
- `private String street`
- `private String city`
- `private String state`
- `private String zipCode`
- `private String country`
- `private User user`

## Services

### AppUser Service
- Create

#### Validation & Business Logic
- Username must be unique
- Password must be 8 or more characters
- Password must contain a digit, a letter, and a non-digit/non-letter
- email must be unique
- username, password and email cannot be null

### AppRole Service
- role name cannot be blank or null

### Category Service
- Create
- Delete

#### Validation & Business Logic
- Category Name cannot be null

### Address Service
- Create
- Update

#### Validation & Business Logic
- Street cannot be null
- City cannot be null
- State cannot be null
- Zip Code cannot be null
- Country cannot be null
- App User cannot be null

### Bid Service
- Create
- Delete

#### Validation & Business Logic
- Bid Amount cannot be less than or equal to the price of the product being bid on
- Timestamp cannot be in the future
- Product cannot be null and must exist
- user cannot be null and must exist

### Offer Service
- Create
- Delete

#### Validation & Business Logic
- Offer Amount cannot be equal to the price of the product
- Product must be BUY_NOW type
- Sent At cannot be in the future
- Message cannot be llonger than 200 characters
- user cannot be null and must exist
- product cannot be null and must exist

### Coupon Service
- Create
- Delete

#### Validation & Business Logic
- Coupon Code must be 16 characters
- discount cannot be negative
- discount cannot be more than 100 if DiscountType is Percentage
- Is Active cannot be null

### Image Service
- Create
- Delete

#### Validation & Business Logic
- Image URL cannot be null
- Product must exist and cannot be null

### Order Service
- Create

#### Vallidation & Business Logic
- User must exist and cannot be null
- Ordered At must be in the past
- Shipping and Billing Addresses must exist and cannot be null
- total Amount is the amount of money from the OrderProducts associated with this order
- OrderStatus cannot be null

### OrderProduct Service
- Create

#### Validation & Business Logic
- Order must exist and cannot be null
- Product must exist and cannot be null
- quantity must be 1 or greater
- Unit Price must be the same as the price of the product at the time of purchase
- Sub Total is the Unit Price * Quantity

### Payment Service
- Create

#### Validation & Business Logic
- Order must exist and cannot be null
- amount paid must add up to the Total Amount from Order
- Paid At must be in the past

### Product Service
- Create
- Update
- Delete

#### Validation & Business Logic
- Product Status defaults to ACTIVE
- Any Auction that ends if bids were made the Top bid will be made the Winning Bid
- Auction End is required if Sale Type is AUCTION
- price cannot be negative
- Posted At must be in the past
- product name cannot be blank or null
- description cannot be blank or null
- description must be at or under 500 characters
- Quantity must be 1 or greater
- Product Status id required
- User must exist and cannot be null

### ProductCategory Service
- Create
- Delete

#### Validation & Business Logic
- product cannot be null and must exist
- category cannot be null and must exist

### Review Service
- Create
- Update
- Delete

#### Validation & Business Logic
- rating must be from 1 to 5
- review must be equal to or less than 500 characters
- Posted By must exist and cannot be null
- seller must exist and cannot be null
- product must exist and cannot be null
- created at must be in the past
- One Review per product on from seller
- user can only review a seller once their shipment has been delivered

### Shipment Service
- Create

#### Validation & Business Logic
- Order must exist and cannot be null
- Shipment Status cannot be null
- Tracking number must be 18 characters
- shipped at must be in the past
- Once Shipment Status is changed to Delivered the deleivered at date must be added

### ShoppingCart Service
- Create
- Delete

#### Validation & Business Logic
- Product must exist and cannot be null
- User must exist and cannot be null
- only one combination of user and product allowed, no duplicate items in the shopping cart
- quantity must be 1 or greater, cannot be higher than the products availiable quantity

### Watchlist Service
- Create
- Delete

#### Validation & Business Logic
- Product must exist and cannot be null
- User must exist and cannot be null
- only one combination of user and product allowed, no duplicate items in the watch list

## App Functionality

### Products
- **Selling User can add products**
- **Buying User can buy products, changing their status**
- **Products can be found by name, category**
- **Products can be filtered by condition type, category, location, price**
- **Products can be in mulitple categories**
- **Products should support pagination and optional sorting (by price, date posted, etc.)**
- **Products can be found based on their status**
- **Products can be found by their seller**
- **Only sellers can post products and remove their own products**
- **Admins can remove all products**

### Categories
- **Categories can be added or removed by an Admin**

### Bids
- **Bids can be made by buying users**
- **Bids can be removed by bidding user aslong as ther is more than 12 hours before end time**
- **Admins can remove any bid**
- **Bids are found by the product**
- **Bids are sorted by most recent to least**

### Offers
- **Made buy buying users to sellers for a product**
- **Sellers can accept or refuse the offer**
- **Buyers can send a message along with an offer**
- **Offers Expire after 24 hours**
- **Accepted Offers must be confirmed by user in 24 hours or they are expired**

### Shopping Cart
- **Holds products for user**
- **Entering cart will prompt a check of the status of all products in cart, any invalid items will be removed**

### Watchlist
- **A list of items to watch**
- **Will list the status of products as well**

### Shipments
- **Shipments can be seen on profile page**
- **Shipments will change status over time**

### Payments
- **Users can view their own payment history**
- **Admins can view all user payment histories**

### Reviews
- **Buyers can leave reviews and number ratings for sellers after purchasing a product from them**
- **Buyers can only reveiw sellers they have bought from**
- **Only one review per product bought from a seller**
- **Buyer can only leave review after product is delivered**
- **Buyer can edit or delete review**
- **Admin can remove any reviews**
- **seller cannot remove or edit reviews**

### Coupons
- **Admins add or remove coupons**
- **Coupons have expired date**

### Image
- **Sellers will need to upload at least one iamge for a product**
- **Images will be uploaded to cloud provider, Cloudinary**

### Address
- **Sellers will need to add an address to their profile before they add any products to sell**
- **Buyers add address at checkout**



