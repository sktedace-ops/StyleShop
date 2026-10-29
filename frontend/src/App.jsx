import {useEffect,useState} from "react";
import {Routes,Route,Navigate} from "react-router-dom";
import {request} from "./api";
import Header from "./components/Header";
import Home from "./pages/Home";
import Login from "./pages/Login";
import ProductDetail from "./pages/ProductDetail";
import Cart from "./pages/Cart";
import Orders from "./pages/Orders";
import OrderDetail from "./pages/OrderDetail";
import AdminProducts from "./pages/AdminProducts";
import AdminCategories from "./pages/AdminCategories";

export default function App(){
  const [auth,setAuth]=useState({loggedIn:false,username:"",role:""});
  const refreshAuth=()=>request("/api/auth/me")
    .then(setAuth)
    .catch(()=>setAuth({loggedIn:false,username:"",role:""}));
  useEffect(()=>{refreshAuth()},[]);
  return <><Header auth={auth} onAuthChange={refreshAuth}/>
    <Routes>
      <Route path="/" element={<Home/>}/>
      <Route path="/login" element={auth.loggedIn?<Navigate to="/"/>:<Login onLogin={refreshAuth}/>}/>
      <Route path="/product/:id" element={<ProductDetail/>}/>
      <Route path="/cart" element={<Cart/>}/>
      <Route path="/orders" element={<Orders/>}/>
      <Route path="/orders/:id" element={<OrderDetail/>}/>
      <Route path="/admin/products" element={auth.role==="ADMIN"?<AdminProducts/>:<Navigate to="/"/>}/>
      <Route path="/admin/categories" element={auth.role==="ADMIN"?<AdminCategories/>:<Navigate to="/"/>}/>
    </Routes>
  </>;
}
