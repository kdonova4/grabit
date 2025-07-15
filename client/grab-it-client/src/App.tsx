import React from 'react';
import ProductViewer from "./ProductViewer";
import Home from './Home';
import LandingPage from './LandingPage';
import { Route, BrowserRouter as Router, Routes } from 'react-router-dom';
import NavBar from './NavBar';
import Login from './TestCalls/AppUserTestsCalls/Login';
import Register from './TestCalls/AppUserTestsCalls/Register';
import Verify from './TestCalls/AppUserTestsCalls/Verify';
import ProductForm from './ProductForm';

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
      </Routes>
    </Router>
      
    
  );
}

export default App;
