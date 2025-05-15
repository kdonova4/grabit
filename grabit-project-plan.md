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
- **Payment**
- **Shipment**
- **Review**
- **Coupon**
- **Image**

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
- **categoryId**: What category the product is in - **[REQUIRED]**
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

## Other Validation Rules

### Product 
- ****
- ****

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
│               │       
│               ├───data
│               │       GamePlatformJdbctemplateRepository.java  -- concrete repository
│               │       GamePlatformRepository.java      -- repository interface
│               │
│               ├───domain
│               │       Result.java          -- domain result for handling success/failure
│               │       ResultType.java         -- enum value for Result
│               │       
│               │
│               ├───models
│               │       User.java     -- user model
│               │       
│               │
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
│               │       
│               │       
│               │       
|               |       
|               |       
|               |       
|               |       
|               |         
│               │       
│               │       
                │
                ├───domain
│               │           
│               │            
│               │              
│               │          
│               │           
│               │            
│               │            
│               │          
│               │            
                └───data
│               │         
│               │         
│               │       
│               │       
│               │       
│               │        
│               │       
│               │       
│               │       
```

# Class Details

## Models

### Model.User
- `private int userId`