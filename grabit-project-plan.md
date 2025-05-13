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

- ****
- ****
- ****
- ****
- ****
- ****
- ****
- ****
- ****
- ****
- ****
