import React from 'react';
import Home from './Home';
import LandingPage from './LandingPage';
import { Route, BrowserRouter as Router, Routes } from 'react-router-dom';
import NavBar from './NavBar';
import Login from './TestCalls/AppUserTestsCalls/Login';
import Register from './TestCalls/AppUserTestsCalls/Register';
import Verify from './TestCalls/AppUserTestsCalls/Verify';
import ProductForm from './ProductForm';
import ProductPage from './ProductPage';
import ShoppingCartPage from './ShoppingCartPage';
import WatchPage from './WatchPage';
import ProfilePage from './ProfilePage';

function App() {
  return (
    <Router>
      <NavBar/>
      <Routes>
        <Route path='/' element={<LandingPage/>}/>
        <Route path='/login' element={<Login/>}/>
        <Route path='/register' element={<Register/>}/>
        <Route path='/verify' element={<Verify/>}/>
        <Route path='/product/add' element={<ProductForm/>}/>
        <Route path='/product/edit/:id' element={<ProductForm/>}/>
        <Route path='/product/:id' element={<ProductPage/>}/>
        <Route path='/cart' element={<ShoppingCartPage/>}/>
        <Route path='/watch' element={<WatchPage/>}/>
        <Route path='/profile' element={<ProfilePage/>}/>

      </Routes>
    </Router>
      
    
  );
}

export default App;
