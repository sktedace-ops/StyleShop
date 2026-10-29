import {useEffect,useState} from "react";import {useNavigate,useParams,Link} from "react-router-dom";import {request,imageUrl} from "../api";
import PageActions from "../components/PageActions";
export default function ProductDetail(){const {id}=useParams(),nav=useNavigate(),[p,setP]=useState(null),[msg,setMsg]=useState("");
 useEffect(()=>{request(`/api/products/${id}`).then(setP).catch(e=>{if(e.status===401)nav("/login");else setMsg(e.message)})},[id]);
 async function add(){try{await request(`/api/cart/add/${id}`,{method:"POST"});nav("/cart")}catch(e){if(e.status===401)nav("/login");else setMsg(e.message)}}
 if(!p)return <main><PageActions/><p>{msg||"載入中..."}</p></main>;
 return <main className="detail-page"><PageActions/><div className="detail"><img src={imageUrl(p.imageUrl)}/><section><span className="badge">{p.categoryName}</span><h1>{p.name}</h1><h2>NT$ {Number(p.price).toLocaleString()}</h2><p>{p.description}</p>{p.productUrl&&<a href={p.productUrl} target="_blank">商品連結</a>}<button className="primary" onClick={add}>加入購物車</button><Link to="/">返回商品列表</Link></section></div></main>}